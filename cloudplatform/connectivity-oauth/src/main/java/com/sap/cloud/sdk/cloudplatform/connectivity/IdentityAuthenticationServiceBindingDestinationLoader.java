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
import com.sap.cloud.environment.servicebinding.api.exception.ValueCastException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * A {@link ServiceBindingDestinationLoader} that loads destinations from service bindings backed by the Identity
 * Authentication Service (IAS).
 *
 * @since 5.6.0
 */
@Beta
@Slf4j
public class IdentityAuthenticationServiceBindingDestinationLoader implements ServiceBindingDestinationLoader
{
    private static final Exception NOT_AN_IAS_SERVICE_BINDING =
        new DestinationNotFoundException("The bound service is not backed by the IAS service.");
    private static final DestinationAccessException NO_ENDPOINTS_DEFINED =
        new DestinationAccessException("The IAS-based service binding does not contain any HTTP endpoints.");

    private static final ServiceIdentifier NULL_IDENTIFIER = ServiceIdentifier.of("unknown-service");
    @Nonnull
    private final ServiceBindingDestinationLoader delegateLoader;

    /**
     * The default constructor.
     */
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
        final Try<IasServiceBindingView> maybeBindingView =
            Try.of(() -> IasServiceBindingView.fromServiceBindingOrThrow(serviceBinding));
        if( maybeBindingView.isFailure() ) {
            return Try.failure(maybeBindingView.getCause());
        }

        final IasServiceBindingView bindingView = maybeBindingView.get();
        if( bindingView == null ) {
            return Try.failure(NOT_AN_IAS_SERVICE_BINDING);
        }

        final Try<HttpEndpointEntry> maybeEndpoint = Try.of(() -> getEndpointOrThrow(bindingView));
        if( maybeEndpoint.isFailure() ) {
            return Try.failure(maybeEndpoint.getCause());
        }

        final HttpEndpointEntry endpoint = maybeEndpoint.get();

        final ServiceBindingDestinationOptions.Builder optionsBuilder =
            ServiceBindingDestinationOptions
                .forService(ServiceIdentifier.of("identity"))
                .onBehalfOf(options.getOnBehalfOf())
                .withOption(BtpServiceOptions.IasOptions.withTargetUri(endpoint.uri));

        if( !endpoint.requiresTokenForTechnicalAccess
            && options.getOnBehalfOf() != OnBehalfOf.NAMED_USER_CURRENT_TENANT ) {
            optionsBuilder.withOption(BtpServiceOptions.IasOptions.withMutualTlsOnly());
        } else if( bindingView.applicationName != null ) {
            optionsBuilder.withOption(BtpServiceOptions.IasOptions.withApplicationName(bindingView.applicationName));
        }

