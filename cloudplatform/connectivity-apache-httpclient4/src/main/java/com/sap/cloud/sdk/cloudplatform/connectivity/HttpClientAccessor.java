/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Accessor for {@link HttpClient}s.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class HttpClientAccessor
{
    /**
     * Configures the {@code HttpClientCache} that is used by the {@code #getHttpClient(String)} and
     * {@code #getHttpClient(Destination)} methods.
     * <p>
     * By default, this uses the {@link DefaultHttpClientCache} implementation, which caches the {@link HttpClient} for
     * 5 minutes.
     * <p>
     * <strong>CAUTION:</strong> This factory is accessed concurrently. Therefore, you have to make sure that you do not
     * introduce any concurrency issues when changing the factory. Furthermore, be aware that setting a custom factory
     * will affect <strong>all consumers</strong> of the {@link HttpClientAccessor} within the application.
     */
    @Getter
    @Nonnull
    private static HttpClientCache httpClientCache = new DefaultHttpClientCache();

    @Nonnull
    private static HttpClientFactory httpClientFactory = new DefaultHttpClientFactory();

    /**
     * Returns the {@link HttpClientFactory} that is used when creating instances of {@link HttpClient} within the
     * {@link HttpClientAccessor}.
     *
     * @return The {@link HttpClientFactory}.
     */
    @Nonnull
    public static HttpClientFactory getHttpClientFactory()
    {
        return httpClientFactory;
    }

    /**
     * Sets the {@link HttpClientFactory} that is used when creating instances of {@link HttpClient} within the
     * {@link HttpClientAccessor}.
     * <p>
     * <strong>CAUTION:</strong> This factory is accessed concurrently. Therefore, you have to make sure that you do not
     * introduce any concurrency issues when changing the factory. Furthermore, be aware that setting a custom factory
     * will affect <strong>all consumers</strong> of the {@link HttpClientAccessor} within the application.
     *
     * @param httpClientFactory
     *            The global {@link HttpClientFactory} instance to be used. Use {@code null} to reset the factory.
     */
    public static void setHttpClientFactory( @Nullable final HttpClientFactory httpClientFactory )
    {
        if( httpClientFactory == null ) {
            HttpClientAccessor.httpClientFactory = new DefaultHttpClientFactory();
        } else {
            HttpClientAccessor.httpClientFactory = httpClientFactory;
        }
    }

    /**
     * Sets the {@link HttpClientCache} that is used when resolving cached instances of {@link HttpClient} within the
     * {@link HttpClientAccessor}.
     * <p>
     * <strong>CAUTION:</strong> This cache is accessed concurrently. Therefore, you have to make sure that you do not
     * introduce any concurrency issues when changing the cache. Furthermore, be aware that setting a custom cache will
     * affect <strong>all consumers</strong> of the {@link HttpClientAccessor} within the application.
     *
     * @param httpClientCache
     *            The global {@link HttpClientCache} instance to be used. Use {@code null} to reset the cache.
     */
    public static void setHttpClientCache( @Nullable final HttpClientCache httpClientCache )
    {
        if( httpClientCache == null ) {
            HttpClientAccessor.httpClientCache = new DefaultHttpClientCache();
        } else {
            HttpClientAccessor.httpClientCache = httpClientCache;
        }
    }

    /**
     * Returns an {@link HttpClient} independent of any destination.
     *
     * @return An {@link HttpClient} independent of any destination.
     *
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     */
    @Nonnull
    public static HttpClient getHttpClient()
        throws HttpClientInstantiationException
    {
        return tryGetHttpClient().getOrElseThrow(failure -> {
            if( failure instanceof DestinationAccessException ) {
                throw (DestinationAccessException) failure;
            } else if( failure instanceof HttpClientInstantiationException ) {
                throw (HttpClientInstantiationException) failure;
            } else {
                throw new HttpClientInstantiationException("Failed to get HttpClient.", failure);
            }
        });
    }

    /**
     * Returns an {@link HttpClient} independent of any destination.
     *
     * @return An {@link HttpClient} independent of any destination.
     */
    @Nonnull
    public static Try<HttpClient> tryGetHttpClient()
    {
        return httpClientCache.tryGetHttpClient(httpClientFactory);
    }

    /**
     * Returns an {@link HttpClient} for the given {@link Destination}. The instance may be cached.
     *
     * @param destination
     *            The destination to get the {@link HttpClient} for.
     *
     * @return An {@link HttpClient} for the given {@link Destination}. The instance may be cached.
     *
     * @throws DestinationAccessException
     *             If there is an issue accessing the {@link Destination}.
     *
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     */
    @Nonnull
    public static HttpClient getHttpClient( @Nonnull final HttpDestinationProperties destination )
        throws DestinationAccessException,
            HttpClientInstantiationException
    {
        return getHttpClient((Destination) destination);
    }

    /**
     * Returns an {@link HttpClient} for the given {@link Destination}. The instance may be cached.
     *
     * @param destination
     *            The destination to get the {@link HttpClient} for.
     *
     * @return An {@link HttpClient} for the given {@link Destination}. The instance may be cached.
     *
     * @throws DestinationAccessException
     *             If there is an issue accessing the {@link Destination}.
     *
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     */
    @Nonnull
    public static HttpClient getHttpClient( @Nonnull final Destination destination )
        throws DestinationAccessException,
            HttpClientInstantiationException
    {
        return tryGetHttpClient(destination).getOrElseThrow(failure -> {
            if( failure instanceof DestinationAccessException ) {
                throw (DestinationAccessException) failure;
            } else if( failure instanceof HttpClientInstantiationException ) {
                throw (HttpClientInstantiationException) failure;
            } else {
                throw new HttpClientInstantiationException("Failed to get HttpClient for destination.", failure);
            }
        });
    }

    /**
     * Returns a {@link Try} of an {@link HttpClient} for the given {@link Destination}. The instance may be cached.
     *
     * @param destination
     *            The destination to get the {@link HttpClient} for.
     *
     * @return A {@link Try} of an {@link HttpClient} for the given {@link Destination}. The instance may be cached.
     */
    @Nonnull
    public static Try<HttpClient> tryGetHttpClient( @Nonnull final Destination destination )
    {
        if( !destination.isHttp() ) {
            return Try.failure(new DestinationAccessException("The given destination is not an HTTP destination."));
        }
        return httpClientCache.tryGetHttpClient(destination.asHttp(), httpClientFactory);
    }
}
