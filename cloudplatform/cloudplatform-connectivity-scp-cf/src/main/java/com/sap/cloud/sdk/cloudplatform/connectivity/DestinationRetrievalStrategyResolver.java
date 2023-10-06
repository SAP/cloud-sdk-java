/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.SYSTEM_USER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationRetrievalStrategy.ALWAYS_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationRetrievalStrategy.CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationRetrievalStrategy.CURRENT_TENANT_THEN_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationRetrievalStrategy.ONLY_SUBSCRIBER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.LOOKUP_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings( { "PMD.TooManyStaticImports", "deprecation" } ) // without these static imports the code becomes unreadable
class DestinationRetrievalStrategyResolver
{
    private static final Strategy tokenExchangeOnlyStrategy = new Strategy(NAMED_USER_CURRENT_TENANT, false);
    private final Supplier<String> providerTenantIdSupplier;
    private final Function<Strategy, ScpCfDestinationServiceV1Response> destinationRetriever;
    private final Function<OnBehalfOf, List<Destination>> allDstinationRetriever;

    static final String JWT_ATTR_EXT = "ext_attr";
    static final String JWT_ATTR_ENHANCER = "enhancer";
    static final String JWT_ATTR_XSUAA = "XSUAA";

    @Value
    static class Strategy
    {
        OnBehalfOf behalf;
        boolean forwardToken;
    }

    static DestinationRetrievalStrategyResolver forSingleDestination(
        final Supplier<String> providerTenantIdSupplier,
        final Function<Strategy, ScpCfDestinationServiceV1Response> destinationRetriever )
    {
        return new DestinationRetrievalStrategyResolver(
            providerTenantIdSupplier,
            destinationRetriever,
            ( any ) -> Collections.emptyList());
    }

    static DestinationRetrievalStrategyResolver forAllDestinations(
        final Supplier<String> providerTenantIdSupplier,
        final Function<OnBehalfOf, List<Destination>> allDstinationRetriever )
    {
        return new DestinationRetrievalStrategyResolver(
            providerTenantIdSupplier,
            ( any ) -> null,
            allDstinationRetriever);
    }

    Strategy resolveSingleRequestStrategy(
        @Nonnull final ScpCfDestinationRetrievalStrategy retrievalStrategy,
        @Nonnull final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy )
    {
        final OnBehalfOf behalfTechnicalUser;

        switch( retrievalStrategy ) {
            case ALWAYS_PROVIDER:
                behalfTechnicalUser = TECHNICAL_USER_PROVIDER;
                break;
            case CURRENT_TENANT:
            case ONLY_SUBSCRIBER:
                behalfTechnicalUser = TECHNICAL_USER_CURRENT_TENANT;
                break;
            // sanity check that this method is never called for CURRENT_TENANT_THEN_PROVIDER
            default:
                throw new IllegalStateException(
                    "Unexpected retrieval strategy "
                        + retrievalStrategy
                        + " when building a request towards the destination service.");
        }

        switch( tokenExchangeStrategy ) {
            case FORWARD_USER_TOKEN:
                return new Strategy(behalfTechnicalUser, true);
            case LOOKUP_ONLY:
                return new Strategy(behalfTechnicalUser, false);
            case EXCHANGE_ONLY:
                return new Strategy(NAMED_USER_CURRENT_TENANT, false);
            default:
                // sanity check that this method is never called for LOOKUP_THEN_EXCHANGE
                throw new IllegalStateException(
                    "Unexpected token exchange strategy "
                        + tokenExchangeStrategy
                        + " when building a request towards the destination service.");
        }
    }

