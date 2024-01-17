/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.security.xsuaa.util.UriUtil.expandPath;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationOAuthTokenException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestFailedException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.token.Token;
import com.sap.cloud.security.xsuaa.client.DefaultOAuth2TokenService;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenService;
import com.sap.cloud.security.xsuaa.tokenflows.ClientCredentialsTokenFlow;
import com.sap.cloud.security.xsuaa.tokenflows.JwtBearerTokenFlow;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * This interface handles the communication with an OAuth2 service.
 */
@Slf4j
class OAuth2Service
{
    private static final Duration DEFAULT_TOKEN_RETRIEVAL_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Cache to reuse OAuth2TokenService and with that reuse the underlying response cache.
     * <p>
     * The {@code OAuth2Service} is newly instantiated by {@code OAuth2DestinationBuilder} and
     * {@code OAuth2ServiceBindingDestinationLoader} for each destination they build/load. This means, without the
     * cache, also new {@code OAuth2TokenService} would be created for each destination, which in turns defeats the
     * purpose of the response cache used therein.
     * <p>
     * The cache key is composed of the following parts:
     * <ul>
     * <li>{@code zoneId}, that way flow is tenant isolated, especially for the HttpClient used against the OAuth2
     * service.</li>
     * <li>{@code ClientIdentity}, to separate by the credentials used against the OAuth2 service.</li>
     * </ul>
     */
    private static final Cache<CacheKey, OAuth2TokenService> tokenServiceCache =
        Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

    private final OAuth2ServiceEndpointsProvider endpoints;
    private final ClientIdentity identity;
    private final OnBehalfOf onBehalfOf;
    @Getter( AccessLevel.PACKAGE )
    private final ResilienceConfiguration resilienceConfig;

    OAuth2Service( final String uri, final ClientIdentity identity, final OnBehalfOf onBehalfOf )
    {
        endpoints = Endpoints.fromBaseUri(URI.create(uri));
        this.identity = identity;
        this.onBehalfOf = onBehalfOf;
        /*
         * Reasoning for always using ResilienceIsolationMode.TENANT_OPTIONAL, regardless of onBehalfOf:
         * - for TECHNICAL_USER_CURRENT_TENANT this is trivially correct
         * - for NAMED_USER_CURRENT_TENANT the resilience should still be applied per-tenant only
         * - for TECHNICAL_USER_PROVIDER && current tenant != provider the isolation is stronger than it needs to be,
         *   but the downside is arguably not worth keeping a second configuration for this case only
         */
        resilienceConfig =
            ResilienceConfiguration
                .of(endpoints.getTokenEndpoint().getHost() + "-" + identity.getId())
                .isolationMode(ResilienceIsolationMode.TENANT_OPTIONAL)
                .timeLimiterConfiguration(
                    ResilienceConfiguration.TimeLimiterConfiguration.of(DEFAULT_TOKEN_RETRIEVAL_TIMEOUT));
    }

    static void clearCache()
    {
        log.warn("Resetting the TokenService cache. This should not be done outside of testing.");
        tokenServiceCache.invalidateAll();
    }

    XsuaaTokenFlows getTokenFlowFactory( final String zoneId )
    {
        final CacheKey cacheKey = CacheKey.fromIds(zoneId, null).append(identity);
        final OAuth2TokenService tokenService =
            tokenServiceCache.get(cacheKey, key -> new DefaultOAuth2TokenService(HttpClientFactory.create(identity)));
        return new XsuaaTokenFlows(tokenService, endpoints, identity);
    }

    @Nonnull
    String retrieveAccessToken()
    {
        log.debug("Retrieving Access Token from XSUAA on behalf of {}.", onBehalfOf);

        final OAuth2TokenResponse tokenResponse = ResilienceDecorator.executeSupplier(() -> {
            switch( onBehalfOf ) {
                case TECHNICAL_USER_PROVIDER:
                    log.debug("Using subdomain of provider tenant.");
                    return executeClientCredentialsFlow(null);

                case TECHNICAL_USER_CURRENT_TENANT:
                    final String zoneId = TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId).getOrNull();
                    if( zoneId == null ) {
                        log.debug("No current tenant/zone available.");
                        log.debug("Falling back to provider tenant/zone using the provider UAA subdomain.");
                    }
                    return executeClientCredentialsFlow(zoneId);

                case NAMED_USER_CURRENT_TENANT:
                    return executeUserExchangeFlow();

                default:
                    throw new IllegalStateException("Unknown behalf " + onBehalfOf);
            }
        }, resilienceConfig);

