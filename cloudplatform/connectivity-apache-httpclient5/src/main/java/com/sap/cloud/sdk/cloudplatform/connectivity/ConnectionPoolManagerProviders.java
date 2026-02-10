package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
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
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.util.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 * // Cache connection managers by tenant
 * ApacheHttpClient5Factory factory =
 *     new ApacheHttpClient5FactoryBuilder()
 *         .connectionPoolManagerProvider(ConnectionPoolManagerProviders.byTenant())
 *         .build();
 *
 * // Use a single global connection manager
 * ApacheHttpClient5Factory factory =
 *     new ApacheHttpClient5FactoryBuilder()
 *         .connectionPoolManagerProvider(ConnectionPoolManagerProviders.global())
 *         .build();
 *
 * // Custom caching strategy
 * ApacheHttpClient5Factory factory =
 *     new ApacheHttpClient5FactoryBuilder()
 *         .connectionPoolManagerProvider(
 *             ConnectionPoolManagerProviders
 *                 .withCacheKey(dest -> dest.get(DestinationProperty.NAME).getOrElse("default")))
 *         .build();
 * }
 * </pre>
 *
 * @see ConnectionPoolManagerProvider
 * @see ConnectionPoolSettings
 * @see ApacheHttpClient5FactoryBuilder#connectionPoolManagerProvider(ConnectionPoolManagerProvider)
 * @since 5.XX.0
 */
@Beta
@Slf4j
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class ConnectionPoolManagerProviders
{
    // Constant keys for cache entries
    private static final String GLOBAL_KEY = "__GLOBAL__";
    private static final String NULL_KEY = "__NULL__";

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
     * Creates a provider that caches connection managers by the current tenant.
     * <p>
     * Connection managers are shared among all destinations accessed within the same tenant context. This is useful
     * when tenant isolation is required but destination-level isolation is not necessary.
     * </p>
     * <p>
     * If no tenant is available in the current context, a shared "no-tenant" connection manager is used.
     * </p>
     *
     * @return A provider that caches connection managers by tenant.
     * @see TenantAccessor#tryGetCurrentTenant()
     */
    @Nonnull
    public static ConnectionPoolManagerProvider byTenant()
    {
        return withCacheKey(dest -> TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId).getOrNull());
    }

    /**
     * Creates a provider that caches connection managers by destination name.
     * <p>
     * Connection managers are shared among all requests to destinations with the same name. This is useful when
     * different destinations may have different TLS or proxy configurations.
     * </p>
     * <p>
     * If the destination has no name or is {@code null}, a shared "unnamed" connection manager is used.
     * </p>
     *
     * @return A provider that caches connection managers by destination name.
     */
    @Nonnull
    public static ConnectionPoolManagerProvider byDestinationName()
    {
        return withCacheKey(dest -> dest != null ? dest.get(DestinationProperty.NAME).getOrNull() : null);
    }

    /**
     * Creates a provider that uses a single global connection manager for all destinations.
     * <p>
     * This provides the lowest memory consumption but no isolation between tenants or destinations. Use this only when
     * all destinations have compatible TLS configurations and isolation is not required.
     * </p>
     * <p>
     * <strong>Warning:</strong> This strategy does not support destination-specific TLS configurations. All
     * destinations will use the default TLS settings.
     * </p>
     *
     * @return A provider that uses a single global connection manager.
     */
    @Nonnull
    public static ConnectionPoolManagerProvider global()
    {
        return withCacheKey(dest -> GLOBAL_KEY);
    }

    /**
     * Creates a provider that caches connection managers using a custom cache key extractor.
     * <p>
     * The cache key extractor function is called for each request to determine which cached connection manager to use.
     * Requests that produce equal cache keys (via {@link Object#equals(Object)}) will share the same connection
     * manager.
     * </p>
     * <p>
     * <strong>Note:</strong> The cache key extractor should return consistent keys for destinations that can safely
     * share a connection manager. Consider TLS configuration, proxy settings, and isolation requirements when designing
     * the key extraction logic.
     * </p>
     *
     * @param cacheKeyExtractor
     *            A function that extracts a cache key from the destination. The function receives {@code null} when
     *            creating a generic (non-destination-specific) connection manager. The returned key may be {@code null}
     *            to indicate a shared "null-key" bucket.
     * @return A provider that caches connection managers using the custom key extractor.
     */
    @Nonnull
    public static ConnectionPoolManagerProvider withCacheKey(
        @Nonnull final Function<HttpDestinationProperties, Object> cacheKeyExtractor )
    {
        Objects.requireNonNull(cacheKeyExtractor, "Cache key extractor must not be null");
        return new CachingProvider(cacheKeyExtractor);
    }

    /**
     * Provider that caches connection managers using a custom key extractor.
     */
    private static final class CachingProvider implements ConnectionPoolManagerProvider
    {
        private final Function<HttpDestinationProperties, Object> cacheKeyExtractor;
        private final ConcurrentMap<Object, HttpClientConnectionManager> cache = new ConcurrentHashMap<>();

        CachingProvider( final Function<HttpDestinationProperties, Object> cacheKeyExtractor )
        {
            this.cacheKeyExtractor = cacheKeyExtractor;
        }

        @Nonnull
        @Override
        public HttpClientConnectionManager getConnectionManager(
            @Nonnull final ConnectionPoolSettings settings,
            @Nullable final HttpDestinationProperties destination )
            throws HttpClientInstantiationException
        {
            final Object rawKey = cacheKeyExtractor.apply(destination);
            final Object cacheKey = rawKey != null ? rawKey : NULL_KEY;

            return cache.computeIfAbsent(cacheKey, key -> {
                log.debug("Creating new connection manager for cache key: {}", rawKey);
                return createConnectionManager(settings, destination);
            });
        }
    }

    /**
     * Creates a new connection manager with the given settings and destination-specific TLS configuration.
     */
    @Nonnull
    private static HttpClientConnectionManager createConnectionManager(
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
