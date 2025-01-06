/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

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
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;

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
    @Nonnull
    @Getter
    private static final MegacliteConnectivityProxyInformationResolver instance =
        new MegacliteConnectivityProxyInformationResolver();

    @Nonnull
    private final MegacliteDestinationFactory destinationFactory;
    @Nonnull
    private final ResilienceConfiguration responseResilience =
        ResilienceConfiguration
            .of(MegacliteConnectivityProxyInformationResolver.class + "-" + UUID.randomUUID()) // use a unique identifier so that different instances of this class don't share the same resilience state
            .isolationMode(ResilienceIsolationMode.TENANT_OPTIONAL)
            .bulkheadConfiguration(ResilienceConfiguration.BulkheadConfiguration.of().maxConcurrentCalls(1)); // make sure we don't run parallel requests for the same tenant
    @Nonnull
    private final Cache<CacheKey, JsonObject> responseCache =
        Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(15)).build();

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
        return getProxyInformationFromMegacliteWithResilience("proxyAuth");
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
        return URI.create(getProxyInformationFromMegacliteWithResilience("proxy"));
    }

    @SuppressWarnings( "DataFlowIssue" )
    @Nonnull
    private String getProxyInformationFromMegacliteWithResilience( @Nonnull final String jsonKey )
    {
        final CacheKey cacheKey = CacheKey.ofTenantOptionalIsolation();
        JsonObject cachedValue = responseCache.getIfPresent(cacheKey);
        if( cachedValue != null ) {
            return cachedValue.get(jsonKey).getAsString();
        }

        try {
            cachedValue = ResilienceDecorator.executeSupplier(() -> {
                final JsonObject response = responseCache.getIfPresent(cacheKey);
                return Objects.requireNonNullElseGet(response, this::getProxyInformationFromMegaclite);
            }, responseResilience);
            responseCache.put(cacheKey, cachedValue);
            return cachedValue.get(jsonKey).getAsString();
        }
        catch( final ResilienceRuntimeException e ) {
            throw new IllegalStateException(e);
        }
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
