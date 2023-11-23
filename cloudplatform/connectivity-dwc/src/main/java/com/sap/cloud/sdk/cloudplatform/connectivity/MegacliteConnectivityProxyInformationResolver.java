/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MegacliteConnectivityProxyInformationResolver implements DestinationHeaderProvider
{
    // ideally we would get this from the connectivity service binding
    // however, due to object lifecycles this is currently not easily possible
    // see https://github.com/SAP/cloud-sdk-java-backlog/issues/209
    private static final String CONNECTIVITY_SERVICE_PATH = "/config/v1/connectivity/token";
    private static final Gson GSON = new Gson();
    private static final String TOKEN_JSON_KEY = "proxyAuth";
    private static final String URL_JSON_KEY = "proxy";
    @Nonnull
    @Getter
    private static final MegacliteConnectivityProxyInformationResolver instance =
        new MegacliteConnectivityProxyInformationResolver();

    @Nonnull
    private final MegacliteDestinationFactory destinationFactory;
    @Nonnull
    private final Cache<CacheKey, String> tokenCache =
        Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(15L)).build();
    @Nonnull
    @Getter( AccessLevel.PACKAGE ) // for testing
    private final Cache<CacheKey, String> proxyUrlCache =
        Caffeine.newBuilder().expireAfterWrite(Duration.ofDays(1L)).build();
    @Nonnull
    @Getter( AccessLevel.PACKAGE ) // for testing
    private final Cache<CacheKey, Lock> requestLocks =
        Caffeine.newBuilder().expireAfterAccess(Duration.ofHours(1L)).build();

    private MegacliteConnectivityProxyInformationResolver()
    {
        this(MegacliteDestinationFactory.getInstance());
    }

    public MegacliteConnectivityProxyInformationResolver(
        @Nonnull final MegacliteDestinationFactory destinationFactory )
    {
        this.destinationFactory = destinationFactory;
    }

    @Nonnull
    @Override
    public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
    {
        final String authToken;
        try {
            authToken = getAuthorizationToken();
        }
        catch( final IllegalStateException e ) {
            throw new DestinationAccessException(e);
        }
        final Header header = new Header(HttpHeaders.PROXY_AUTHORIZATION, authToken);
        return Collections.singletonList(header);
    }

    /**
     * Retrieves the <i>ProxyAuth</i> token from Megaclite.
     * <p>
     * <b>Hint</b>: The token will be cached by this implementation.
     * </p>
     *
     * @return The <i>ProxyAuth</i> token.
     * @throws IllegalStateException
     *             If the token could not be retrieved.
     */
    @Nonnull
    String getAuthorizationToken()
    {
        final CacheKey cacheKey = createTokenCacheKey();
        return getProxyInformationFromMegacliteWithCache(TOKEN_JSON_KEY, tokenCache, cacheKey);
    }

    /**
     * Retrieves the <i>Proxy</i> URL from Megaclite.
     * <p>
     * <b>Hint</b>: The URL will be cached by this implementation.
     * </p>
     *
     * @return The <i>Proxy</i> URL.
     * @throws IllegalStateException
     *             If the URL could not be retrieved.
     */
    @Nonnull
    URI getProxyUrl()
    {
        final CacheKey cacheKey = createProxyUrlCacheKey();
        return URI.create(getProxyInformationFromMegacliteWithCache(URL_JSON_KEY, proxyUrlCache, cacheKey));
    }

    @Nonnull
    CacheKey createTokenCacheKey()
    {
        return CacheKey.ofTenantOptionalIsolation().append(TOKEN_JSON_KEY);
    }

    @Nonnull
    CacheKey createProxyUrlCacheKey()
    {
        return CacheKey.ofNoIsolation().append(URL_JSON_KEY);
    }

    @SuppressWarnings( "DataFlowIssue" )
    @Nonnull
    private String getProxyInformationFromMegacliteWithCache(
        @Nonnull final String jsonKey,
        @Nonnull final Cache<CacheKey, String> cache,
        @Nonnull final CacheKey cacheKey )
    {
        return ResilienceDecorator.executeSupplier(() -> {
            String result = cache.getIfPresent(cacheKey);
                    if( result != null ) {
                        return result;
                    }
            JsonObject json = getProxyInformationFromMegaclite();
            result = json.get(jsonKey).getAsString();
            cache.put(cacheKey, result);
            return result;
        }, ResilienceConfiguration.empty("foooo")
                        .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .bulkheadConfiguration(ResilienceConfiguration.BulkheadConfiguration.of().maxConcurrentCalls(1)));
       /* String maybeValue = cache.getIfPresent(cacheKey);
        if( maybeValue != null ) {
            return maybeValue;
        }

        final Lock requestLock = requestLocks.get(cacheKey, k -> new ReentrantLock());
        requestLock.lock();
        try {
            maybeValue = cache.getIfPresent(cacheKey);
            if( maybeValue != null ) {
                return maybeValue;
            }

            final JsonObject json = getProxyInformationFromMegaclite();
            maybeValue = json.get(jsonKey).getAsString();
            cache.put(cacheKey, maybeValue);

            return maybeValue;
        }
        catch( final Exception e ) {
            throw new IllegalStateException(e);
        }
        finally {
            requestLock.unlock();
        }*/
    }

    @Nonnull
    JsonObject getProxyInformationFromMegaclite()
    {
        final Destination megacliteDestination = destinationFactory.getMegacliteDestination(CONNECTIVITY_SERVICE_PATH);
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(megacliteDestination);

        final HttpResponse response;
        try {
            response = makeHttpRequest(httpClient);
        }
        catch( final IOException e ) {
            throw new IllegalStateException("Failed to request onpremise proxy information from megaclite", e);
        }

        final String responseBody;
        try {
            if( response.getEntity() != null ) {
                responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            } else {
                responseBody = "";
            }
        }
        catch( final IOException e ) {
            throw new IllegalStateException(
                "Failed to read onpremise proxy information response body from megaclite",
                e);
        }

        if( response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST ) {
            log
                .debug(
                    "Failed to resolve onpremise proxy information. Megaclite responded with status code {} and response body '{}'.",
                    response.getStatusLine().getStatusCode(),
                    responseBody);
            throw new IllegalStateException(
                "Failed to resolve onpremise proxy information. "
                    + "Megaclite responded with status code: "
                    + response.getStatusLine().getStatusCode());
        }

        try {
            return GSON.fromJson(responseBody, JsonObject.class);
        }
        catch( final JsonSyntaxException e ) {
            throw new IllegalStateException(
                "Failed to parse the response with onpremise proxy information from megaclite.",
                e);
        }
    }

    HttpResponse makeHttpRequest( @Nonnull final HttpClient client )
        throws IOException
    {
        return client.execute(new HttpGet());
    }
}
