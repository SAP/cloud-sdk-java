package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.util.Timeout;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class DefaultApacheHttpClient5Factory implements ApacheHttpClient5Factory
{
    @Nonnull
    private final ConnectionPoolSettings settings;

    @Nonnull
    private final ConnectionPoolManagerProvider connectionPoolManagerProvider;

    @Nullable
    private final HttpRequestInterceptor requestInterceptor;

    @Nonnull
    private final ApacheHttpClient5FactoryBuilder.TlsUpgrade tlsUpgrade;

    @Nonnull
    @Override
    public HttpClient createHttpClient( @Nullable final HttpDestinationProperties destination )
        throws DestinationAccessException,
            HttpClientInstantiationException
    {
        final var requestConfig = getRequestConfig(destination);
        final CloseableHttpClient httpClient = buildHttpClient(destination, requestConfig);
        if( destination == null ) {
            return httpClient;
        }

        return new ApacheHttpClient5Wrapper(httpClient, destination, requestConfig);
    }

    @Nonnull
    private CloseableHttpClient buildHttpClient(
        @Nullable final HttpDestinationProperties destination,
        @Nonnull final RequestConfig requestConfig )
    {
        final HttpClientConnectionManager connManager =
            connectionPoolManagerProvider.getConnectionManager(settings, destination);

        final HttpClientBuilder builder =
            HttpClients
                .custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .setProxy(getProxy(destination));

        if( requestInterceptor != null ) {
            builder.addRequestInterceptorFirst(requestInterceptor);
        }

        return builder.build();
    }

    @Nonnull
    private RequestConfig getRequestConfig( @Nullable final HttpDestinationProperties destination )
    {
        return RequestConfig
            .custom()
            .setProtocolUpgradeEnabled(isProtocolUpgradeEnabled(destination))
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(settings.getConnectionRequestTimeout().toMillis()))
            .build();
    }

    private boolean isProtocolUpgradeEnabled( @Nullable final HttpDestinationProperties destination )
    {
        return switch( tlsUpgrade ) {
            case ENABLED -> true;
            case DISABLED -> false;
            case AUTOMATIC -> {
                if( destination == null ) {
                    yield true;
                }
                if( destination.getTlsVersion().isDefined() ) {
                    yield false;
                }
                yield !destination.getProxyType().contains(ProxyType.ON_PREMISE);
            }
        };
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
