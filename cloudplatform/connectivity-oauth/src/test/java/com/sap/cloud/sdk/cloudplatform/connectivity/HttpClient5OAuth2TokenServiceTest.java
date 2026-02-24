/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.sdk.cloudplatform.connectivity.SecurityLibWorkarounds.ZtisClientIdentity;
import com.sap.cloud.sdk.testutil.TestContext;
import com.sap.cloud.security.client.HttpClientException;
import com.sap.cloud.security.config.ClientCertificate;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;

import lombok.SneakyThrows;

/**
 * Unit tests for the HTTP client creation methods in {@link HttpClient5OAuth2TokenService}.
 * <p>
 * These tests verify the different scenarios for creating HTTP clients based on various ClientIdentity types and
 * KeyStore configurations, including proper handling of null inputs and error conditions.
 */
@Isolated( "Tests HTTP client creation which may have global implications" )
class HttpClient5OAuth2TokenServiceTest
{
    @RegisterExtension
    static TestContext context = TestContext.withThreadContext();

    @Test
    @DisplayName( "createHttpClient with null ClientIdentity should return default HTTP client" )
    void testCreateHttpClientWithNullClientIdentity()
        throws HttpClientException
    {
        final CloseableHttpClient result = HttpClient5OAuth2TokenService.createHttpClient(null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CloseableHttpClient.class);
    }

    @Test
    @DisplayName( "createHttpClient with null ClientIdentity and null KeyStore should return default HTTP client" )
    void testCreateHttpClientWithNullClientIdentityAndNullKeyStore()
        throws HttpClientException
    {
        final CloseableHttpClient result = HttpClient5OAuth2TokenService.createHttpClient(null, null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CloseableHttpClient.class);
    }

