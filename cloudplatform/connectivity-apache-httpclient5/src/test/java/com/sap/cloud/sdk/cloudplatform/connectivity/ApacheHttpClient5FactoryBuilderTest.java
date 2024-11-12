/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.function.Predicate.not;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder.TlsUpgrade.DISABLED;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder.TlsUpgrade.ENABLED;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination.builder;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType.INTERNET;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType.ON_PREMISE;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.ValueMatcher;
import com.github.tomakehurst.wiremock.stubbing.SubEvent;

import lombok.SneakyThrows;

class ApacheHttpClient5FactoryBuilderTest
{

    @Test
    void testBuilderContainsOptionalParametersOnly()
    {
        // make sure we can build a new factory instance without supplying any parameters
        assertThatNoException().isThrownBy(() -> new ApacheHttpClient5FactoryBuilder().build());
    }

    @Test
    void testTlsUpgradeToggle()
    {
        var server = new WireMockServer(wireMockConfig().dynamicPort());
        server.stubFor(get(anyUrl()).willReturn(ok()));
        server.start();

        var service = server.baseUrl();
        var proxy = ProxyConfiguration.of(server.baseUrl());

        var destInternet = builder(service).trustAllCertificates().build();
        var destOnPremise = builder(service).proxyType(ON_PREMISE).proxyConfiguration(proxy).buildInternal();
        var destProxy = builder(service).trustAllCertificates().proxyType(INTERNET).proxyConfiguration(proxy).build();
        var destTlsVersion = builder(service).trustAllCertificates().tlsVersion("TLSv1.1").build();

        ApacheHttpClient5Factory sut;

        // force upgrade=true
        sut = new ApacheHttpClient5FactoryBuilder().tlsUpgrade(ENABLED).build();
        new TlsUpgradeAssertion(server, sut)
            .withUpgradeHeader(destInternet)
            .withUpgradeHeader(destProxy)
            .withUpgradeHeader(destOnPremise)
            .withUpgradeHeader(destTlsVersion);

        // force upgrade=false
        sut = new ApacheHttpClient5FactoryBuilder().tlsUpgrade(DISABLED).build();
        new TlsUpgradeAssertion(server, sut)
            .withoutUpgradeHeader(destInternet)
            .withoutUpgradeHeader(destProxy)
            .withoutUpgradeHeader(destOnPremise)
            .withoutUpgradeHeader(destTlsVersion);

        // default
        sut = new ApacheHttpClient5FactoryBuilder().build();
        new TlsUpgradeAssertion(server, sut)
            .withUpgradeHeader(destInternet)
            .withUpgradeHeader(destProxy)
            .withoutUpgradeHeader(destOnPremise)
            .withoutUpgradeHeader(destTlsVersion);
    }

    record TlsUpgradeAssertion(WireMockServer server, ApacheHttpClient5Factory factory) {
        static ValueMatcher<Request> allowedHeaders(String... headers) {
            return req -> {
                var excess = req.getAllHeaderKeys().stream().filter(not(List.of(headers)::contains)).map(SubEvent::error).toList();
                return excess.isEmpty() ? MatchResult.exactMatch() : MatchResult.noMatch(excess);
            };
        }

        @SneakyThrows
        private TlsUpgradeAssertion withUpgradeHeader(
            @Nonnull final DefaultHttpDestination dest )
        {
            factory.createHttpClient(dest).execute(new HttpGet("/foo"), new BasicHttpClientResponseHandler());
            server.verify(getRequestedFor(anyUrl())
                .andMatching(allowedHeaders("Connection", "Host", "User-Agent", "Accept-Encoding", "Upgrade"))
                .withHeader("Connection", equalTo("Upgrade"))
                .withHeader("Upgrade", equalTo("TLS/1.2")));
            return this;
        }

        @SneakyThrows
        private TlsUpgradeAssertion withoutUpgradeHeader(
            @Nonnull final DefaultHttpDestination dest )
        {
            factory.createHttpClient(dest).execute(new HttpGet("/foo"), new BasicHttpClientResponseHandler());
            server.verify(getRequestedFor(anyUrl())
                .andMatching(allowedHeaders("Connection", "Host", "User-Agent", "Accept-Encoding"))
                .withHeader("Connection", equalTo("keep-alive")));
            return this;
        }
    }
}
