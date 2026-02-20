/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.security.xsuaa.client.OAuth2TokenServiceConstants.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.json.JSONObject;

import com.sap.cloud.security.client.DefaultTokenClientConfiguration;
import com.sap.cloud.security.client.HttpClientException;
import com.sap.cloud.security.config.ClientCertificate;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.mtls.SSLContextFactory;
import com.sap.cloud.security.servlet.MDCHelper;
import com.sap.cloud.security.xsuaa.Assertions;
import com.sap.cloud.security.xsuaa.client.AbstractOAuth2TokenService;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceException;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;
import com.sap.cloud.security.xsuaa.http.HttpHeaders;
import com.sap.cloud.security.xsuaa.tokenflows.TokenCacheConfiguration;
import com.sap.cloud.security.xsuaa.util.HttpClientUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 token service implementation using Apache HttpClient 5.
 * <p>
 * This class extends {@link AbstractOAuth2TokenService} and provides the HTTP client specific logic to perform token
 * requests using Apache HttpClient 5 instead of HttpClient 4.
 */
@Slf4j
class HttpClient5OAuth2TokenService extends AbstractOAuth2TokenService
{
    private static final char[] EMPTY_PASSWORD = {};

    private final CloseableHttpClient httpClient;
    private final DefaultTokenClientConfiguration config = DefaultTokenClientConfiguration.getInstance();

    /**
     * Creates a new instance with the given HTTP client and default cache configuration.
     *
     * @param httpClient
     *            The HTTP client to use for token requests.
     */
    HttpClient5OAuth2TokenService( @Nonnull final CloseableHttpClient httpClient )
    {
        this(httpClient, TokenCacheConfiguration.defaultConfiguration());
    }

    /**
     * Creates a new instance with the given HTTP client and cache configuration.
     *
     * @param httpClient
     *            The HTTP client to use for token requests.
     * @param tokenCacheConfiguration
     *            The cache configuration to use.
     */
    HttpClient5OAuth2TokenService(
        @Nonnull final CloseableHttpClient httpClient,
        @Nonnull final TokenCacheConfiguration tokenCacheConfiguration )
    {
        super(tokenCacheConfiguration);
        Assertions.assertNotNull(httpClient, "http client is required");
        this.httpClient = httpClient;
    }

    @Override
    protected
        OAuth2TokenResponse
        requestAccessToken( final URI tokenUri, final HttpHeaders headers, final Map<String, String> parameters )
            throws OAuth2ServiceException
    {
        Assertions.assertNotNull(tokenUri, "Token endpoint URI must not be null!");
        return convertToOAuth2TokenResponse(
            executeRequest(tokenUri, headers, parameters, config.isRetryEnabled() ? config.getMaxRetryAttempts() : 0));
    }

    private String executeRequest(
        final URI tokenUri,
        final HttpHeaders headers,
        final Map<String, String> parameters,
        final int attemptsLeft )
        throws OAuth2ServiceException
    {
        final HttpPost httpPost = createHttpPost(tokenUri, createRequestHeaders(headers), parameters);
        log
            .debug(
                "Requesting access token from url {} with headers {} and {} retries left",
                tokenUri,
                headers,
                attemptsLeft);
        try {
            return httpClient.execute(httpPost, response -> {
                final int statusCode = response.getCode();
                final String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.debug("Received statusCode {} from {}", statusCode, tokenUri);
                if( HttpStatus.SC_OK == statusCode ) {
                    log.debug("Successfully retrieved access token from {} with params {}.", tokenUri, parameters);
                    return body;
                } else if( attemptsLeft > 0 && config.getRetryStatusCodes().contains(statusCode) ) {
                    log.warn("Request failed with status {} but is retryable. Retrying...", statusCode);
                    pauseBeforeNextAttempt(config.getRetryDelayTime());
                    return executeRequest(tokenUri, headers, parameters, attemptsLeft - 1);
                }
                throw OAuth2ServiceException
                    .builder("Error requesting access token!")
                    .withStatusCode(statusCode)
                    .withUri(tokenUri)
                    .withRequestHeaders(getHeadersAsStringArray(httpPost.getHeaders()))
                    .withResponseHeaders(getHeadersAsStringArray(response.getHeaders()))
                    .withResponseBody(body)
                    .build();
            });
        }
        catch( final IOException e ) {
            if( e instanceof final OAuth2ServiceException oAuth2Exception ) {
                throw oAuth2Exception;
            } else {
                throw OAuth2ServiceException
                    .builder("Error requesting access token!")
                    .withUri(tokenUri)
                    .withRequestHeaders(getHeadersAsStringArray(httpPost.getHeaders()))
                    .withResponseBody(e.getMessage())
                    .build();
            }
        }
    }

