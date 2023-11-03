/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
class DefaultHttpDestinationBuilderProxyHandler
{
    @Setter( AccessLevel.PACKAGE )
    private static ServiceBinding serviceBindingConnectivity = null;

    @Setter( AccessLevel.PACKAGE )
    private static ServiceBindingDestinationLoader serviceBindingDestinationLoader = null;

    /**
     * Handler to work resolve a proxied {@link DefaultHttpDestination} object.
     *
     * @param builder
     *            The builder.
     * @throws DestinationAccessException
     *             in case the handling was not successful.
     */
    @Nonnull
    DefaultHttpDestination handle( @Nonnull final DefaultHttpDestination.Builder builder )
        throws DestinationAccessException,
            DestinationNotFoundException
    {
        // add location id header provider, useful to identify one of multiple cloud connectors
        builder.headerProviders(new SapConnectivityLocationIdHeaderProvider());

        // resolve connectivity service binding, necessary for on-premise proxying
        final ServiceBinding serviceBinding = getServiceBindingConnectivity();
        if( serviceBinding == null ) {
            throw new DestinationAccessException("Unable to resolve connectivity service binding.");
        }

        final OnBehalfOf derivedOnBehalfOf = deriveOnBehalfOf(builder);
        final DefaultHttpDestination destination = builder.buildInternal();

        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(serviceBinding)
                .onBehalfOf(derivedOnBehalfOf)
                .withOption(ServiceBindingDestinationOptions.Options.ProxyOptions.destinationToBeProxied(destination))
                .build();

        return (DefaultHttpDestination) getServiceBindingDestinationLoader().getDestination(options);
    }

    // Auth type            | retrieval option | tenant   | user      | result
    // *                    + SUB_ONLY        + NO_TENANT + *        -> (should never happen as it should fail on destination loading)
    // *                    + SUB_ONLY        + TENANT_PR + *        -> (should never happen as it should fail on destination loading)
    // BasicAuth            + ALWAYS_PROVIDER + *         + NO_USER  -> TECHNICAL_USER_PROVIDER
    // BasicAuth            + CURRENT_TENANT  + *         + NO_USER  -> TECHNICAL_USER_CURRENT_TENANT
    // BasicAuth            + SUB_ONLY        + TENANT_T1 + NO_USER  -> TECHNICAL_USER_CURRENT_TENANT
    // PrincipalPropagation + *               + *         + NO_USER  -> (fail, on getHeaders())
    // PrincipalPropagation + ALWAYS_PROVIDER + TENANT_PR + USER_PR  -> TECHNICAL_USER_CURRENT_TENANT (compatibility strategy) or NAME_USER_CURRENT_TENANT (recommended strategy)
    // PrincipalPropagation + ALWAYS_PROVIDER + TENANT_T1 + *        -> (fail, on getHeaders())
    // PrincipalPropagation + CURRENT_TENANT  + *         + *        -> TECHNICAL_USER_CURRENT_TENANT
    @Nonnull
    private static OnBehalfOf deriveOnBehalfOf( @Nonnull final DefaultHttpDestination.Builder builder )
    {
        // lookup authentication type
        final Option<AuthenticationType> authType =
            builder
                .get(DestinationProperty.AUTH_TYPE)
                .orElse(() -> builder.get(DestinationProperty.AUTH_TYPE_FALLBACK));

        // special handling in case of principal propagation
        if( authType.contains(AuthenticationType.PRINCIPAL_PROPAGATION) ) {
            final PrincipalPropagationMode mode =
                builder
                    .get(DestinationProperty.PRINCIPAL_PROPAGATION_MODE)
                    .getOrElse(PrincipalPropagationMode.COMPATIBILITY);

            switch( mode ) {
                case RECOMMENDED:
                    return OnBehalfOf.NAMED_USER_CURRENT_TENANT;
                case COMPATIBILITY: // default
                    builder.headerProviders(new SapConnectivityAuthenticationHeaderProvider());
                    return OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
                case UNKNOWN:
                    throw new IllegalStateException("Principal propagation mode is unknown.");
            }
        }

        final boolean isProvider = builder.get(DestinationProperty.TENANT_ID).filter(String::isEmpty).isDefined();
        return isProvider ? OnBehalfOf.TECHNICAL_USER_PROVIDER : OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
    }

    /**
     * Service Binding lookup for Connectivity Service. Use Service Binding Library in case no static service binding
     * has been assigned previously.
     *
     * @return The service binding representing Connectivity Service. Or {@code null} if no service binding could be
     *         found.
     */
    @Nullable
    private ServiceBinding getServiceBindingConnectivity()
    {
        if( serviceBindingConnectivity == null ) {
            final ServiceBindingAccessor serviceBindingAccessor = DefaultServiceBindingAccessor.getInstance();

            final Predicate<ServiceBinding> predicate =
                b -> ServiceIdentifier.CONNECTIVITY.equals(b.getServiceIdentifier().orElse(null));

            final List<ServiceBinding> serviceBindings =
                serviceBindingAccessor.getServiceBindings().stream().filter(predicate).collect(Collectors.toList());

            if( serviceBindings.isEmpty() ) {
                log.debug("No service bindings found matching Connectivity.");
            } else if( serviceBindings.size() > 1 ) {
                log.debug("More than one service bindings found that match Connectivity.");
            } else {
                serviceBindingConnectivity = serviceBindings.get(0);
            }
        }
        return serviceBindingConnectivity;
    }

    /**
     * Service Binding Destination Loader lookup. Having this manipulatable accessor allows for extensive testing.
     *
     * @return The loader object.
     */
    @Nonnull
    private ServiceBindingDestinationLoader getServiceBindingDestinationLoader()
    {
        if( serviceBindingDestinationLoader == null ) {
            serviceBindingDestinationLoader = ServiceBindingDestinationLoader.defaultLoaderChain();
        }
        return serviceBindingDestinationLoader;
    }

    /**
     * Header provider for SAP-Connectivity-Authentication header.
     */
    static class SapConnectivityAuthenticationHeaderProvider implements DestinationHeaderProvider
    {
        private static final String HEADER_NAME = "SAP-Connectivity-Authentication";

        @Nonnull
        @Override
        public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
        {
            final String headerValue = "Bearer " + AuthTokenAccessor.getCurrentToken().getJwt().getToken();
            return Collections.singletonList(new Header(HEADER_NAME, headerValue));
        }
    }

    /**
     * Header provider for SAP-Connectivity-SCC-Location_ID header.
     */
    static class SapConnectivityLocationIdHeaderProvider implements DestinationHeaderProvider
    {
        @Nonnull
        @Override
        public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
        {
            return requestContext
                .getDestination()
                .get(DestinationProperty.CLOUD_CONNECTOR_LOCATION_ID)
                .map(locationId -> new Header("SAP-Connectivity-SCC-Location_ID", locationId))
                .toJavaList();
        }
    }
}
