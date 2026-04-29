package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

class LoggingHttpRequestRetryStrategyTest
{
    @RegisterExtension
    static final WireMockExtension WIRE_MOCK_SERVER =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    @Test
    void testHttpClientRetriesOn503()
        throws IOException
    {
        // Use WireMock Scenarios to simulate: first request returns 503, second returns 200
        WIRE_MOCK_SERVER
            .stubFor(
                get(urlEqualTo("/retry-test"))
                    .inScenario("Retry 503")
                    .whenScenarioStateIs(STARTED)
                    .willReturn(aResponse().withStatus(503))
                    .willSetStateTo("Recovered"));

        WIRE_MOCK_SERVER
            .stubFor(
                get(urlEqualTo("/retry-test"))
                    .inScenario("Retry 503")
                    .whenScenarioStateIs("Recovered")
                    .willReturn(aResponse().withStatus(200).withBody("OK")));

        final ApacheHttpClient5Factory factory = new ApacheHttpClient5FactoryBuilder().build();

        try( CloseableHttpClient client = (CloseableHttpClient) factory.createHttpClient() ) {
            final HttpGet request = new HttpGet(WIRE_MOCK_SERVER.url("/retry-test"));
            final int statusCode = client.execute(request, HttpResponse::getCode);

            assertThat(statusCode).isEqualTo(200);
            WIRE_MOCK_SERVER.verify(2, getRequestedFor(urlEqualTo("/retry-test")));
        }
    }

    @Test
    void testHttpClientRetriesOn429()
        throws IOException
    {
        // Use WireMock Scenarios to simulate: first request returns 429, second returns 200
        WIRE_MOCK_SERVER
            .stubFor(
                get(urlEqualTo("/rate-limit-test"))
                    .inScenario("Retry 429")
                    .whenScenarioStateIs(STARTED)
                    .willReturn(aResponse().withStatus(429))
                    .willSetStateTo("Recovered"));

        WIRE_MOCK_SERVER
            .stubFor(
                get(urlEqualTo("/rate-limit-test"))
                    .inScenario("Retry 429")
                    .whenScenarioStateIs("Recovered")
                    .willReturn(aResponse().withStatus(200).withBody("OK")));

        final ApacheHttpClient5Factory factory = new ApacheHttpClient5FactoryBuilder().build();

        try( CloseableHttpClient client = (CloseableHttpClient) factory.createHttpClient() ) {
            final HttpGet request = new HttpGet(WIRE_MOCK_SERVER.url("/rate-limit-test"));
            final int statusCode = client.execute(request, HttpResponse::getCode);

            assertThat(statusCode).isEqualTo(200);
            WIRE_MOCK_SERVER.verify(2, getRequestedFor(urlEqualTo("/rate-limit-test")));
        }
    }

    @Test
    void testHttpClientDoesNotRetryOn500()
        throws IOException
    {
        // 500 Internal Server Error should not trigger retry
        WIRE_MOCK_SERVER.stubFor(get(urlEqualTo("/no-retry-test")).willReturn(aResponse().withStatus(500)));

        final ApacheHttpClient5Factory factory = new ApacheHttpClient5FactoryBuilder().build();

        try( CloseableHttpClient client = (CloseableHttpClient) factory.createHttpClient() ) {
            final HttpGet request = new HttpGet(WIRE_MOCK_SERVER.url("/no-retry-test"));
            final int statusCode = client.execute(request, HttpResponse::getCode);

            assertThat(statusCode).isEqualTo(500);
            WIRE_MOCK_SERVER.verify(1, getRequestedFor(urlEqualTo("/no-retry-test")));
        }
    }

    @Test
    void testHttpClientDoesNotRetryOn400()
        throws IOException
    {
        // 400 Bad Request should not trigger retry
        WIRE_MOCK_SERVER.stubFor(get(urlEqualTo("/bad-request-test")).willReturn(aResponse().withStatus(400)));

        final ApacheHttpClient5Factory factory = new ApacheHttpClient5FactoryBuilder().build();

        try( CloseableHttpClient client = (CloseableHttpClient) factory.createHttpClient() ) {
            final HttpGet request = new HttpGet(WIRE_MOCK_SERVER.url("/bad-request-test"));
            final int statusCode = client.execute(request, HttpResponse::getCode);

            assertThat(statusCode).isEqualTo(400);
            WIRE_MOCK_SERVER.verify(1, getRequestedFor(urlEqualTo("/bad-request-test")));
        }
    }

    @Test
    void testHttpClientStopsRetryingAfterMaxAttempts()
        throws IOException
    {
        // Server always returns 503 - should stop after max retries (1 retry = 2 total requests)
        WIRE_MOCK_SERVER.stubFor(get(urlEqualTo("/always-503")).willReturn(aResponse().withStatus(503)));

        final ApacheHttpClient5Factory factory = new ApacheHttpClient5FactoryBuilder().build();

        try( CloseableHttpClient client = (CloseableHttpClient) factory.createHttpClient() ) {
            final HttpGet request = new HttpGet(WIRE_MOCK_SERVER.url("/always-503"));
            final int statusCode = client.execute(request, HttpResponse::getCode);

            // DefaultHttpRequestRetryStrategy allows 1 retry, so 2 total requests
            assertThat(statusCode).isEqualTo(503);
            WIRE_MOCK_SERVER.verify(2, getRequestedFor(urlEqualTo("/always-503")));
        }
    }
}
