package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.util.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;

/**
 * Factory class providing pre-built {@link ConnectionPoolManagerProvider} implementations with various caching
 * strategies.
 * <p>
 * Connection pool managers can consume significant memory (~100KB each). By caching and reusing connection managers
 * based on appropriate keys, applications can reduce memory consumption while maintaining proper isolation where
 * needed.
 * </p>
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>
 * {@code
 * // No caching (default behavior)
 * ApacheHttpClient5Factory factory =
 *     new ApacheHttpClient5FactoryBuilder()
 *         .connectionPoolManagerProvider(ConnectionPoolManagerProviders.noCache())
 *         .build();
 *
 * // Cache connection managers by tenant using default ConcurrentHashMap
 * ApacheHttpClient5Factory factory =
 *     new ApacheHttpClient5FactoryBuilder()
 *         .connectionPoolManagerProvider(ConnectionPoolManagerProviders.cached().byTenant())
 *         .build();
 *
 * // Cache with custom ConcurrentMap
 * ConcurrentMap<Object, HttpClientConnectionManager> myCache = new ConcurrentHashMap<>();
 * ApacheHttpClient5Factory factory =
 *     new ApacheHttpClient5FactoryBuilder()
 *         .connectionPoolManagerProvider(ConnectionPoolManagerProviders.cached(myCache::computeIfAbsent).byTenant())
 *         .build();
 *
 * // Cache with Caffeine cache (supports expiration, size limits, etc.)
 * Cache<Object, HttpClientConnectionManager> caffeineCache =
 *     Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(30)).maximumSize(100).build();
 * ApacheHttpClient5Factory factory =
 *     new ApacheHttpClient5FactoryBuilder()
 *         .connectionPoolManagerProvider(ConnectionPoolManagerProviders.cached(caffeineCache::get).byDestinationName())
 *         .build();
 * }
 * </pre>
 *
 * @see ConnectionPoolManagerProvider
 * @see ConnectionPoolSettings
 * @see ApacheHttpClient5FactoryBuilder#connectionPoolManagerProvider(ConnectionPoolManagerProvider)
 * @since 5.27.0
 */
