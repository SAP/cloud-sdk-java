package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpHeaders;

import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.Options;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
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
public class OAuth2ServiceBindingDestinationLoader implements ServiceBindingDestinationLoader
{
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
        final ServiceIdentifier identifier = options.getServiceBinding().getServiceIdentifier().orElse(null);

        log
            .debug(
                "Checking if the service binding with identifier '{}' can be transformed into a OAuth destination.",
                identifier);

        final OAuth2PropertySupplier propertySupplier = getOAuth2PropertySupplier(options);
        if( propertySupplier == null ) {
            final String msg =
                "Could not transform service binding with identifier '%s' into a destination: None of the %s implementations matched the service binding format. "
                    + "If the binding contains OAuth credentials and is expected to be handled by this loader please inspect the log output. "
                    + "In case the service binding is not supported by default you may provide your own implementation by using the static `registerPropertySupplier` method.";
            return Try
                .failure(new DestinationNotFoundException(null, String.format(msg, identifier, resolvers.size())));
        }
        log.debug("Creating an OAuth2 destination for service '{}'.", identifier);

        final URI serviceUri;
        final URI tokenUri;
        final ClientIdentity clientIdentity;
        final OAuth2Options tokenRetrievalOptions;
        try {
            serviceUri = propertySupplier.getServiceUri();
            tokenUri = propertySupplier.getTokenUri();
            clientIdentity = propertySupplier.getClientIdentity();
            tokenRetrievalOptions = propertySupplier.getOAuth2Options();
        }
        catch( final DestinationAccessException e ) {
            return Try.failure(e);
        }
        catch( final Exception e ) {
            final String msg = "Failed to retrieve OAuth2 properties for service binding of service '%s'.";
            return Try.failure(new DestinationAccessException(String.format(msg, identifier), e));
        }

        final Option<HttpDestination> destinationToBeProxied = options.getOption(Options.ProxyOptions.class);

