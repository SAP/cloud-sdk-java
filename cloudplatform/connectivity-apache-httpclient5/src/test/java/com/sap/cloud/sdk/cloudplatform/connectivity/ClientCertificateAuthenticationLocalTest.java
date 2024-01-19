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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

class ClientCertificateAuthenticationLocalTest
{
    private static final String BASE_PATH =
        "src/test/resources/" + ClientCertificateAuthenticationLocalTest.class.getSimpleName();
    private static final String PKCS12_PW = "cca-password";
    private static final String PKCS12_PATH = BASE_PATH + "/client-cert.p12";
    private static final String PKCS12_PATH_NOPW = BASE_PATH + "/client-emptypw.p12";
    private static final String JKS_PATH_NOPW = BASE_PATH + "/client-nopw.jks";

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

    @RequiredArgsConstructor
    enum TestCase
    {
        PKCS12_WITH_PW("PKCS12", PKCS12_PATH, PKCS12_PW),
        PKCS12_EMPTY_PW("PKCS12", PKCS12_PATH_NOPW, ""),
        JKS_EMPTY_PW("JKS", JKS_PATH_NOPW, ""),
        JKS_NULL_PW("JKS", JKS_PATH_NOPW, null);

        final String format, path, pw;
    }

    @ParameterizedTest
    @EnumSource( TestCase.class )
    @SneakyThrows
    void testClientCorrectlyConfigured( @Nonnull final TestCase testCase )
    {
        final DefaultHttpDestination.Builder destBuilder =
            DefaultHttpDestination
                .builder(server.baseUrl())
                .authenticationType(AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION)
                .proxyType(ProxyType.INTERNET)
                .trustAllCertificates()
                .keyStore(getClientKeyStore(testCase.format, testCase.path, testCase.pw));
        if( testCase.pw != null ) {
            destBuilder.keyStorePassword(testCase.pw);
        }

        final HttpDestination destination = spy(destBuilder.build());
        final HttpClientContext context = HttpClientContext.create();
        final HttpClient httpClient = ApacheHttpClient5Accessor.getHttpClient(destination);

        httpClient.execute(new HttpGet("/"), context, r -> assertThat(r.getCode()).isEqualTo(200));

        assertThat(context.getUserToken()).isNotNull();
        assertThat(context.getUserToken()).isInstanceOf(X500Principal.class);
        assertThat(context.getUserToken(X500Principal.class).getName()).contains("CN=localhost");

        // assert keystore methods have been used
        verify(destination).getKeyStorePassword();
        verify(destination).getKeyStore();

        // requests had no authorization header
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
        testClientCorrectlyConfigured(TestCase.PKCS12_WITH_PW);
    }

    private static WireMockConfiguration buildWireMockConfiguration()
    {
        return wireMockConfig()
            .httpDisabled(true)
            .dynamicHttpsPort()
            .needClientAuth(true)
            .trustStorePath(PKCS12_PATH)
            .trustStorePassword(PKCS12_PW);
    }

    private static
        KeyStore
        getClientKeyStore( @Nonnull final String format, @Nonnull final String ksPath, @Nullable final String pw )
            throws KeyStoreException,
                IOException,
                CertificateException,
                NoSuchAlgorithmException
    {
        final KeyStore keyStore = KeyStore.getInstance(format);
        keyStore.load(new FileInputStream(ksPath), pw == null ? null : pw.toCharArray());
        return keyStore;
    }
}
