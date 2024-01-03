/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.HttpClient;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Accessor for {@link HttpClient}s.
 *
 * @since 4.20.0
 */
@Beta
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class ApacheHttpClient5Accessor
{
    /**
     * Configures the {@code HttpClient5Cache} that is used by the {@code #getHttpClient(String)} and
     * {@code #getHttpClient(Destination)} methods.
     * <p>
     * By default, this uses an implementation, which caches the {@link HttpClient} for 5 minutes.
     * <p>
     * <strong>CAUTION:</strong> This factory is accessed concurrently. Therefore, you have to make sure that you do not
     * introduce any concurrency issues when changing the factory. Furthermore, be aware that setting a custom factory
     * will affect <strong>all consumers</strong> of the {@link ApacheHttpClient5Accessor} within the application.
     */
    @Getter
    @Nonnull
    private static ApacheHttpClient5Cache httpClientCache = newDefaultCache();

    /**
     * Returns the {@link ApacheHttpClient5Factory} that is used when creating instances of {@link HttpClient} within
     * the {@link ApacheHttpClient5Accessor}.
     */
    @Getter
    @Nonnull
    private static ApacheHttpClient5Factory httpClientFactory = newDefaultFactory();

    /**
     * Sets the {@link ApacheHttpClient5Factory} that is used when creating instances of {@link HttpClient} within the
     * {@link ApacheHttpClient5Accessor}.
     * <p>
     * <strong>CAUTION:</strong> This factory is accessed concurrently. Therefore, you have to make sure that you do not
     * introduce any concurrency issues when changing the factory. Furthermore, be aware that setting a custom factory
     * will affect <strong>all consumers</strong> of the {@link ApacheHttpClient5Accessor} within the application.
     *
     * @param httpClientFactory
     *            The global {@link ApacheHttpClient5Factory} instance to be used. Use {@code null} to reset the
     *            factory.
     */
    public static void setHttpClientFactory( @Nullable final ApacheHttpClient5Factory httpClientFactory )
    {
        if( httpClientFactory == null ) {
            ApacheHttpClient5Accessor.httpClientFactory = newDefaultFactory();
        } else {
            ApacheHttpClient5Accessor.httpClientFactory = httpClientFactory;
        }
    }

    /**
     * Sets the {@link ApacheHttpClient5Cache} that is used when resolving cached instances of {@link HttpClient} within
     * the {@link ApacheHttpClient5Accessor}.
     * <p>
     * <strong>CAUTION:</strong> This cache is accessed concurrently. Therefore, you have to make sure that you do not
     * introduce any concurrency issues when changing the cache. Furthermore, be aware that setting a custom cache will
     * affect <strong>all consumers</strong> of the {@link ApacheHttpClient5Accessor} within the application.
     *
     * @param httpClientCache
     *            The global {@link ApacheHttpClient5Cache} instance to be used. Use {@code null} to reset the cache.
     */
    public static void setHttpClientCache( @Nullable final ApacheHttpClient5Cache httpClientCache )
    {
        if( httpClientCache == null ) {
            ApacheHttpClient5Accessor.httpClientCache = newDefaultCache();
        } else {
            ApacheHttpClient5Accessor.httpClientCache = httpClientCache;
        }
    }

    @Nonnull
    private static ApacheHttpClient5Cache newDefaultCache()
    {
        return new ApacheHttpClient5CacheBuilder().build();
    }

    @Nonnull
    private static ApacheHttpClient5Factory newDefaultFactory()
    {
        return new ApacheHttpClient5FactoryBuilder().build();
    }

    /**
     * Returns an {@link HttpClient} independent of any destination.
     *
     * @return An {@link HttpClient} independent of any destination.
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
     * @return An {@link HttpClient} for the given {@link Destination}. The instance may be cached.
     * @throws DestinationAccessException
     *             If there is an issue accessing the {@link Destination}.
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
     * @return An {@link HttpClient} for the given {@link Destination}. The instance may be cached.
     * @throws DestinationAccessException
     *             If there is an issue accessing the {@link Destination}.
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
