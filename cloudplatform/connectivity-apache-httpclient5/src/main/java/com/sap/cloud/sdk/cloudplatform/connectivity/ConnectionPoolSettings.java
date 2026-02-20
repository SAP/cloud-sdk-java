package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

/**
 * Configuration settings for HTTP connection pool managers and HTTP client request configuration.
 * <p>
 * These settings control the behavior of {@link org.apache.hc.client5.http.io.HttpClientConnectionManager} instances
 * created by {@link ConnectionPoolManagerProvider} implementations, as well as the request-level timeout configuration.
 * </p>
 * <p>
 * Use {@link DefaultConnectionPoolSettings#ofDefaults()} or {@link DefaultConnectionPoolSettings#builder()} to create
 * instances. Users can also implement this interface to provide custom settings implementations.
 * </p>
 *
 * @see ConnectionPoolManagerProviders
 * @see DefaultConnectionPoolSettings
 * @since 5.27.0
 */
@Beta
public interface ConnectionPoolSettings
{
    /**
     * Default timeout of 2 minutes (applies to connect, socket, and connection request timeouts).
     */
    Duration DEFAULT_TIMEOUT = Duration.ofMinutes(2L);

    /**
     * Default maximum total connections of 200.
     */
    int DEFAULT_MAX_CONNECTIONS_TOTAL = 200;

    /**
     * Default maximum connections per route of 100.
     */
    int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 100;

    /**
     * Returns the timeout until a new connection is fully established.
     *
     * @return The connect timeout.
     */
    @Nonnull
    Duration getConnectTimeout();

    /**
     * Returns the default socket timeout value for I/O operations on connections.
     *
     * @return The socket timeout.
     */
    @Nonnull
    Duration getSocketTimeout();

    /**
     * Returns the timeout when requesting a connection lease from the connection pool.
     *
     * @return The connection request timeout.
     */
    @Nonnull
    Duration getConnectionRequestTimeout();

    /**
     * Returns the maximum number of total connections in the pool.
     *
     * @return The maximum total connections.
     */
    int getMaxConnectionsTotal();

    /**
     * Returns the maximum number of connections per route (e.g., per remote host).
     *
     * @return The maximum connections per route.
     */
    int getMaxConnectionsPerRoute();
}
