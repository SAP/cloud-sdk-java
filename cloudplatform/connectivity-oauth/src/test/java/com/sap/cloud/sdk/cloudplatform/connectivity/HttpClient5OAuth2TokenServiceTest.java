/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
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
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceException;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;
import com.sap.cloud.security.xsuaa.http.HttpHeaders;

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

    // Tests for requestAccessToken method

    private CloseableHttpClient mockHttpClient;
    private ClassicHttpResponse mockResponse;
    private HttpClient5OAuth2TokenService tokenService;

    @BeforeEach
    void setUp()
    {
        mockHttpClient = mock(CloseableHttpClient.class);
        mockResponse = mock(ClassicHttpResponse.class);
        tokenService = new HttpClient5OAuth2TokenService(mockHttpClient);
    }

    @Test
    @DisplayName( "requestAccessToken should successfully retrieve access token with valid response" )
    void testRequestAccessTokenSuccess()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();
        headers.withHeader("Content-Type", "application/x-www-form-urlencoded");

        final Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        parameters.put("client_id", "test-client");
        parameters.put("client_secret", "test-secret");

        final String responseBody = """
            {
                "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
                "token_type": "Bearer",
                "expires_in": 3600,
                "refresh_token": "refresh-token-value"
            }
            """;

        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(responseBody, StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpClientResponseHandler<String> handler = invocation.getArgument(1);
                return handler.handleResponse(mockResponse);
            });

        // When
        final OAuth2TokenResponse result = tokenService.requestAccessToken(tokenUri, headers, parameters);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiredAt().truncatedTo(ChronoUnit.HOURS)).isEqualTo(Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.HOURS));
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token-value");

        // Verify HTTP client was called with correct request
        verify(mockHttpClient).execute(argThat(httpPost -> {
            try {
                return httpPost instanceof HttpPost
                    && httpPost.getUri().equals(tokenUri)
                    && httpPost.getFirstHeader("Content-Type").getValue().equals("application/x-www-form-urlencoded");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }), any(HttpClientResponseHandler.class));
    }

    @Test
    @DisplayName( "requestAccessToken should handle minimal valid response" )
    void testRequestAccessTokenMinimalResponse()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, String> parameters = Map.of("grant_type", "client_credentials");

        final String responseBody = """
            {
                "access_token": "minimal-token",
                "token_type": "Bearer",
                "expires_in": 3600
            }
            """;

        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(responseBody, StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpClientResponseHandler<String> handler = invocation.getArgument(1);
                return handler.handleResponse(mockResponse);
            });

        // When
        final OAuth2TokenResponse result = tokenService.requestAccessToken(tokenUri, headers, parameters);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("minimal-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");assertThat(result.getExpiredAt().truncatedTo(ChronoUnit.HOURS)).isEqualTo(Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.HOURS));
        assertThat(result.getRefreshToken()).isEqualTo("null"); // Should be "null" as string when not present
    }

    @Test
    @DisplayName( "requestAccessToken should throw OAuth2ServiceException for HTTP error status" )
    void testRequestAccessTokenHttpError()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, String> parameters = Map.of("grant_type", "client_credentials");

        final String errorResponseBody = """
            {
                "error": "invalid_client",
                "error_description": "Client authentication failed"
            }
            """;

        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_UNAUTHORIZED);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(errorResponseBody, StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpClientResponseHandler<String> handler = invocation.getArgument(1);
                return handler.handleResponse(mockResponse);
            });

        // When & Then
        assertThatThrownBy(() -> tokenService.requestAccessToken(tokenUri, headers, parameters))
            .isInstanceOf(OAuth2ServiceException.class)
            .hasMessageContaining("Error requesting access token!")
            .satisfies(exception -> {
                OAuth2ServiceException oauthException = (OAuth2ServiceException) exception;
                assertThat(oauthException.getHttpStatusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
                assertThat(oauthException.getMessage()).contains("invalid_client");
            });
    }

    @Test
    @DisplayName( "requestAccessToken should throw OAuth2ServiceException for IOException" )
    void testRequestAccessTokenIOException()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, String> parameters = Map.of("grant_type", "client_credentials");

        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenThrow(new IOException("Connection timeout"));

        // When & Then
        assertThatThrownBy(() -> tokenService.requestAccessToken(tokenUri, headers, parameters))
            .isInstanceOf(OAuth2ServiceException.class)
            .hasMessageContaining("Error requesting access token!")
            .satisfies(exception -> {
                OAuth2ServiceException oauthException = (OAuth2ServiceException) exception;
                assertThat(oauthException.getMessage()).contains("Connection timeout");
            });
    }

    @Test
    @DisplayName( "requestAccessToken should handle invalid JSON response" )
    void testRequestAccessTokenInvalidJson()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, String> parameters = Map.of("grant_type", "client_credentials");

        final String invalidJsonResponse = "{invalid: json response}";

        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(invalidJsonResponse, StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpClientResponseHandler<String> handler = invocation.getArgument(1);
                return handler.handleResponse(mockResponse);
            });

        // When & Then
        assertThatThrownBy(() -> tokenService.requestAccessToken(tokenUri, headers, parameters))
            .isInstanceOf(OAuth2ServiceException.class);
    }

    @Test
    @DisplayName( "requestAccessToken should handle invalid expires_in value" )
    void testRequestAccessTokenInvalidExpiresIn()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, String> parameters = Map.of("grant_type", "client_credentials");

        final String responseBody = """
            {
                "access_token": "test-token",
                "token_type": "Bearer",
                "expires_in": "invalid-number"
            }
            """;

        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(responseBody, StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpClientResponseHandler<String> handler = invocation.getArgument(1);
                return handler.handleResponse(mockResponse);
            });

        // When & Then
        assertThatThrownBy(() -> tokenService.requestAccessToken(tokenUri, headers, parameters))
            .isInstanceOf(OAuth2ServiceException.class)
            .hasMessageContaining("Cannot convert expires_in from response");
    }

    @Test
    @DisplayName( "requestAccessToken should throw IllegalArgumentException for null tokenUri" )
    void testRequestAccessTokenNullTokenUri()
    {
        // Given
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, String> parameters = Map.of("grant_type", "client_credentials");

        // When & Then
        assertThatThrownBy(() -> tokenService.requestAccessToken(null, headers, parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Token endpoint URI must not be null!");
    }

    @Test
    @DisplayName( "requestAccessToken should properly handle custom headers" )
    void testRequestAccessTokenWithCustomHeaders()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();
        headers.withHeader("Authorization", "Basic dGVzdDp0ZXN0");
        headers.withHeader("X-Custom-Header", "custom-value");

        final Map<String, String> parameters = Map.of("grant_type", "client_credentials");

        final String responseBody = """
            {
                "access_token": "test-token",
                "token_type": "Bearer",
                "expires_in": 3600
            }
            """;

        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(responseBody, StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpClientResponseHandler<String> handler = invocation.getArgument(1);
                return handler.handleResponse(mockResponse);
            });

        // When
        final OAuth2TokenResponse result = tokenService.requestAccessToken(tokenUri, headers, parameters);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("test-token");

        // Verify custom headers were included
        verify(mockHttpClient).execute(argThat(httpPost -> {
            return httpPost instanceof HttpPost
                && httpPost.getFirstHeader("Authorization") != null
                && httpPost.getFirstHeader("Authorization").getValue().equals("Basic dGVzdDp0ZXN0")
                && httpPost.getFirstHeader("X-Custom-Header") != null
                && httpPost.getFirstHeader("X-Custom-Header").getValue().equals("custom-value");
        }), any(HttpClientResponseHandler.class));
    }

    @Test
    @DisplayName( "requestAccessToken should properly handle complex parameters" )
    void testRequestAccessTokenWithComplexParameters()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "authorization_code");
        parameters.put("client_id", "test-client");
        parameters.put("client_secret", "test-secret");
        parameters.put("code", "auth-code-123");
        parameters.put("redirect_uri", "https://app.example.com/callback");
        parameters.put("scope", "read write");

        final String responseBody = """
            {
                "access_token": "complex-token",
                "token_type": "Bearer",
                "expires_in": 3600,
                "refresh_token": "refresh-token-123",
                "scope": "read write"
            }
            """;

        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(responseBody, StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpClientResponseHandler<String> handler = invocation.getArgument(1);
                return handler.handleResponse(mockResponse);
            });

        // When
        final OAuth2TokenResponse result = tokenService.requestAccessToken(tokenUri, headers, parameters);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("complex-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getTokenType()).isEqualTo("Bearer");assertThat(result.getExpiredAt().truncatedTo(ChronoUnit.HOURS)).isEqualTo(Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.HOURS));
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token-123");

        // Verify all parameters were included in the request
        verify(mockHttpClient).execute(argThat(httpPost -> {
            try {
                if (!(httpPost instanceof HttpPost)) {
                    return false;
                }
                final String requestBody = new String(
                    httpPost.getEntity().getContent().readAllBytes(),
                    StandardCharsets.UTF_8
                );
                return requestBody.contains("grant_type=authorization_code")
                    && requestBody.contains("client_id=test-client")
                    && requestBody.contains("client_secret=test-secret")
                    && requestBody.contains("code=auth-code-123")
                    && requestBody.contains("redirect_uri=https%3A%2F%2Fapp.example.com%2Fcallback")
                    && requestBody.contains("scope=read+write");
            } catch (final IOException e) {
                return false;
            }
        }), any(HttpClientResponseHandler.class));
    }

    @Test
    @DisplayName( "requestAccessToken should handle empty parameters map" )
    void testRequestAccessTokenWithEmptyParameters()
        throws Exception
    {
        // Given
        final URI tokenUri = URI.create("https://oauth.server.com/oauth/token");
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, String> parameters = new HashMap<>();

        final String responseBody = """
            {
                "access_token": "empty-params-token",
                "token_type": "Bearer",
                "expires_in": 3600
            }
            """;

        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(responseBody, StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(invocation -> {
                HttpClientResponseHandler<String> handler = invocation.getArgument(1);
                return handler.handleResponse(mockResponse);
            });

        // When
        final OAuth2TokenResponse result = tokenService.requestAccessToken(tokenUri, headers, parameters);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("empty-params-token");
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
