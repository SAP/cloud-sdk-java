/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import io.vavr.control.Option;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles the getting and setting of {@link DestinationOptions} parameters specific to SCP Cloud Foundry platform.
 */
@NoArgsConstructor
@Slf4j
public class DestinationServiceOptionsAugmenter implements DestinationOptionsAugmenter
{
    static final String DESTINATION_RETRIEVAL_STRATEGY_KEY = "scp.cf.destinationRetrievalStrategy";
    static final String DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY = "scp.cf.destinationTokenExchangeStrategy";
    static final String X_REFRESH_TOKEN_KEY = "x-refresh-token";
    static final String X_FRAGMENT_KEY = "X-fragment-name";

    private final Map<String, Object> parameters = new HashMap<>();

    /**
     * Creates instances of {@link DestinationServiceOptionsAugmenter} in a builder-like style.
     *
     * @return A new augmenter instance.
     */
    @Nonnull
    public static DestinationServiceOptionsAugmenter augmenter()
    {
        return new DestinationServiceOptionsAugmenter();
    }

    /**
     * Sets the strategy to use when loading destinations in a multi-tenant subscription environment. Not setting this
     * value or setting to null results in platform default behaviour.
     *
     * @param strategy
     *            Strategy to use when loading destinations.
     * @return The same augmenter that called this method.
     */
    @Nonnull
    public DestinationServiceOptionsAugmenter retrievalStrategy(
        @Nonnull final DestinationServiceRetrievalStrategy strategy )
    {
        parameters.put(DESTINATION_RETRIEVAL_STRATEGY_KEY, strategy);
        return this;
    }

    /**
     * Sets the {@link DestinationServiceTokenExchangeStrategy} to use when loading destinations. Not setting this value
     * results in the platform default behavior.
     *
     * @param strategy
     *            Strategy to use when loading destinations.
     * @return The same augmenter that called this method.
     */
    @Nonnull
    public DestinationServiceOptionsAugmenter tokenExchangeStrategy(
        @Nonnull final DestinationServiceTokenExchangeStrategy strategy )
    {
        parameters.put(DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY, strategy);
        return this;
    }

    /**
     * Refresh token to be sent for destination of type {@link AuthenticationType#OAUTH2_REFRESH_TOKEN}.
     *
     * @param refreshToken
     *            Refresh token as encoded string.
     * @return The same augmenter that called this method.
     * @since 5.9.0
     */
    @Nonnull
    public DestinationServiceOptionsAugmenter refreshToken( @Nonnull final String refreshToken )
    {
        parameters.put(X_REFRESH_TOKEN_KEY, refreshToken);
        return this;
    }

    /**
     * Fragment that should enhance the destination to be fetched.
     *
     * @param fragmentName
     *            The fragment name.
     * @return The same augmenter that called this method.
     * @since 5.11.0
     */
    @Nonnull
    public DestinationServiceOptionsAugmenter fragmentName( @Nonnull final String fragmentName )
    {
        parameters.put(X_FRAGMENT_KEY, fragmentName);
        if( DestinationService.Cache.isEnabled() && DestinationService.Cache.isChangeDetectionEnabled() ) {
            log
                .warn(
                    """
                        A fragment was requested while change detection caching is enabled.\
                        This is not recommended, as fragment-based destinations will effectively not be cached with this strategy.\
                        Consider disabling change detection, if you frequently use destination fragments.
                        """);
        }
        return this;
    }

    @Override
    public void augmentBuilder( @Nonnull final DestinationOptions.Builder builder )
    {
        for( final Map.Entry<String, Object> param : parameters.entrySet() ) {
            builder.parameter(param.getKey(), param.getValue());
        }
    }

    /**
     * Retrieves the configured strategy to use when loading destinations in a multi-tenant subscription environment.
     *
     * @param options
     *            The destination options instance that stores the key/value pair.
     * @return An {@link Option} wrapping the retrieval strategy if the parameter is present, otherwise a
     *         {@link io.vavr.control.Option.None}.
     */
    @Nonnull
    public static Option<DestinationServiceRetrievalStrategy> getRetrievalStrategy(
        @Nonnull final DestinationOptions options )
    {
        final Option<Object> strategy = options.get(DESTINATION_RETRIEVAL_STRATEGY_KEY);

        if( strategy.isDefined() && strategy.get() instanceof String ) {
            return Option
                .of(DestinationServiceRetrievalStrategy.ofIdentifier((String) strategy.get()))
                .onEmpty(() -> log.warn("Unsupported destination retrieval strategy: {}", strategy.get()));
        }

        return strategy.map(DestinationServiceRetrievalStrategy.class::cast);
    }

    /**
     * Retrieves the configured {@link DestinationServiceTokenExchangeStrategy} to use when loading destinations.
     *
     * @param options
     *            The destination options instance that stores the key/value pair.
     * @return An {@link Option} wrapping the token exchange strategy if the parameter is present, otherwise a
     *         {@link io.vavr.control.Option.None}.
     */
    @Nonnull
    public static Option<DestinationServiceTokenExchangeStrategy> getTokenExchangeStrategy(
        @Nonnull final DestinationOptions options )
    {
        final Option<Object> strategy = options.get(DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY);

        if( strategy.isDefined() && strategy.get() instanceof String ) {
            return Option
                .of(DestinationServiceTokenExchangeStrategy.ofIdentifier((String) strategy.get()))
                .onEmpty(() -> log.warn("Unsupported token exchange strategy: {}", strategy.get()));
        }

        return strategy.map(DestinationServiceTokenExchangeStrategy.class::cast);
    }

    @Nonnull
    static Option<String> getRefreshToken( @Nonnull final DestinationOptions options )
    {
        return options.get(X_REFRESH_TOKEN_KEY).filter(String.class::isInstance).map(String.class::cast);
    }

    @Nonnull
    static Option<String> getFragmentName( @Nonnull final DestinationOptions options )
    {
        return options.get(X_FRAGMENT_KEY).filter(String.class::isInstance).map(String.class::cast);
    }
}
