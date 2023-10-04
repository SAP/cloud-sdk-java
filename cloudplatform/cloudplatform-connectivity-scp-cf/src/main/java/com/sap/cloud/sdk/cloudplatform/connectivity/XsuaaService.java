/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.ClientCertificate;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.OAuth2ServiceSettings;
import com.sap.cloud.sdk.cloudplatform.security.OAuth2TokenServiceCache;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestDeniedException;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestFailedException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.token.Token;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenService;
import com.sap.cloud.security.xsuaa.tokenflows.ClientCredentialsTokenFlow;
import com.sap.cloud.security.xsuaa.tokenflows.JwtBearerTokenFlow;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class handles the communication (and the caching of the responses) with an XSUAA service instance.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
public class XsuaaService extends AbstractOAuth2Service
{
    private final OAuth2TokenServiceCache tokenServiceCache;

    /**
     * Default constructor, caching up to 100.000 responses for 60 minutes.
     */
    public XsuaaService()
    {
        this(OAuth2TokenServiceCache.create());
    }

    @Override
    @Nonnull
    public AccessToken retrieveAccessTokenViaClientCredentialsGrant(
        @Nonnull final OAuth2ServiceSettings authorizationServerSettings,
        @Nullable final Credentials credentials,
        final boolean useProviderTenant )
        throws TokenRequestFailedException,
            TokenRequestDeniedException
    {
        log
            .debug(
                "Retrieving Access Token from XSUAA via Client Credentials Grant for {}.",
                useProviderTenant ? "Provider Tenant" : "Subscriber Tenant");

        final ClientCredentialsTokenFlow tokenFlow =
            createTokenFlow(authorizationServerSettings, credentials, XsuaaTokenFlows::clientCredentialsTokenFlow);

        @Nullable
        final String zoneId;

        if( useProviderTenant ) {
            // we can't use the tenant ID in this case, since we don't know if current tenant == provider tenant
            log.debug("Using subdomain of provider tenant.");
            zoneId = null;
        } else {
            zoneId =
                TenantAccessor
                    .tryGetCurrentTenant()
                    .map(Tenant::getTenantId)
                    .onFailure(t -> log.debug("No current tenant/zone available.", t))
                    .onFailure(t -> log.debug("Falling back to provider tenant/zone using the provider UAA subdomain."))
                    .getOrNull();
        }

        if( zoneId != null ) {
            tokenFlow.zoneId(zoneId);
        }

        final OAuth2TokenResponse tokenResponse =
            Try
                .of(tokenFlow::execute)
                .getOrElseThrow(e -> new TokenRequestFailedException("Failed to resolve access token.", e));
        return AccessToken.of(tokenResponse);
    }

    @Override
    @Nonnull
    public AccessToken retrieveAccessTokenViaJwtBearerGrant(
        @Nonnull final OAuth2ServiceSettings authorizationServerSettings,
        @Nullable final Credentials credentials )
        throws TokenRequestFailedException,
            TokenRequestDeniedException,
            AuthTokenAccessException
    {
        log.debug("Retrieving Access Token from XSUAA via JWT Bearer Token Grant");

        final JwtBearerTokenFlow tokenFlow =
            createTokenFlow(authorizationServerSettings, credentials, XsuaaTokenFlows::jwtBearerTokenFlow);

        final Try<Token> maybeToken =
            AuthTokenAccessor
                .tryGetCurrentToken()
                .map(AuthToken::getJwt)
                .map(DecodedJWT::getToken)
                .map(Token::create)
                .peek(tokenFlow::token);

        if( maybeToken.isFailure() ) {
            throw new CloudPlatformException("Failed to get the current user token.", maybeToken.getCause());
        }

        final OAuth2TokenResponse tokenResponse =
            Try
                .of(tokenFlow::execute)
                .getOrElseThrow(e -> new TokenRequestFailedException("Failed to resolve access token.", e));
        return AccessToken.of(tokenResponse);
    }

    private <T> T createTokenFlow(
        @Nonnull final OAuth2ServiceSettings authorizationServerSettings,
        @Nullable final Credentials credentials,
        @Nonnull final Function<XsuaaTokenFlows, T> tokenFlow )
    {
        final ClientIdentity clientIdentity = getClientIdentity(credentials);
        final OAuth2TokenService tokenService = tokenServiceCache.getTokenService(clientIdentity);
        final OAuth2ServiceEndpointsProvider endpoints = authorizationServerSettings.toOAuth2Endpoints();
        final XsuaaTokenFlows tokenFlows = new XsuaaTokenFlows(tokenService, endpoints, clientIdentity);
        return tokenFlow.apply(tokenFlows);
    }

    @Nonnull
    private ClientIdentity getClientIdentity( @Nonnull final Credentials credentials )
    {
        if( credentials instanceof ClientCredentials ) {
            return new com.sap.cloud.security.config.ClientCredentials(
                ((ClientCredentials) credentials).getClientId(),
                ((ClientCredentials) credentials).getClientSecret());
        }
        if( credentials instanceof ClientCertificate ) {
            return new com.sap.cloud.security.config.ClientCertificate(
                ((ClientCertificate) credentials).getCertificate(),
                ((ClientCertificate) credentials).getKey(),
                ((ClientCertificate) credentials).getClientId());
        }
        throw new IllegalStateException(
            "Unsupported credentials type for authenticating against OAuth2 endpoint: "
                + credentials.getClass().getSimpleName());
    }
}