    private HttpHeaders createRequestHeaders( final HttpHeaders headers )
    {
        final HttpHeaders requestHeaders = new HttpHeaders();
        headers.getHeaders().forEach(h -> requestHeaders.withHeader(h.getName(), h.getValue()));
        requestHeaders.withHeader(MDCHelper.CORRELATION_HEADER, MDCHelper.getOrCreateCorrelationId());
        return requestHeaders;
    }

    private void logRequest( final HttpHeaders headers, final Map<String, String> parameters )
    {
        log.debug("access token request {} - {}", headers, parameters.entrySet().stream().map(e -> {
            if( e.getKey().contains(PASSWORD)
                || e.getKey().contains(CLIENT_SECRET)
                || e.getKey().contains(ASSERTION) ) {
                return new AbstractMap.SimpleImmutableEntry<>(e.getKey(), "****");
            }
            return e;
        }).toList());
    }

    private HttpPost createHttpPost( final URI uri, final HttpHeaders headers, final Map<String, String> parameters )
        throws OAuth2ServiceException
    {
        final HttpPost httpPost = new HttpPost(uri);
        headers.getHeaders().forEach(header -> httpPost.setHeader(header.getName(), header.getValue()));
        final List<BasicNameValuePair> basicNameValuePairs =
            parameters
                .entrySet()
                .stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .toList();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8));
            httpPost.addHeader(org.apache.hc.core5.http.HttpHeaders.USER_AGENT, HttpClientUtil.getUserAgent());
        }
        catch( final Exception e ) {
            throw new OAuth2ServiceException("Unexpected error parsing URI: " + e.getMessage());
        }
        logRequest(headers, parameters);
        return httpPost;
    }

    private OAuth2TokenResponse convertToOAuth2TokenResponse( final String responseBody )
        throws OAuth2ServiceException
    {
        final Map<String, Object> accessTokenMap = new JSONObject(responseBody).toMap();
        final String accessToken = getParameter(accessTokenMap, ACCESS_TOKEN);
        final String refreshToken = getParameter(accessTokenMap, REFRESH_TOKEN);
        final String expiresIn = getParameter(accessTokenMap, EXPIRES_IN);
        final String tokenType = getParameter(accessTokenMap, TOKEN_TYPE);
        return new OAuth2TokenResponse(accessToken, convertExpiresInToLong(expiresIn), refreshToken, tokenType);
    }

    private Long convertExpiresInToLong( final String expiresIn )
        throws OAuth2ServiceException
    {
        try {
            return Long.parseLong(expiresIn);
        }
        catch( final NumberFormatException e ) {
            throw new OAuth2ServiceException(
                String.format("Cannot convert expires_in from response (%s) to long", expiresIn));
        }
    }

    private String getParameter( final Map<String, Object> accessTokenMap, final String key )
    {
        return String.valueOf(accessTokenMap.get(key));
    }

    private static String[] getHeadersAsStringArray( final Header[] headers )
    {
        return headers != null ? Arrays.stream(headers).map(Header::toString).toArray(String[]::new) : new String[0];
    }

    private void pauseBeforeNextAttempt( final long sleepTime )
    {
        try {
            log.info("Retry again in {} ms", sleepTime);
            Thread.sleep(sleepTime);
        }
        catch( final InterruptedException e ) {
            log.warn("Thread.sleep has been interrupted. Retry starts now.");
        }
    }

    /**
     * Creates a CloseableHttpClient (HttpClient5) based on ClientIdentity details.
     * <p>
     * For ClientIdentity that is certificate based it will resolve HTTPS client using the provided ClientIdentity. If
     * the ClientIdentity wasn't provided or is not certificate-based, it will return default HttpClient.
     *
     * @param clientIdentity
     *            for X.509 certificate based communication {@link ClientCertificate} implementation of ClientIdentity
     *            interface should be provided
     * @return HTTP or HTTPS client (HttpClient5)
     * @throws HttpClientException
     *             in case HTTPS Client could not be setup
     */
    @Nonnull
    static CloseableHttpClient createHttpClient( @Nullable final ClientIdentity clientIdentity )
        throws HttpClientException
    {
        return createHttpClient(clientIdentity, null);
    }

    /**
     * Creates a CloseableHttpClient (HttpClient5) based on ClientIdentity details and optional KeyStore.
     * <p>
     * For ClientIdentity that is certificate based it will resolve HTTPS client using the provided ClientIdentity. If a
     * KeyStore is provided (e.g., for ZTIS), it will be used directly. If the ClientIdentity wasn't provided or is not
     * certificate-based, it will return default HttpClient.
     *
     * @param clientIdentity
     *            for X.509 certificate based communication {@link ClientCertificate} implementation of ClientIdentity
     *            interface should be provided
     * @param keyStore
     *            optional KeyStore to use for mTLS (e.g., for ZTIS)
     * @return HTTP or HTTPS client (HttpClient5)
     * @throws HttpClientException
     *             in case HTTPS Client could not be setup
     */
    @Nonnull
    static
        CloseableHttpClient
        createHttpClient( @Nullable final ClientIdentity clientIdentity, @Nullable final KeyStore keyStore )
            throws HttpClientException
    {
        // If a KeyStore is provided directly (e.g., for ZTIS), use it
        if( keyStore != null ) {
            log
                .debug(
                    "Creating HTTPS HttpClient5 with provided KeyStore for client '{}'",
                    clientIdentity != null ? clientIdentity.getId() : "unknown");
            return createHttpClientWithKeyStore(keyStore);
        }

        if( clientIdentity == null ) {
            log.debug("No ClientIdentity provided, creating default HttpClient5");
            return createDefaultHttpClient();
        }

        if( !clientIdentity.isCertificateBased() ) {
            log.debug("ClientIdentity is not certificate-based, creating default HttpClient5");
            return createDefaultHttpClient();
        }

        log
            .debug(
                "Creating HTTPS HttpClient5 with certificate-based authentication for client '{}'",
                clientIdentity.getId());

        try {
            final KeyStore identityKeyStore = SSLContextFactory.getInstance().createKeyStore(clientIdentity);
            return createHttpClientWithKeyStore(identityKeyStore);
        }
        catch( final Exception e ) {
            throw new HttpClientException(
                "Failed to create HTTPS HttpClient5 with certificate authentication: " + e.getMessage());
        }
    }

    @Nonnull
    private static CloseableHttpClient createDefaultHttpClient()
    {
        return HttpClients.custom().useSystemProperties().build();
    }

    @Nonnull
    private static CloseableHttpClient createHttpClientWithKeyStore( @Nonnull final KeyStore keyStore )
        throws HttpClientException
    {
        try {
            final SSLContext sslContext = SSLContextBuilder.create().loadKeyMaterial(keyStore, EMPTY_PASSWORD).build();

            final var tlsStrategy = new DefaultClientTlsStrategy(sslContext);
            final var connectionManager =
                PoolingHttpClientConnectionManagerBuilder.create().setTlsSocketStrategy(tlsStrategy).build();

            return HttpClientBuilder.create().useSystemProperties().setConnectionManager(connectionManager).build();
        }
        catch( final Exception e ) {
            throw new HttpClientException("Failed to create HTTPS HttpClient5 with KeyStore: " + e.getMessage());
        }
    }
}
