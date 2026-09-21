package com.sap.cloud.sdk.datamodel.odata.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Cache;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5CacheBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Factory;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Accessor for {@link HttpClient} instances suitable for OData requests.
 * <p>
 * Unlike the general-purpose {@link com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor}, the
 * clients returned by {@link #getHttpClient(HttpDestinationProperties)} have a CSRF token interceptor enabled (see
 * {@link com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder#withCsrfTokenInterceptor()}) by default. The
 * interceptor automatically fetches a CSRF token via a HEAD request before every mutating HTTP request (POST, PUT,
 * PATCH, DELETE) that does not already carry an {@code x-csrf-token} header, which is required for OData services that
 * enforce CSRF protection.
 * <p>
 * For OData requests that must opt out of CSRF token handling (e.g. against services that do not support it), use
 * {@link #getHttpClientWithoutCsrf(HttpDestinationProperties)}, which returns a client without the interceptor.
 * <p>
 * For non-OData use cases (REST, OpenAPI) use
 * {@link com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor} instead to avoid unnecessary CSRF HEAD
 * requests.
 *
 * @since 5.35.0
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class ODataApacheHttpClient5Accessor
{
    /**
     * Cache and factory backing {@link #getHttpClient(HttpDestinationProperties)}. The factory has the CSRF token
     * interceptor enabled by default and can be replaced via {@link #setHttpClientFactory(ApacheHttpClient5Factory)},
     * e.g. to inject a custom or mocked client.
     */
    @Nonnull
    private static ApacheHttpClient5Cache httpClientCache = newDefaultCache();

    @Nonnull
    private static ApacheHttpClient5Factory httpClientFactory = newDefaultCsrfFactory();

    /**
     * Dedicated cache and factory backing {@link #getHttpClientWithoutCsrf(HttpDestinationProperties)}. These are kept
     * separate from the CSRF-enabled cache above because the cache is keyed by destination only: sharing a single cache
     * would return a CSRF-enabled client where a CSRF-disabled one was requested (and vice versa) for the same
     * destination.
     */
    @Nonnull
    private static final ApacheHttpClient5Cache NO_CSRF_CACHE = newDefaultCache();

    @Nonnull
    private static final ApacheHttpClient5Factory NO_CSRF_FACTORY = new ApacheHttpClient5FactoryBuilder().build();

    /**
     * Sets the {@link ApacheHttpClient5Factory} used by {@link #getHttpClient(HttpDestinationProperties)}.
     * <p>
     * <strong>CAUTION:</strong> This factory is accessed concurrently. Therefore, you have to make sure that you do not
     * introduce any concurrency issues when changing the factory. Furthermore, be aware that setting a custom factory
     * will affect <strong>all consumers</strong> of the {@link ODataApacheHttpClient5Accessor} within the application.
     *
     * @param httpClientFactory
     *            The {@link ApacheHttpClient5Factory} instance to be used. Use {@code null} to reset the factory to the
     *            default CSRF-enabled factory.
     */
    public static void setHttpClientFactory( @Nullable final ApacheHttpClient5Factory httpClientFactory )
    {
        ODataApacheHttpClient5Accessor.httpClientFactory =
            httpClientFactory == null ? newDefaultCsrfFactory() : httpClientFactory;
    }

    /**
     * Sets the {@link ApacheHttpClient5Cache} used by {@link #getHttpClient(HttpDestinationProperties)}.
     * <p>
     * <strong>CAUTION:</strong> This cache is accessed concurrently. Therefore, you have to make sure that you do not
     * introduce any concurrency issues when changing the cache. Furthermore, be aware that setting a custom cache will
     * affect <strong>all consumers</strong> of the {@link ODataApacheHttpClient5Accessor} within the application.
     *
     * @param httpClientCache
     *            The {@link ApacheHttpClient5Cache} instance to be used. Use {@code null} to reset the cache.
     */
    public static void setHttpClientCache( @Nullable final ApacheHttpClient5Cache httpClientCache )
    {
        ODataApacheHttpClient5Accessor.httpClientCache = httpClientCache == null ? newDefaultCache() : httpClientCache;
    }

    @Nonnull
    private static ApacheHttpClient5Cache newDefaultCache()
    {
        return new ApacheHttpClient5CacheBuilder().build();
    }

    @Nonnull
    private static ApacheHttpClient5Factory newDefaultCsrfFactory()
    {
        return new ApacheHttpClient5FactoryBuilder().withCsrfTokenInterceptor().build();
    }

    /**
     * Returns an {@link HttpClient} for the given destination with the CSRF token interceptor enabled. The instance may
     * be cached.
     *
     * @param destination
     *            The destination to get the {@link HttpClient} for.
     * @return An {@link HttpClient} configured for OData communication with the given destination.
     * @throws DestinationAccessException
     *             If there is an issue accessing the destination.
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
     * Returns an {@link HttpClient} for the given destination with the CSRF token interceptor enabled. The instance may
     * be cached.
     *
     * @param destination
     *            The destination to get the {@link HttpClient} for.
     * @return An {@link HttpClient} configured for OData communication with the given destination.
     * @throws DestinationAccessException
     *             If there is an issue accessing the destination.
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     */
    @Nonnull
    public static HttpClient getHttpClient( @Nonnull final Destination destination )
        throws DestinationAccessException,
            HttpClientInstantiationException
    {
        return unwrap(tryGetHttpClient(destination, httpClientCache, httpClientFactory));
    }

    /**
     * Returns an {@link HttpClient} for the given destination <strong>without</strong> the CSRF token interceptor. Use
     * this for OData requests that must opt out of CSRF token handling. The instance may be cached.
     *
     * @param destination
     *            The destination to get the {@link HttpClient} for.
     * @return An {@link HttpClient} for OData communication with the given destination, without CSRF token handling.
     * @throws DestinationAccessException
     *             If there is an issue accessing the destination.
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     */
    @Nonnull
    public static HttpClient getHttpClientWithoutCsrf( @Nonnull final HttpDestinationProperties destination )
        throws DestinationAccessException,
            HttpClientInstantiationException
    {
        return getHttpClientWithoutCsrf((Destination) destination);
    }

    /**
     * Returns an {@link HttpClient} for the given destination <strong>without</strong> the CSRF token interceptor. Use
     * this for OData requests that must opt out of CSRF token handling. The instance may be cached.
     *
     * @param destination
     *            The destination to get the {@link HttpClient} for.
     * @return An {@link HttpClient} for OData communication with the given destination, without CSRF token handling.
     * @throws DestinationAccessException
     *             If there is an issue accessing the destination.
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     */
    @Nonnull
    public static HttpClient getHttpClientWithoutCsrf( @Nonnull final Destination destination )
        throws DestinationAccessException,
            HttpClientInstantiationException
    {
        return unwrap(tryGetHttpClient(destination, NO_CSRF_CACHE, NO_CSRF_FACTORY));
    }

    @Nonnull
    private static Try<HttpClient> tryGetHttpClient(
        @Nonnull final Destination destination,
        @Nonnull final ApacheHttpClient5Cache cache,
        @Nonnull final ApacheHttpClient5Factory factory )
    {
        if( !destination.isHttp() ) {
            return Try.failure(new DestinationAccessException("The given destination is not an HTTP destination."));
        }
        return cache.tryGetHttpClient(destination.asHttp(), factory);
    }

    @Nonnull
    private static HttpClient unwrap( @Nonnull final Try<HttpClient> httpClient )
    {
        return httpClient.getOrElseThrow(failure -> {
            if( failure instanceof DestinationAccessException ) {
                throw (DestinationAccessException) failure;
            } else if( failure instanceof HttpClientInstantiationException ) {
                throw (HttpClientInstantiationException) failure;
            } else {
                throw new HttpClientInstantiationException("Failed to get HttpClient for destination.", failure);
            }
        });
    }
}
