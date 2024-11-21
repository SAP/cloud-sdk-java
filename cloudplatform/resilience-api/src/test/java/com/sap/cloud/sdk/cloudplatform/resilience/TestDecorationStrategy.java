/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.resilience;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Try;

public class TestDecorationStrategy implements ResilienceDecorationStrategy
{
    @Nonnull
    @Override
    public <T> Supplier<T> decorateSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        if( fallbackFunction != null ) {
            return () -> {
                final Try<T> callWithFallback = Try.ofSupplier(supplier).recover(fallbackFunction);
                return callWithFallback.get();
            };
        }
        return supplier;
    }

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        if( fallbackFunction != null ) {
            return () -> {
                final Try<T> callWithFallback = Try.ofCallable(callable).recover(fallbackFunction);
                return callWithFallback.get();
            };
        }
        return callable;
    }

    @Nonnull
    public String getThreadName()
    {
        return Thread.currentThread().getName();
    }
}
