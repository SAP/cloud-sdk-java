package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.TypedMapView;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * @since 5.5.0
 */
@Beta
@Slf4j
public class IdentityAuthenticationServiceBindingDestinationLoader implements ServiceBindingDestinationLoader
{
    @Nonnull
    private final ServiceBindingDestinationLoader delegateLoader;

    public IdentityAuthenticationServiceBindingDestinationLoader()
    {
        this(ServiceBindingDestinationLoader.defaultLoaderChain());
    }

    // for testing purposes
    IdentityAuthenticationServiceBindingDestinationLoader(
        @Nonnull final ServiceBindingDestinationLoader delegateLoader )
    {
        this.delegateLoader = delegateLoader;
    }

    @Nonnull
    @Override
    public Try<HttpDestination> tryGetDestination( @Nonnull final ServiceBindingDestinationOptions options )
    {
        final ServiceBinding serviceBinding = options.getServiceBinding();
        if( !isIdentityAuthenticationServiceBinding(serviceBinding) ) {
            return Try.failure(new DestinationNotFoundException("The bound service is not backed by the IAS service."));
        }

        final List<EndpointEntry> endpoints = EndpointEntry.allFromServiceBinding(serviceBinding);
        final EndpointEntry endpoint = getEndpoint(options, endpoints);
        if( endpoint == null ) {
            final DestinationAccessException exception =
                new DestinationAccessException("Unable to determine the endpoint for the IAS-based service binding.");
            return Try.failure(exception);
        }

        final ServiceBindingDestinationOptions.Builder optionsBuilder =
            ServiceBindingDestinationOptions
                .forService(ServiceIdentifier.of("identity"))
                .onBehalfOf(options.getOnBehalfOf())
                .withOption(BtpServiceOptions.IasOptions.withTargetUri(endpoint.uri));

        if( endpoint.mutualTlsOnly ) {
            optionsBuilder.withOption(BtpServiceOptions.IasOptions.withMutualTlsOnly());
        }

        return delegateLoader.tryGetDestination(optionsBuilder.build());
    }

    private static boolean isIdentityAuthenticationServiceBinding( @Nonnull final ServiceBinding serviceBinding )
    {
        final Object rawAuthenticationService = serviceBinding.getCredentials().get("authentication-service");
        if( !(rawAuthenticationService instanceof String) ) {
            return false;
        }

        return "identity".equalsIgnoreCase((String) rawAuthenticationService);
    }

    @Nullable
    private static EndpointEntry getEndpoint(
        @Nonnull final ServiceBindingDestinationOptions options,
        @Nonnull final List<EndpointEntry> allEndpoints )
    {
        // TODO: implement a generic option so that users can select the endpoint they want to use
        if( allEndpoints.size() != 1 ) {
            log.warn("IAS-based service binding does not contain exactly one endpoint.");
            return null;
        }

        return allEndpoints.get(0);
    }

    @Value
    private static class EndpointEntry
    {
        String name;
        URI uri;
        boolean mutualTlsOnly;

        @Nonnull
        static List<EndpointEntry> allFromServiceBinding( @Nonnull final ServiceBinding serviceBinding )
        {
            final TypedMapView rootView = TypedMapView.of(serviceBinding);
            if( !rootView.containsKey("endpoints") ) {
                log.warn("IAS-based service binding does not contain any endpoints.");
                return List.of();
            }
            final TypedMapView endpoints = rootView.getMapView("endpoints");
            final List<EndpointEntry> entries = new ArrayList<>();

            try {
                for( final String name : endpoints.getKeys() ) {
                    final TypedMapView rawEntry = endpoints.getMapView(name);
                    entries.add(of(name, rawEntry));
                }
            }
            catch( final Exception e ) {
                log.warn("Failed to parse IAS-based service binding endpoints.", e);
                return List.of();
            }

            return entries;
        }

        @Nonnull
        private static EndpointEntry of( @Nonnull final String name, @Nonnull final TypedMapView rawEntry )
        {
            final URI uri = URI.create(rawEntry.getString("uri"));
            boolean mutualTlsOnly = false;
            if( rawEntry.containsKey("requires-token") ) {
                mutualTlsOnly = !rawEntry.getBoolean("requires-token");
            }

            return new EndpointEntry(name, uri, mutualTlsOnly);
        }
    }
}
