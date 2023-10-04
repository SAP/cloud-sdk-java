/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import lombok.Getter;
import lombok.Setter;

/**
 * Accessor class for decorating functions with resilient properties.
 */
public final class ResilienceDecorator
{
    /**
     * The current instance of {@link ResilienceDecorationStrategy} to be used to guarantee resilient function
     * properties.
     */
    @Nonnull
    @Getter
    @Setter
    private static ResilienceDecorationStrategy decorationStrategy;

    static {
        decorationStrategy = getDefaultDecorationStrategy();
    }

    /**
     * Resets the decoration strategy to its default.
     */
    public static void resetDecorationStrategy()
    {
        decorationStrategy = getDefaultDecorationStrategy();
    }

    /**
     * Returns the default decoration strategy.
     *
     * @return The default decoration strategy.
     */
    private static ResilienceDecorationStrategy getDefaultDecorationStrategy()
    {
        return FacadeLocator
            .getFacade(ResilienceDecorationStrategy.class)
            .getOrElse(NoResilienceDecorationStrategy::new);
    }

    /**
     * Clears the cache associated with the given {@link ResilienceConfiguration} if any exists. This method respects
     * tenant/principal isolation according to the {@link ResilienceIsolationMode} as well as parameter isolation.<br>
     * To clear all cache entries for all tenants, principals, and parameters, use
     * {@link #clearAllCacheEntries(ResilienceConfiguration)} instead.
     *
     * @param configuration
     *            The {@code ResilienceConfiguration} the cache is attached to.
     */
    public static void clearCache( @Nonnull final ResilienceConfiguration configuration )
    {
        decorationStrategy.clearCache(configuration);
    }

    /**
     * Clears the cache associated with the given {@link ResilienceConfiguration} if any exists. Cache entries to clear
     * are determined by applying the given {@code filter}. Each cache entry for which the cache filter matches, gets
     * cleared from the cache.
     *
     * @param configuration
     *            The {@code ResilienceConfiguration} the cache is attached to.
     * @param filter
     *            A {@link CacheFilter} to apply in order to select entries that should be cleared from the cache.
     */
    public static
        void
        clearCache( @Nonnull final ResilienceConfiguration configuration, @Nonnull final CacheFilter filter )
    {
        decorationStrategy.clearCache(configuration, filter);
    }

    /**
     * Clears the entire cache associated with the given {@link ResilienceConfiguration} if any exists, independent of
     * the specified {@link ResilienceIsolationMode} and defined parameters. Be aware that this operation affects all
     * tenants and principals.<br>
     * Use {@link #clearCache(ResilienceConfiguration)} to respect the tenant/principal and parameter isolation.
     *
     * @param configuration
     *            The {@code ResilienceConfiguration} the cache is attached to.
     */
    public static void clearAllCacheEntries( @Nonnull final ResilienceConfiguration configuration )
    {
        decorationStrategy.clearAllCacheEntries(configuration);
    }