    DestinationRetrieval prepareSupplier( @Nonnull final DestinationOptions options )
    {
        final ScpCfDestinationRetrievalStrategy retrievalStrategy =
            ScpCfDestinationOptionsAugmenter.getRetrievalStrategy(options).getOrElse(CURRENT_TENANT);

        final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy =
            ScpCfDestinationOptionsAugmenter
                .getTokenExchangeStrategy(options)
                .getOrElse(this::getDefaultTokenExchangeStrategy);

        log
            .debug(
                "Loading destination from reuse-destination-service with retrieval strategy {} and token exchange strategy {}.",
                retrievalStrategy,
                tokenExchangeStrategy);
        return prepareSupplier(retrievalStrategy, tokenExchangeStrategy);
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
    private ScpCfDestinationTokenExchangeStrategy getDefaultTokenExchangeStrategy()
    {
        // extract extended attributes from current token, or null<br>
        final Map<String, Object> attributes =
            AuthTokenAccessor.tryGetCurrentToken().map(t -> t.getJwt().getClaim(JWT_ATTR_EXT).asMap()).getOrNull();

        // consider scenario where "forward user token" will not work out-of-the-box, e.g. for IAS identity service
        if( attributes == null || !JWT_ATTR_XSUAA.equalsIgnoreCase(attributes.get(JWT_ATTR_ENHANCER) + "") ) {
            log.debug("Falling back to {}. Current user token may not originate from XSUAA.", LOOKUP_THEN_EXCHANGE);
            return LOOKUP_THEN_EXCHANGE;
        }

        return FORWARD_USER_TOKEN;
    }

    DestinationRetrieval prepareSupplier(
        @Nonnull final ScpCfDestinationRetrievalStrategy originalRetrievalStrategy,
        @Nonnull final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy )
        throws DestinationAccessException
    {
        log
            .debug(
                "Preparing request(s) towards the destination service based on the strategies {} and {}",
                originalRetrievalStrategy,
                tokenExchangeStrategy);
        warnOrThrowOnDeprecatedOrUnsupportedCombinations(originalRetrievalStrategy, tokenExchangeStrategy);

        final ScpCfDestinationRetrievalStrategy retrievalStrategy;
        if( originalRetrievalStrategy == CURRENT_TENANT_THEN_PROVIDER && currentTenantIsProvider() ) {
            retrievalStrategy = CURRENT_TENANT;
        } else {
            retrievalStrategy = originalRetrievalStrategy;
        }

        final Strategy strategy;

        // handle the simple cases
        if( tokenExchangeStrategy != LOOKUP_THEN_EXCHANGE && retrievalStrategy != CURRENT_TENANT_THEN_PROVIDER ) {
            strategy = resolveSingleRequestStrategy(retrievalStrategy, tokenExchangeStrategy);
            return new DestinationRetrieval(() -> destinationRetriever.apply(strategy), strategy.getBehalf());
        }

        // deal with LOOKUP_THEN_EXCHANGE but exclude CURRENT_TENANT_THEN_PROVIDER for now
        if( retrievalStrategy != CURRENT_TENANT_THEN_PROVIDER ) {
            strategy = resolveSingleRequestStrategy(retrievalStrategy, LOOKUP_ONLY);
            return new DestinationRetrieval(() -> {
                final ScpCfDestinationServiceV1Response result = destinationRetriever.apply(strategy);
                if( !doesDestinationConfigurationRequireUserTokenExchange(result) ) {
                    return result;
                }
                if( retrievalStrategy == ALWAYS_PROVIDER && !currentTenantIsProvider() ) {
                    throw new DestinationAccessException(
                        "Can't perform token exchange, the current token is not issued for the provider tenant.");
                }
                return destinationRetriever.apply(tokenExchangeOnlyStrategy);
            }, strategy.getBehalf());
        }

        // handle CURRENT_TENANT_THEN_PROVIDER where current tenant != provider
        return prepareSupplierForSubscriberThenProviderCase(tokenExchangeStrategy);
    }

    DestinationRetrieval prepareSupplierForSubscriberThenProviderCase(
        @Nonnull final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy )
        throws DestinationAccessException
    {
        // sanity check that this is never called with the provider tenant
        if( currentTenantIsProvider() ) {
            throw new IllegalStateException(
                "Unexpected state: Preparing request to destination service for subscriber tenant, but current tenant is provider.");
        }
        final Strategy strategy;
        final Strategy providerLookupOnlyStrategy = resolveSingleRequestStrategy(ALWAYS_PROVIDER, LOOKUP_ONLY);

        if( tokenExchangeStrategy == LOOKUP_ONLY || tokenExchangeStrategy == FORWARD_USER_TOKEN ) {
            strategy = resolveSingleRequestStrategy(ONLY_SUBSCRIBER, tokenExchangeStrategy);
            return new DestinationRetrieval(() -> {
                try {
                    return destinationRetriever.apply(strategy);
                }
                catch( final DestinationNotFoundException e ) {
                    log
                        .debug(
                            "Did not find the destination in the subscriber account, falling back to the provider account.",
                            e);
                    return destinationRetriever.apply(providerLookupOnlyStrategy);
                }
            }, strategy.getBehalf());
        }

        if( tokenExchangeStrategy == EXCHANGE_ONLY ) {
            strategy = resolveSingleRequestStrategy(ONLY_SUBSCRIBER, EXCHANGE_ONLY);
            return new DestinationRetrieval(() -> destinationRetriever.apply(strategy), strategy.getBehalf());
        }

        // handle LOOKUP_THEN_EXCHANGE
        strategy = resolveSingleRequestStrategy(ONLY_SUBSCRIBER, LOOKUP_ONLY);

        return new DestinationRetrieval(() -> {
            try {
                final ScpCfDestinationServiceV1Response result = destinationRetriever.apply(strategy);
                if( !doesDestinationConfigurationRequireUserTokenExchange(result) ) {
                    return result;
                }
                return destinationRetriever.apply(tokenExchangeOnlyStrategy);
            }
            catch( final DestinationNotFoundException e ) {
                log
                    .debug(
                        "Did not find the destination in the subscriber account, falling back to the provider account.",
                        e);
                return destinationRetriever.apply(providerLookupOnlyStrategy);
            }
        }, strategy.getBehalf());
    }

    private void warnOrThrowOnDeprecatedOrUnsupportedCombinations(
        @Nonnull final ScpCfDestinationRetrievalStrategy retrievalStrategy,
        @Nullable final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy )
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
            } else if( tokenExchangeStrategy != LOOKUP_ONLY ) {
                log
                    .warn(
                        "The current tenant is not the provider tenant. Only destinations which don't require a user token will be supported."
                            + " Use retrieval strategy {} to avoid this warning.",
                        LOOKUP_ONLY);
            }
        }
        if( retrievalStrategy != CURRENT_TENANT_THEN_PROVIDER ) {
            return;
        }
        log
            .warn(
                "The retrieval strategy {} is deprecated and should no longer be used."
                    + " Please query subscriber and provider accounts individually using {} and {}",
                CURRENT_TENANT_THEN_PROVIDER,
                ONLY_SUBSCRIBER,
                ALWAYS_PROVIDER);
        if( currentTenantIsProvider() ) {
            log
                .warn(
                    "The retrieval strategy {} is used unnecessarily, the current tenant is the provider tenant."
                        + " Only a single request will be made.",
                    CURRENT_TENANT_THEN_PROVIDER);
            return;
        }
        if( tokenExchangeStrategy == null || tokenExchangeStrategy == LOOKUP_ONLY ) {
            return;
        }
        log.warn("Option {} is not supported in conjunction with {}.", retrievalStrategy, tokenExchangeStrategy);
        switch( tokenExchangeStrategy ) {
            case EXCHANGE_ONLY:
                log.warn("Falling back to {} with {}.", CURRENT_TENANT, EXCHANGE_ONLY);
                break;
            case FORWARD_USER_TOKEN: //fallthrough
            case LOOKUP_THEN_EXCHANGE:
                log
                    .warn(
                        "Attempting to apply {} for {}, hoping that destinations requiring a user token will only be present in the subscriber account."
                            + " Potential requests to the provider account will not contain any user information.",
                        tokenExchangeStrategy,
                        CURRENT_TENANT_THEN_PROVIDER);
                break;
            default:
                throw new IllegalStateException("Unexpected token strategy " + tokenExchangeStrategy);
        }
    }

    Supplier<List<Destination>> prepareSupplierAllDestinations( @Nonnull final DestinationOptions options )
    {
        final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy =
            ScpCfDestinationOptionsAugmenter.getTokenExchangeStrategy(options).getOrElse(LOOKUP_ONLY);
        if( tokenExchangeStrategy != LOOKUP_ONLY ) {
            log
                .warn(
                    "The provided token exchange strategy {} is not applicable while retrieving all destinations, hence switching to {} ",
                    tokenExchangeStrategy,
                    LOOKUP_ONLY);
        }
        final ScpCfDestinationRetrievalStrategy retrievalStrategy =
            ScpCfDestinationOptionsAugmenter.getRetrievalStrategy(options).getOrElse(CURRENT_TENANT);

        return prepareSupplierAllDestinations(retrievalStrategy);
    }

    Supplier<List<Destination>>
        prepareSupplierAllDestinations( @Nonnull final ScpCfDestinationRetrievalStrategy strategy )
    {
        warnOrThrowOnDeprecatedOrUnsupportedCombinations(strategy, null);
        switch( strategy ) {
            case ALWAYS_PROVIDER: {
                return () -> allDstinationRetriever.apply(TECHNICAL_USER_PROVIDER);
            }
            case CURRENT_TENANT_THEN_PROVIDER: {
                return () -> {
                    try {
                        final List<Destination> destinations =
                            allDstinationRetriever.apply(TECHNICAL_USER_CURRENT_TENANT);
                        if( currentTenantIsProvider() || !destinations.isEmpty() ) {
                            return destinations;
                        }
                    }
                    catch( final Exception e ) {
                        log
                            .warn(
                                "Falling back to the provider tenant after failing to retrieve destinations for the subscriber tenant.");
                        log.debug("Lookup of all destinations for the subscriber tenant failed.", e);
                    }
                    return allDstinationRetriever.apply(TECHNICAL_USER_PROVIDER);
                };
            }
            case ONLY_SUBSCRIBER:
            case CURRENT_TENANT:
            default: {
                return () -> allDstinationRetriever.apply(TECHNICAL_USER_CURRENT_TENANT);
            }
        }
    }

    boolean doesDestinationConfigurationRequireUserTokenExchange(
        @Nonnull final ScpCfDestinationServiceV1Response response )
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
