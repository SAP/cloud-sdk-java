/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLHandshakeException;
import javax.security.auth.x500.X500Principal;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import lombok.SneakyThrows;

class ClientCertificateAuthenticationLocalTest
{
    private static final String BASE_PATH =
        "src/test/resources/" + ClientCertificateAuthenticationLocalTest.class.getSimpleName();
    private static final String JKS_PATH = BASE_PATH + "/client-cert.p12";
    private static final String JKS_PATH_NOPW = BASE_PATH + "/client-nopw.p12";
    private static final String JKS_PW = "cca-password";

    @RegisterExtension
    static final WireMockExtension server =
        WireMockExtension.newInstance().options(buildWireMockConfiguration()).build();

    @BeforeEach
    @AfterEach
    void resetHttpClientCacheBeforeEach()
    {
        // although the WireMock URL is dynamic (and thus different HTTP clients are created),
        // it is safer to reset the cache in case URLs randomly match
        ApacheHttpClient5Accessor.setHttpClientCache(null);
    }

    @BeforeEach
    void before()
    {
        server.stubFor(WireMock.get(anyUrl()).willReturn(ok()));
    }

    @Test
    @SneakyThrows
    void testClientCorrectlyConfigured()
    {
        final HttpDestination destination =
            spy(
                DefaultHttpDestination
                    .builder(server.baseUrl())
                    .authenticationType(AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION)
                    .proxyType(ProxyType.INTERNET)
                    .keyStore(getClientKeyStore(JKS_PATH, JKS_PW.toCharArray()))
                    .keyStorePassword(JKS_PW)
                    .trustAllCertificates()
                    .build());

        final HttpClientContext context = HttpClientContext.create();
        final HttpClient httpClient = ApacheHttpClient5Accessor.getHttpClient(destination);

        httpClient.execute(new HttpGet("/"), context, r -> assertThat(r.getCode()).isEqualTo(200));

        assertThat(context.getUserToken()).isNotNull();
        assertThat(context.getUserToken()).isInstanceOf(X500Principal.class);
        assertThat(context.getUserToken(X500Principal.class).getName()).contains("CN=localhost");

        // assert keystore methods have been used
        verify(destination).getKeyStorePassword();
        verify(destination).getKeyStore();

        // request had no authorization header
        server.verify(getRequestedFor(anyUrl()).withoutHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @SneakyThrows
    void testClientCorrectlyConfiguredNoPassword()
    {
        final HttpDestination destination =
            spy(
                DefaultHttpDestination
                    .builder(server.baseUrl())
                    .authenticationType(AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION)
                    .proxyType(ProxyType.INTERNET)
                    .keyStore(getClientKeyStore(JKS_PATH_NOPW, new char[0])) // `null` password does NOT work
                    .trustAllCertificates()
                    .build());

        final HttpClientContext context = HttpClientContext.create();
        final HttpClient httpClient = ApacheHttpClient5Accessor.getHttpClient(destination);

        httpClient.execute(new HttpGet("/"), context, r -> assertThat(r.getCode()).isEqualTo(200));

        assertThat(context.getUserToken()).isNotNull();
        assertThat(context.getUserToken()).isInstanceOf(X500Principal.class);
        assertThat(context.getUserToken(X500Principal.class).getName()).contains("CN=localhost");

        // assert keystore methods have been used
        verify(destination).getKeyStorePassword();
        verify(destination).getKeyStore();

        // request had no authorization header
        server.verify(getRequestedFor(anyUrl()).withoutHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @SneakyThrows
    void testClientNotConfigured()
    {
        final HttpDestination destination =
            spy(
                DefaultHttpDestination
                    .builder(server.baseUrl())
                    .authenticationType(AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION)
                    .proxyType(ProxyType.INTERNET)
                    .trustAllCertificates()
                    .build());

        final HttpClient httpClient = ApacheHttpClient5Accessor.getHttpClient(destination);

        assertThatThrownBy(() -> httpClient.execute(new HttpGet("/"), r -> null))
            .isInstanceOfAny(SSLHandshakeException.class, SocketException.class);

        // sanity check: we can't be certain we will get an SSLHandshakeException
        // sometimes we get a SocketException instead, unknown why
        // thus we run the success test case again to verify the server hasn't crashed
        testClientCorrectlyConfigured();
    }

    private static WireMockConfiguration buildWireMockConfiguration()
    {
        return wireMockConfig()
            .httpDisabled(true)
            .dynamicHttpsPort()
            .needClientAuth(true)
            .trustStorePath(JKS_PATH)
            .trustStorePassword(JKS_PW)
            .trustStoreType("JKS");
    }

    private static KeyStore getClientKeyStore( @Nonnull final String ksPath, @Nullable final char[] pw )
        throws KeyStoreException,
            IOException,
            CertificateException,
            NoSuchAlgorithmException
    {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(ksPath), pw);
        return keyStore;
    }
}
