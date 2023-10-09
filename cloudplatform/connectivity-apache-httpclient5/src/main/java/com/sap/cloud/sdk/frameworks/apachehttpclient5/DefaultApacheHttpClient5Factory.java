/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.frameworks.apachehttpclient5;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DefaultApacheHttpClient5Factory implements ApacheHttpClient5Factory
{
    static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(2L);
    static final int DEFAULT_MAX_CONNECTIONS_TOTAL = 200;
    static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 100;

    @Nonnull
    private final Timeout timeout;
    private final int maxConnectionsTotal;
    private final int maxConnectionsPerRoute;
    // for testing purposes
    @Nullable
    private final HttpRequestInterceptor requestInterceptor;

    DefaultApacheHttpClient5Factory(
        @Nonnull final Duration timeout,
        final int maxConnectionsTotal,
        final int maxConnectionsPerRoute )
    {
        this(timeout, maxConnectionsTotal, maxConnectionsPerRoute, null);
    }

    DefaultApacheHttpClient5Factory(
        @Nonnull final Duration timeout,
        final int maxConnectionsTotal,
        final int maxConnectionsPerRoute,
        @Nullable final HttpRequestInterceptor requestInterceptor )
    {
        this.timeout = toTimeout(timeout);
        this.maxConnectionsTotal = maxConnectionsTotal;
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        this.requestInterceptor = requestInterceptor;
    }

    @Nonnull
    @Override
    public HttpClient createHttpClient( @Nullable final HttpDestinationProperties destination )
        throws DestinationAccessException,
            HttpClientInstantiationException
    {
        final CloseableHttpClient httpClient = buildHttpClient(destination);
        if( destination == null ) {
            return httpClient;
        }

        return new ApacheHttpClient5Wrapper(httpClient, destination);
    }

    @Nonnull
    private CloseableHttpClient buildHttpClient( @Nullable final HttpDestinationProperties destination )
    {
        final HttpClientBuilder builder =
            HttpClients
                .custom()
                .setConnectionManager(getConnectionManager(destination))
                .setDefaultRequestConfig(getRequestConfig())
                .setProxy(getProxy(destination));

        if( requestInterceptor != null ) {
            builder.addRequestInterceptorFirst(requestInterceptor);
        }

        return builder.build();
    }

    @Nonnull
    private HttpClientConnectionManager getConnectionManager( @Nullable final HttpDestinationProperties destination )
    {
        try {
            return PoolingHttpClientConnectionManagerBuilder
                .create()
                .setSSLSocketFactory(getConnectionSocketFactory(destination))
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(timeout).build())
                .setDefaultConnectionConfig(
                    ConnectionConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build())
                .setMaxConnTotal(maxConnectionsTotal)
                .setMaxConnPerRoute(maxConnectionsPerRoute)
                .build();
        }
        catch( final GeneralSecurityException | IOException e ) {
            throw new HttpClientInstantiationException("Failed to create HTTP client connection manager.", e);
        }
    }

    @Nonnull
    private static Timeout toTimeout( @Nonnull final Duration duration )
    {
        return Timeout.ofMilliseconds(duration.toMillis());
    }

    @Nullable
    private SSLConnectionSocketFactory getConnectionSocketFactory(
        @Nullable final HttpDestinationProperties destination )
        throws GeneralSecurityException,
            IOException
    {
        if( !supportsTls(destination) ) {
            return null;
        }

        log.debug("The destination uses HTTPS for target \"{}\".", destination.getUri());
        final SSLContext sslContext = new SSLContextFactory().createSSLContext(destination);

        final HostnameVerifier hostnameVerifier = getHostnameVerifier(destination);

        return new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
    }

    private boolean supportsTls( @Nullable final HttpDestinationProperties destination )
    {
        if( destination == null ) {
            return false;
        }
        final String scheme = destination.getUri().getScheme();
        return "https".equalsIgnoreCase(scheme) || StringUtils.isEmpty(scheme);
    }

    private HostnameVerifier getHostnameVerifier( final HttpDestinationProperties destination )
    {
        return destination.isTrustingAllCertificates() ? new NoopHostnameVerifier() : new DefaultHostnameVerifier();
    }

    @Nonnull
    private RequestConfig getRequestConfig()
    {
        return RequestConfig.custom().setConnectionRequestTimeout(timeout).build();
    }

    @Nullable
    private HttpHost getProxy( @Nullable final HttpDestinationProperties destination )
    {
        if( !isValidProxyConfigurationUriInDestination(destination) ) {
            return null;
        }

        final URI proxyUri = Objects.requireNonNull(destination).getProxyConfiguration().get().getUri();
        final HttpHost proxyHost = new HttpHost(proxyUri.getScheme(), proxyUri.getHost(), proxyUri.getPort());

        log
            .debug(
                "Using the following proxy for destination {} pointing to URL {}: {}.",
                destination.get(DestinationProperty.NAME).getOrElse("without name"),
                destination.getUri(),
                proxyUri);

        return proxyHost;
    }

    private boolean isValidProxyConfigurationUriInDestination( @Nullable final HttpDestinationProperties destination )
    {
        if( destination == null ) {
            log.trace("No destination defined for HTTP client builder.");
            return false;
        }
        final Option<ProxyConfiguration> proxyConfiguration = destination.getProxyConfiguration();
        if( proxyConfiguration.isEmpty() ) {
            log.trace("No proxy configuration found for destination {}.", destination);
            return false;
        }

        final URI uri = proxyConfiguration.get().getUri();
        final String undefinedProxyMayBeExpectedMessage =
            "Failed to load proxy information for destination {}: Undefined {} in URI of proxy configuration. This behavior may be expected in tests and some local runtimes.";

        final String host = uri.getHost();
        if( host == null ) {
            log.error(undefinedProxyMayBeExpectedMessage, destination, "host");
            return false;
        }

        final int port = uri.getPort();
        if( port < 0 ) {
            log.error(undefinedProxyMayBeExpectedMessage, destination, "port");
            return false;
        }
        return true;
    }
}
