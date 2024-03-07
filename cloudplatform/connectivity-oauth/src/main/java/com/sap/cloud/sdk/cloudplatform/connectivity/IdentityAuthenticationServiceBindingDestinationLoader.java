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
 * A {@link ServiceBindingDestinationLoader} that loads destinations from service bindings backed by the Identity
 * Authentication Service (IAS).
 *
 * @since 5.6.0
 */
@Beta
@Slf4j
public class IdentityAuthenticationServiceBindingDestinationLoader implements ServiceBindingDestinationLoader
{
    private static final DestinationNotFoundException NOT_AN_IAS_SERVICE_BINDING =
        new DestinationNotFoundException("The bound service is not backed by the IAS service.");
    private static final DestinationAccessException NOT_EXACTLY_ONE_ENDPOINT =
        new DestinationAccessException("The IAS-based service binding contains multiple HTTP endpoints.");

    private static final String PROPERTY_MISSING_TEMPLATE =
        "The '%s' attribute of the IAS-based service binding is missing.";
    private static final String PROPERTY_TYPE_MISMATCH_TEMPLATE =
        "The '%s' attribute of the IAS-based service binding is expected to be an instance of %s, but is of type %s instead.";
    private static final String PROPERTY_TYPE_MISMATCH_WITH_FALLBACK_TEMPLATE =
        "The '{}' attribute of the IAS-based service binding is expected to be an instance of {}, which is not the case. The fallback value will be used instead.";

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
        final Try<HttpEndpointEntry> maybeEndpoint =
            IasServiceBindingView
                .tryFromServiceBinding(serviceBinding)
                .flatMapTry(IdentityAuthenticationServiceBindingDestinationLoader::tryGetEndpoint);
        if( maybeEndpoint.isFailure() ) {
            return Try.failure(maybeEndpoint.getCause());
        }

        final HttpEndpointEntry endpoint = maybeEndpoint.get();

        final ServiceBindingDestinationOptions.Builder optionsBuilder =
            ServiceBindingDestinationOptions
                .forService(ServiceIdentifier.of("identity"))
                .onBehalfOf(options.getOnBehalfOf())
                .withOption(BtpServiceOptions.IasOptions.withTargetUri(endpoint.uri));

        if( !endpoint.alwaysRequiresToken ) {
            optionsBuilder.withOption(BtpServiceOptions.IasOptions.withoutTokenForTechnicalProviderUser());
        }