    /**
     * Decorate an instance of a supplier function.
     *
     * @param supplier
     *            The supplier.
     * @param configuration
     *            The configuration of the resilient call.
     * @param <T>
     *            The return type of the call.
     *
     * @return A decorated supplier.
     */
    @Nonnull
    public static <T> Supplier<T> decorateSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration )
    {
        return decorationStrategy.decorateSupplier(supplier, configuration);
    }

    /**
     * Decorate and execute an instance of a supplier function.
     *
     * @param supplier
     *            The supplier.
     * @param configuration
     *            The configuration of the resilient call.
     * @param <T>
     *            The return type of the call.
     *
     * @return The value of the supplier.
     */
    @Nullable
    public static <T> T executeSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration )
    {
        return decorationStrategy.executeSupplier(supplier, configuration);
    }

    /**
     * Decorate an instance of a supplier function.
     *
     * @param supplier
     *            The supplier.
     * @param configuration
     *            The configuration of the resilient call.
     * @param fallbackFunction
     *            In case of failure, execute this function.
     * @param <T>
     *            The return type of the call.
     *
     * @return A decorated supplier.
     */
    @Nonnull
    public static <T> Supplier<T> decorateSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        return decorationStrategy.decorateSupplier(supplier, configuration, fallbackFunction);
    }

    /**
     * Decorate and execute an instance of a supplier function.
     *
     * @param supplier
     *            The supplier.
     * @param configuration
     *            The configuration of the resilient call.
     * @param fallbackFunction
     *            In case of failure, execute this function.
     * @param <T>
     *            The return type of the call.
     *
     * @return The value of the supplier.
     */
    @Nullable
    public static <T> T executeSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        return decorationStrategy.executeSupplier(supplier, configuration, fallbackFunction);
    }

    /**
     * Decorate an instance of a callable function.
     *
     * @param callable
     *            The callable.
     * @param configuration
     *            The configuration of the resilient call.
     * @param <T>
     *            The return type of the call.
     *
     * @return A decorated callable.
     */
    @Nonnull
    public static <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        return decorationStrategy.decorateCallable(callable, configuration);
    }

    /**
     * Decorate and execute an instance of a callable function.
     *
     * @param callable
     *            The callable.
     * @param configuration
     *            The configuration of the resilient call.
     * @param <T>
     *            The return type of the call.
     *
     * @throws Exception
     *             Exception that can be thrown by the callable.
     *
     * @return The value returned by the callable.
     */
    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nullable
    public static <T> T executeCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
        throws Exception
    {
        return decorationStrategy.executeCallable(callable, configuration);
    }

    /**
     * Decorate an instance of a callable function.
     *
     * @param callable
     *            The callable.
     * @param configuration
     *            The configuration of the resilient call.
     * @param fallbackFunction
     *            In case of failure, execute this function.
     * @param <T>
     *            The return type of the call.
     *
     * @return A decorated callable.
     */
    @Nonnull
    public static <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        return decorationStrategy.decorateCallable(callable, configuration, fallbackFunction);
    }

    /**
     * Decorate and execute an instance of a callable function.
     *
     * @param callable
     *            The callable.
     * @param configuration
     *            The configuration of the resilient call.
     * @param fallbackFunction
     *            (Optional) In case of failure, execute this function.
     * @param <T>
     *            The return type of the call.
     *
     * @return The value returned by the callable.
     */
    @Nullable
    public static <T> T executeCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        return decorationStrategy.executeCallable(callable, configuration, fallbackFunction);
    }

    /**
     * Decorate an instance of a callable function. Automatically schedule the asynchronous execution.
     *
     * @param callable
     *            The callable.
     * @param configuration
     *            The configuration of the resilient call.
     * @param fallbackFunction
     *            (Optional) In case of failure, execute this function.
     * @param <T>
     *            The return type of the call.
     *
     * @return An instance of Future being executed asynchronously.
     */
    @Nonnull
    public static <T> CompletableFuture<T> queueCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        return decorationStrategy.queueCallable(callable, configuration, fallbackFunction);
    }

    /**
     * Decorate an instance of a callable function. Automatically schedule the asynchronous execution.
     *
     * @param callable
     *            The callable.
     * @param configuration
     *            The configuration of the resilient call.
     * @param <T>
     *            The return type of the call.
     *
     * @return An instance of Future being executed asynchronously.
     */
    @Nonnull
    public static <T> CompletableFuture<T> queueCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        return decorationStrategy.queueCallable(callable, configuration, null);
    }

    /**
     * Decorate an instance of a supplier function. Automatically schedule the asynchronous execution.
     *
     * @param supplier
     *            The supplier.
     * @param configuration
     *            The configuration of the resilient call.
     * @param fallbackFunction
     *            (Optional) In case of failure, execute this function.
     * @param <T>
     *            The return type of the call.
     *
     * @return An instance of Future being executed asynchronously.
     */
    @Nonnull
    public static <T> CompletableFuture<T> queueSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        return decorationStrategy.queueSupplier(supplier, configuration, fallbackFunction);
    }

    /**
     * Decorate an instance of a supplier function. Automatically schedule the asynchronous execution.
     *
     * @param supplier
     *            The supplier.
     * @param configuration
     *            The configuration of the resilient call.
     * @param <T>
     *            The return type of the call.
     *
     * @return An instance of Future being executed asynchronously.
     */
    @Nonnull
    public static <T> CompletableFuture<T> queueSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration )
    {
        return decorationStrategy.queueSupplier(supplier, configuration, null);
    }
}
