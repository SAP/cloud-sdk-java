package com.sap.cloud.sdk.cloudplatform.resilience;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

/**
 * Function decorator which does not change the call.
 */
@Slf4j
public class NoResilienceDecorationStrategy implements ResilienceDecorationStrategy
{
    @Nonnull
    @Override
    public <T> Supplier<T> decorateSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        log.warn("The resilience function decorator was called without a proper implementation.");
        return supplier;
    }

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        log.warn("The resilience function decorator was called without a proper implementation.");
        return callable;
    }
}
