/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Try;

/**
 * Provides caching functionality to the {@code HttpClient5Accessor}.
 * <p>
 * A default implementation of this interface can be instantiated by using the {@link ApacheHttpClient5CacheBuilder}.
 * </p>
 *
 * @since 4.20.0
 */
public interface ApacheHttpClient5Cache
{
    /**
     * Creates a new {@code HttpClient5Cache} with the given {@code cacheDuration}.
     *
     * @param cacheDuration
     *            The duration for which {@link HttpClient} instances will be cached.
     * @return A new {@code HttpClient5Cache} instance.
     */
    @Nonnull
    static ApacheHttpClient5Cache newDefaultCache( @Nonnull final Duration cacheDuration )
    {
        return new DefaultApacheHttpClient5Cache(cacheDuration);
    }

    /**
     * Gets the {@code HttpClient} for the given {@code Destination} from this cache.
     * <p>
     * If there is no {@code HttpClient} for {@code destination} it is created by using the {@code HttpClient5Factory}.
     *
     * @param destination
     *            The {@code Destination} to get the {@code HttpClient} for.
     * @param httpClientFactory
     *            The {@code HttpClient5Factory} used to create a new {@code HttpClient} if no cached one can be found.
     * @return The cached {@code HttpClient} for the given {@code Destination}.
     * @throws HttpClientInstantiationException
     *             If there is an issue while retrieving the {@code HttpClient}.
     */
    @Nonnull
    Try<HttpClient> tryGetHttpClient(
        @Nonnull final HttpDestinationProperties destination,
        @Nonnull final ApacheHttpClient5Factory httpClientFactory );

    /**
     * Gets the non-destination-specific {@code HttpClient} from this cache.
     * <p>
     * If there is no generic {@code HttpClient} it is created by using the {@code HttpClient5Factory}.
     *
     * @param httpClientFactory
     *            The {@code HttpClient5Factory} used to create a new {@code HttpClient} if no cached one can be found.
     * @return The cached {@code HttpClient}.
     */
    @Nonnull
    Try<HttpClient> tryGetHttpClient( @Nonnull final ApacheHttpClient5Factory httpClientFactory );

    /**
     * Constant implementation of {@see HttpClient5Cache} with disabled the cache logic. Useful for testing and
     * troubleshooting. Don't use in production.
     */
    @Nonnull
    ApacheHttpClient5Cache DISABLED = new ApacheHttpClient5Cache()
    {
        @Nonnull
        @Override
        public Try<HttpClient> tryGetHttpClient(
            @Nonnull final HttpDestinationProperties destination,
            @Nonnull final ApacheHttpClient5Factory httpClientFactory )
        {
            return Try.of(() -> httpClientFactory.createHttpClient(destination));
        }

        @Nonnull
        @Override
        public Try<HttpClient> tryGetHttpClient( @Nonnull final ApacheHttpClient5Factory httpClientFactory )
        {
            return Try.of(httpClientFactory::createHttpClient);
        }
    };
}
