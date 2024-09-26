/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides caching functionality to the {@code HttpClientAccessor}.
 */
@Slf4j
public abstract class AbstractHttpClientCache implements HttpClientCache
{
    /**
     * Gets the {@code HttpClient} for the given {@code Destination} from this cache.
     * <p>
     * If there is no {@code HttpClient} for {@code destination} it is created by using the {@code HttpClientFactory}.
     *
     * @param destination
     *            The {@code Destination} to get the {@code HttpClient} for.
     * @param httpClientFactory
     *            The {@code HttpClientFactory} used to create a new {@code HttpClient} if no cached one can be found.
     *
     * @return A {@link Try} of the cached {@code HttpClient} for the given {@code Destination}.
     */
    @Override
    @Nonnull
    public Try<HttpClient> tryGetHttpClient(
        @Nonnull final HttpDestinationProperties destination,
        @Nonnull final HttpClientFactory httpClientFactory )
    {
        return tryGetOrCreateHttpClient(httpClientFactory, destination);
    }

    /**
     * Gets the non-destination-specific {@code HttpClient} from this cache.
     * <p>
     * If there is no generic {@code HttpClient} it is created by using the {@code HttpClientFactory}.
     *
     * @param httpClientFactory
     *            The {@code HttpClientFactory} used to create a new {@code HttpClient} if no cached one can be found.
     *
     * @return A {@link Try} of the cached {@code HttpClient}.
     */
    @Override
    @Nonnull
    public Try<HttpClient> tryGetHttpClient( @Nonnull final HttpClientFactory httpClientFactory )
    {
        return tryGetOrCreateHttpClient(httpClientFactory, null);
    }

    private Try<HttpClient> tryGetOrCreateHttpClient(
        @Nonnull final HttpClientFactory httpClientFactory,
        @Nullable final HttpDestinationProperties destination )
    {
        final Supplier<HttpClient> createHttpClient = () -> {
            log.debug("HttpClient with given cache key is not yet in the cache.");
            return destination != null
                ? httpClientFactory.createHttpClient(destination)
                : httpClientFactory.createHttpClient();
        };

        final Try<Cache<CacheKey, HttpClient>> maybeCache = getCache();
        if( maybeCache.isFailure() ) {
            return Try
                .failure(new HttpClientInstantiationException("Failed to get HttpClientCache.", maybeCache.getCause()));
        }

        final Try<CacheKey> maybeKey = destination != null ? getCacheKey(destination) : getCacheKey();

        if( maybeKey.isFailure() ) {
            return Try
                .failure(
                    new HttpClientInstantiationException(
                        "Failed to create cache key for HttpClient",
                        maybeKey.getCause()));
        }

        final Cache<CacheKey, HttpClient> cache = maybeCache.get();
        final CacheKey cacheKey = maybeKey.get();

        final HttpClient httpClient;
        try {
            httpClient = cache.get(cacheKey, anyKey -> createHttpClient.get());
            Objects
                .requireNonNull(
                    httpClient,
                    "Failed to create HttpClient: The registered HttpClientFactory unexpectedly returned null.");
        }
        catch( final HttpClientInstantiationException e ) {
            return Try.failure(e);
        }
        catch( final RuntimeException e ) {
            return Try.failure(new HttpClientInstantiationException(e));
        }
        if( destination != null && httpClient instanceof HttpClientWrapper ) {
            return Try.success(((HttpClientWrapper) httpClient).withDestination(destination));
        }
        return Try.success(httpClient);
    }

    /**
     * Getter for the cache to be used.
     * <p>
     * If the optional is empty a new {@code HttpClient} will be created and not cached.
     *
     * @throws HttpClientInstantiationException
     *             If there is an issue while accessing the cache.
     *
     * @return A {@code Try} of the cache to be used by the
     *         {@link #tryGetHttpClient(HttpDestinationProperties, HttpClientFactory)} method.
     */
    @Nonnull
    protected abstract Try<Cache<CacheKey, HttpClient>> getCache();

    /**
     * Method called in the {@code #getClient(Destination, HttpClientFactory)} method to create a {@code CacheKey} for
     * the given {@code Destination}.
     *
     * @param destination
     *            The destination to create a {@code CacheKey} for.
     *
     * @throws HttpClientInstantiationException
     *             If there is an issue while accessing the cache key.
     *
     * @return A {@code Try} of the {@code CacheKey} for {@code destination.}
     */
    @Nonnull
    protected abstract Try<CacheKey> getCacheKey( @Nonnull final HttpDestinationProperties destination );

    /**
     * Method called in the {@code #getClient(HttpClientFactory)} method to create a {@code CacheKey} for no specific
     * {@code Destination}.
     *
     * @throws HttpClientInstantiationException
     *             If there is an issue while accessing the cache key.
     *
     * @return A {@code Try} of the {@code CacheKey} for a generic {@link HttpClient}.
     */
    @Nonnull
    protected abstract Try<CacheKey> getCacheKey();
}
