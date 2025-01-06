/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

import lombok.SneakyThrows;

@WireMockTest
class DefaultHttpClientFactoryTest
{
    @Test
    void testProxy()
        throws KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException,
            IOException
    {
        final DefaultHttpClientFactory builder = new DefaultHttpClientFactory();

        // simple HTTP Client
        {
            assertThat(builder.createHttpClient()).isNotNull();
        }

        // HTTP Client derived from destination
        {
            final String pass = "some password";

            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, pass.toCharArray());

            final KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
            ts.load(null, pass.toCharArray());

            final DefaultHttpDestination destination =
                DefaultHttpDestination
                    .builder("https://foo")
                    .keyStore(ks)
                    .keyStorePassword(pass)
                    .trustStore(ts)
                    .trustStorePassword(pass)
                    .build();

            assertThat(builder.createHttpClient(destination)).isNotNull();
        }
    }

    @Test
    void testFactoryBuilderDefault()
    {
        final DefaultHttpClientFactory defaultA = new DefaultHttpClientFactory();

        final DefaultHttpClientFactory defaultB = DefaultHttpClientFactory.builder().build();

        final DefaultHttpClientFactory defaultC =
            DefaultHttpClientFactory
                .builder()
                .timeoutMilliseconds(DefaultHttpClientFactory.DEFAULT_TIMEOUT_MINUTES * 60 * 1000)
                .maxConnectionsTotal(DefaultHttpClientFactory.DEFAULT_MAX_TOTAL_CONNECTIONS)
                .maxConnectionsPerRoute(DefaultHttpClientFactory.DEFAULT_MAX_CONNECTIONS_PER_ROUTE)
                .build();

        final DefaultHttpClientFactory defaultD = new DefaultHttpClientFactory()
        {
            @Override
            protected int getTimeoutMilliseconds()
            {
                return DefaultHttpClientFactory.DEFAULT_TIMEOUT_MINUTES * 60 * 1000;
            }

            @Override
            protected int getMaxConnectionsTotal()
            {
                return DefaultHttpClientFactory.DEFAULT_MAX_TOTAL_CONNECTIONS;
            }

            @Override
            protected int getMaxConnectionsPerRoute()
            {
                return DefaultHttpClientFactory.DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
            }
        };

        assertThat(list(defaultA, defaultB, defaultC, defaultD))
            .extracting(DefaultHttpClientFactory::getTimeoutMilliseconds)
            .containsOnly(DefaultHttpClientFactory.DEFAULT_TIMEOUT_MINUTES * 60 * 1000);

        assertThat(list(defaultA, defaultB, defaultC, defaultD))
            .extracting(DefaultHttpClientFactory::getMaxConnectionsPerRoute)
            .containsOnly(DefaultHttpClientFactory.DEFAULT_MAX_CONNECTIONS_PER_ROUTE);

        assertThat(list(defaultA, defaultB, defaultD))
            .extracting(DefaultHttpClientFactory::getMaxConnectionsTotal)
            .containsOnly(DefaultHttpClientFactory.DEFAULT_MAX_TOTAL_CONNECTIONS);
    }

    @Test
    void testFactoryBuilderCustom()
    {
        final DefaultHttpClientFactory customA =
            DefaultHttpClientFactory
                .builder()
                .timeoutMilliseconds(100)
                .maxConnectionsTotal(5)
                .maxConnectionsPerRoute(2)
                .build();

        final DefaultHttpClientFactory customB = new DefaultHttpClientFactory()
        {
            @Override
            protected int getTimeoutMilliseconds()
            {
                return 100;
            }

            @Override
            protected int getMaxConnectionsTotal()
            {
                return 5;
            }

            @Override
            protected int getMaxConnectionsPerRoute()
            {
                return 2;
            }
        };

        assertThat(list(customA, customB))
            .extracting(DefaultHttpClientFactory::getTimeoutMilliseconds)
            .containsOnly(100);
        assertThat(list(customA, customB))
            .extracting(DefaultHttpClientFactory::getMaxConnectionsPerRoute)
            .containsOnly(2);
        assertThat(list(customA, customB)).extracting(DefaultHttpClientFactory::getMaxConnectionsTotal).containsOnly(5);
    }

    @Test
    void testFactoryBuilderProxy( @Nonnull final WireMockRuntimeInfo wm )
        throws IOException
    {
        final String path = "/proxy";
        stubFor(get(urlEqualTo(path)).willReturn(ok()));

        final String destinationUri = "http://destination";
        final DefaultHttpDestination httpDestination = DefaultHttpDestination.builder(destinationUri).build();

        final DefaultHttpClientFactory customFactory = new DefaultHttpClientFactory()
        {
            @Nonnull
            @Override
            protected RequestConfig.Builder getRequestConfigBuilder(
                @Nullable final HttpDestinationProperties destination )
            {
                return super.getRequestConfigBuilder(destination)
                    .setProxy(new HttpHost("127.0.0.1", wm.getHttpPort(), "http"));
            }
        };

        final HttpClient httpClient = customFactory.createHttpClient(httpDestination);
        final HttpResponse response = httpClient.execute(new HttpGet(path));
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    void testFactoryBuilderCustomBuilder( @Nonnull final WireMockRuntimeInfo wm )
        throws IOException
    {
        final String path = "/custom-builder";
        stubFor(get(urlEqualTo(path)).withHeader("User-Agent", equalTo("SDK")).willReturn(ok().withFixedDelay(200)));

        final DefaultHttpClientFactory customFactory = new DefaultHttpClientFactory()
        {
            @Nonnull
            @Override
            protected HttpClientBuilder getHttpClientBuilder( @Nullable final HttpDestinationProperties destination )
            {
                return super.getHttpClientBuilder(destination).setUserAgent("SDK");
            }
        };

        final HttpClient httpClient = customFactory.createHttpClient();
        final HttpResponse response = httpClient.execute(new HttpGet(wm.getHttpBaseUrl() + path));
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    @SneakyThrows
    void testProxyConfigurationIsConsidered( @Nonnull final WireMockRuntimeInfo wm )
    {
        final StubMapping stub = stubFor(get(urlEqualTo("/proxy-with-auth")).willReturn(ok()));

        final BasicCredentials credentials = new BasicCredentials("user", "pass");

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder("http://www.sap.com")
                .proxyConfiguration(ProxyConfiguration.of(wm.getHttpBaseUrl(), credentials))
                .build();
        final DefaultHttpDestination spiedDestination = spy(destination);

        final DefaultHttpClientFactory sut = new DefaultHttpClientFactory();

        final HttpClient httpClient = sut.createHttpClient(spiedDestination);
        Mockito.verify(spiedDestination, atLeastOnce()).getProxyConfiguration();

        final HttpResponse response = httpClient.execute(new HttpGet("/proxy-with-auth"));
        final List<ServeEvent> events = getAllServeEvents(ServeEventQuery.forStubMapping(stub));

        assertThat(events).hasSize(1);
        assertThat(events.get(0)).satisfies(event -> {
            assertThat(event.getRequest().getHeaders().keys())
                .containsExactlyInAnyOrder(
                    HttpHeaders.HOST,
                    "Proxy-Connection",
                    HttpHeaders.USER_AGENT,
                    HttpHeaders.ACCEPT_ENCODING,
                    HttpHeaders.PROXY_AUTHORIZATION);
            assertThat(event.getRequest().getHeader(HttpHeaders.PROXY_AUTHORIZATION))
                .contains(credentials.getHttpHeaderValue());
        });
    }
}