    @Test
    @DisplayName( "createHttpClient with non-certificate-based ClientIdentity should return default HTTP client" )
    void testCreateHttpClientWithNonCertificateBasedClientIdentity()
        throws HttpClientException
    {
        final ClientIdentity clientCredentials = new ClientCredentials("client-id", "client-secret");

        final CloseableHttpClient result = HttpClient5OAuth2TokenService.createHttpClient(clientCredentials);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CloseableHttpClient.class);
    }

    @Test
    @DisplayName( "createHttpClient with non-certificate-based ClientIdentity and null KeyStore should return default HTTP client" )
    void testCreateHttpClientWithNonCertificateBasedClientIdentityAndNullKeyStore()
        throws HttpClientException
    {
        final ClientIdentity clientCredentials = new ClientCredentials("client-id", "client-secret");

        final CloseableHttpClient result = HttpClient5OAuth2TokenService.createHttpClient(clientCredentials, null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CloseableHttpClient.class);
    }

    @Test
    @DisplayName( "createHttpClient with provided KeyStore should use KeyStore regardless of ClientIdentity" )
    @SneakyThrows
    void testCreateHttpClientWithProvidedKeyStore()
    {
        final KeyStore keyStore = createEmptyKeyStore();
        final ClientIdentity clientCredentials = new ClientCredentials("client-id", "client-secret");

        final CloseableHttpClient result = HttpClient5OAuth2TokenService.createHttpClient(clientCredentials, keyStore);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CloseableHttpClient.class);
    }

    @Test
    @DisplayName( "createHttpClient with only KeyStore provided should use KeyStore" )
    @SneakyThrows
    void testCreateHttpClientWithOnlyKeyStore()
    {
        final KeyStore keyStore = createEmptyKeyStore();

        final CloseableHttpClient result = HttpClient5OAuth2TokenService.createHttpClient(null, keyStore);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CloseableHttpClient.class);
    }

    @Test
    @DisplayName( "createHttpClient with certificate-based ClientIdentity should handle invalid certificates gracefully" )
    void testCreateHttpClientWithCertificateBasedClientIdentity()
    {
        final ClientIdentity certificateIdentity = createMockCertificateBasedIdentity();

        // These should fail because of invalid certificate format
        assertThatThrownBy(() -> HttpClient5OAuth2TokenService.createHttpClient(certificateIdentity))
            .isInstanceOf(HttpClientException.class)
            .hasMessageContaining("Failed to create HTTPS HttpClient5 with certificate authentication");

        assertThatThrownBy(() -> HttpClient5OAuth2TokenService.createHttpClient(certificateIdentity, null))
            .isInstanceOf(HttpClientException.class)
            .hasMessageContaining("Failed to create HTTPS HttpClient5 with certificate authentication");
    }

    @Test
    @DisplayName( "createHttpClient with ZTIS ClientIdentity should handle certificate validation" )
    @SneakyThrows
    void testCreateHttpClientWithZtisClientIdentity()
    {
        final KeyStore keyStore = createEmptyKeyStore();
        final ClientIdentity ztisIdentity = new ZtisClientIdentity("ztis-client-id", keyStore);

        // ZtisClientIdentity is certificate-based but doesn't implement certificate methods properly
        // This should fail with certificate validation error
        assertThatThrownBy(() -> HttpClient5OAuth2TokenService.createHttpClient(ztisIdentity))
            .isInstanceOf(HttpClientException.class)
            .hasMessageContaining("Failed to create HTTPS HttpClient5 with certificate authentication");
    }

    @Test
    @DisplayName( "createHttpClient with ZTIS ClientIdentity and explicit KeyStore should prefer explicit KeyStore" )
    @SneakyThrows
    void testCreateHttpClientWithZtisClientIdentityAndExplicitKeyStore()
    {
        final KeyStore embeddedKeyStore = createEmptyKeyStore();
        final KeyStore explicitKeyStore = createEmptyKeyStore();
        final ClientIdentity ztisIdentity = new ZtisClientIdentity("ztis-client-id", embeddedKeyStore);

        final CloseableHttpClient result =
            HttpClient5OAuth2TokenService.createHttpClient(ztisIdentity, explicitKeyStore);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CloseableHttpClient.class);
    }

    @Test
    @DisplayName( "createHttpClient should handle certificate creation failures gracefully" )
    void testCreateHttpClientWithInvalidCertificateIdentity()
    {
        final ClientIdentity invalidCertificateIdentity = createInvalidCertificateBasedIdentity();

        assertThatThrownBy(() -> HttpClient5OAuth2TokenService.createHttpClient(invalidCertificateIdentity))
            .isInstanceOf(HttpClientException.class)
            .hasMessageContaining("Failed to create HTTPS HttpClient5 with certificate authentication");
    }

    @Test
    @DisplayName( "createHttpClient with invalid KeyStore should throw HttpClientException" )
    void testCreateHttpClientWithInvalidKeyStore()
    {
        final KeyStore invalidKeyStore = createInvalidKeyStore();
        final ClientIdentity clientCredentials = new ClientCredentials("client-id", "client-secret");

        assertThatThrownBy(() -> HttpClient5OAuth2TokenService.createHttpClient(clientCredentials, invalidKeyStore))
            .isInstanceOf(HttpClientException.class)
            .hasMessageContaining("Failed to create HTTPS HttpClient5 with KeyStore");
    }

    @Test
    @DisplayName( "Multiple calls to createHttpClient should return different instances" )
    void testCreateHttpClientReturnsNewInstances()
        throws HttpClientException
    {
        final CloseableHttpClient client1 = HttpClient5OAuth2TokenService.createHttpClient(null);
        final CloseableHttpClient client2 = HttpClient5OAuth2TokenService.createHttpClient(null);

        assertThat(client1).isNotNull();
        assertThat(client2).isNotNull();
        assertThat(client1).isNotSameAs(client2);
    }

    @Test
    @DisplayName( "createHttpClient with different ClientIdentity types should handle appropriately" )
    void testCreateHttpClientWithDifferentIdentityTypes()
        throws HttpClientException
    {
        final ClientIdentity credentials = new ClientCredentials("client-id", "client-secret");

        final CloseableHttpClient credentialsClient = HttpClient5OAuth2TokenService.createHttpClient(credentials);

        assertThat(credentialsClient).isNotNull();
        assertThat(credentialsClient).isInstanceOf(CloseableHttpClient.class);

        // Test that certificate-based identity throws exception due to invalid certificate
        final ClientIdentity certificate = createMockCertificateBasedIdentity();
        assertThatThrownBy(() -> HttpClient5OAuth2TokenService.createHttpClient(certificate))
            .isInstanceOf(HttpClientException.class)
            .hasMessageContaining("Failed to create HTTPS HttpClient5 with certificate authentication");
    }

    @Test
    @DisplayName( "createHttpClient should handle concurrent access safely" )
    void testCreateHttpClientConcurrentAccess()
        throws InterruptedException
    {
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final CloseableHttpClient[] results = new CloseableHttpClient[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        for( int i = 0; i < threadCount; i++ ) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    results[index] = HttpClient5OAuth2TokenService.createHttpClient(null);
                }
                catch( final Exception e ) {
                    exceptions[index] = e;
                }
            });
        }

        // Start all threads
        for( final Thread thread : threads ) {
            thread.start();
        }

        // Wait for all threads to complete
        for( final Thread thread : threads ) {
            thread.join();
        }

        // Verify results
        for( int i = 0; i < threadCount; i++ ) {
            assertThat(exceptions[i]).isNull();
            assertThat(results[i]).isNotNull();
        }
    }

    // Helper methods for creating test objects

    @Nonnull
    private static KeyStore createEmptyKeyStore()
        throws KeyStoreException,
            CertificateException,
            IOException,
            NoSuchAlgorithmException
    {
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        return keyStore;
    }

    @Nonnull
    private static ClientIdentity createMockCertificateBasedIdentity()
    {
        final ClientCertificate mockCertificate = mock(ClientCertificate.class);
        when(mockCertificate.isCertificateBased()).thenReturn(true);
        when(mockCertificate.getId()).thenReturn("certificate-client-id");

        // Mock the certificate methods with valid string values
        when(mockCertificate.getCertificate())
            .thenReturn("-----BEGIN CERTIFICATE-----\nMIIBIjANBgkqhkiG9w0BAQEF...\n-----END CERTIFICATE-----");
        when(mockCertificate.getKey())
            .thenReturn("-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEF...\n-----END PRIVATE KEY-----");

        return mockCertificate;
    }

    @Nonnull
    private static ClientIdentity createInvalidCertificateBasedIdentity()
    {
        final ClientCertificate mockCertificate = mock(ClientCertificate.class);
        when(mockCertificate.isCertificateBased()).thenReturn(true);
        when(mockCertificate.getId()).thenReturn("invalid-certificate-client-id");

        // Return null for certificate methods to trigger the validation error
        when(mockCertificate.getCertificate()).thenReturn(null);
        when(mockCertificate.getCertificateChain()).thenReturn(null);

        return mockCertificate;
    }

    @Nullable
    private static KeyStore createInvalidKeyStore()
    {
        // Return a mock KeyStore that will cause SSL context creation to fail
        return mock(KeyStore.class);
    }
}
