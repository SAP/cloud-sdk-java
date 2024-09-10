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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.event.Level;

import com.google.common.base.Strings;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.TypedMapView;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.exception.MultipleServiceBindingsException;
import com.sap.cloud.sdk.cloudplatform.exception.NoServiceBindingException;

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

        throw new DestinationAccessException("""
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
    String getConfigurationAsJson(
        @Nonnull final String servicePath,
        @Nonnull final DestinationRetrievalStrategy strategy )
        throws DestinationAccessException,
            DestinationNotFoundException
    {
        final HttpDestination serviceDestination =
            Objects
                .requireNonNull(
                    serviceDestinationLoader.apply(strategy.behalf()),
                    () -> "Destination for Destination Service on behalf of " + strategy.behalf() + " not found.");

        final HttpUriRequest request = prepareRequest(servicePath, strategy);

        final HttpResponse response;
        try {
            response = HttpClientAccessor.getHttpClient(serviceDestination).execute(request);
        }
        catch( final IOException e ) {
            throw new DestinationAccessException(e);
        }
        return handleResponse(request, response);
    }

    @Nonnull
    private static String handleResponse( final HttpUriRequest request, final HttpResponse response )
    {
        final StatusLine status = response.getStatusLine();
        final int statusCode = status.getStatusCode();
        final String reasonPhrase = status.getReasonPhrase();

        Try<String> maybeBody = Try.of(() -> HttpEntityUtil.getResponseBody(response));
        String logMessage = "Destination service returned HTTP status %s (%s)";
        if( maybeBody.isFailure() ) {
            final var ex =
                new DestinationAccessException("Failed to read body from HTTP response", maybeBody.getCause());
            maybeBody = Try.failure(ex);
            logMessage = String.format(logMessage, statusCode, reasonPhrase);
        } else {
            logMessage = String.format(logMessage + "and body '%s'", statusCode, reasonPhrase, maybeBody.get());
        }

        if( statusCode == HttpStatus.SC_OK ) {
            final var ex = new DestinationAccessException("Failed to get destinations: no body returned in response.");
            maybeBody = maybeBody.filter(it -> !Strings.isNullOrEmpty(it), () -> ex);
            log.atLevel(maybeBody.isSuccess() ? Level.DEBUG : Level.ERROR).log(logMessage);
            return maybeBody.get();
        }

        log.error(logMessage);
        final String requestUri = request.getURI().getPath();
        if( statusCode == HttpStatus.SC_NOT_FOUND ) {
            throw new DestinationNotFoundException(null, "Destination could not be found for path " + requestUri + ".");
        }
        throw new DestinationAccessException(
            String
                .format(
                    "Failed to get destinations: destination service returned HTTP status %s (%S) at '%s'.,",
                    statusCode,
                    reasonPhrase,
                    requestUri));

    }

    private HttpUriRequest prepareRequest( final String servicePath, final DestinationRetrievalStrategy strategy )
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

        final String headerName = switch( strategy.tokenForwarding() ) {
            case USER_TOKEN -> "x-user-token";
            case REFRESH_TOKEN -> "x-refresh-token";
            case NONE -> null;
        };
        if( headerName != null ) {
            request.addHeader(headerName, strategy.token());
        }
        if( strategy.fragment() != null ) {
            request.addHeader("x-fragment-name", strategy.fragment());
        }
        return request;
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
                .toList();

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