        return delegateLoader.tryGetDestination(optionsBuilder.build());
    }

    @Nonnull
    private static HttpEndpointEntry getEndpointOrThrow( @Nonnull final IasServiceBindingView bindingView )
    {
        if( bindingView.endpoints.isEmpty() ) {
            throw NO_ENDPOINTS_DEFINED;
        }

        if( bindingView.endpoints.size() > 1 ) {
            log
                .warn(
                    "The IAS-based service binding for service '{}' contains multiple HTTP endpoints. Only the first one will be used.",
                    bindingView.serviceIdentifier);
        }

        return bindingView.endpoints.get(0);
    }

    @Value
    private static class IasServiceBindingView
    {
        @Nonnull
        ServiceIdentifier serviceIdentifier;
        @Nullable
        String applicationName;
        @Nonnull
        List<HttpEndpointEntry> endpoints;

        @Nullable
        static IasServiceBindingView fromServiceBindingOrThrow( @Nonnull final ServiceBinding serviceBinding )
        {
            final TypedMapView credentials = TypedMapView.ofCredentials(serviceBinding);
            final TypedMapView authenticationService = getAuthenticationServiceOrNull(credentials);

            if( authenticationService == null || !isBackedByIas(authenticationService) ) {
                return null;
            }

            final String applicationName = getApplicationNameOrNull(authenticationService);
            final List<HttpEndpointEntry> endpoints = getHttpEndpointsOrThrow(credentials);
            return new IasServiceBindingView(
                serviceBinding.getServiceIdentifier().orElse(NULL_IDENTIFIER),
                applicationName,
                endpoints);
        }

        private static TypedMapView getAuthenticationServiceOrNull( @Nonnull final TypedMapView credentials )
        {
            if( !credentials.containsKey("authentication-service") ) {
                return null;
            }

            try {
                return credentials.getMapView("authentication-service");
            }
            catch( final ValueCastException e ) {
                // the `authentication-service` entry is not an object
                return null;
            }
        }

        private static boolean isBackedByIas( @Nonnull final TypedMapView authenticationService )
        {
            if( !authenticationService.containsKey("service-label") ) {
                return false;
            }

            try {
                return "identity".equalsIgnoreCase(authenticationService.getString("service-label"));
            }
            catch( final ValueCastException e ) {
                // the `service-label` entry is not a string
                return false;
            }
        }

        @Nullable
        private static String getApplicationNameOrNull( @Nonnull final TypedMapView authenticationService )
        {
            if( !authenticationService.containsKey("application-name") ) {
                return null;
            }

            try {
                return authenticationService.getString("application-name");
            }
            catch( final ValueCastException e ) {
                // the `application-name` entry is not a string
                return null;
            }
        }

        @Nonnull
        private static List<HttpEndpointEntry> getHttpEndpointsOrThrow( @Nonnull final TypedMapView credentials )
        {
            if( !credentials.containsKey("endpoints") ) {
                throw NO_ENDPOINTS_DEFINED;
            }

            final TypedMapView rawEndpoints;
            try {
                rawEndpoints = credentials.getMapView("endpoints");
            }
            catch( final ValueCastException e ) {
                // the `endpoints` entry is not an object
                throw new DestinationAccessException(
                    "The IAS-based service binding does not contain a valid 'endpoints' attribute.",
                    e);
            }

            final List<HttpEndpointEntry> endpoints = new ArrayList<>();
            for( final String name : rawEndpoints.getKeys() ) {
                final TypedMapView rawEntry;
                try {
                    rawEntry = rawEndpoints.getMapView(name);
                }
                catch( final ValueCastException e ) {
                    // the entry is not an object
                    throw new DestinationAccessException(
                        "The IAS-based service binding contains an endpoint that is not an object.",
                        e);
                }

                final HttpEndpointEntry endpoint = HttpEndpointEntry.fromRawEntryOrThrow(name, rawEntry);
                if( endpoint != null ) {
                    endpoints.add(endpoint);
                }
            }

            return endpoints;
        }
    }

    @Value
    private static class HttpEndpointEntry
    {
        @Nonnull
        String name;
        @Nonnull
        URI uri;
        boolean requiresTokenForTechnicalAccess;

        boolean requiresMutualTls;

        @Nullable
        static HttpEndpointEntry fromRawEntryOrThrow( @Nonnull final String name, @Nonnull final TypedMapView rawEntry )
        {
            if( !isHttpEndpoint(rawEntry) ) {
                return null;
            }

            final URI uri = getUriOrThrow(name, rawEntry);
            final boolean requiresTokenForTechnicalAccess = getRequiresTokenForTechnicalAccessOrThrow(name, rawEntry);
            final boolean requiresMutualTls = getRequiresMutualTlsOrThrow(name, rawEntry);

            return new HttpEndpointEntry(name, uri, requiresTokenForTechnicalAccess, requiresMutualTls);
        }

        private static boolean isHttpEndpoint( @Nonnull final TypedMapView rawEntry )
        {
            if( !rawEntry.containsKey("protocol") ) {
                return false;
            }

            try {
                return "http".equalsIgnoreCase(rawEntry.getString("protocol"));
            }
            catch( final ValueCastException e ) {
                // the entry is not of type `String`
                return false;
            }
        }

        @Nonnull
        private static URI getUriOrThrow( @Nonnull final String endpointName, @Nonnull final TypedMapView rawEntry )
        {
            try {
                return URI.create(rawEntry.getString("uri"));
            }
            catch( final Exception e ) {
                throw new DestinationAccessException(
                    "The URI of the IAS-based service binding for endpoint '%s' is not a valid URI."
                        .formatted(endpointName),
                    e);
            }
        }

        private static boolean getRequiresTokenForTechnicalAccessOrThrow(
            @Nonnull final String endpointName,
            @Nonnull final TypedMapView rawEntry )
        {
            try {
                if( rawEntry.containsKey("requires-token-for-technical-access") ) {
                    return rawEntry.getBoolean("requires-token-for-technical-access");
                }
                return true;
            }
            catch( final Exception e ) {
                throw new DestinationAccessException(
                    "The 'requires-token-for-technical-access' attribute of the IAS-based service binding for endpoint '%s' is not a valid boolean."
                        .formatted(endpointName),
                    e);
            }
        }

        private static
            boolean
            getRequiresMutualTlsOrThrow( @Nonnull final String endpointName, @Nonnull final TypedMapView rawEntry )
        {
            try {
                if( rawEntry.containsKey("requires-mtls") ) {
                    return rawEntry.getBoolean("requires-mtls");
                }
                return true;
            }
            catch( final Exception e ) {
                throw new DestinationAccessException(
                    "The 'requires-mtls' attribute of the IAS-based service binding for endpoint '%s' is not a valid boolean."
                        .formatted(endpointName),
                    e);
            }
        }

    }
}
