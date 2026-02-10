package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.hc.client5.http.classic.HttpClient;

import com.google.common.annotations.Beta;

/**
 * Builder class for a default implementation of the {@link ApacheHttpClient5Factory} interface.
 *
 * @since 4.20.0
 */
@Accessors( fluent = true )
public class ApacheHttpClient5FactoryBuilder
{
    /**
     * The {@code Upgrade} header. Only {@link ProxyType#INTERNET} has the {@code Upgrade} header by default.
     * <p>
     * <b>{@link TlsUpgrade#DISABLED} only works for {@link ProxyType#INTERNET}</b>
     * <p>
     * <b>{@link TlsUpgrade#ENABLED} only works for {@link ProxyType#ON_PREMISE}</b>
     *
     * @since 5.14.0
     */
    @Setter
    @Nonnull
    private TlsUpgrade tlsUpgrade = TlsUpgrade.AUTOMATIC;

    /**
     * The {@link ConnectionPoolSettings} to use for configuring connection pool managers and request timeouts.
     * <p>
     * This replaces any previously configured settings from {@link #timeout(Duration)},
     * {@link #maxConnectionsTotal(int)}, or {@link #maxConnectionsPerRoute(int)}.
     * </p>
     * <p>
     * This is an <b>optional</b> parameter. By default, settings use the default values.
     * </p>
     *
     * @see DefaultConnectionPoolSettings#ofDefaults()
     * @see DefaultConnectionPoolSettings#builder()
     * @since 5.XX.0
     */
    @Setter( onMethod_ = @Beta )
    @Nonnull
    private DefaultConnectionPoolSettings settings = DefaultConnectionPoolSettings.ofDefaults();

    /**
     * A custom {@link ConnectionPoolManagerProvider} for creating and managing HTTP connection pool managers.
     * <p>
     * This allows customization of how connection managers are created and cached. Use
     * {@link ConnectionPoolManagerProviders} to obtain pre-built implementations with common caching strategies:
     * </p>
     * <ul>
     * <li>{@link ConnectionPoolManagerProviders#noCache()} - No caching (default behavior)</li>
     * <li>{@link ConnectionPoolManagerProviders#byTenant()} - Cache by current tenant</li>
     * <li>{@link ConnectionPoolManagerProviders#byDestinationName()} - Cache by destination name</li>
     * <li>{@link ConnectionPoolManagerProviders#global()} - Single global connection manager</li>
     * <li>{@link ConnectionPoolManagerProviders#withCacheKey(java.util.function.Function)} - Custom cache key</li>
     * </ul>
     * <p>
     * This is an <b>optional</b> parameter. By default, a new connection manager is created for each HTTP client
     * without caching.
     * </p>
     *
     * <h3>Example Usage</h3>
     *
     * <pre>
     * {@code
     * // Cache connection managers by tenant to reduce memory consumption
     * ApacheHttpClient5Factory factory =
     *     new ApacheHttpClient5FactoryBuilder()
     *         .connectionPoolManagerProvider(ConnectionPoolManagerProviders.byTenant())
     *         .build();
     * }
     * </pre>
     *
     * @see ConnectionPoolManagerProvider
     * @see ConnectionPoolManagerProviders
     * @since 5.XX.0
     */
    @Setter( onMethod_ = @Beta )
    @Nonnull
    private ConnectionPoolManagerProvider connectionPoolManagerProvider = ConnectionPoolManagerProviders.noCache();

    /**
     * Enum to control the automatic TLS upgrade feature for insecure connections.
     *
     * @since 5.14.0
     */
    @Beta
    public enum TlsUpgrade
    {
        /**
         * Automatic TLS upgrade is enabled.
         */
        ENABLED,
        /**
         * Automatic TLS upgrade is disabled.
         */
        DISABLED,
        /**
         * Automatic TLS upgrade is enabled only for {@link ProxyType#INTERNET}, default.
         */
        AUTOMATIC
    }

    /**
     * Sets the timeout {@link HttpClient} instances created by the to-be-built {@link ApacheHttpClient5Factory} should
     * use. This timeout applies to the following concerns:
     * <ul>
     * <li>Connection timeout: The time to establish the connection with the remote host.</li>
     * <li>Socket timeout: The time to wait for data – after the connection was established; maximum time of inactivity
     * between two data packets.</li>
     * <li>Connection Request timeout: The time to wait when requesting a connection lease from the underlying
     * {@link org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager}.</li>
     * </ul>
     * <p>
     * This is an <b>optional</b> parameter. By default, the timeout is set to 2 minutes.
     * </p>
     *
     * @param timeoutInMilliseconds
     *            The timeout in milliseconds.
     * @return This builder.
     */
    @Nonnull
    public ApacheHttpClient5FactoryBuilder timeoutInMilliseconds( final int timeoutInMilliseconds )
    {
        return timeout(Duration.ofMillis(timeoutInMilliseconds));
    }

    /**
     * Sets the timeout {@link HttpClient} instances created by the to-be-built {@link ApacheHttpClient5Factory} should
     * use. This timeout applies to the following concerns:
     * <ul>
     * <li>Connection timeout: The time to establish the connection with the remote host.</li>
     * <li>Socket timeout: The time to wait for data – after the connection was established; maximum time of inactivity
     * between two data packets.</li>
     * <li>Connection Request timeout: The time to wait when requesting a connection lease from the underlying
     * {@link org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager}.</li>
     * </ul>
     * <p>
     * This is an <b>optional</b> parameter. By default, the timeout is set to 2 minutes.
     * </p>
     *
     * @param timeout
     *            The timeout to use.
     * @return This builder.
     */
    @Nonnull
    public ApacheHttpClient5FactoryBuilder timeout( @Nonnull final Duration timeout )
    {
        settings =
            settings.withConnectTimeout(timeout).withSocketTimeout(timeout).withConnectionRequestTimeout(timeout);
        return this;
    }

    /**
     * Sets the maximum number of parallel connections that can be established with a {@link HttpClient} created by the
     * to-be-built {@link ApacheHttpClient5Factory}.
     * <p>
     * This is an <b>optional</b> parameter. By default, the maximum number of parallel connections is set to 200.
     * </p>
     *
     * @param maxConnectionsTotal
     *            The maximum number of parallel connections.
     * @return This builder.
     */
    @Nonnull
    public ApacheHttpClient5FactoryBuilder maxConnectionsTotal( final int maxConnectionsTotal )
    {
        settings = settings.withMaxConnectionsTotal(maxConnectionsTotal);
        return this;
    }

    /**
     * Sets the maximum number of parallel connections <b>per route</b> (e.g. per remote host) that can be established
     * with a {@link HttpClient} created by the to-be-built {@link ApacheHttpClient5Factory}.
     * <p>
     * This is an <b>optional</b> parameter. By default, the maximum number of parallel connections per route is set to
     * 100.
     * </p>
     *
     * @param maxConnectionsPerRoute
     *            The maximum number of parallel connections per route.
     * @return This builder.
     */
    @Nonnull
    public ApacheHttpClient5FactoryBuilder maxConnectionsPerRoute( final int maxConnectionsPerRoute )
    {
        settings = settings.withMaxConnectionsPerRoute(maxConnectionsPerRoute);
        return this;
    }

    /**
     * Builds a new {@link ApacheHttpClient5Factory} instance with the previously configured parameters.
     *
     * @return A new {@link ApacheHttpClient5Factory} instance.
     */
    @Nonnull
    public ApacheHttpClient5Factory build()
    {
        return new DefaultApacheHttpClient5Factory(settings, connectionPoolManagerProvider, null, tlsUpgrade);
    }
}
