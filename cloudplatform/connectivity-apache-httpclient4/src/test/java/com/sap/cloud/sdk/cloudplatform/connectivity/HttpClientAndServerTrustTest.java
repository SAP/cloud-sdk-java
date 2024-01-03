/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

/**
 * Tests different variants of trust between the HttpClient and its server.
 *
 * Read the readme.md in the resources folder on how to generate keys, certificates and key stores. Certificates expire
 * after 50 years in around 2070.
 */
class HttpClientAndServerTrustTest
{
    private static final String RELATIVE_PATH = "some/path";

    private static final String KEY_AND_TRUST_STORE_PASSWORD = "password";

    private static final String SERVER_TRUSTSTORE_FILE_NAME = "server_cacerts.jks";
    private static final String SERVER_KEYSTORE_FILE_NAME = "server_keystore.jks";
    private static final String CLIENT_TRUSTSTORE_FILE_NAME = "client_cacerts.jks";
    private static final String CLIENT_KEYSTORE_FILE_NAME = "client_keystore.jks";

    @Test
    void testClientTrustsAllAndServerTrustsClient( @Nonnull @TempDir final File temporaryFolder )
        throws IOException,
            KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException
    {
        final File serverTrustStoreFile = copyResourceToTemporaryFolder(SERVER_TRUSTSTORE_FILE_NAME, temporaryFolder);
        final File clientKeyStoreFile = copyResourceToTemporaryFolder(CLIENT_KEYSTORE_FILE_NAME, temporaryFolder);

        final KeyStore keyStore = loadKeyStoreFromFile(clientKeyStoreFile, KEY_AND_TRUST_STORE_PASSWORD);

        executeTest(
            server -> server.needClientAuth(true).trustStorePath(serverTrustStoreFile.getPath()),
            client -> client.keyStore(keyStore).keyStorePassword(KEY_AND_TRUST_STORE_PASSWORD).trustAllCertificates());
    }

    @Test
    void testMutualTrustBetweenClientAndServer( @Nonnull @TempDir final File temporaryFolder )
        throws IOException,
            CertificateException,
            NoSuchAlgorithmException,
            KeyStoreException
    {
        final File serverTrustStoreFile = copyResourceToTemporaryFolder(SERVER_TRUSTSTORE_FILE_NAME, temporaryFolder);
        final File serverKeyStoreFile = copyResourceToTemporaryFolder(SERVER_KEYSTORE_FILE_NAME, temporaryFolder);

        final File clientKeyStoreFile = copyResourceToTemporaryFolder(CLIENT_KEYSTORE_FILE_NAME, temporaryFolder);
        final File clientTrustStoreFile = copyResourceToTemporaryFolder(CLIENT_TRUSTSTORE_FILE_NAME, temporaryFolder);

        final KeyStore clientKeyStore = loadKeyStoreFromFile(clientKeyStoreFile, KEY_AND_TRUST_STORE_PASSWORD);
        final KeyStore clientTrustStore = loadKeyStoreFromFile(clientTrustStoreFile, KEY_AND_TRUST_STORE_PASSWORD);

        executeTest(
            server -> server
                .needClientAuth(true)
                .trustStorePath(serverTrustStoreFile.getPath())
                .keystorePassword(KEY_AND_TRUST_STORE_PASSWORD)
                .keystorePath(serverKeyStoreFile.getPath()),
            client -> client
                .keyStore(clientKeyStore)
                .keyStorePassword(KEY_AND_TRUST_STORE_PASSWORD)
                .trustStore(clientTrustStore));
    }

    @Test
    void testClientTrustsServerAndServerTrustsAll( @Nonnull @TempDir final File temporaryFolder )
        throws IOException,
            CertificateException,
            NoSuchAlgorithmException,
            KeyStoreException
    {
        final File serverKeyStoreFile = copyResourceToTemporaryFolder(SERVER_KEYSTORE_FILE_NAME, temporaryFolder);

        final File clientTrustStoreFile = copyResourceToTemporaryFolder(CLIENT_TRUSTSTORE_FILE_NAME, temporaryFolder);

        final KeyStore clientTrustStore = loadKeyStoreFromFile(clientTrustStoreFile, KEY_AND_TRUST_STORE_PASSWORD);

        executeTest(
            server -> server.keystorePassword(KEY_AND_TRUST_STORE_PASSWORD).keystorePath(serverKeyStoreFile.getPath()),
            client -> client.trustStore(clientTrustStore));
    }

    private void executeTest(
        final Function<WireMockConfiguration, WireMockConfiguration> wireMockConfigurationFunction,
        final Function<DefaultHttpDestination.Builder, DefaultHttpDestination.Builder> destinationBuilderFunction )
        throws IOException
    {
        final WireMockConfiguration defaultWireMockConfig = wireMockConfig().dynamicHttpsPort().httpDisabled(true);

        final WireMockConfiguration wireMockConfiguration = wireMockConfigurationFunction.apply(defaultWireMockConfig);

        final WireMockServer server = new WireMockServer(wireMockConfiguration);

        server.stubFor(get(anyUrl()).willReturn(ok()));

        try {
            server.start();

            final DefaultHttpDestination.Builder destinationBuilder = DefaultHttpDestination.builder(server.baseUrl());

            final Destination httpDestination = destinationBuilderFunction.apply(destinationBuilder).build();

            final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

            final HttpUriRequest getRequest = new HttpGet(RELATIVE_PATH);
            httpClient.execute(getRequest);
        }
        finally {
            server.stop();
        }

        server.verify(WireMock.getRequestedFor(urlEqualTo("/" + RELATIVE_PATH)));
    }

    private KeyStore loadKeyStoreFromFile( final File location, final String password )
        throws KeyStoreException,
            IOException,
            CertificateException,
            NoSuchAlgorithmException
    {
        try( final FileInputStream is = new FileInputStream(location) ) {
            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(is, password.toCharArray());
            return ks;
        }
    }

    private File copyResourceToTemporaryFolder( final String resourceName, final File temporaryFolder )
        throws IOException
    {
        try(
            InputStream stream =
                getClass().getClassLoader().getResourceAsStream(getClass().getSimpleName() + "/" + resourceName) ) {
            if( stream == null ) {
                throw new IllegalArgumentException("Resource " + resourceName + " not found.");
            }

            final File newFile = File.createTempFile(resourceName, null, temporaryFolder);

            Files.copy(stream, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return newFile;
        }
    }
}
