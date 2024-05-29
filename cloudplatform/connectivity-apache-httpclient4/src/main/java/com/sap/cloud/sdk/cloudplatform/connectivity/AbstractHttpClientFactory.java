/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract implementation of {@link HttpClientFactory}.
 */
@Slf4j
public abstract class AbstractHttpClientFactory implements HttpClientFactory
{
    @Nonnull
    @Override
    public HttpClient createHttpClient( @Nullable final HttpDestinationProperties destination )
        throws DestinationAccessException,
            HttpClientInstantiationException
    {
        final HttpClientBuilder clientBuilder = getHttpClientBuilder(destination);
        final CloseableHttpClient httpClient = clientBuilder.build();
        if( destination != null ) {
            return new HttpClientWrapper(httpClient, destination);
        }
        return httpClient;
    }

    /**
     * Get the request configuration builder for the HTTP client builder.
     *
     * @param destination
     *            The optional destination reference.
     * @return A request configuration builder reference.
     */
    @Nonnull
    protected RequestConfig.Builder getRequestConfigBuilder( @Nullable final HttpDestinationProperties destination )
    {
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        if( isValidProxyConfigurationUriInDestination(destination) ) {
            final URI proxyUri = Objects.requireNonNull(destination).getProxyConfiguration().get().getUri();
            final HttpHost proxyHost = new HttpHost(proxyUri.getHost(), proxyUri.getPort(), proxyUri.getScheme());

            log
                .debug(
                    "Using the following proxy for destination {} pointing to URL {}: {}.",
                    destination.get(DestinationProperty.NAME).getOrElse("without name"),
                    destination.getUri(),
                    proxyUri);

            requestConfigBuilder.setProxy(proxyHost);
        }

        return requestConfigBuilder;
    }

    /**
     * Get the socket configuration builder for the HTTP client builder.
     *
     * @param destination
     *            The optional destination reference.
     * @return A socket configuration builder reference.
     */
    @Nonnull
    protected SocketConfig.Builder getSocketConfigBuilder( @Nullable final HttpDestinationProperties destination )
    {
        return SocketConfig.custom();
    }

    /**
     * Get a preconfigured HTTP client builder instance.
     *
     * @param destination
     *            The optional destination reference to create a client builder for.
     * @return A new and preconfigured instance of HTTP client builder.
     * @throws HttpClientInstantiationException
     *             if there occurred an error during the configuration of the client builder.
     */
    protected HttpClientBuilder getHttpClientBuilder( @Nullable final HttpDestinationProperties destination )
        throws HttpClientInstantiationException
    {
        try {
            final HttpClientBuilder clientBuilder = createHttpClientBuilder();
            clientBuilder.setDefaultRequestConfig(getRequestConfigBuilder(destination).build());
            clientBuilder.setDefaultSocketConfig(getSocketConfigBuilder(destination).build());

            // Note: Applying a ConnectionManager to HttpClientBuilder overrides previously set properties:
            //       DnsResolver, HostnameVerifier, SSLHostnameVerifier, SSLContext, SSLSocketFactory, MaxConnTotal,
            //       MaxConnPerRoute, DefaultSocketConfig, DefaultConnectionConfig, ConnectionTimeToLive
            clientBuilder.setConnectionManager(getConnectionManager(destination));

            return clientBuilder;
        }
        catch( final IOException | GeneralSecurityException | IllegalArgumentException e ) {
            log
                .error(
                    "Failed to instantiate an {} builder for destination {}",
                    HttpClient.class.getSimpleName(),
                    destination,
                    e);
            throw new HttpClientInstantiationException(e);
        }
    }

    /**
     * Get the connection manager for the HTTP client builder.
     * <p>
     * <strong>Note:</strong> Since the destination may have custom TLS/SSL settings, the returned connection manager
     * shall not be cached or reused. As a result the cardinality is: One {@code HttpClient} to one
     * {@code HttpClientConnectionManager}.
     *
     * @param destination
     *            The optional destination reference.
     * @return An optional connection manager instance.
     * @throws IOException
     *             If trust store or key store could not be loaded.
     * @throws GeneralSecurityException
     *             If trust store or key store could not be loaded.
     */
    @Nonnull
    protected PoolingHttpClientConnectionManager getConnectionManager(
        @Nullable final HttpDestinationProperties destination )
        throws GeneralSecurityException,
            IOException
    {
        final Registry<ConnectionSocketFactory> connectionSocketFactoryRegistry =
            SSLSocketFactoryUtil.getConnectionSocketFactoryRegistry(destination);

        if( connectionSocketFactoryRegistry != null ) {
            return new PoolingHttpClientConnectionManager(connectionSocketFactoryRegistry);
        }
        return new PoolingHttpClientConnectionManager();
    }

    /**
     * The {@link HttpClientBuilder} reference to create instances of {@link HttpClient} from.
     *
     * @return The HTTP client builder.
     */
    @Nonnull
    private HttpClientBuilder createHttpClientBuilder()
    {
        log.debug("Building a new custom HttpClient.");

        return HttpClients.custom();
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
