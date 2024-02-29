/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.HttpClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DefaultApacheHttpClient5Cache implements ApacheHttpClient5Cache
{

    static final Duration DEFAULT_DURATION = Duration.ofMinutes(5L);
    static final Ticker DEFAULT_TICKER = Ticker.systemTicker();

    @Nonnull
    private final Cache<CacheKey, HttpClient> cache;

    DefaultApacheHttpClient5Cache( @Nonnull final Duration cacheDuration )
    {
        this(cacheDuration, DEFAULT_TICKER);
    }

    DefaultApacheHttpClient5Cache( @Nonnull final Duration cacheDuration, @Nonnull final Ticker ticker )
    {
        cache = Caffeine.newBuilder().expireAfterWrite(cacheDuration).ticker(ticker).build();
        CacheManager.register(cache);
    }

    @Nonnull
    @Override
    public Try<HttpClient> tryGetHttpClient(
        @Nonnull final HttpDestinationProperties destination,
        @Nonnull final ApacheHttpClient5Factory httpClientFactory )
    {
        return tryGetOrCreateHttpClient(httpClientFactory, destination);
    }

    @Nonnull
    @Override
    public Try<HttpClient> tryGetHttpClient( @Nonnull final ApacheHttpClient5Factory httpClientFactory )
    {
        return tryGetOrCreateHttpClient(httpClientFactory, null);
    }

    private Try<HttpClient> tryGetOrCreateHttpClient(
        @Nonnull final ApacheHttpClient5Factory httpClientFactory,
        @Nullable final HttpDestinationProperties destination )
    {
        final Supplier<HttpClient> createHttpClient = () -> {
            log.debug("HttpClient with given cache key is not yet in the cache.");
            return destination != null
                ? httpClientFactory.createHttpClient(destination)
                : httpClientFactory.createHttpClient();
        };

        final Try<CacheKey> maybeKey = destination != null ? getCacheKey(destination) : getCacheKey();
        maybeKey.onFailure(cause -> logCachePropagationFailure("Could not get HttpClient cache key.", cause));

        if( maybeKey.isFailure() ) {
            return Try.ofSupplier(createHttpClient);
        }

        final CacheKey cacheKey = maybeKey.get();

        final HttpClient httpClient;
        try {
            httpClient = cache.get(cacheKey, anyKey -> createHttpClient.get());
            Objects
                .requireNonNull(
                    httpClient,
                    "Failed to create HttpClient: The registered HttpClient5Factory unexpectedly returned null.");
        }
        catch( final HttpClientInstantiationException e ) {
            return Try.failure(e);
        }
        catch( final RuntimeException e ) {
            return Try.failure(new HttpClientInstantiationException(e));
        }
        if( destination != null && httpClient instanceof ApacheHttpClient5Wrapper ) {
            return Try.success(((ApacheHttpClient5Wrapper) httpClient).withDestination(destination));
        }
        return Try.success(httpClient);
    }

    private void logCachePropagationFailure( @Nonnull final String message, @Nonnull final Throwable cause )
    {
        log.info(message);
        log.debug(message, cause);
    }

    private Try<CacheKey> getCacheKey( @Nonnull final HttpDestinationProperties destination )
    {
        return Try.of(() -> {
            if( DestinationUtility.requiresUserTokenExchange(destination) ) {
                return CacheKey.ofTenantAndPrincipalOptionalIsolation().append(destination);
            }
            return CacheKey.ofTenantOptionalIsolation().append(destination);
        });
    }

    private Try<CacheKey> getCacheKey()
    {
        return Try.of(CacheKey::ofTenantAndPrincipalOptionalIsolation);
    }
}
