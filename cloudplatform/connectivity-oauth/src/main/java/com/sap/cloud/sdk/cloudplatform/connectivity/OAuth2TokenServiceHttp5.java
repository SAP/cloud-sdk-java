/**
 * SPDX-FileCopyrightText: 2018-2023 SAP SE or an SAP affiliate company and Cloud Security Client
 * Java contributors
 *
 * <p>SPDX-License-Identifier: Apache-2.0
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class OAuth2TokenServiceHttp5 extends AbstractOAuth2TokenService
{

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2TokenServiceHttp5.class);
    private final CloseableHttpClient httpClient;
    private final DefaultTokenClientConfiguration config = DefaultTokenClientConfiguration.getInstance();

    public OAuth2TokenServiceHttp5( @Nonnull final CloseableHttpClient httpClient )
    {
        this(httpClient, TokenCacheConfiguration.defaultConfiguration());
    }

    public OAuth2TokenServiceHttp5(
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
            LOGGER.info("Retry again in {} ms", sleepTime);
            Thread.sleep(sleepTime);
        }
        catch( final InterruptedException e ) {
            LOGGER.warn("Thread.sleep has been interrupted. Retry starts now.");
        }
    }

    /**
     * Creates a CloseableHttpClient (HttpClient5) based on ClientIdentity details, replicating the logic of
     * HttpClientFactory.create(identity) for HttpClient5.
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
    public static CloseableHttpClient createHttpClient5WithIdentityValidation(
        @Nonnull final ClientIdentity clientIdentity )
        throws HttpClientException
    {
        if( clientIdentity == null ) {
            LOGGER.debug("No ClientIdentity provided, creating default HttpClient5");
            return HttpClients.createDefault();
        }

        if( !(clientIdentity instanceof ClientCertificate) ) {
            LOGGER.debug("ClientIdentity is not certificate-based, creating default HttpClient5");
            return HttpClients.createDefault();
        }

        final ClientCertificate clientCertificate = (ClientCertificate) clientIdentity;
        LOGGER
            .debug(
                "Creating HTTPS HttpClient5 with certificate-based authentication for client '{}'",
                clientCertificate.getId());

        try {
            // Use the SSLContextFactory to create a KeyStore from the ClientIdentity
            final var keyStore = SSLContextFactory.getInstance().createKeyStore(clientIdentity);
            final char[] password = {}; // Empty password as per the factory pattern

            final SSLContext sslContext = SSLContextBuilder.create().loadKeyMaterial(keyStore, password).build();

            final var tlsStrategy = new DefaultClientTlsStrategy(sslContext);
            final var connectionManager =
                PoolingHttpClientConnectionManagerBuilder.create().setTlsSocketStrategy(tlsStrategy).build();

            return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        }
        catch( final Exception e ) {
            throw new HttpClientException(
                "Failed to create HTTPS HttpClient5 with certificate authentication: " + e.getMessage());
        }
    }
}
