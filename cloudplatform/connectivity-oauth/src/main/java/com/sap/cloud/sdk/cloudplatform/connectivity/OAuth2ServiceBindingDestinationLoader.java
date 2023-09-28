package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpHeaders;

import com.google.common.annotations.Beta;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.Options;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.security.config.ClientIdentity;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * An implementation of the {@link ServiceBindingDestinationLoader} interface that is capable of producing OAuth2 based
 * {@link HttpDestination} instances.
 * <p>
 * This class will automatically be picked up by the <i>service loader pattern</i>.
 *
 * @since 4.16.0
 */
@Slf4j
@Beta
public class OAuth2ServiceBindingDestinationLoader implements ServiceBindingDestinationLoader
{
    private static final Duration TOKEN_RETRIEVAL_TIMEOUT = Duration.ofSeconds(10);
    @Nonnull
    // package-private for testing
    static final List<OAuth2PropertySupplierResolver> DEFAULT_SERVICE_RESOLVERS =
        BtpServicePropertySuppliers.getDefaultServiceResolvers();

    @Nonnull
    private final List<OAuth2PropertySupplierResolver> resolvers;

    /**
     * Default constructor. This will use the static default resolvers which can be modified via
     * {@link #registerPropertySupplier(Predicate, Function)}.
     */
    public OAuth2ServiceBindingDestinationLoader()
    {
        this(DEFAULT_SERVICE_RESOLVERS);
    }

    // note: this is for unit testing only
    // in productive code this always should be DEFAULT_SERVICE_RESOLVERS so that custom suppliers passed via registerPropertySupplier are respected
    OAuth2ServiceBindingDestinationLoader( @Nonnull final List<OAuth2PropertySupplierResolver> resolvers )
    {
        this.resolvers = resolvers;
    }

    /**
     * Adds the given {@link OAuth2PropertySupplier} to handle matching {@link ServiceBindingDestinationOptions}
     * according to the provided {@link ServiceIdentifier}. The provided supplier will be checked with higher priority
     * than previously registered suppliers.
     * <p>
     * Note: The order of registering suppliers is important. Therefore, the given {@code propertySupplier} will be
     * <b>prepended</b> to the list of all registered property suppliers. The first supplier that matches the given
     * options will be used.
     *
     * @param service
     *            The {@link ServiceIdentifier service} that can be handled by the given property supplier.
     * @param propertySupplier
     *            The {@link OAuth2PropertySupplier} capable of parsing bindings for the given service.
     *
     * @see #registerPropertySupplier(Predicate, Function)
     */
    public static void registerPropertySupplier(
        @Nonnull final ServiceIdentifier service,
        @Nonnull final Function<ServiceBindingDestinationOptions, OAuth2PropertySupplier> propertySupplier )
    {
        log.debug("Prepending a new default resolver {} for service identifier {}.", propertySupplier, service);
        DEFAULT_SERVICE_RESOLVERS
            .add(0, OAuth2PropertySupplierResolver.forServiceIdentifier(service, propertySupplier));
    }

    /**
     * Adds the given {@link OAuth2PropertySupplier} to handle matching {@link ServiceBindingDestinationOptions}. The
     * provided matcher/supplier combination will be checked with higher priority than previously registered suppliers.
     * <p>
     * Note: The order of registering suppliers is important. Therefore, the given {@code propertySupplier} will be
     * <b>prepended</b> to the list of all registered property suppliers. The first supplier that matches the given
     * options will be used.
     *
     * @param optionsMatcher
     *            The {@link Predicate} to determine wether the {@link ServiceBindingDestinationOptions} can be handled
     *            by the given property supplier.
     * @param propertySupplier
     *            The {@link OAuth2PropertySupplier} capable of parsing bindings for the given service.
     * @since 4.23.0
     */
    public static void registerPropertySupplier(
        @Nonnull final Predicate<ServiceBindingDestinationOptions> optionsMatcher,
        @Nonnull final Function<ServiceBindingDestinationOptions, OAuth2PropertySupplier> propertySupplier )
    {
        log.debug("Prepending a new default resolver {} with matcher {}.", propertySupplier, optionsMatcher);
        DEFAULT_SERVICE_RESOLVERS.add(0, new OAuth2PropertySupplierResolver(optionsMatcher, propertySupplier));
    }

    // package private for testing
    static void resetPropertySuppliers()
    {
        log.warn("Resetting the default OAuth2 property suppliers. This should only be used in tests.");

        DEFAULT_SERVICE_RESOLVERS.clear();
        DEFAULT_SERVICE_RESOLVERS.addAll(BtpServicePropertySuppliers.getDefaultServiceResolvers());
    }

