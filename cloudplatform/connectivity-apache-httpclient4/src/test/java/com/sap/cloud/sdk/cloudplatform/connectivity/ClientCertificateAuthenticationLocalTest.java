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
import static com.github.tomakehurst.wiremock.jetty11.Jetty11Utils.createHttpConfig;
import static com.github.tomakehurst.wiremock.jetty11.Jetty11Utils.createServerConnector;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.security.auth.x500.X500Principal;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.eclipse.jetty.io.NetworkTrafficListener;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.HttpsSettings;
import com.github.tomakehurst.wiremock.common.JettySettings;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.AdminRequestHandler;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import com.github.tomakehurst.wiremock.jetty11.Jetty11HttpServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;

abstract class ClientCertificateAuthenticationLocalTest
{
    @BeforeEach
    void before()
    {
        CacheManager.invalidateAll();
        stubFor(WireMock.get(anyUrl()).willReturn(ok()));
    }

    @Value
    @Builder
    private static class CcaTestConfig
    {
        String hostKeyStoreFile;
        String hostKeyStorePassword;
        String clientKeyStoreFile;
        String clientKeyStorePassword;
    }

    static class HostCorrectlyConfiguredTest extends ClientCertificateAuthenticationLocalTest
    {
        private static final CcaTestConfig TEST_CONFIG =
            CcaTestConfig
                .builder()
                .hostKeyStoreFile("cca-host.jks")
                .hostKeyStorePassword("cca-password")
                .clientKeyStoreFile("cca-client.p12")
                .clientKeyStorePassword("cca-password")
                .build();

        @RegisterExtension
        static final WireMockExtension server =
            WireMockExtension
                .newInstance()
                .options(getWireMockConfiguration(TEST_CONFIG))
                .configureStaticDsl(true)
                .build();

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
                        .keyStore(getClientKeyStore(TEST_CONFIG))
                        .keyStorePassword(TEST_CONFIG.getClientKeyStorePassword())
                        .trustAllCertificates()
                        .build());

            final HttpClientContext context = HttpClientContext.create();
            final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

            final HttpResponse result = httpClient.execute(new HttpGet(), context);
            assertThat(result.getStatusLine().getStatusCode()).isEqualTo(200);

            assertThat(context.getUserToken()).isNotNull();
            assertThat(context.getUserToken()).isInstanceOf(X500Principal.class);
            assertThat(context.getUserToken(X500Principal.class).getName()).contains("CN=cca-client");

            // assert keystore methods have been used
            Mockito.verify(destination).getKeyStorePassword();
            Mockito.verify(destination).getKeyStore();

            // request had no authorization header
            verify(getRequestedFor(anyUrl()).withoutHeader(HttpHeaders.AUTHORIZATION));
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

            assertThatThrownBy(() -> httpClient.execute(new HttpGet(), context))
                .isInstanceOf(SSLHandshakeException.class);

            // keystore methods have been used
            Mockito.verify(destination).getKeyStore();

