/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Try;

/**
 * Provides caching functionality to the {@code HttpClientAccessor}.
 */
public interface HttpClientCache
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
     * @throws HttpClientInstantiationException
     *             If there is an issue while retrieving the {@code HttpClient}.
     *
     * @return The cached {@code HttpClient} for the given {@code Destination}.
     */
    @Nonnull
    Try<HttpClient> tryGetHttpClient(
        @Nonnull final HttpDestinationProperties destination,
        @Nonnull final HttpClientFactory httpClientFactory );

    /**
     * Gets the non-destination-specific {@code HttpClient} from this cache.
     * <p>
     * If there is no generic {@code HttpClient} it is created by using the {@code HttpClientFactory}.
     *
     * @param httpClientFactory
     *            The {@code HttpClientFactory} used to create a new {@code HttpClient} if no cached one can be found.
     *
     * @return The cached {@code HttpClient}.
     */
    @Nonnull
    Try<HttpClient> tryGetHttpClient( @Nonnull final HttpClientFactory httpClientFactory );

    /**
     * Constant implementation of {@see HttpClientCache} with disabled the cache logic. Useful for testing and
     * troubleshooting. Don't use in production.
     */
    @Nonnull
    HttpClientCache DISABLED = new HttpClientCache()
    {
        @Nonnull
        @Override
        public Try<HttpClient> tryGetHttpClient(
            @Nonnull final HttpDestinationProperties destination,
            @Nonnull final HttpClientFactory httpClientFactory )
        {
            return Try.of(() -> httpClientFactory.createHttpClient(destination));
        }

        @Nonnull
        @Override
        public Try<HttpClient> tryGetHttpClient( @Nonnull final HttpClientFactory httpClientFactory )
        {
            return Try.of(httpClientFactory::createHttpClient);
        }
    };
}
