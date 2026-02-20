/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.security.xsuaa.client.OAuth2TokenServiceConstants.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.security.client.DefaultTokenClientConfiguration;
import com.sap.cloud.security.servlet.MDCHelper;
import com.sap.cloud.security.xsuaa.Assertions;
import com.sap.cloud.security.xsuaa.client.AbstractOAuth2TokenService;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceException;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;
import com.sap.cloud.security.xsuaa.http.HttpHeaders;
import com.sap.cloud.security.xsuaa.tokenflows.TokenCacheConfiguration;

/**
 * Implementation of {@link com.sap.cloud.security.xsuaa.client.OAuth2TokenService} using Apache HttpClient 5.
 * <p>
 * This class provides OAuth2 token retrieval functionality using Apache HttpClient 5 instead of HttpClient 4.
 */
public class HttpClient5OAuth2TokenService extends AbstractOAuth2TokenService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient5OAuth2TokenService.class);
    private static final String USER_AGENT_HEADER = "User-Agent";

    private final CloseableHttpClient httpClient;
    private final DefaultTokenClientConfiguration config = DefaultTokenClientConfiguration.getInstance();

    /**
     * Creates a new instance with the given HTTP client and default cache configuration.
     *
     * @param httpClient
     *            The Apache HttpClient 5 instance to use for HTTP requests.
     */
    public HttpClient5OAuth2TokenService( @Nonnull final CloseableHttpClient httpClient )
    {
        this(httpClient, TokenCacheConfiguration.defaultConfiguration());
    }

    /**
     * Creates a new instance with the given HTTP client and cache configuration.
     *
     * @param httpClient
     *            The Apache HttpClient 5 instance to use for HTTP requests.
     * @param tokenCacheConfiguration
     *            The cache configuration to use.
     */
    public HttpClient5OAuth2TokenService(
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
        final ClassicHttpRequest httpPost = createHttpPost(tokenUri, createRequestHeaders(headers), parameters);
        LOGGER
            .debug(
                "Requesting access token from url {} with headers {} and {} retries left",
                tokenUri,
                headers,
                attemptsLeft);
        try {
            return httpClient.execute(httpPost, response -> {
                final int statusCode = response.getCode();
                final String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                LOGGER.debug("Received statusCode {} from {}", statusCode, tokenUri);
                if( HttpStatus.SC_OK == statusCode ) {
                    LOGGER.debug("Successfully retrieved access token from {} with params {}.", tokenUri, parameters);
                    return body;
                } else if( attemptsLeft > 0 && config.getRetryStatusCodes().contains(statusCode) ) {
                    LOGGER.warn("Request failed with status {} but is retryable. Retrying...", statusCode);
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
        LOGGER.debug("access token request {} - {}", headers, parameters.entrySet().stream().map(e -> {
            if( e.getKey().contains(PASSWORD)
                || e.getKey().contains(CLIENT_SECRET)
                || e.getKey().contains(ASSERTION) ) {
                return new AbstractMap.SimpleImmutableEntry<>(e.getKey(), "****");
            }
            return e;
        }).toList());
    }

    private
        ClassicHttpRequest
        createHttpPost( final URI uri, final HttpHeaders headers, final Map<String, String> parameters )
            throws OAuth2ServiceException
    {
        final ClassicRequestBuilder requestBuilder = ClassicRequestBuilder.post(uri);

        headers.getHeaders().forEach(header -> requestBuilder.addHeader(header.getName(), header.getValue()));

        final List<BasicNameValuePair> basicNameValuePairs =
            parameters
                .entrySet()
                .stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .toList();

        requestBuilder.setEntity(new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8));
        requestBuilder.addHeader(USER_AGENT_HEADER, getUserAgent());

        logRequest(headers, parameters);
        return requestBuilder.build();
    }

    private String getUserAgent()
    {
        // Construct a user agent string similar to what HttpClientUtil provides
        final String javaVersion = System.getProperty("java.version", "unknown");
        return "cloud-security-xsuaa-integration/HttpClient5 (Java/" + javaVersion + ")";
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
            LOGGER.info("Retry again in {} ms", sleepTime);
            Thread.sleep(sleepTime);
        }
        catch( final InterruptedException e ) {
            LOGGER.warn("Thread.sleep has been interrupted. Retry starts now.");
            Thread.currentThread().interrupt();
        }
    }
}