    @Nonnull
    @Override
    public Try<HttpDestination> tryGetDestination( @Nonnull final ServiceBindingDestinationOptions options )
    {
        final OAuth2PropertySupplier propertySupplier = getOAuth2PropertySupplier(options);

        if( propertySupplier == null ) {
            return Try
                .failure(
                    new DestinationNotFoundException(
                        null,
                        "No property mapping for the provided service "
                            + options.getServiceBinding().getServiceIdentifier()
                            + " found. You may provide your own mapping by using the static `registerPropertySupplier` method."));
        }

        log.debug("Creating an OAuth2 destination for service {}.", options.getServiceBinding().getServiceIdentifier());

        final URI serviceUri;
        final URI tokenUri;
        final ClientIdentity clientIdentity;
        try {
            serviceUri = propertySupplier.getServiceUri();
            tokenUri = propertySupplier.getTokenUri();
            clientIdentity = propertySupplier.getClientIdentity();
        }
        catch( final DestinationAccessException e ) {
            return Try.failure(e);
        }

        final Option<HttpDestination> destinationToBeProxied = options.getOption(Options.ProxyOptions.class);

        final Supplier<HttpDestination> destinationSupplier;
        if( destinationToBeProxied.isDefined() ) {
            log
                .debug(
                    "Using the given service {} as a proxy service to enhance destination {}.",
                    options.getServiceBinding().getServiceIdentifier(),
                    destinationToBeProxied.get());

            destinationSupplier =
                () -> toProxiedDestination(
                    destinationToBeProxied.get(),
                    serviceUri,
                    tokenUri,
                    clientIdentity,
                    options.getOnBehalfOf());
        } else {
            final ServiceIdentifier identifier = options.getServiceBinding().getServiceIdentifier().orElse(null);
            destinationSupplier =
                () -> toDestination(serviceUri, tokenUri, clientIdentity, options.getOnBehalfOf(), identifier);
        }

        try {
            final HttpDestination result = destinationSupplier.get();
            return Try.success(result);
        }
        catch( final Exception e ) {
            // might happen in case of invalid certificate
            return Try
                .failure(
                    new DestinationAccessException(
                        "Failed to instantiate OAuth destination based on given properties.",
                        e));
        }
    }

    @Nullable
    @SuppressWarnings( "PMD.AvoidBranchingStatementAsLastInLoop" )
    private OAuth2PropertySupplier getOAuth2PropertySupplier( @Nonnull final ServiceBindingDestinationOptions options )
    {
        for( final OAuth2PropertySupplierResolver resolver : resolvers ) {
            final Try<Boolean> matchTry = Try.of(() -> resolver.matches(options));
            if( matchTry.isFailure() ) {
                log.error("Failed to check whether binding options match the resolver.", matchTry.getCause());
                continue;
            }
            if( !matchTry.get() ) {
                continue;
            }
            final Try<OAuth2PropertySupplier> supplierTry = Try.of(() -> resolver.resolve(options));
            if( supplierTry.isFailure() ) {
                log.error("Failed to resolve the property supplier with provided options.", supplierTry.getCause());
                continue;
            }
            final Try<Boolean> isOAuthTry = Try.of(() -> supplierTry.get().isOAuth2Binding());
            if( isOAuthTry.isFailure() ) {
                log.error("Failed to check whether the property supplier supports OAuth2.", isOAuthTry.getCause());
                continue;
            }
            if( !isOAuthTry.get() ) {
                continue;
            }
            return supplierTry.get();
        }
        return null;
    }

    @Nonnull
    HttpDestination toProxiedDestination(
        @Nonnull final HttpDestination destinationToBeProxied,
        @Nonnull final URI proxyUrl,
        @Nonnull final URI tokenUrl,
        @Nonnull final ClientIdentity clientIdentity,
        @Nonnull final OnBehalfOf behalf )
    {
        final DestinationHeaderProvider headerProvider =
            createHeaderProvider(tokenUrl, clientIdentity, behalf, HttpHeaders.PROXY_AUTHORIZATION);

        final String name =
            destinationToBeProxied.get(DestinationProperty.NAME).getOrElse(() -> UUID.randomUUID().toString());

        return DefaultHttpDestination
            .fromDestination(destinationToBeProxied)
            .proxy(proxyUrl)
            .headerProviders(headerProvider)
            .property(
                OAuthHeaderProvider.PROPERTY_OAUTH2_RESILIENCE_CONFIG,
                createTokenRetrievalResilienceConfiguration(name))
            .build();
    }

    @Nonnull
    HttpDestination toDestination(
        @Nonnull final URI serviceUri,
        @Nonnull final URI tokenUri,
        @Nonnull final ClientIdentity clientIdentity,
        @Nonnull final OnBehalfOf behalf,
        @Nullable final ServiceIdentifier serviceIdentifier )
    {
        log.debug("Creating a new OAuth2 destination for service {}.", serviceIdentifier);
        final DestinationHeaderProvider headerProvider =
            createHeaderProvider(tokenUri, clientIdentity, behalf, HttpHeaders.AUTHORIZATION);
        final String idString = Option.of(serviceIdentifier).map(ServiceIdentifier::toString).getOrElse("unknown");
        final String name = idString + "-" + UUID.randomUUID();
        return DefaultHttpDestination
            .builder(serviceUri)
            .headerProviders(headerProvider)
            .name(name)
            .property(
                OAuthHeaderProvider.PROPERTY_OAUTH2_RESILIENCE_CONFIG,
                createTokenRetrievalResilienceConfiguration(name))
            .build();
    }

    DestinationHeaderProvider createHeaderProvider(
        @Nonnull final URI tokenUrl,
        @Nonnull final ClientIdentity clientIdentity,
        @Nonnull final OnBehalfOf behalf,
        @Nonnull final String authHeader )
    {
        log.debug("Creating a new OAuth2 header provider for client id {}.", clientIdentity.getId());

        final OAuth2ServiceImpl oAuth2Service = OAuth2ServiceImpl.fromCredentials(tokenUrl.toString(), clientIdentity);
        return new OAuthHeaderProvider(oAuth2Service, behalf, authHeader);
    }

    @Nonnull
    private static ResilienceConfiguration createTokenRetrievalResilienceConfiguration(
        @Nonnull final String destinationName )
    {
        return ResilienceConfiguration
            .of(destinationName)
            .isolationMode(ResilienceIsolationMode.TENANT_OPTIONAL)
            .timeLimiterConfiguration(ResilienceConfiguration.TimeLimiterConfiguration.of(TOKEN_RETRIEVAL_TIMEOUT));
    }
}
