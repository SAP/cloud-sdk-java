/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.cache.Cache;
import javax.cache.Caching;

import com.google.common.collect.Streams;
import com.sap.cloud.sdk.cloudplatform.cache.GenericCacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheFilter;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorationStrategy;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Configurable implementation of ResilienceDecorationStrategy using Resilience4j.
 */
@Slf4j
@Builder
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class Resilience4jDecorationStrategy implements ResilienceDecorationStrategy
{
    @Nonnull
    @Singular
    private final List<GenericDecorator> decorators;

    /**
     * Constant list of default decorators.
     */
    protected static final List<GenericDecorator> DEFAULT_DECORATORS =
        Arrays
            .asList(
                new DefaultBulkheadProvider(),
                new DefaultTimeLimiterProvider(),
                new DefaultRateLimiterProvider(),
                new DefaultCircuitBreakerProvider(),
                new DefaultCachingDecorator(),
                new DefaultRetryProvider());

    /**
     * Builder class for custom instances of {@link Resilience4jDecorationStrategy}. The order of attached decorators
     * indicates the order of execution.
     */
    public static class Resilience4jDecorationStrategyBuilder
    {
        /**
         * Attach the default decorators in the order of {@link Resilience4jDecorationStrategy#DEFAULT_DECORATORS
         * DEFAULT_DECORATORS}.
         *
         * @return The same builder instance.
         */
        @Nonnull
        public Resilience4jDecorationStrategyBuilder defaultDecorators()
        {
            decorators(DEFAULT_DECORATORS);
            return this;
        }
    }

    /**
     * Default constructor with default set of decorators.
     */
    public Resilience4jDecorationStrategy()
    {
        this(DEFAULT_DECORATORS);
    }

    /**
     * Default constructor with enumerated decorators.
     *
     * @param decorators
     *            The resilience decorators to be attached.
     */
    public Resilience4jDecorationStrategy( @Nonnull final GenericDecorator... decorators )
    {
        this(Arrays.stream(decorators).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Override
    @SuppressWarnings( "PMD.CloseResource" ) // closing JCache resource will disable caching feature
    public void clearCache( @Nonnull final ResilienceConfiguration configuration )
    {
        clearCache(
            configuration,
            CacheFilter
                .and(
                    CacheFilter.keyMatchesTenant(),
                    CacheFilter.keyMatchesPrincipal(),
                    CacheFilter.keyMatchesParameters()));
    }

    @Override
    @SuppressWarnings( "PMD.CloseResource" ) // closing JCache resource will disable caching feature
    public void clearCache( @Nonnull final ResilienceConfiguration configuration, @Nonnull final CacheFilter filter )
    {
        final Cache<GenericCacheKey<?, ?>, ?> cache =
            Caching.getCachingProvider().getCacheManager().getCache(configuration.identifier());
        if( cache == null ) {
            return;
        }

        final Set<GenericCacheKey<?, ?>> keysToClear =
            Streams
                .stream(cache)
                .filter(entry -> filter.matches(configuration, entry.getKey(), entry.getValue()))
                .map(Cache.Entry::getKey)
                .collect(Collectors.toSet());

        log
            .debug(
                "Removing {} entries from the cache with configuration id \"{}\".",
                keysToClear.size(),
                configuration.identifier());

        cache.removeAll(keysToClear);
    }

    @Override
    @SuppressWarnings( "PMD.CloseResource" ) // closing JCache resource will disable caching feature
    public void clearAllCacheEntries( @Nonnull final ResilienceConfiguration configuration )
    {
        final Cache<?, ?> cache = Caching.getCachingProvider().getCacheManager().getCache(configuration.identifier());
        if( cache != null ) {
            cache.clear();
        }
    }

    @Nonnull
    @Override
    public <T> Supplier<T> decorateSupplier(
        @Nonnull final Supplier<T> supplier,
        @Nonnull final ResilienceConfiguration configuration,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        final Callable<T> callable = decorateCallable(supplier::get, configuration, fallbackFunction);
        return () -> Try.ofCallable(callable).get();
    }

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration config,
        @Nullable final Function<? super Throwable, T> fallbackFunction )
    {
        Callable<T> decoratedCall = () -> {
            log
                .debug(
                    "Invoking decorated callable {} with applied decorators {} and configuration {}.",
                    callable,
                    decorators,
                    config);

            // add Resilience4jDecorationStrategy to stacktrace for quality listener
            return callable.call();
        };

        for( final GenericDecorator deco : decorators ) {
            log.trace("Decorating callable {} with decorator {}.", callable, deco);
            decoratedCall = deco.decorateCallable(decoratedCall, config);
        }

        final Callable<T> finalDecoratedCallable = decoratedCall;

        return () -> {
            Try<T> callableResult = Try.ofCallable(finalDecoratedCallable);
            if( fallbackFunction != null ) {
                callableResult = callableResult.recover(fallbackFunction);
            }
            return callableResult.onFailure(t -> {
                throw new ResilienceRuntimeException(t);
            }).get();
        };
    }
}
