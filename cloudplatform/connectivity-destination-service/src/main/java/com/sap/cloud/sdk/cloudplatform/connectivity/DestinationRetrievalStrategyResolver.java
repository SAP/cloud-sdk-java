/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.SYSTEM_USER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ONLY_SUBSCRIBER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_PROVIDER;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings( "PMD.TooManyStaticImports" ) // without these static imports the code becomes unreadable
class DestinationRetrievalStrategyResolver
{
    private final Supplier<String> providerTenantIdSupplier;
    private final Function<DestinationRetrievalStrategy, DestinationServiceV1Response> destinationRetriever;
    private final Function<OnBehalfOf, List<DestinationProperties>> allDestinationRetriever;

    static final String JWT_ATTR_EXT = "ext_attr";
    static final String JWT_ATTR_ENHANCER = "enhancer";
    static final String JWT_ATTR_XSUAA = "XSUAA";

    static DestinationRetrievalStrategyResolver forSingleDestination(
        final Supplier<String> providerTenantIdSupplier,
        final Function<DestinationRetrievalStrategy, DestinationServiceV1Response> destinationRetriever )
    {
        return new DestinationRetrievalStrategyResolver(
            providerTenantIdSupplier,
            destinationRetriever,
            ( any ) -> Collections.emptyList());
    }

    static DestinationRetrievalStrategyResolver forAllDestinations(
        final Supplier<String> providerTenantIdSupplier,
        final Function<OnBehalfOf, List<DestinationProperties>> allDestinationRetriever )
    {
        return new DestinationRetrievalStrategyResolver(
            providerTenantIdSupplier,
            ( any ) -> null,
            allDestinationRetriever);
    }

    @SuppressWarnings( "deprecation" )
    DestinationRetrievalStrategy resolveSingleRequestStrategy(
        @Nonnull final DestinationServiceRetrievalStrategy retrievalStrategy,
        @Nonnull final DestinationServiceTokenExchangeStrategy tokenExchangeStrategy,
        @Nullable final String refreshToken )
    {
        final OnBehalfOf behalfTechnicalUser = switch( retrievalStrategy ) {
            case ALWAYS_PROVIDER -> TECHNICAL_USER_PROVIDER;
            case CURRENT_TENANT, ONLY_SUBSCRIBER -> TECHNICAL_USER_CURRENT_TENANT;
        };

        if( refreshToken != null ) {
            return DestinationRetrievalStrategy.withRefreshToken(behalfTechnicalUser, refreshToken);
        }

        final Try<String> maybeToken =
            AuthTokenAccessor.tryGetCurrentToken().map(AuthToken::getJwt).map(DecodedJWT::getToken);

        return switch( tokenExchangeStrategy ) {
            case FORWARD_USER_TOKEN -> maybeToken.isEmpty()
                ? DestinationRetrievalStrategy.withoutToken(behalfTechnicalUser)
                : DestinationRetrievalStrategy.withUserToken(behalfTechnicalUser, maybeToken.get());
            case LOOKUP_ONLY -> DestinationRetrievalStrategy.withoutToken(behalfTechnicalUser);
            case EXCHANGE_ONLY -> DestinationRetrievalStrategy.withoutToken(NAMED_USER_CURRENT_TENANT);
            // this method must never be called for LOOKUP_THEN_EXCHANGE
            case LOOKUP_THEN_EXCHANGE -> throw new IllegalStateException(
                "Unexpected token exchange strategy "
                    + tokenExchangeStrategy
                    + " when building a request towards the destination service.");
        };
    }

    DestinationRetrieval prepareSupplier( @Nonnull final DestinationOptions options )
    {
        final DestinationServiceRetrievalStrategy retrievalStrategy =
            DestinationServiceOptionsAugmenter.getRetrievalStrategy(options).getOrElse(CURRENT_TENANT);

        final DestinationServiceTokenExchangeStrategy tokenExchangeStrategy =
            DestinationServiceOptionsAugmenter
                .getTokenExchangeStrategy(options)
                .getOrElse(this::getDefaultTokenExchangeStrategy);
        final String refreshToken =
            DestinationServiceOptionsAugmenter
                .getRefreshToken(options)
                .peek(any -> log.debug("Refresh token given, applying refresh token flow."))
                .getOrNull();
        final String fragmentName =
            DestinationServiceOptionsAugmenter
                .getFragmentName(options)
                .peek(it -> log.debug("Found fragment name '{}'.", it))
                .getOrNull();

        log
            .debug(
                "Loading destination from reuse-destination-service with retrieval strategy {} and token exchange strategy {}.",
                retrievalStrategy,
                tokenExchangeStrategy);
        return prepareSupplier(retrievalStrategy, tokenExchangeStrategy, refreshToken, fragmentName);
    }

