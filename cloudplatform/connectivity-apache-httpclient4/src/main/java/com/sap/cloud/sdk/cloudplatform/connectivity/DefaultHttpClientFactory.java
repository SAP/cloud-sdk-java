/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of {@link HttpClientFactory}.
 */
@NoArgsConstructor
@AllArgsConstructor( access = AccessLevel.PRIVATE )
@Slf4j
@Builder
public class DefaultHttpClientFactory extends AbstractHttpClientFactory
{
    static final int DEFAULT_TIMEOUT_MINUTES = 2;
    static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 200;
    static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 100;

    /**
     * The timeout threshold.
     */
    @Builder.Default
    @Getter( AccessLevel.PROTECTED )
    private final int timeoutMilliseconds = (int) TimeUnit.MINUTES.toMillis(DEFAULT_TIMEOUT_MINUTES);

    /**
     * The maximum number of connections per route.
     */
    @Builder.Default
    @Getter( AccessLevel.PROTECTED )
    private final int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;

    /**
     * The maximum number of connections in total.
     */
    @Builder.Default
    @Getter( AccessLevel.PROTECTED )
    private final int maxConnectionsTotal = DEFAULT_MAX_TOTAL_CONNECTIONS;

    @Nonnull
    @Override
    protected RequestConfig.Builder getRequestConfigBuilder( @Nullable final HttpDestinationProperties destination )
    {
        return super.getRequestConfigBuilder(destination)
            .setConnectTimeout(getTimeoutMilliseconds()) // until a connection is established
            .setConnectionRequestTimeout(getTimeoutMilliseconds()) // when requesting a connection from the connection manager
            .setSocketTimeout(getTimeoutMilliseconds()); // maximum period inactivity between two consecutive data packets
    }

    @Nonnull
    @Override
    protected SocketConfig.Builder getSocketConfigBuilder( @Nullable final HttpDestinationProperties destination )
    {
        return super.getSocketConfigBuilder(destination).setSoTimeout(getTimeoutMilliseconds());
    }

    @Nonnull
    @Override
    protected PoolingHttpClientConnectionManager getConnectionManager(
        @Nullable final HttpDestinationProperties destination )
        throws GeneralSecurityException,
            IOException
    {
        final PoolingHttpClientConnectionManager connectionManager = super.getConnectionManager(destination);
        connectionManager.setMaxTotal(getMaxConnectionsTotal());
        connectionManager.setDefaultMaxPerRoute(getMaxConnectionsPerRoute());
        return connectionManager;
    }
}
