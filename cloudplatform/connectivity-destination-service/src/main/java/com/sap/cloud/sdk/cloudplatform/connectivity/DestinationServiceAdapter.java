/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.TypedMapView;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.exception.MultipleServiceBindingsException;
import com.sap.cloud.sdk.cloudplatform.exception.NoServiceBindingException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DestinationServiceAdapter
{
    private static final String SERVICE_PATH = "destination-configuration/v1";

    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    final Function<OnBehalfOf, HttpDestination> serviceDestinationLoader;

    @Nonnull
    private final Supplier<ServiceBinding> serviceBindingSupplier;

    @Nullable
    private final String providerTenantId;

    DestinationServiceAdapter()
    {
        this(null, null, null);
    }

    DestinationServiceAdapter(
        @Nullable final Function<OnBehalfOf, HttpDestination> serviceDestinationLoader,
        @Nullable final Supplier<ServiceBinding> serviceBindingSupplier,
        @Nullable final String providerTenantId )
    {
        this.serviceDestinationLoader =
            serviceDestinationLoader != null ? serviceDestinationLoader : prepareServiceDestinationComputation();
        this.serviceBindingSupplier =
            serviceBindingSupplier != null
                ? serviceBindingSupplier
                : DestinationServiceAdapter::getDestinationServiceBinding;
        this.providerTenantId = providerTenantId;
    }

    @Nonnull
    String getProviderTenantId()
        throws DestinationAccessException
    {
        if( providerTenantId != null ) {
            return providerTenantId;
        }

        final TypedMapView credentials = Try.of(() -> {
            final ServiceBinding binding = serviceBindingSupplier.get();
            return TypedMapView.ofCredentials(binding);
        })
            .getOrElseThrow(
                e -> new DestinationAccessException(
                    "Could not resolve destination to Destination Service on behalf of provider.",
                    e));

        if( credentials.containsKey("tenantid") ) {
            return credentials.getString("tenantid");
        }

        throw new DestinationAccessException(
            """
            The provider tenant id is not defined in the service binding.\
             Please verify that the service binding contains the field 'tenantid' in the credentials list.\
            """);
    }

    private Function<OnBehalfOf, HttpDestination> prepareServiceDestinationComputation()
    {
        final Map<OnBehalfOf, HttpDestination> result = new ConcurrentHashMap<>();
        return behalf -> result.computeIfAbsent(behalf, b -> {
            try {
                final ServiceBinding binding = serviceBindingSupplier.get();
                final ServiceBindingDestinationOptions options =
                    ServiceBindingDestinationOptions.forService(binding).onBehalfOf(b).build();
                return ServiceBindingDestinationLoader.defaultLoaderChain().getDestination(options);
            }
            catch( final Exception e ) {
                throw new DestinationAccessException(
                    "Could not resolve destination to Destination Service on behalf of " + b + ".",
                    e);
            }
        });
    }

    @Nonnull
    private String requestDestinationConfigurationAsJson(
        @Nonnull final String servicePath,
        @Nonnull final Destination serviceDestination,
        final boolean enableUserToken )
        throws DestinationAccessException,
            DestinationNotFoundException
    {
        final URI requestUri;
        try {
            requestUri = new URI(SERVICE_PATH + servicePath);
        }
        catch( final URISyntaxException e ) {
            throw new DestinationAccessException(e);
        }

        log.debug("Querying Destination Service via URI {}.", requestUri);

        final HttpUriRequest request = new HttpGet(requestUri);
        if( enableUserToken ) {
            AuthTokenAccessor
                .tryGetCurrentToken()
                .map(AuthToken::getJwt)
                .map(DecodedJWT::getToken)
                .map(it -> new BasicHeader("x-user-token", it))
                .peek(request::addHeader);
        }

        final HttpResponse response;
        try {
            response = HttpClientAccessor.getHttpClient(serviceDestination).execute(request);
        }
        catch( final IOException e ) {
            throw new DestinationAccessException(e);
        }

        log
            .debug(
                "Destination service returned HTTP status {} ({})",
                response.getStatusLine().getStatusCode(),
                response.getStatusLine().getReasonPhrase());

        final StatusLine status = response.getStatusLine();
        final int statusCode = status.getStatusCode();
        final String reasonPhrase = status.getReasonPhrase();

        if( statusCode != HttpStatus.SC_OK ) {
            if( statusCode == HttpStatus.SC_NOT_FOUND ) {
                throw new DestinationNotFoundException(
                    null,
                    "Destination could not be found for path " + servicePath + ".");
            } else {
                throw new DestinationAccessException(
                    String
                        .format(
                            "Failed to get destinations: destination service returned HTTP status %s (%S) at '%s'.,",
                            statusCode,
                            reasonPhrase,
                            requestUri));
            }
        }

        try {
            final String responseBody = HttpEntityUtil.getResponseBody(response);
            if( responseBody == null ) {
                throw new DestinationAccessException("Failed to get destinations: no body returned in response.");
            }
            return responseBody;
        }
        catch( final IOException e ) {
            throw new DestinationAccessException(e);
        }
    }

    @Nonnull
    String getConfigurationAsJsonWithUserToken( @Nonnull final String servicePath, @Nonnull final OnBehalfOf behalf )
        throws DestinationAccessException,
            DestinationNotFoundException
    {
        return getConfigurationAsJsonInternal(servicePath, behalf, true);
    }

    @Nonnull
    String getConfigurationAsJson( @Nonnull final String servicePath, @Nonnull final OnBehalfOf behalf )
        throws DestinationAccessException,
            DestinationNotFoundException
    {
        return getConfigurationAsJsonInternal(servicePath, behalf, false);
    }

    @Nonnull
    @SuppressWarnings( "ConstantConditions" )
    private String getConfigurationAsJsonInternal(
        @Nonnull final String servicePath,
        @Nonnull final OnBehalfOf behalf,
        final boolean xUserToken )
        throws DestinationAccessException,
            DestinationNotFoundException
    {
        final HttpDestination serviceDestination =
            Objects
                .requireNonNull(
                    serviceDestinationLoader.apply(behalf),
                    () -> "Destination for Destination Service on behalf of " + behalf + " not found.");
        log
            .debug(
                "Querying BTP destination service on service path {} to fetch all destinations at service instance level and using destination: {}",
                servicePath,
                serviceDestination);
        return requestDestinationConfigurationAsJson(servicePath, serviceDestination, xUserToken);
    }

    @Nonnull
    // package-private for testing
    static ServiceBinding getDestinationServiceBinding()
    {
        final List<ServiceBinding> matchingBindings =
            DefaultServiceBindingAccessor
                .getInstance()
                .getServiceBindings()
                .stream()
                .filter(binding -> ServiceIdentifier.DESTINATION.equals(binding.getServiceIdentifier().orElse(null)))
                .collect(Collectors.toList());

        if( matchingBindings.isEmpty() ) {
            throw new NoServiceBindingException(
                "Please make sure you have the Destination Service bound to your application.");
        }

        if( matchingBindings.size() > 1 ) {
            throw new MultipleServiceBindingsException(
                "Having multiple Destination Service bindings is not supported.");
        }
        return matchingBindings.get(0);
    }
}
