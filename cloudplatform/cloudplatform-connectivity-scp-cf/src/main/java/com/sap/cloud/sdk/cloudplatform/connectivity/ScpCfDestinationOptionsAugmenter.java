/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
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
public class ScpCfDestinationOptionsAugmenter implements DestinationOptionsAugmenter
{
    static final String DESTINATION_RETRIEVAL_STRATEGY_KEY = "scp.cf.destinationRetrievalStrategy";
    static final String DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY = "scp.cf.destinationTokenExchangeStrategy";

    private final Map<String, Object> parameters = new HashMap<>();

    /**
     * Creates instances of {@link ScpCfDestinationOptionsAugmenter} in a builder-like style.
     *
     * @return A new augmenter instance.
     */
    @Nonnull
    public static ScpCfDestinationOptionsAugmenter augmenter()
    {
        return new ScpCfDestinationOptionsAugmenter();
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
    public ScpCfDestinationOptionsAugmenter retrievalStrategy(
        @Nonnull final ScpCfDestinationRetrievalStrategy strategy )
    {
        parameters.put(DESTINATION_RETRIEVAL_STRATEGY_KEY, strategy);
        return this;
    }

    /**
     * Sets the {@link ScpCfDestinationTokenExchangeStrategy} to use when loading destinations. Not setting this value
     * results in the platform default behavior.
     *
     * @param strategy
     *            Strategy to use when loading destinations.
     * @return The same augmenter that called this method.
     */
    @Nonnull
    public ScpCfDestinationOptionsAugmenter tokenExchangeStrategy(
        @Nonnull final ScpCfDestinationTokenExchangeStrategy strategy )
    {
        parameters.put(DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY, strategy);
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
    public static Option<ScpCfDestinationRetrievalStrategy> getRetrievalStrategy(
        @Nonnull final DestinationOptions options )
    {
        final Option<Object> strategy = options.get(DESTINATION_RETRIEVAL_STRATEGY_KEY);

        if( strategy.isDefined() && strategy.get() instanceof String ) {
            return Option
                .of(ScpCfDestinationRetrievalStrategy.ofIdentifier((String) strategy.get()))
                .onEmpty(() -> log.warn("Unsupported destination retrieval strategy: {}", strategy.get()));
        }

        return strategy.map(ScpCfDestinationRetrievalStrategy.class::cast);
    }

    /**
     * Retrieves the configured {@link ScpCfDestinationTokenExchangeStrategy} to use when loading destinations.
     *
     * @param options
     *            The destination options instance that stores the key/value pair.
     * @return An {@link Option} wrapping the token exchange strategy if the parameter is present, otherwise a
     *         {@link io.vavr.control.Option.None}.
     */
    @Nonnull
    public static Option<ScpCfDestinationTokenExchangeStrategy> getTokenExchangeStrategy(
        @Nonnull final DestinationOptions options )
    {
        final Option<Object> strategy = options.get(DESTINATION_TOKEN_EXCHANGE_STRATEGY_KEY);

        if( strategy.isDefined() && strategy.get() instanceof String ) {
            return Option
                .of(ScpCfDestinationTokenExchangeStrategy.ofIdentifier((String) strategy.get()))
                .onEmpty(() -> log.warn("Unsupported token exchange strategy: {}", strategy.get()));
        }

        return strategy.map(ScpCfDestinationTokenExchangeStrategy.class::cast);
    }
}