            // no request successful
            verify(0, getRequestedFor(anyUrl()));
        }
    }

    static class HostNotConfiguredTest extends ClientCertificateAuthenticationLocalTest
    {
        private static final CcaTestConfig TEST_CONFIG =
            CcaTestConfig.builder().clientKeyStoreFile("cca-client.p12").clientKeyStorePassword("cca-password").build();

        @RegisterExtension
        static final WireMockExtension server =
            WireMockExtension
                .newInstance()
                .options(getWireMockConfiguration(TEST_CONFIG))
                .configureStaticDsl(true)
                .build();

        @Test
        void testClientCorrectlyConfigured()
            throws Exception
        {
            final HttpDestination destination =
                spy(
                    DefaultHttpDestination
                        .builder(server.baseUrl())
                        .authenticationType(AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION)
                        .proxyType(ProxyType.INTERNET)
                        .keyStore(getClientKeyStore(TEST_CONFIG))
                        .keyStorePassword(TEST_CONFIG.getClientKeyStorePassword())
                        .trustAllCertificates()
                        .build());

            final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
            final HttpClientContext context = HttpClientContext.create();

            assertThatThrownBy(() -> httpClient.execute(new HttpGet(), context))
                .isInstanceOf(SSLHandshakeException.class);

            // keystore methods have been used
            Mockito.verify(destination).getKeyStorePassword();
            Mockito.verify(destination).getKeyStore();

            // no request successful
            verify(0, getRequestedFor(anyUrl()));
        }
    }

    private static WireMockConfiguration getWireMockConfiguration( final CcaTestConfig testConfig )
    {
        final WireMockConfiguration configuration =
            wireMockConfig().dynamicPort().dynamicHttpsPort().httpServerFactory(MyJettyServer::new);

        configuration.needClientAuth(true);

        if( testConfig.hostKeyStoreFile != null && testConfig.hostKeyStorePassword != null ) {
            final String testClassName = ClientCertificateAuthenticationLocalTest.class.getSimpleName();
            final String path = "src/test/resources/" + testClassName + "/cca-host.jks";
            configuration.keystorePath(path).keystorePassword(testConfig.hostKeyStorePassword).keystoreType("JKS");
        }
        return configuration;
    }

    private static KeyStore getClientKeyStore( final CcaTestConfig testConfig )
        throws KeyStoreException,
            IOException,
            CertificateException,
            NoSuchAlgorithmException
    {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        if( testConfig.clientKeyStoreFile != null ) {
            final InputStream fileStream = getResourceFile(testConfig.clientKeyStoreFile);
            keyStore
                .load(
                    fileStream,
                    testConfig.clientKeyStorePassword != null
                        ? testConfig.clientKeyStorePassword.toCharArray()
                        : new char[0]);
        }
        return keyStore;
    }

    @SneakyThrows
    private static InputStream getResourceFile( final String resourceFileName )
    {
        final String resource = ClientCertificateAuthenticationLocalTest.class.getSimpleName() + "/" + resourceFileName;
        final URL resourceUrl = ClientCertificateAuthenticationLocalTest.class.getClassLoader().getResource(resource);
        return resourceUrl.openStream();
    }

    private static class MySslContextFactory extends SslContextFactory.Server
    {
        @Override
        public void customize( final SSLEngine sslEngine )
        {
            final SSLParameters sslParams = sslEngine.getSSLParameters();
            sslEngine.setSSLParameters(sslParams);
            if( super.getWantClientAuth() ) {
                sslEngine.setWantClientAuth(super.getWantClientAuth());
            }

            if( super.getNeedClientAuth() ) {
                sslEngine.setNeedClientAuth(super.getNeedClientAuth());
            }

            /* <FIX> */
            super.selectCipherSuites(sslEngine.getEnabledCipherSuites(), sslEngine.getSupportedCipherSuites());
            super.selectProtocols(sslEngine.getEnabledProtocols(), sslEngine.getSupportedProtocols());
            sslEngine.setEnabledCipherSuites(sslEngine.getEnabledCipherSuites());
            sslEngine.setEnabledProtocols(sslEngine.getEnabledProtocols());
            /* </FIX> */
        }
    }

    private static class MyJettyServer extends Jetty11HttpServer
    {
        MyJettyServer(
            final Options options,
            final AdminRequestHandler adminRequestHandler,
            final StubRequestHandler stubRequestHandler )
        {
            super(options, adminRequestHandler, stubRequestHandler);
        }

        @Override
        protected ServerConnector createHttpsConnector(
            String bindAddress,
            HttpsSettings httpsSettings,
            JettySettings jettySettings,
            NetworkTrafficListener listener )
        {
            final MySslContextFactory sslContextFactory = getCustomizedSslContextFactory(httpsSettings);
            final HttpConfiguration httpConfig = createHttpConfig(jettySettings);
            httpConfig.addCustomizer(new SecureRequestCustomizer());
            final int port = httpsSettings.port();
            return createServerConnector(
                jettyServer,
                bindAddress,
                jettySettings,
                port,
                listener,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(httpConfig));
        }

        private MySslContextFactory getCustomizedSslContextFactory( final HttpsSettings httpsSettings )
        {
            final MySslContextFactory sslContextFactory = new MySslContextFactory();
            sslContextFactory.setKeyStorePath(httpsSettings.keyStorePath());
            sslContextFactory.setKeyManagerPassword(httpsSettings.keyStorePassword());
            sslContextFactory.setKeyStoreType(httpsSettings.keyStoreType());
            sslContextFactory.setNeedClientAuth(httpsSettings.needClientAuth());
            if( httpsSettings.hasTrustStore() ) {
                sslContextFactory.setTrustStorePath(httpsSettings.trustStorePath());
                sslContextFactory.setTrustStorePassword(httpsSettings.trustStorePassword());
                sslContextFactory.setTrustStoreType(httpsSettings.trustStoreType());
            }
            return sslContextFactory;
        }
    }
}