        return delegateLoader.tryGetDestination(optionsBuilder.build());
    }

    @Nonnull
    private static Try<HttpEndpointEntry> tryGetEndpoint( @Nonnull final IasServiceBindingView bindingView )
    {
        if( bindingView.endpoints.size() != 1 ) {
            return Try.failure(NOT_EXACTLY_ONE_ENDPOINT);
        }

        return Try.success(bindingView.endpoints.get(0));
    }

    @Value
    private static class IasServiceBindingView
    {
        @Nonnull
        List<HttpEndpointEntry> endpoints;

        @Nonnull
        static Try<IasServiceBindingView> tryFromServiceBinding( @Nonnull final ServiceBinding serviceBinding )
        {
            final TypedMapView credentials = TypedMapView.ofCredentials(serviceBinding);
            final TypedMapView authenticationService = getAuthenticationServiceOrNull(credentials);

            if( authenticationService == null || !isBackedByIas(authenticationService) ) {
                return Try.failure(NOT_AN_IAS_SERVICE_BINDING);
            }
            return Try.of(() -> getHttpEndpointsOrThrow(credentials)).map(IasServiceBindingView::new);
        }

        private static TypedMapView getAuthenticationServiceOrNull( @Nonnull final TypedMapView credentials )
        {
            return getWithFallback(TypedMapView.class, credentials, "authentication-service", null);
        }

        private static boolean isBackedByIas( @Nonnull final TypedMapView authenticationService )
        {
            final String serviceLabel = getWithFallback(String.class, authenticationService, "service-label", null);
            return "identity".equalsIgnoreCase(serviceLabel);
        }

        @Nonnull
        private static List<HttpEndpointEntry> getHttpEndpointsOrThrow( @Nonnull final TypedMapView credentials )
        {
            final TypedMapView rawEndpoints = getOrThrow(TypedMapView.class, credentials, "endpoints");

            final List<HttpEndpointEntry> endpoints = new ArrayList<>();
            for( final String name : rawEndpoints.getKeys() ) {
                final TypedMapView rawEntry = getOrThrow(TypedMapView.class, rawEndpoints, name);

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
        boolean alwaysRequiresToken;
        boolean requiresMutualTls;

        @Nullable
        static HttpEndpointEntry fromRawEntryOrThrow( @Nonnull final String name, @Nonnull final TypedMapView rawEntry )
        {
            if( !isHttpEndpoint(rawEntry) ) {
                return null;
            }

            final URI uri = getUriOrThrow(rawEntry);
            final boolean requiresTokenForTechnicalAccess = getAlwaysRequiresTokenOrThrow(rawEntry);
            final boolean requiresMutualTls = getRequiresMutualTlsOrThrow(rawEntry);

            return new HttpEndpointEntry(name, uri, requiresTokenForTechnicalAccess, requiresMutualTls);
        }

        private static boolean isHttpEndpoint( @Nonnull final TypedMapView rawEntry )
        {
            final String protocol = getWithFallback(String.class, rawEntry, "protocol", "http");
            return "http".equalsIgnoreCase(protocol);
        }

        @Nonnull
        private static URI getUriOrThrow( @Nonnull final TypedMapView rawEntry )
        {
            return URI.create(getOrThrow(String.class, rawEntry, "uri"));
        }

        private static boolean getAlwaysRequiresTokenOrThrow( @Nonnull final TypedMapView rawEntry )
        {
            return getWithFallbackOrThrow(Boolean.class, rawEntry, "always-requires-token", true);
        }

        private static boolean getRequiresMutualTlsOrThrow( @Nonnull final TypedMapView rawEntry )
        {
            return getWithFallbackOrThrow(Boolean.class, rawEntry, "requires-mtls", true);
        }
    }

    @SuppressWarnings( "unchecked" )
    private static <
        T>
        T
        getOrThrow( @Nonnull final Class<T> valueClass, @Nonnull final TypedMapView mapView, @Nonnull final String key )
    {
        if( !mapView.containsKey(key) ) {
            throw new DestinationAccessException(PROPERTY_MISSING_TEMPLATE.formatted(key));
        }

        final Object value = mapView.get(key);
        if( value == null || !valueClass.isAssignableFrom(value.getClass()) ) {
            final String actualValueClassName = value == null ? "null" : value.getClass().getSimpleName();
            throw new DestinationAccessException(
                PROPERTY_TYPE_MISMATCH_TEMPLATE.formatted(key, valueClass.getSimpleName(), actualValueClassName));
        }

        return (T) value;
    }

    private static <T> T getWithFallbackOrThrow(
        @Nonnull final Class<T> valueClass,
        @Nonnull final TypedMapView mapView,
        @Nonnull final String key,
        @Nullable final T fallback )
    {
        if( !mapView.containsKey(key) ) {
            return fallback;
        }

        return getOrThrow(valueClass, mapView, key);
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T getWithFallback(
        @Nonnull final Class<T> valueClass,
        @Nonnull final TypedMapView mapView,
        @Nonnull final String key,
        @Nullable final T fallback )
    {
        if( !mapView.containsKey(key) ) {
            return fallback;
        }

        final Object value = mapView.get(key);
        if( value == null || !valueClass.isAssignableFrom(value.getClass()) ) {
            log.trace(PROPERTY_TYPE_MISMATCH_WITH_FALLBACK_TEMPLATE, key, valueClass);
            return fallback;
        }

        return (T) mapView.get(key);
    }
}