@Beta
@Slf4j
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class ConnectionPoolManagerProviders
{
    private static final Duration DEFAULT_CACHE_DURATION = DefaultApacheHttpClient5Cache.DEFAULT_DURATION;

    /**
     * Creates a provider that does not cache connection managers.
     * <p>
     * A new {@link HttpClientConnectionManager} is created for each call. This is the default behavior and provides
     * maximum isolation but highest memory consumption.
     * </p>
     *
     * @return A provider that creates a new connection manager for each request.
     */
    @Nonnull
    public static ConnectionPoolManagerProvider noCache()
    {
        return ConnectionPoolManagerProviders::createConnectionManager;
    }

    /**
     * Creates a builder for cached connection pool manager providers using a default {@link ConcurrentHashMap} as the
     * cache.
     * <p>
     * The returned builder allows selecting a caching strategy (by tenant, by destination name, global, or custom).
     * </p>
     *
     * <h3>Example Usage</h3>
     *
     * <pre>
     * {@code
     * // Cache by tenant
     * ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byTenant();
     *
     * // Cache by destination name
     * ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byDestinationName();
     *
     * // Single global cache
     * ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().global();
     *
     * // Custom cache key
     * ConnectionPoolManagerProvider provider =
     *     ConnectionPoolManagerProviders.cached().withCacheKey(dest -> dest.getUri().getHost());
     * }
     * </pre>
     *
     * @return A builder for configuring the caching strategy.
     */
    @Nonnull
    public static CachedProviderBuilder cached()
    {
        final Cache<Object, HttpClientConnectionManager> cache =
            Caffeine.newBuilder().expireAfterAccess(DEFAULT_CACHE_DURATION).build();
        return new CachedProviderBuilder(cache::get);
    }

    /**
     * Creates a builder for cached connection pool manager providers using a custom cache function.
     * <p>
     * The cache function must have the signature {@code (key, loader) -> value}, which is compatible with:
     * </p>
     * <ul>
     * <li>{@link java.util.concurrent.ConcurrentMap#computeIfAbsent(Object, Function)} - e.g.,
     * {@code myMap::computeIfAbsent}</li>
     * <li>{@code com.github.benmanes.caffeine.cache.Cache#get(Object, Function)} - e.g.,
     * {@code caffeineCache::get}</li>
     * </ul>
     *
     * <h3>Example Usage</h3>
     *
     * <pre>
     * {@code
     * // Using a custom ConcurrentMap
     * ConcurrentMap<Object, HttpClientConnectionManager> myCache = new ConcurrentHashMap<>();
     * ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached(myCache::computeIfAbsent).byTenant();
     *
     * // Using Caffeine cache with expiration
     * Cache<Object, HttpClientConnectionManager> caffeineCache =
     *     Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(30)).maximumSize(100).build();
     * ConnectionPoolManagerProvider provider =
     *     ConnectionPoolManagerProviders.cached(caffeineCache::get).byDestinationName();
     * }
     * </pre>
     *
     * @param cacheFunction
     *            A function that takes a cache key and a loader function, and returns the cached or newly computed
     *            value. The signature matches {@code ConcurrentMap::computeIfAbsent} and {@code Cache::get}.
     * @return A builder for configuring the caching strategy.
     */
    @Nonnull
    public static CachedProviderBuilder cached(
        @Nonnull final BiFunction<Object, Function<Object, HttpClientConnectionManager>, HttpClientConnectionManager> cacheFunction )
    {
        Objects.requireNonNull(cacheFunction, "Cache function must not be null");
        return new CachedProviderBuilder(cacheFunction);
    }

    /**
     * Builder class for creating cached {@link ConnectionPoolManagerProvider} instances with various caching
     * strategies.
     * <p>
     * Use {@link ConnectionPoolManagerProviders#cached()} or {@link ConnectionPoolManagerProviders#cached(BiFunction)}
     * to obtain an instance of this builder.
     * </p>
     *
     * @since 5.27.0
     */
    @Beta
    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    public static final class CachedProviderBuilder
    {
        @Nonnull
        private final BiFunction<Object, Function<Object, HttpClientConnectionManager>, HttpClientConnectionManager> cacheFunction;

        /**
         * Creates a provider that caches connection managers by the current tenant.
         * <p>
         * Connection managers are shared among all destinations accessed within the same tenant context. This is useful
         * when tenant isolation is required but destination-level isolation is not necessary.
         * </p>
         * <p>
         * If no tenant is available in the current context, a new connection manager is used.
         * </p>
         *
         * @return A provider that caches connection managers by tenant.
         * @see TenantAccessor#tryGetCurrentTenant()
         */
        @Nonnull
        public ConnectionPoolManagerProvider byCurrentTenant()
        {
            return by(dest -> TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId).getOrNull());
        }

        /**
         * Creates a provider that caches connection managers by destination name.
         * <p>
         * Connection managers are shared among all requests to destinations with the same name. This is useful when
         * different destinations may have different TLS or proxy configurations.
         * </p>
         * <p>
         * If the destination has no name or is {@code null}, a new connection manager is used.
         * </p>
         *
         * @return A provider that caches connection managers by destination name.
         */
        @Nonnull
        public ConnectionPoolManagerProvider byDestinationName()
        {
            return by(dest -> dest != null ? dest.get(DestinationProperty.NAME).getOrNull() : null);
        }

        @Nonnull
        public ConnectionPoolManagerProvider byIndicatedBehalfOf()
        {
            return by(destination -> {
                if( !(destination instanceof final DefaultHttpDestination dest) ) {
                    return null;
                }
                final boolean indicatesCurrentTenant =
                    dest
                        .getCustomHeaderProviders()
                        .stream()
                        .filter(IsOnBehalfOf.class::isInstance)
                        .map(d -> ((IsOnBehalfOf) d).getOnBehalfOf())
                        .anyMatch(b -> b == NAMED_USER_CURRENT_TENANT || b == TECHNICAL_USER_CURRENT_TENANT);

                // If the destination indicates that it is on behalf of the current tenant, include the tenant ID in the cache key to ensure proper isolation.
                if( indicatesCurrentTenant ) {
                    return List.of(TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId).getOrNull(), dest);
                }

                // Otherwise, return a cache key that does not include tenant information, allowing sharing across tenants if other properties match.
                return dest;
            });
        }

        /**
         * Creates a provider that caches connection managers using a custom cache key extractor.
         * <p>
         * The cache key extractor function is called for each request to determine which cached connection manager to
         * use. Requests that produce equal cache keys (via {@link Object#equals(Object)}) will share the same
         * connection manager.
         * </p>
         * <p>
         * <strong>Note:</strong> The cache key extractor should return consistent keys for destinations that can safely
         * share a connection manager. Consider TLS configuration, proxy settings, and isolation requirements when
         * designing the key extraction logic.
         * </p>
         *
         * @param cacheKeyExtractor
         *            A function that extracts a cache key from the destination. The function receives {@code null} when
         *            creating a generic (non-destination-specific) connection manager. The returned key may be
         *            {@code null} to indicate a new uncached entry.
         * @return A provider that caches connection managers using the custom key extractor.
         */
        @Nonnull
        public ConnectionPoolManagerProvider by(
            @Nonnull final Function<HttpDestinationProperties, Object> cacheKeyExtractor )
        {
            Objects.requireNonNull(cacheKeyExtractor, "Cache key extractor must not be null");
            return ( settings, destination ) -> {
                final Object rawKey = cacheKeyExtractor.apply(destination);
                if( rawKey == null ) {
                    log
                        .debug(
                            "Creating new connection manager due to missing cache key for destination: {}",
                            destination);
                    return createConnectionManager(settings, destination);
                }
                return cacheFunction.apply(rawKey, key -> {
                    log.debug("Creating new connection manager for cache key: {}", rawKey);
                    return createConnectionManager(settings, destination);
                });
            };
        }
    }

    /**
     * Creates a new connection manager with the given settings and destination-specific TLS configuration.
     */
    @Nonnull
    static HttpClientConnectionManager createConnectionManager(
        @Nonnull final ConnectionPoolSettings settings,
        @Nullable final HttpDestinationProperties destination )
        throws HttpClientInstantiationException
    {
        try {
            final Timeout timeoutSocket = Timeout.of(settings.getSocketTimeout());
            final Timeout timeoutConnect = Timeout.of(settings.getConnectTimeout());

            return PoolingHttpClientConnectionManagerBuilder
                .create()
                .setTlsSocketStrategy(getTlsSocketStrategy(destination))
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(timeoutSocket).build())
                .setDefaultConnectionConfig(
                    ConnectionConfig.custom().setConnectTimeout(timeoutConnect).setSocketTimeout(timeoutSocket).build())
                .setMaxConnTotal(settings.getMaxConnectionsTotal())
                .setMaxConnPerRoute(settings.getMaxConnectionsPerRoute())
                .build();
        }
        catch( final GeneralSecurityException | IOException e ) {
            throw new HttpClientInstantiationException("Failed to create HTTP client connection manager.", e);
        }
    }

    @Nullable
    private static TlsSocketStrategy getTlsSocketStrategy( @Nullable final HttpDestinationProperties destination )
        throws GeneralSecurityException,
            IOException
    {
        if( !supportsTls(destination) ) {
            return null;
        }

        log.debug("The destination uses HTTPS for target \"{}\".", destination.getUri());
        final SSLContext sslContext = new SSLContextFactory().createSSLContext(destination);
        final HostnameVerifier hostnameVerifier = getHostnameVerifier(destination);

        return new DefaultClientTlsStrategy(sslContext, hostnameVerifier);
    }

    private static HostnameVerifier getHostnameVerifier( @Nonnull final HttpDestinationProperties destination )
    {
        return destination.isTrustingAllCertificates() ? new NoopHostnameVerifier() : new DefaultHostnameVerifier();
    }

    private static boolean supportsTls( @Nullable final HttpDestinationProperties destination )
    {
        if( destination == null ) {
            return false;
        }
        final String scheme = destination.getUri().getScheme();
        return "https".equalsIgnoreCase(scheme) || StringUtils.isEmpty(scheme);
    }
}
