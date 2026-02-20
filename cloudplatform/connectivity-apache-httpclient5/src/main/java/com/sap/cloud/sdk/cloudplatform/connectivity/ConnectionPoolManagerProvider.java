package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.io.HttpClientConnectionManager;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

/**
 * Functional interface for creating or retrieving {@link HttpClientConnectionManager} instances.
 * <p>
 * Implementations can choose to cache connection managers based on various strategies (e.g., by tenant, by destination
 * name, globally) to reduce memory consumption. Each connection manager typically consumes around 100KB of memory.
 * </p>
 * <p>
 * Use {@link ConnectionPoolManagerProviders} to obtain pre-built implementations with common caching strategies.
 * </p>
 *
 * <h2>Example Usage</h2>
 *
 * <pre>
 * {@code
 * // Simple lambda implementation (no caching)
 * ConnectionPoolManagerProvider provider =
 *     ( settings, dest ) -> PoolingHttpClientConnectionManagerBuilder
 *         .create()
 *         .setMaxConnTotal(settings.maxConnectionsTotal())
 *         .build();
 *
 * // Using pre-built providers
 * ConnectionPoolManagerProvider cachingProvider = ConnectionPoolManagerProviders.byTenant();
 * }
 * </pre>
 *
 * @see ConnectionPoolManagerProviders
 * @see ApacheHttpClient5FactoryBuilder#connectionPoolManagerProvider(ConnectionPoolManagerProvider)
 * @since 5.27.0
 */
@Beta
@FunctionalInterface
public interface ConnectionPoolManagerProvider
{
    /**
     * Gets or creates an {@link HttpClientConnectionManager} for the given destination.
     * <p>
     * Implementations may cache connection managers based on destination properties, tenant context, or other criteria.
     * The settings parameter provides the configuration for creating new connection managers.
     * </p>
     *
     * @param settings
     *            The connection pool settings to use when creating a new connection manager.
     * @param destination
     *            The destination properties to create the connection manager for, or {@code null} for a generic
     *            connection manager.
     * @return A connection manager suitable for the given destination.
     * @throws HttpClientInstantiationException
     *             If the connection manager cannot be created.
     */
    @Nonnull
    HttpClientConnectionManager getConnectionManager(
        @Nonnull ConnectionPoolSettings settings,
        @Nullable HttpDestinationProperties destination )
        throws HttpClientInstantiationException;
}
