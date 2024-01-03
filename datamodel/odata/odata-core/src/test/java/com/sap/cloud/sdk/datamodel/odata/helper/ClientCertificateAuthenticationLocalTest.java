/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
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
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import org.apache.http.HttpHeaders;
import org.eclipse.jetty.io.NetworkTrafficListener;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.jupiter.api.BeforeAll;
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
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.ProxyType;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataConnectionException;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;

@SuppressWarnings( "deprecation" )
abstract class ClientCertificateAuthenticationLocalTest
{
    private static final String ODATA_ENDPOINT_URL = "/sap/opu/odata/sap/API_ENTITIES";
    private static final String ODATA_FUNCTION_IMPORT_URL = ODATA_ENDPOINT_URL + "/MyEntityCollection.*";

    private static final FluentHelperFactory REQUEST_FACTORY = FluentHelperFactory.withServicePath(ODATA_ENDPOINT_URL);
    private static String responseJson;

    @BeforeAll
    static void beforeClass()
    {
        responseJson = readResourceFile("odataResponse.json");
    }

    @BeforeEach
    void before()
    {
        CacheManager.invalidateAll();
        // remove?
        stubFor(WireMock.get(urlMatching(ODATA_FUNCTION_IMPORT_URL)).willReturn(WireMock.okJson(responseJson)));
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

    @Getter
    static class MyEntity extends VdmEntity<MyEntity>
    {
        private static final String ENTITY_COLLECTION = "MyEntityCollection";
        private final String entityCollection = ENTITY_COLLECTION;
        private final Class<MyEntity> type = MyEntity.class;
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
        static final WireMockExtension ERP_SERVER =
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
                        .builder(ERP_SERVER.baseUrl())
                        .authenticationType(AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION)
                        .proxyType(ProxyType.INTERNET)
                        .keyStore(getClientKeyStore(TEST_CONFIG))
                        .keyStorePassword(TEST_CONFIG.getClientKeyStorePassword())
                        .trustAllCertificates()
                        .build());

            final List<MyEntity> result =
                REQUEST_FACTORY.read(MyEntity.class, MyEntity.ENTITY_COLLECTION).top(1).executeRequest(destination);
            assertThat(result).isNotNull().isNotEmpty();

            // keystore methods have been used
            Mockito.verify(destination).getKeyStorePassword();
            Mockito.verify(destination).getKeyStore();

            // request had no authorization header
            verify(getRequestedFor(urlMatching(ODATA_FUNCTION_IMPORT_URL)).withoutHeader(HttpHeaders.AUTHORIZATION));
        }

        @Test
        void testClientNotConfigured()
        {
            final HttpDestination destination =
                spy(
                    DefaultHttpDestination
                        .builder(ERP_SERVER.baseUrl())
                        .authenticationType(AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION)
                        .proxyType(ProxyType.INTERNET)
                        .trustAllCertificates()
                        .build());

            final Callable<?> brokenCall =
                () -> REQUEST_FACTORY.read(MyEntity.class, MyEntity.ENTITY_COLLECTION).executeRequest(destination);

            // expect an exception from broken call
            assertThatThrownBy(() -> ThreadContextExecutor.fromNewContext().execute(brokenCall))
                .isInstanceOf(ThreadContextExecutionException.class)
                .hasCauseExactlyInstanceOf(ODataConnectionException.class);

            // keystore methods have been used
            Mockito.verify(destination).getKeyStore();

            // no request successful
            verify(0, getRequestedFor(urlMatching(ODATA_FUNCTION_IMPORT_URL)));
        }
    }

    static class HostNotConfiguredTest extends ClientCertificateAuthenticationLocalTest
    {
        private static final CcaTestConfig TEST_CONFIG =
            CcaTestConfig.builder().clientKeyStoreFile("cca-client.p12").clientKeyStorePassword("cca-password").build();

        @RegisterExtension
        static final WireMockExtension ERP_SERVER =
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
                        .builder(ERP_SERVER.baseUrl())
                        .authenticationType(AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION)
                        .proxyType(ProxyType.INTERNET)
                        .keyStore(getClientKeyStore(TEST_CONFIG))
                        .keyStorePassword(TEST_CONFIG.getClientKeyStorePassword())
                        .trustAllCertificates()
                        .build());

            final Callable<?> brokenCall =
                () -> REQUEST_FACTORY.read(MyEntity.class, MyEntity.ENTITY_COLLECTION).executeRequest(destination);

            // expect an exception from broken call
            assertThatThrownBy(() -> ThreadContextExecutor.fromNewContext().execute(brokenCall))
                .isInstanceOf(ThreadContextExecutionException.class)
                .hasCauseExactlyInstanceOf(ODataConnectionException.class);

            // keystore methods have been used
            Mockito.verify(destination).getKeyStorePassword();
            Mockito.verify(destination).getKeyStore();

            // no request successful
            verify(0, getRequestedFor(urlMatching(ODATA_FUNCTION_IMPORT_URL)));
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
    private static String readResourceFile( final String resourceFileName )
    {
        final String resource = ClientCertificateAuthenticationLocalTest.class.getSimpleName() + "/" + resourceFileName;
        final URL resourceUrl = ClientCertificateAuthenticationLocalTest.class.getClassLoader().getResource(resource);
        return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
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
