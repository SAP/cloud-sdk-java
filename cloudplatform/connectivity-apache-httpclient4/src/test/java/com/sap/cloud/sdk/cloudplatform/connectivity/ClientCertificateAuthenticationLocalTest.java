/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLHandshakeException;
import javax.security.auth.x500.X500Principal;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import lombok.SneakyThrows;

class ClientCertificateAuthenticationLocalTest
{
    private static final String CCA_PASSWORD = "cca-password";
    private static final String JKS_PATH =
        "src/test/resources/" + ClientCertificateAuthenticationLocalTest.class.getSimpleName() + "/client-cert.pkcs12";

    @RegisterExtension
    static final WireMockExtension server =
        WireMockExtension.newInstance().options(buildWireMockConfiguration()).build();

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
                    .keyStore(getClientKeyStore())
                    .keyStorePassword(CCA_PASSWORD)
                    .trustAllCertificates()
                    .build());

        final HttpClientContext context = HttpClientContext.create();
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

        final HttpResponse result = httpClient.execute(new HttpGet(), context);
        assertThat(result.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(context.getUserToken()).isNotNull();
        assertThat(context.getUserToken()).isInstanceOf(X500Principal.class);
        assertThat(context.getUserToken(X500Principal.class).getName()).contains("CN=localhost");

        // assert keystore methods have been used
        Mockito.verify(destination).getKeyStorePassword();
        Mockito.verify(destination).getKeyStore();

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

        final HttpClientContext context = HttpClientContext.create();
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

        assertThatThrownBy(() -> httpClient.execute(new HttpGet(), context)).isInstanceOf(SSLHandshakeException.class);

        // keystore methods have been used
        Mockito.verify(destination).getKeyStore();

        // no request successful
        server.verify(0, getRequestedFor(anyUrl()));
    }

    private static WireMockConfiguration buildWireMockConfiguration()
    {
        return wireMockConfig()
            .httpDisabled(true)
            .dynamicHttpsPort()
            .needClientAuth(true)
            .trustStorePath(JKS_PATH)
            .trustStorePassword(CCA_PASSWORD)
            .trustStoreType("JKS");
    }

    private static KeyStore getClientKeyStore()
        throws KeyStoreException,
            IOException,
            CertificateException,
            NoSuchAlgorithmException
    {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        final InputStream fileStream = Resource.newResource(JKS_PATH).getInputStream();
        keyStore.load(fileStream, CCA_PASSWORD.toCharArray());
        return keyStore;
    }
}
