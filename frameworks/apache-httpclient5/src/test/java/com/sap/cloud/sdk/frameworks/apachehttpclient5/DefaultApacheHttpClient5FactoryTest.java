package com.sap.cloud.sdk.frameworks.apachehttpclient5;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.RouteInfo;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyConfiguration;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

import io.vavr.control.Option;
import lombok.SneakyThrows;

public class DefaultApacheHttpClient5FactoryTest
{
    private static final int TEST_TIMEOUT = 300_000;
    private static final Duration CLIENT_TIMEOUT = Duration.ofSeconds(10L);
    private static final int MAX_CONNECTIONS = 10;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 5;

    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());
    @Rule
    public final WireMockRule secondWireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    private SoftAssertions softly;
    private ApacheHttpClient5Factory sut;
    private HttpRequestInterceptor requestInterceptor;

    @Before
    @SneakyThrows
    public void setup()
    {
        softly = new SoftAssertions();

        requestInterceptor = mock(HttpRequestInterceptor.class);
        doNothing().when(requestInterceptor).process(any(), any(), any());

        sut =
            new DefaultApacheHttpClient5Factory(
                CLIENT_TIMEOUT,
                MAX_CONNECTIONS,
                MAX_CONNECTIONS_PER_ROUTE,
                requestInterceptor);
    }

    @Test
    @SneakyThrows
    public void testHttpClientUsesTimeout()
    {
        wireMockServer.stubFor(get(urlEqualTo("/timeout")).willReturn(ok().withFixedDelay(5_000)));

        final ApacheHttpClient5Factory factoryWithTooLittleTimeout =
            new DefaultApacheHttpClient5Factory(
                Duration.ofSeconds(3L),
                MAX_CONNECTIONS,
                MAX_CONNECTIONS_PER_ROUTE,
                requestInterceptor);

        final ApacheHttpClient5Factory factoryWithEnoughTimeout =
            new DefaultApacheHttpClient5Factory(
                Duration.ofSeconds(7L),
                MAX_CONNECTIONS,
                MAX_CONNECTIONS_PER_ROUTE,
                requestInterceptor);

        final ClassicHttpRequest request = new HttpGet(wireMockServer.url("/timeout"));

        final HttpClient clientWithTooLittleTimeout = factoryWithTooLittleTimeout.createHttpClient();
        assertThatThrownBy(() -> clientWithTooLittleTimeout.execute(request, ignoreResponse()))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("Read timed out");

        final HttpClient clientWithEnoughTimeout = factoryWithEnoughTimeout.createHttpClient();
        clientWithEnoughTimeout.execute(request, assertOk());

        softly.assertAll();
    }

    @Test( timeout = TEST_TIMEOUT )
    @SneakyThrows
    public void testHttpClientUsesMaxConnections()
    {
        wireMockServer.stubFor(get(urlEqualTo("/max-connections-1")).willReturn(ok()));
        wireMockServer.stubFor(get(urlEqualTo("/max-connections-2")).willReturn(ok()));

        final ApacheHttpClient5Factory sut =
            new DefaultApacheHttpClient5Factory(
                Duration.ofSeconds(3L), // this timeout is also used for the connection lease
                1,
                MAX_CONNECTIONS_PER_ROUTE,
                requestInterceptor);

        final HttpClient client = sut.createHttpClient();
        final ClassicHttpRequest firstRequest = new HttpGet(wireMockServer.url("/max-connections-1"));
        final ClassicHttpRequest secondRequest = new HttpGet(wireMockServer.url("/max-connections-2"));

        assertCannotBeExecutedInParallel(firstRequest, secondRequest, client);
    }

    @Test( timeout = TEST_TIMEOUT )
    @SneakyThrows
    public void testHttpClientUsesMaxConnectionsPerRoute()
    {
        wireMockServer.stubFor(get(urlEqualTo("/max-connections-per-route")).willReturn(ok()));
        secondWireMockServer.stubFor(get(urlEqualTo("/max-connections-per-route")).willReturn(ok()));

        final ApacheHttpClient5Factory sut =
            new DefaultApacheHttpClient5Factory(
                Duration.ofSeconds(3L), // this timeout is also used for the connection lease
                MAX_CONNECTIONS,
                1,
                requestInterceptor);

        final ClassicHttpRequest firstRequest = new HttpGet(wireMockServer.url("/max-connections-per-route"));
        final ClassicHttpRequest secondRequest = new HttpGet(secondWireMockServer.url("/max-connections-per-route"));
        final HttpClient client = sut.createHttpClient();

        assertCanBeExecutedInParallel(firstRequest, secondRequest, client);
        assertCannotBeExecutedInParallel(firstRequest, firstRequest, client);
    }

    @Test
    @SneakyThrows
    public void testProxyConfigurationIsConsidered()
    {
        wireMockServer.stubFor(get(urlEqualTo("/proxy")).willReturn(ok()));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder("http://www.sap.com").build();
        final DefaultHttpDestination spiedDestination = spy(destination);
        doReturn(Option.of(ProxyConfiguration.of(wireMockServer.baseUrl(), new BasicCredentials("user", "pass"))))
            .when(spiedDestination)
            .getProxyConfiguration();

        final HttpClient httpClient = sut.createHttpClient(spiedDestination);
        Mockito.verify(spiedDestination, atLeastOnce()).getProxyConfiguration();

        doAnswer(invocation -> {
            final HttpRequest request = invocation.getArgument(0);
            final HttpClientContext context = invocation.getArgument(2);

            assertThat(request.getUri()).isEqualTo(URI.create("http://www.sap.com/proxy"));
            // NOTE: The passed basic credentials for the proxy authorization are NOT considered
            assertThat(Arrays.stream(request.getHeaders()).map(NameValuePair::getName).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(HttpHeaders.ACCEPT_ENCODING);

            final RouteInfo httpRoute = context.getHttpRoute();
            assertThat(httpRoute).isNotNull();
            assertThat(httpRoute.getHopCount()).isEqualTo(2);
            assertThat(httpRoute.getHopTarget(0)).isEqualTo(HttpHost.create(wireMockServer.baseUrl()));
            assertThat(httpRoute.getHopTarget(1)).isEqualTo(HttpHost.create("http://www.sap.com:80"));

            return null;
        }).when(requestInterceptor).process(any(), any(), any());

        try( final ClassicHttpResponse response = httpClient.execute(new HttpGet("/proxy"), r -> r) ) {
            wireMockServer.verify(getRequestedFor(urlEqualTo("/proxy")));
            assertThat(response.getCode()).isEqualTo(HttpStatus.SC_OK);
        }
    }

    @SneakyThrows
    private void assertCannotBeExecutedInParallel(
        @Nonnull final ClassicHttpRequest firstRequest,
        @Nonnull final ClassicHttpRequest secondRequest,
        @Nonnull final HttpClient client )
    {
        final CountDownLatch firstResponseReceived = new CountDownLatch(1);
        final CountDownLatch secondRequestFailed = new CountDownLatch(1);

        final CompletableFuture<Void> firstFuture = CompletableFuture.runAsync(() -> {
            try {
                client.execute(firstRequest, r -> {
                    firstResponseReceived.countDown();
                    try {
                        secondRequestFailed.await();
                    }
                    catch( final InterruptedException e ) {
                        softly.fail("Interrupted while waiting for request to be sent", e);
                    }
                    return null;
                });
            }
            catch( final IOException e ) {
                softly.fail("Failed to execute request", e);
            }
        });

        firstResponseReceived.await();
        final CompletableFuture<Void> secondFuture = CompletableFuture.runAsync(() -> {
            softly
                .assertThatThrownBy(() -> client.execute(secondRequest, ignoreResponse()))
                .isExactlyInstanceOf(ConnectionRequestTimeoutException.class);
            secondRequestFailed.countDown();
        });

        secondRequestFailed.await();

        firstFuture.get();
        secondFuture.get();
        softly.assertAll();
    }

    @SneakyThrows
    private void assertCanBeExecutedInParallel(
        @Nonnull final ClassicHttpRequest firstRequest,
        @Nonnull final ClassicHttpRequest secondRequest,
        @Nonnull final HttpClient client )
    {
        final CountDownLatch firstResponseReceived = new CountDownLatch(1);
        final CountDownLatch secondResponseReceived = new CountDownLatch(1);

        final CompletableFuture<Void> firstFuture = CompletableFuture.runAsync(() -> {
            try {
                client.execute(firstRequest, r -> {
                    firstResponseReceived.countDown();
                    try {
                        secondResponseReceived.await();
                    }
                    catch( final InterruptedException e ) {
                        softly.fail("Interrupted while waiting for request to be sent", e);
                    }
                    return null;
                });
            }
            catch( final IOException e ) {
                softly.fail("Failed to execute request", e);
            }
        });

        firstResponseReceived.await();
        final CompletableFuture<Void> secondFuture = CompletableFuture.runAsync(() -> {
            try {
                client.execute(secondRequest, r -> {
                    secondResponseReceived.countDown();
                    return null;
                });
            }
            catch( final IOException e ) {
                softly.fail("Failed to execute request", e);
            }
        });

        secondResponseReceived.await();

        firstFuture.get();
        secondFuture.get();
        softly.assertAll();
    }

    @Nonnull
    private HttpClientResponseHandler<?> ignoreResponse()
    {
        return r -> null;
    }

    @Nonnull
    private HttpClientResponseHandler<?> assertOk()
    {
        return r -> {
            softly.assertThat(r.getCode()).isEqualTo(HttpStatus.SC_OK);
            return null;
        };
    }
}
