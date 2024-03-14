/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;

import io.vavr.control.Option;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
class DefaultHttpDestinationBuilderProxyHandler
{
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
        if( !builder.get(DestinationProperty.PROXY_TYPE).contains(ProxyType.ON_PREMISE) ) {
            throw new ShouldNotHappenException(
                "Proxy handler was invoked for destination "
                    + builder.get(DestinationProperty.NAME).getOrElse("unnamed-destination")
                    + " that is not supposed to be proxied.");
        }
        // add location id header provider, useful to identify one of multiple cloud connectors
        builder.headerProviders(new SapConnectivityLocationIdHeaderProvider());

        // resolve connectivity service binding, necessary for on-premise proxying
        final ServiceBinding serviceBinding = getServiceBindingConnectivity();

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

    // Auth type            | destination tenant | PP mode           | result
    // *                    + TENANT_PROVIDER    + *                -> TECHNICAL_USER_PROVIDER
    // *                    + *                  + *                -> TECHNICAL_USER_CURRENT_TENANT
    // PrincipalPropagation + TENANT_PROVIDER    + TOKEN_FORWARDING -> TECHNICAL_USER_PROVIDER
    // PrincipalPropagation + *                  + TOKEN_FORWARDING -> TECHNICAL_USER_CURRENT_TENANT
    // PrincipalPropagation + *                  + TOKEN_EXCHANGE   -> NAMED_USER_CURRENT_TENANT
    //
    // note: destination tenant is one of: [undefined, empty, non-empty]. empty is listed here as TENANT_PROVIDER.
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
                    .getOrElse(PrincipalPropagationMode.TOKEN_FORWARDING);

            return switch (mode) {
                case TOKEN_EXCHANGE -> OnBehalfOf.NAMED_USER_CURRENT_TENANT;
                case TOKEN_FORWARDING -> { // default
                    builder.headerProviders(new SapConnectivityAuthenticationHeaderProvider());
                    yield OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
                }
                case UNKNOWN -> throw new IllegalStateException("Principal propagation mode is unknown.");
            };
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
    @Nonnull
    ServiceBinding getServiceBindingConnectivity()
    {
        final ServiceBindingAccessor serviceBindingAccessor = DefaultServiceBindingAccessor.getInstance();

        final Predicate<ServiceBinding> predicate =
            b -> ServiceIdentifier.CONNECTIVITY.equals(b.getServiceIdentifier().orElse(null));

        final List<ServiceBinding> serviceBindings =
            serviceBindingAccessor.getServiceBindings().stream().filter(predicate).toList();

        if( serviceBindings.isEmpty() ) {
            throw new DestinationAccessException(
                "Unable to resolve connectivity service binding. No service bindings found matching Connectivity.");
        } else if( serviceBindings.size() > 1 ) {
            throw new DestinationAccessException(
                "Unable to resolve connectivity service binding. More than one service bindings found that match Connectivity.");
        } else {
            return serviceBindings.get(0);
        }
    }

    /**
     * Service Binding Destination Loader lookup. Having this manipulatable accessor allows for extensive testing.
     *
     * @return The loader object.
     */
    @Nonnull
    ServiceBindingDestinationLoader getServiceBindingDestinationLoader()
    {
        return ServiceBindingDestinationLoader.defaultLoaderChain();
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