        if( tokenResponse == null ) {
            final String message = "OAuth2 token request failed";
            log.debug(message);
            throw new DestinationOAuthTokenException(null, message);
        }

        final String accessToken = tokenResponse.getAccessToken();
        if( accessToken == null ) {
            final String message = "OAuth2 token request succeeded but the response did not contain an access token";
            log.debug(message + ": {}", tokenResponse);
            throw new DestinationOAuthTokenException(null, message);
        }
        return accessToken;
    }

    @Nullable
    private OAuth2TokenResponse executeClientCredentialsFlow( @Nullable final String zoneId )
    {
        final ClientCredentialsTokenFlow flow = getTokenFlowFactory(zoneId).clientCredentialsTokenFlow();
        if( zoneId != null ) {
            flow.zoneId(zoneId);
        }

        return Try
            .of(flow::execute)
            .getOrElseThrow(e -> new TokenRequestFailedException("Failed to resolve access token.", e));
    }

    @Nullable
    private OAuth2TokenResponse executeUserExchangeFlow()
    {
        final Try<DecodedJWT> maybeToken = AuthTokenAccessor.tryGetCurrentToken().map(AuthToken::getJwt);
        final Try<String> maybeTenant = TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId);

        if( maybeToken.isFailure() ) {
            throw new CloudPlatformException("Failed to get the current user token.", maybeToken.getCause());
        }

        final Token token = maybeToken.map(DecodedJWT::getToken).map(Token::create).get();
        if( maybeTenant.isFailure() ) {
            log.warn("""
                Unexpected state: An Auth Token was found in the current context, but the current tenant is undefined.\
                This is unexpected, please ensure the TenantAccessor and AuthTokenAccessor return consistent results.\
                Proceeding with tenant {} defined in the current token.\
                """, token.getAppTid());
            log.debug("The following token is used for the JwtBearerTokenFlow: {}", token);
        } else if( !maybeTenant.get().equals(token.getAppTid()) ) {
            throw new CloudPlatformException(
                "Unexpected state: Auth Token and tenant of the current context have different tenant IDs."
                    + "AuthTokenAccessor returned a token containing tenant ID "
                    + token.getAppTid()
                    + " while TenantAccessor returned "
                    + maybeTenant.get()
                    + ". This is unexpected, please ensure the TenantAccessor and AuthTokenAccessor return consistent results.");
        }
        final JwtBearerTokenFlow flow = getTokenFlowFactory(token.getAppTid()).jwtBearerTokenFlow();
        flow.token(token);

        return Try
            .of(flow::execute)
            .getOrElseThrow(e -> new TokenRequestFailedException("Failed to resolve access token.", e));
    }

    @Value
    static class Endpoints implements OAuth2ServiceEndpointsProvider
    {
        private static final String TOKEN_PATH = "/oauth/token";
        private static final String AUTHORIZE_PATH = "/oauth/authorize";
        private static final String KEYSET_PATH = "/token_keys";

        @Nonnull
        URI tokenEndpoint;

        @Nullable
        URI authorizeEndpoint;

        @Nullable
        URI jwksUri;

        @Nonnull
        static Endpoints fromBaseUri( final URI baseUri )
        {
            final URI tokenEndpoint = expandPath(baseUri, TOKEN_PATH);
            final URI authorizeEndpoint = expandPath(baseUri, AUTHORIZE_PATH);
            final URI jwksUri = expandPath(baseUri, KEYSET_PATH);
            return new Endpoints(tokenEndpoint, authorizeEndpoint, jwksUri);
        }
    }
}
