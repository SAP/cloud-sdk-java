/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.HttpClient;

import com.google.common.annotations.Beta;

/**
 * Builder class for a default implementation of the {@link ApacheHttpClient5Factory} interface.
 *
 * @since 4.20.0
 */
public class ApacheHttpClient5FactoryBuilder
{
    @Nonnull
    private Duration timeout = DefaultApacheHttpClient5Factory.DEFAULT_TIMEOUT;
    private TlsUpgrade tlsUpgrade = TlsUpgrade.INTERNET;
    private int maxConnectionsTotal = DefaultApacheHttpClient5Factory.DEFAULT_MAX_CONNECTIONS_TOTAL;
    private int maxConnectionsPerRoute = DefaultApacheHttpClient5Factory.DEFAULT_MAX_CONNECTIONS_PER_ROUTE;

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
        INTERNET;
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
        this.timeout = timeout;
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
        this.maxConnectionsTotal = maxConnectionsTotal;
        return this;
    }

    /**
     * Sets the automatic TLS upgrade strategy. This strategy controls whether insecure connections should be
     * automatically upgraded.
     *
     * @since 5.14.0
     */
    @Beta
    @Nonnull
    public ApacheHttpClient5FactoryBuilder tlsUpgrade( @Nonnull final TlsUpgrade tlsUpgrade )
    {
        this.tlsUpgrade = tlsUpgrade;
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
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
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
        return new DefaultApacheHttpClient5Factory(
            timeout,
            maxConnectionsTotal,
            maxConnectionsPerRoute,
            null,
            tlsUpgrade);
    }
}