    /**
     * Get the default token exchange strategy, in case the user hasn't specified one.
     * <ul>
     * <li>If no token is available in current context, fallback to {@code LOOKUP_THEN_EXCHANGE}.</li>
     * <li>If current token is not originating from XSUAA, fallback to {@code LOOKUP_THEN_EXCHANGE}.</li>
     * <li>Use {@code FORWARD_USER_TOKEN} otherwise.</li>
     * </ul>
     * An XSUAA token is recognized when its payload contains {@code {"ext_attr":{"enhancer":"XSUAA"}}}.
     *
     * @return The current default token exchange strategy.
     */
    @Nonnull
    @SuppressWarnings( "deprecation" )
    private DestinationServiceTokenExchangeStrategy getDefaultTokenExchangeStrategy()
    {
        // extract extended attributes from current token, or null<br>
        final Map<String, Object> attributes =
            AuthTokenAccessor.tryGetCurrentToken().map(t -> t.getJwt().getClaim(JWT_ATTR_EXT).asMap()).getOrNull();

        // consider scenario where "forward user token" will not work out-of-the-box, e.g. for IAS identity service
        if( attributes == null || !JWT_ATTR_XSUAA.equalsIgnoreCase(attributes.get(JWT_ATTR_ENHANCER) + "") ) {
            log
                .debug(
                    "Falling back to {}. Current user token may not originate from XSUAA.",
                    DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
            return DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE;
        }

        return FORWARD_USER_TOKEN;
    }

    @SuppressWarnings( "deprecation" )
    DestinationRetrieval prepareSupplier(
        @Nonnull final DestinationServiceRetrievalStrategy retrievalStrategy,
        @Nonnull final DestinationServiceTokenExchangeStrategy tokenExchangeStrategy,
        @Nullable final String refreshToken,
        @Nullable final String fragmentName )
        throws DestinationAccessException
    {
        log
            .debug(
                "Preparing request(s) towards the destination service based on the strategies {} and {}",
                retrievalStrategy,
                tokenExchangeStrategy);
        warnOrThrowOnUnsupportedCombinations(retrievalStrategy, tokenExchangeStrategy);

        if( tokenExchangeStrategy == DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE ) {
            final DestinationRetrievalStrategy strategy =
                resolveSingleRequestStrategy(
                    retrievalStrategy,
                    DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY,
                    refreshToken);
            if( fragmentName != null ) {
                strategy.withFragmentName(fragmentName);
            }
            return new DestinationRetrieval(() -> {
                final DestinationServiceV1Response result = destinationRetriever.apply(strategy);
                if( !doesDestinationConfigurationRequireUserTokenExchange(result) ) {
                    return result;
                }
                if( retrievalStrategy == ALWAYS_PROVIDER && !currentTenantIsProvider() ) {
                    throw new DestinationAccessException(
                        "Can't perform token exchange, the current token is not issued for the provider tenant.");
                }
                return destinationRetriever.apply(DestinationRetrievalStrategy.withoutToken(NAMED_USER_CURRENT_TENANT));
            }, strategy.behalf());
        }

        final DestinationRetrievalStrategy strategy =
            resolveSingleRequestStrategy(retrievalStrategy, tokenExchangeStrategy, refreshToken);
        if( fragmentName != null ) {
            strategy.withFragmentName(fragmentName);
        }
        return new DestinationRetrieval(() -> destinationRetriever.apply(strategy), strategy.behalf());
    }

    @SuppressWarnings( "deprecation" )
    private void warnOrThrowOnUnsupportedCombinations(
        @Nonnull final DestinationServiceRetrievalStrategy retrievalStrategy,
        @Nullable final DestinationServiceTokenExchangeStrategy tokenExchangeStrategy )
    {
        if( retrievalStrategy == ONLY_SUBSCRIBER && currentTenantIsProvider() ) {
            throw new DestinationAccessException(
                "The current tenant is the provider tenant, which should not be the case with the option "
                    + ONLY_SUBSCRIBER
                    + ". Cannot retrieve destination.");
        }
        if( retrievalStrategy == ALWAYS_PROVIDER && !currentTenantIsProvider() ) {
            if( tokenExchangeStrategy == EXCHANGE_ONLY ) {
                throw new DestinationAccessException(
                    "The current tenant is not the provider tenant, which should not be the case with the options "
                        + ALWAYS_PROVIDER
                        + " and "
                        + EXCHANGE_ONLY
                        + ". Cannot retrieve destination.");
            } else if( tokenExchangeStrategy != DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY ) {
                log
                    .warn(
                        """
                            The current tenant is not the provider tenant. Only destinations which don't require a user token will be supported.\
                             Use retrieval strategy {} to avoid this warning.\
                            """,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY);
            }
        }
    }

    @SuppressWarnings( "deprecation" )
    Supplier<List<DestinationProperties>> prepareSupplierAllDestinations( @Nonnull final DestinationOptions options )
    {
        final DestinationServiceTokenExchangeStrategy tokenExchangeStrategy =
            DestinationServiceOptionsAugmenter
                .getTokenExchangeStrategy(options)
                .getOrElse(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY);
        if( tokenExchangeStrategy != DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY ) {
            log
                .warn(
                    "The provided token exchange strategy {} is not applicable while retrieving all destinations, hence switching to {} ",
                    tokenExchangeStrategy,
                    DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY);
        }
        final DestinationServiceRetrievalStrategy retrievalStrategy =
            DestinationServiceOptionsAugmenter.getRetrievalStrategy(options).getOrElse(CURRENT_TENANT);

        return prepareSupplierAllDestinations(retrievalStrategy);
    }

    Supplier<List<DestinationProperties>>
        prepareSupplierAllDestinations( @Nonnull final DestinationServiceRetrievalStrategy strategy )
            throws IllegalArgumentException
    {
        warnOrThrowOnUnsupportedCombinations(strategy, null);
        switch( strategy ) {
            case ALWAYS_PROVIDER: {
                return () -> allDestinationRetriever.apply(TECHNICAL_USER_PROVIDER);
            }
            case ONLY_SUBSCRIBER:
            case CURRENT_TENANT: {
                return () -> allDestinationRetriever.apply(TECHNICAL_USER_CURRENT_TENANT);
            }
            default: {
                throw new IllegalArgumentException(
                    "The provided destination retrieval strategy " + strategy + " is not valid.");
            }
        }
    }

    boolean doesDestinationConfigurationRequireUserTokenExchange( @Nonnull final DestinationServiceV1Response response )
    {
        final Map<String, String> destinationConfiguration = response.getDestinationConfiguration();
        final Try<AuthenticationType> authenticationType = determineAuthenticationType(destinationConfiguration);

        if( authenticationType.isSuccess() ) {
            final AuthenticationType authType = authenticationType.get();
            final String systemUser = destinationConfiguration.get(SYSTEM_USER.getKeyName());
            return DestinationUtility.requiresUserTokenExchange(authType, systemUser);
        } else {
            log
                .warn(
                    "No configuration value set for 'Authentication' in destination {}.",
                    destinationConfiguration.get(DestinationProperty.NAME.getKeyName()));
        }
        return false;
    }

    private boolean currentTenantIsProvider()
    {
        final String currentTenantId = TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId).getOrNull();
        final String providerTenantId = providerTenantIdSupplier.get();

        return Objects.equals(currentTenantId, providerTenantId);
    }

    private Try<AuthenticationType> determineAuthenticationType( final Map<String, String> destinationConfiguration )
    {
        final TreeMap<String, String> destinationData = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        destinationData.putAll(destinationConfiguration);

        final String authenticationValue = destinationData.get(DestinationProperty.AUTH_TYPE.getKeyName());
        return Try.of(() -> AuthenticationType.ofIdentifier(authenticationValue));
    }
}
