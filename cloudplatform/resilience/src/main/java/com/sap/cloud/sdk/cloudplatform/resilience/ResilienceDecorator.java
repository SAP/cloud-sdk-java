/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.exception.ObjectLookupFailedException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Accessor class for decorating functions with resilient properties.
 */
@Slf4j
public final class ResilienceDecorator
{
    /**
     * The current instance of {@link ResilienceDecorationStrategy} to be used to guarantee resilient function
     * properties.
     */
    @Nonnull
    private static Try<ResilienceDecorationStrategy> decorationStrategy =
        Try.of(ResilienceDecorator::getDefaultDecorationStrategy);

    /**
     * Returns the {@link ResilienceDecorationStrategy} that will be used to decorate resilient code,
     *
     * @return The {@link ResilienceDecorationStrategy} that will be used to decorate resilient code.
     */
    @Nonnull
    public static ResilienceDecorationStrategy getDecorationStrategy()
    {
        return decorationStrategy
            .getOrElseThrow(
                e -> new ResilienceRuntimeException(
                    String.format("Failed to determine %s.", ResilienceDecorationStrategy.class.getName()),
                    e));
    }

    /**
     * Sets the {@link ResilienceDecorationStrategy} that will be used to decorate resilient code.
     *
     * @param decorationStrategy
     *            The {@link ResilienceDecorationStrategy} that will be used to decorate resilient code.
     */
    public static void setDecorationStrategy( @Nonnull final ResilienceDecorationStrategy decorationStrategy )
    {
        ResilienceDecorator.decorationStrategy = Try.success(decorationStrategy);
    }

    /**
     * Resets the decoration strategy to its default.
     */
    public static void resetDecorationStrategy()
    {
        decorationStrategy = Try.of(ResilienceDecorator::getDefaultDecorationStrategy);
    }

    /**
     * Returns the default decoration strategy.
     *
     * @return The default decoration strategy.
     */
    private static ResilienceDecorationStrategy getDefaultDecorationStrategy()
    {
        final Collection<ResilienceDecorationStrategy> facades =
            FacadeLocator.getFacades(ResilienceDecorationStrategy.class);
        if( facades.size() > 1 ) {
            final String classes = facades.stream().map(f -> f.getClass().getName()).collect(Collectors.joining(", "));
            final String exceptionMessage =
                String
                    .format(
                        "Too many implementations of %s found. Make sure to only have a single implementation of the interface on your classpath: %s",
                        ResilienceDecorationStrategy.class.getName(),
                        classes);
            final String logMessage =
                String
                    .format(
                        "%s. Using any resilience pattern will lead to an exception at runtime UNLESS the %s is explicitly overwritten using 'ResilienceDecorator.setDecorationStrategy(ResilienceDecorationStrategy)'.",
                        exceptionMessage,
                        ResilienceDecorationStrategy.class.getName());
            log.warn(logMessage);
            throw new ObjectLookupFailedException(exceptionMessage);
        }

        if( facades.isEmpty() ) {
            return new NoResilienceDecorationStrategy();
        }

        return facades.iterator().next();
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
        getDecorationStrategy().clearCache(configuration);
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
        getDecorationStrategy().clearCache(configuration, filter);
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
        getDecorationStrategy().clearAllCacheEntries(configuration);
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
        return getDecorationStrategy().decorateSupplier(supplier, configuration);
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
        return getDecorationStrategy().executeSupplier(supplier, configuration);
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
        return getDecorationStrategy().decorateSupplier(supplier, configuration, fallbackFunction);
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
        return getDecorationStrategy().executeSupplier(supplier, configuration, fallbackFunction);
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
        return getDecorationStrategy().decorateCallable(callable, configuration);
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
        return getDecorationStrategy().executeCallable(callable, configuration);
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
        return getDecorationStrategy().decorateCallable(callable, configuration, fallbackFunction);
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
        return getDecorationStrategy().executeCallable(callable, configuration, fallbackFunction);
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
        return getDecorationStrategy().queueCallable(callable, configuration, fallbackFunction);
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
        return getDecorationStrategy().queueCallable(callable, configuration, null);
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
        return getDecorationStrategy().queueSupplier(supplier, configuration, fallbackFunction);
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
        return getDecorationStrategy().queueSupplier(supplier, configuration, null);
    }
}
