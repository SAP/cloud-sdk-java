/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

import io.vavr.control.Try;

/**
 * Generic interface to decorate functions with non-functional requirements.
 */
public interface ResilienceDecorationStrategy
{
    /**
     * Clears the cache associated with the given {@link ResilienceConfiguration} if any exists. This method respects
     * tenant/principal isolation according to the {@link ResilienceIsolationMode} as well as parameter isolation.<br>
     * To clear all cache entries for all tenants, principals, and parameters,
     * {@link #clearAllCacheEntries(ResilienceConfiguration)} can be used instead.
     *
     * @param configuration
     *            The {@code ResilienceConfiguration} the cache is attached to.
     */
    default void clearCache( @Nonnull final ResilienceConfiguration configuration )
    {
        throw new UnsupportedOperationException("No implementation provided.");
    }

    /**
     * Clears the cache associated with the given {@link ResilienceConfiguration} by respecting the provided
     * {@link CacheFilter}.
     *
     * @param configuration
     *            The {@code ResilienceConfiguration} the cache is attached to.
     * @param filter
     *            A {@link CacheFilter} to apply in order to select entries that should be cleared from the cache.
     */
    default void clearCache( @Nonnull final ResilienceConfiguration configuration, @Nonnull final CacheFilter filter )
    {
        throw new UnsupportedOperationException("No implementation provided.");
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
    default void clearAllCacheEntries( @Nonnull final ResilienceConfiguration configuration )
    {
        throw new UnsupportedOperationException("No implementation provided.");
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
    default <T> Supplier<T> decorateSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration )
    {
        return decorateSupplier(supplier, configuration, null);
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
    default <T> T executeSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration )
    {
        return decorateSupplier(supplier, configuration).get();
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
    <T> Supplier<T> decorateSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction );

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
    default <T> T executeSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        return decorateSupplier(supplier, configuration, fallbackFunction).get();
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
    default <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        return decorateCallable(callable, configuration, null);
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
    default <T> T executeCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
        throws Exception
    {
        return decorateCallable(callable, configuration).call();
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
    <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction );

    /**
     * Decorate and execute an instance of a callable function. This method assumes that {@code fallbackFunction}
     * handles any exceptions thrown by {@code callable}, and {@code fallbackFunction} does not throw any exceptions
     * itself. If {@code fallbackFunction} throws any exception, then it will be wrapped in a
     * {@link ResilienceRuntimeException}.
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
    default <T> T executeCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        try {
            return decorateCallable(callable, configuration, fallbackFunction).call();
        }
        // CHECKSTYLE:OFF
        catch( final Throwable e ) {
            throw new ResilienceRuntimeException("The provided fallback method threw an exception. ", e);
        }
        // CHECKSTYLE:ON
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
     * @return An instance of Future being executed asynchronously with the static {@link ThreadContextExecutors}
     *         executor.
     */
    @Nonnull
    default <T> CompletableFuture<T> queueCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        final Callable<T> call = decorateCallable(callable, configuration, fallbackFunction);
        final Supplier<T> supp = () -> Try.ofCallable(call).get();
        return CompletableFuture.supplyAsync(supp, ThreadContextExecutors.getExecutor());
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
     * @return An instance of Future being executed asynchronously with the static {@link ThreadContextExecutors}
     *         executor.
     */
    @Nonnull
    default <T> CompletableFuture<T> queueSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        final Supplier<T> func = decorateSupplier(supplier, configuration, fallbackFunction);
        return CompletableFuture.supplyAsync(func, ThreadContextExecutors.getExecutor());
    }
}
