package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.function.Predicate.not;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder.TlsUpgrade;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder.TlsUpgrade.AUTOMATIC;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder.TlsUpgrade.DISABLED;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder.TlsUpgrade.ENABLED;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination.builder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.ValueMatcher;
import com.github.tomakehurst.wiremock.stubbing.SubEvent;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

@WireMockTest
class ApacheHttpClient5HeaderTest
{
    @RequiredArgsConstructor
    enum TestDestination
    {
        INTERNET(url -> builder(url).build()),
        PROXY(url -> builder(url).proxyType(ProxyType.INTERNET).proxyConfiguration(ProxyConfiguration.of(url)).build()),
        ON_PREMISE(
            url -> builder(url)
                .proxyType(ProxyType.ON_PREMISE)
                .proxyConfiguration(ProxyConfiguration.of(url))
                .buildInternal()),
        TLS_VERSION(url -> builder(url).tlsVersion("TLSv1.1").build());

        private final Function<String, HttpDestination> destinationBuilder;
    }

    @RequiredArgsConstructor
    enum TestAssertion
    {
        NONE(List.of("Connection", "Host", "User-Agent", "Accept-Encoding"), Map.of()),
        KEEP_ALIVE(List.of("Connection", "Host", "User-Agent", "Accept-Encoding"),
            Map.of("Connection", "keep-alive")),
        UPGRADE(
            List.of("Connection", "Host", "User-Agent", "Accept-Encoding", "Upgrade"),
            Map.of("Connection", "Upgrade", "Upgrade", "TLS/1.2"));

        private final List<String> headersAllowed;
        private final Map<String, String> headersRequired;
    }

    @Value
    static class TestCase
    {
        TestDestination destination;
        TlsUpgrade tlsFlag;
        TestAssertion assertion;
    }

    @SuppressWarnings( "unused" )
    private static final TestCase[] TEST_CASES =
        {
            // Forced HTTP/TLS upgrade
            new TestCase(TestDestination.INTERNET, ENABLED, TestAssertion.KEEP_ALIVE),
            new TestCase(TestDestination.PROXY, ENABLED, TestAssertion.UPGRADE),
            new TestCase(TestDestination.ON_PREMISE, ENABLED, TestAssertion.UPGRADE),
            new TestCase(TestDestination.TLS_VERSION, ENABLED, TestAssertion.KEEP_ALIVE),

            // Disabled HTTP/TLS upgrade
            new TestCase(TestDestination.INTERNET, DISABLED, TestAssertion.NONE),
            new TestCase(TestDestination.PROXY, DISABLED, TestAssertion.NONE),
            new TestCase(TestDestination.ON_PREMISE, DISABLED, TestAssertion.NONE),
            new TestCase(TestDestination.TLS_VERSION, DISABLED, TestAssertion.KEEP_ALIVE),

            // Automatic HTTP/TLS upgrade
            new TestCase(TestDestination.INTERNET, AUTOMATIC, TestAssertion.KEEP_ALIVE),
            new TestCase(TestDestination.PROXY, AUTOMATIC, TestAssertion.UPGRADE),
            new TestCase(TestDestination.ON_PREMISE, AUTOMATIC, TestAssertion.NONE),
            new TestCase(TestDestination.TLS_VERSION, AUTOMATIC, TestAssertion.KEEP_ALIVE) };

    @SneakyThrows
    @ParameterizedTest
    @FieldSource( "TEST_CASES" )
    void testHeader( @Nonnull final TestCase testCase, @Nonnull final WireMockRuntimeInfo server )
    {
        stubFor(get(anyUrl()).willReturn(ok()));

        var sut = new ApacheHttpClient5FactoryBuilder().tlsUpgrade(testCase.tlsFlag).build();
        var dest = testCase.destination.destinationBuilder.apply(server.getHttpBaseUrl());
        sut.createHttpClient(dest).execute(new HttpGet("/foo"), new BasicHttpClientResponseHandler());

        var request = getRequestedFor(anyUrl()).andMatching(allowedHeaders(testCase.assertion.headersAllowed));
        testCase.assertion.headersRequired.forEach(( k, v ) -> request.withHeader(k, equalTo(v)));
        verify(request);
    }

    static ValueMatcher<Request> allowedHeaders( @Nonnull final List<String> headers )
    {
        return req -> {
            var excess = req.getAllHeaderKeys().stream().filter(not(headers::contains)).map(SubEvent::error).toList();
            return excess.isEmpty() ? MatchResult.exactMatch() : MatchResult.noMatch(excess);
        };
    }
}