        try {
            final OnBehalfOf behalfOf = options.getOnBehalfOf();

            // consider destination to be proxied
            if( destinationToBeProxied.isDefined() ) {
                final String msg = "Using the given service {} as a proxy service to enhance destination {}.";
                log.debug(msg, identifier, destinationToBeProxied.get());

                final HttpDestination dest =
                    toProxiedDestination(
                        destinationToBeProxied.get(),
                        serviceUri,
                        tokenUri,
                        clientIdentity,
                        behalfOf,
                        tokenRetrievalOptions,
                        identifier);
                return Try.success(dest);
            }

            // continue without proxied destination
            return Try
                .success(
                    toDestination(serviceUri, tokenUri, clientIdentity, behalfOf, tokenRetrievalOptions, identifier));
        }
        catch( final Exception e ) {
            // might happen in case of invalid certificate
            final String msg = "Failed to instantiate OAuth destination based on given properties.";
            return Try.failure(new DestinationAccessException(msg, e));
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
            log.debug("A resolver matched the given options, loading the relevant property supplier.");
            final Try<OAuth2PropertySupplier> supplierTry = Try.of(() -> resolver.resolve(options));
            if( supplierTry.isFailure() ) {
                log.error("Failed to resolve the property supplier with provided options.", supplierTry.getCause());
                continue;
            }
            final OAuth2PropertySupplier supplier = supplierTry.get();
            log.debug("Using property supplier {} for the given options.", supplier.getClass().getName());

            final Try<Boolean> isOAuthTry = Try.of(supplier::isOAuth2Binding);
            if( isOAuthTry.isFailure() ) {
                log.error("Failed to check whether the property supplier supports OAuth2.", isOAuthTry.getCause());
                continue;
            }
            if( !isOAuthTry.get() ) {
                log.debug("Supplier {} was applied but claims the binding is not an OAuth2 binding.", supplier);
                continue;
            }
            return supplier;
        }
        return null;
    }

    @Nonnull
    HttpDestination toDestination(
        @Nonnull final URI serviceUri,
        @Nonnull final URI tokenUri,
        @Nonnull final ClientIdentity clientIdentity,
        @Nonnull final OnBehalfOf behalf,
        @Nonnull final OAuth2Options oAuth2Options,
        @Nullable final ServiceIdentifier serviceIdentifier )
    {
        // use a hash code of the client id to not unnecessarily expose the client id
        // (as the destination name is included in the toString() method of the destination
        // this should be optional, as the client id is technically not a secret, but using a hash here doesn't hurt
        final String idString =
            Option.of(serviceIdentifier).map(ServiceIdentifier::toString).getOrElse("unknown")
                + "-"
                + clientIdentity.getId().hashCode();
        log.debug("Creating a new OAuth2 destination for service {} with name '{}'.", serviceIdentifier, idString);

        final DefaultHttpDestination.Builder destinationBuilder =
            DefaultHttpDestination.builder(serviceUri).name(idString);
        if( oAuth2Options.skipTokenRetrieval() ) {
            log.debug("Skipping OAuth2 token retrieval for destination '{}'.", idString);
        } else {
            final DestinationHeaderProvider headerProvider =
                createHeaderProvider(
                    tokenUri,
                    clientIdentity,
                    behalf,
                    HttpHeaders.AUTHORIZATION,
                    oAuth2Options,
                    serviceIdentifier);
            destinationBuilder.headerProviders(headerProvider);
        }

        if( oAuth2Options.getClientKeyStore() != null ) {
            log.debug("Securing communication to OAuth2 destination '{}' using mTLS.", idString);
            destinationBuilder.keyStore(oAuth2Options.getClientKeyStore());
        }

        return destinationBuilder.build();
    }

    @Nonnull
    HttpDestination toProxiedDestination(
        @Nonnull final HttpDestination destinationToBeProxied,
        @Nonnull final URI proxyUrl,
        @Nonnull final URI tokenUrl,
        @Nonnull final ClientIdentity clientIdentity,
        @Nonnull final OnBehalfOf behalf,
        @Nonnull final OAuth2Options oAuth2Options,
        @Nullable final ServiceIdentifier serviceIdentifier )
    {
        final String destinationName =
            destinationToBeProxied.get(DestinationProperty.NAME).getOrElse("<unnamed-destination>");
        final DefaultHttpDestination.Builder destinationBuilder =
            DefaultHttpDestination.fromDestination(destinationToBeProxied);

        if( oAuth2Options.skipTokenRetrieval() ) {
            log.debug("Skipping OAuth2 token retrieval for proxied destination '{}'.", destinationName);
        } else {
            final DestinationHeaderProvider headerProvider =
                createHeaderProvider(
                    tokenUrl,
                    clientIdentity,
                    behalf,
                    HttpHeaders.PROXY_AUTHORIZATION,
                    oAuth2Options,
                    serviceIdentifier);
            destinationBuilder.headerProviders(headerProvider);
        }

        if( oAuth2Options.getClientKeyStore() != null ) {
            log
                .debug(
                    "Securing communication to OAuth2 proxy server for proxied destination '{}' using mTLS.",
                    destinationName);
            destinationBuilder.keyStore(oAuth2Options.getClientKeyStore());
        }

        // don't override the proxy URL if it has been set explicitly/manually already
        if( destinationToBeProxied.getProxyConfiguration().isDefined() ) {
            return destinationBuilder.buildInternal();
        }
        return destinationBuilder.proxy(proxyUrl).buildInternal();
    }

    DestinationHeaderProvider createHeaderProvider(
        @Nonnull final URI tokenUrl,
        @Nonnull final ClientIdentity clientIdentity,
        @Nonnull final OnBehalfOf behalf,
        @Nonnull final String authHeader,
        @Nonnull final OAuth2Options oAuth2Options,
        @Nullable final ServiceIdentifier serviceIdentifier )
    {
        log.debug("Creating a new OAuth2 header provider for client id '{}'.", clientIdentity.getId());

        final OAuth2Service oAuth2Service =
            OAuth2Service
                .builder()
                .withTokenUri(tokenUrl)
                .withIdentity(clientIdentity)
                .withOnBehalfOf(behalf)
                .withTenantPropagationStrategyFrom(serviceIdentifier)
                .withAdditionalParameters(oAuth2Options.getAdditionalTokenRetrievalParameters())
                .withTimeLimiter(oAuth2Options.getTimeLimiter())
                .build();
        return new OAuth2HeaderProvider(oAuth2Service, authHeader);
    }
}
