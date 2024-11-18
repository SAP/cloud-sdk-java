package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.TypedMapView;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.Lazy;
import io.vavr.control.Try;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * A {@link ServiceBindingDestinationLoader} that loads destinations from service bindings backed by the Identity
 * Authentication Service (IAS).
 *
 * @since 5.6.0
 */
@Slf4j
public class IdentityAuthenticationServiceBindingDestinationLoader implements ServiceBindingDestinationLoader
{
    private static final DestinationAccessException NOT_EXACTLY_ONE_ENDPOINT =
        new DestinationAccessException("The IAS-based service binding contains multiple HTTP endpoints.");

    private static final String PROPERTY_MISSING_TEMPLATE =
        "The '%s' attribute of the IAS-based service binding is missing.";
    private static final String PROPERTY_TYPE_MISMATCH_TEMPLATE =
        "The '%s' attribute of the IAS-based service binding is expected to be an instance of %s, but is of type %s instead.";
    private static final String PROPERTY_TYPE_MISMATCH_WITH_FALLBACK_TEMPLATE =
        "The '{}' attribute of the IAS-based service binding is expected to be an instance of {}, which is not the case. The fallback value will be used instead.";

    // We have a sort of circular reference here:
    // ServiceBindingDestinationLoader
    //   -> DefaultServiceBindingDestinationLoaderChain
    //      -> DEFAULT_INSTANCE
    //       -> DEFAULT_DELEGATE_LOADERS
    //          -> IdentityAuthenticationServiceBindingDestinationLoader
    //            -> delegateLoader
    //               -> DefaultServiceBindingDestinationLoaderChain
    //                  -> DEFAULT_INSTANCE
    //                      -> ...
    // So this needs to be lazy, as otherwise it will reference DefaultServiceBindingDestinationLoaderChain#DEFAULT_INSTANCE before it is initialized and cause an NPE.
    @Nonnull
    private final Lazy<ServiceBindingDestinationLoader> delegateLoader;

    /**
     * The default constructor.
     */
    public IdentityAuthenticationServiceBindingDestinationLoader()
    {
        delegateLoader = Lazy.of(ServiceBindingDestinationLoader::defaultLoaderChain);
    }

    // for testing purposes
    IdentityAuthenticationServiceBindingDestinationLoader(
        @Nonnull final ServiceBindingDestinationLoader delegateLoader )
    {
        this.delegateLoader = Lazy.of(() -> delegateLoader);
    }

    ServiceBindingDestinationLoader getDelegateLoader()
    {
        return delegateLoader.get();
    }

    @Nonnull
    @Override
    public Try<HttpDestination> tryGetDestination( @Nonnull final ServiceBindingDestinationOptions options )
    {
        final ServiceBinding serviceBinding = options.getServiceBinding();
        final TypedMapView credentials = TypedMapView.ofCredentials(serviceBinding);

        final String preparedMessage =
            "Failed to create a destination for service '%s' using IAS OAuth credentials."
                .formatted(serviceBinding.getServiceIdentifier().orElse(null));

        // please note: The IAS service binding itself is not meant to be handled by this loader, but is handled by the OAuth loader
        // this
        if( !IasServiceBindingView.isBackedByIas(credentials) ) {
            return Try
                .failure(
                    new DestinationNotFoundException(
                        null,
                        preparedMessage + " The service binding does not represent a re-use service backed by IAS."));
        }

        final Try<HttpEndpointEntry> maybeEndpoint =
            IasServiceBindingView
                .tryFromCredentials(credentials)
                .flatMapTry(IdentityAuthenticationServiceBindingDestinationLoader::tryGetEndpoint);
        if( maybeEndpoint.isFailure() ) {
            return Try.failure(new DestinationAccessException(preparedMessage, maybeEndpoint.getCause()));
        }

        final HttpEndpointEntry endpoint = maybeEndpoint.get();

        final ServiceBindingDestinationOptions.Builder optionsBuilder;
        try {
            optionsBuilder = ServiceBindingDestinationOptions.forService(ServiceIdentifier.IDENTITY_AUTHENTICATION);
        }
        catch( final DestinationAccessException e ) {
            return Try.failure(new DestinationAccessException(preparedMessage, e));
        }
        optionsBuilder
            .onBehalfOf(options.getOnBehalfOf())
            .withOption(BtpServiceOptions.AuthenticationServiceOptions.withTargetUri(endpoint.uri));

        if( !endpoint.alwaysRequiresToken ) {
            optionsBuilder.withOption(BtpServiceOptions.IasOptions.withoutTokenForTechnicalProviderUser());
        }

        final Try<HttpDestination> maybeDestination = getDelegateLoader().tryGetDestination(optionsBuilder.build());
        return maybeDestination.isSuccess()
            ? maybeDestination
            : Try.failure(new DestinationAccessException(null, preparedMessage, maybeDestination.getCause()));
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
        static Try<IasServiceBindingView> tryFromCredentials( @Nonnull final TypedMapView credentials )
        {
            return Try.of(() -> getHttpEndpointsOrThrow(credentials)).map(IasServiceBindingView::new);
        }

        private static boolean isBackedByIas( @Nonnull final TypedMapView credentials )
        {
            @Nullable
            final TypedMapView authenticationService =
                getWithFallback(TypedMapView.class, credentials, "authentication-service", null);
            if( authenticationService == null ) {
                return false;
            }

            @Nullable
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
