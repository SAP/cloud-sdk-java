package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * Default implementation of {@link ConnectionPoolSettings} using Lombok's {@code @Value} for immutability.
 * <p>
 * Use {@link #ofDefaults()} to create an instance with default values, or {@link #builder()} to create instances with
 * custom values.
 * </p>
 *
 * <h2>Example Usage</h2>
 *
 * <pre>
 * {@code
 * // Using defaults
 * ConnectionPoolSettings settings = DefaultConnectionPoolSettings.ofDefaults();
 *
 * // Using builder
 * ConnectionPoolSettings settings =
 *     DefaultConnectionPoolSettings
 *         .builder()
 *         .connectTimeout(Duration.ofSeconds(30))
 *         .socketTimeout(Duration.ofMinutes(1))
 *         .maxConnectionsTotal(500)
 *         .build();
 *
 * // Using with() for copy-with-modification
 * DefaultConnectionPoolSettings modified = settings.withMaxConnectionsTotal(1000);
 * }
 * </pre>
 *
 * @see ConnectionPoolSettings
 * @since 5.27.0
 */
@Beta
@Value
@Builder
@With
public class DefaultConnectionPoolSettings implements ConnectionPoolSettings
{
    /**
     * The timeout until a new connection is fully established.
     */
    @Nonnull
    @Builder.Default
    Duration connectTimeout = ConnectionPoolSettings.DEFAULT_TIMEOUT;

    /**
     * The default socket timeout value for I/O operations on connections.
     */
    @Nonnull
    @Builder.Default
    Duration socketTimeout = ConnectionPoolSettings.DEFAULT_TIMEOUT;

    /**
     * The timeout when requesting a connection lease from the connection pool.
     */
    @Nonnull
    @Builder.Default
    Duration connectionRequestTimeout = ConnectionPoolSettings.DEFAULT_TIMEOUT;

    /**
     * The maximum number of total connections in the pool.
     */
    @Builder.Default
    int maxConnectionsTotal = ConnectionPoolSettings.DEFAULT_MAX_CONNECTIONS_TOTAL;

    /**
     * The maximum number of connections per route (e.g., per remote host).
     */
    @Builder.Default
    int maxConnectionsPerRoute = ConnectionPoolSettings.DEFAULT_MAX_CONNECTIONS_PER_ROUTE;

    /**
     * Creates a new {@link DefaultConnectionPoolSettings} with default values.
     * <p>
     * Default values:
     * <ul>
     * <li>Connect timeout: 2 minutes</li>
     * <li>Socket timeout: 2 minutes</li>
     * <li>Connection request timeout: 2 minutes</li>
     * <li>Max connections total: 200</li>
     * <li>Max connections per route: 100</li>
     * </ul>
     * </p>
     *
     * @return A new instance with default settings.
     */
    @Nonnull
    public static DefaultConnectionPoolSettings ofDefaults()
    {
        return builder().build();
    }
}
