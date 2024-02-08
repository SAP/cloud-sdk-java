/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.security.xsuaa.util.UriUtil.expandPath;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationOAuthTokenException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestFailedException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantWithSubdomain;
import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.token.Token;
import com.sap.cloud.security.xsuaa.client.DefaultOAuth2TokenService;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenService;

import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This interface handles the communication with an OAuth2 service.
 */
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
@Slf4j
class OAuth2Service
{

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

    @Nonnull
    private final URI tokenUri;
    @Nonnull
    private final ClientIdentity identity;
    @Nonnull
    private final OnBehalfOf onBehalfOf;
    @Nonnull
    private final TenantPropagationStrategy tenantPropagationStrategy;
    @Nonnull
    private final Map<String, String> additionalParameters;
    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final ResilienceConfiguration resilienceConfiguration;

    static void clearCache()
    {
        log.warn("Resetting the TokenService cache. This should not be done outside of testing.");
        tokenServiceCache.invalidateAll();
    }

    // package-private for testing
    @Nonnull
    OAuth2TokenService getTokenService( @Nullable final String tenantId )
    {
        final var key = CacheKey.fromIds(tenantId, null).append(identity);
        return tokenServiceCache.get(key, x -> new DefaultOAuth2TokenService(HttpClientFactory.create(identity)));
    }

    @Nonnull
    String retrieveAccessToken() {
        log.debug("Retrieving Access Token from '{}' on behalf of {} with client id '{}'.", tokenUri, onBehalfOf, identity.getId());

        final OAuth2TokenResponse tokenResponse = ResilienceDecorator.executeSupplier(() -> switch (onBehalfOf) {
            case TECHNICAL_USER_PROVIDER -> executeClientCredentialsFlow(null);
            case TECHNICAL_USER_CURRENT_TENANT -> {
                final Tenant tenant = TenantAccessor.tryGetCurrentTenant().getOrNull();
                yield executeClientCredentialsFlow(tenant);
            }
            case NAMED_USER_CURRENT_TENANT -> executeUserExchangeFlow();
            default -> throw new IllegalStateException("Unknown behalf " + onBehalfOf);
        }, resilienceConfiguration);

        if (tokenResponse == null) {
            final String message = "OAuth2 token request failed";
            log.debug(message);
            throw new DestinationOAuthTokenException(null, message);
        }

        final String accessToken = tokenResponse.getAccessToken();
        if (accessToken == null) {
            final String message = "OAuth2 token request succeeded but the response did not contain an access token";
            log.debug(message + ": {}", tokenResponse);
            throw new DestinationOAuthTokenException(null, message);
        }
        return accessToken;
    }

    @Nullable
    private OAuth2TokenResponse executeClientCredentialsFlow( @Nullable final Tenant tenant )
    {
        log
            .debug(
                "Retrieving OAuth token via client credentials flow against '{}' on behalf of {} (using tenant {}).",
                tokenUri,
                onBehalfOf,
                tenant);

        final String tenantId = getTenantId(tenant);
        final String zidHeaderValue = getTenantZoneId(tenantId);
        final String tenantSubdomain = getTenantSubdomain(tenant);
        final OAuth2TokenService tokenService = getTokenService(tenantId);

        return Try
            .of(
                () -> tokenService
                    .retrieveAccessTokenViaClientCredentialsGrant(
                        tokenUri,
                        identity,
                        zidHeaderValue,
                        tenantSubdomain,
                        additionalParameters,
                        false))
            .getOrElseThrow(e -> new TokenRequestFailedException("Failed to resolve access token.", e));
    }

    @Nullable
    private String getTenantId( @Nullable final Tenant tenant )
    {
        return tenant == null ? null : tenant.getTenantId();
    }

    @Nullable
    private String getTenantZoneId( @Nullable final String tenantId )
    {
        if( tenantPropagationStrategy != OAuth2Service.TenantPropagationStrategy.ZID_HEADER ) {
            return null;
        }

        return tenantId;
    }

    @Nullable
    private String getTenantSubdomain( @Nullable final Tenant tenant )
    {
        if( tenantPropagationStrategy != OAuth2Service.TenantPropagationStrategy.TENANT_SUBDOMAIN ) {
            return null;
        }

        if( tenant == null ) {
            return null;
        }

        if( !(tenant instanceof TenantWithSubdomain) ) {
            final var msg = "Unable to get subdomain of tenant '%s' because the instance is not an instance of %s.";
            throw new DestinationAccessException(msg.formatted(tenant, TenantWithSubdomain.class.getSimpleName()));
        }

        return ((TenantWithSubdomain) tenant).getSubdomain();
    }

    @Nullable
    private OAuth2TokenResponse executeUserExchangeFlow()
    {
        log.debug("Retrieving OAuth token via user token exchange flow against '{}'.", tokenUri);

        final Try<DecodedJWT> maybeToken = AuthTokenAccessor.tryGetCurrentToken().map(AuthToken::getJwt);
        final Try<Tenant> maybeTenant = TenantAccessor.tryGetCurrentTenant();

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
        } else if( !maybeTenant.get().getTenantId().equals(token.getAppTid()) ) {
            throw new CloudPlatformException(
                "Unexpected state: Auth Token and tenant of the current context have different tenant IDs."
                    + "AuthTokenAccessor returned a token containing tenant ID "
                    + token.getAppTid()
                    + " while TenantAccessor returned "
                    + maybeTenant.get()
                    + ". This is unexpected, please ensure the TenantAccessor and AuthTokenAccessor return consistent results.");
        }

        final String tenantId = token.getAppTid();
        final OAuth2TokenService tokenService = getTokenService(tenantId);
        final String tenantSubdomain = getTenantSubdomain(maybeTenant.getOrNull());

        final CheckedFunction0<OAuth2TokenResponse> flow;
        switch( tenantPropagationStrategy ) {
            case ZID_HEADER -> flow =
                () -> tokenService
                    .retrieveAccessTokenViaJwtBearerTokenGrant(
                        tokenUri,
                        identity,
                        token.getTokenValue(),
                        additionalParameters,
                        false,
                        tenantId);
            case TENANT_SUBDOMAIN -> flow =
                () -> tokenService
                    .retrieveAccessTokenViaJwtBearerTokenGrant(
                        tokenUri,
                        identity,
                        token.getTokenValue(),
                        tenantSubdomain,
                        additionalParameters,
                        false);
            default -> throw new DestinationAccessException(
                "Unhandled TenantPropagation Strategy: %s.".formatted(tenantPropagationStrategy));
        }

        return Try.of(flow).getOrElseThrow(e -> new TokenRequestFailedException("Failed to resolve access token.", e));
    }

    @Nonnull
    static UriBuilder builder()
    {
        return new Builder();
    }

    interface UriBuilder
    {
        @Nonnull
        IdentityBuilder withTokenUri( @Nonnull final URI tokenUri );

        @Nonnull
        IdentityBuilder withTokenUri( @Nonnull final String tokenUri );
    }

    @FunctionalInterface
    interface IdentityBuilder
    {
        @Nonnull
        OptionalParametersBuilder withIdentity( @Nonnull final ClientIdentity identity );
    }

    interface OptionalParametersBuilder
    {
        @Nonnull
        OptionalParametersBuilder withOnBehalfOf( @Nonnull final OnBehalfOf onBehalfOf );

        @Nonnull
        OptionalParametersBuilder
            withTenantPropagationStrategy( @Nonnull final TenantPropagationStrategy tenantPropagationStrategy );

        @Nonnull
        OptionalParametersBuilder
            withTenantPropagationStrategyFrom( @Nullable final ServiceIdentifier serviceIdentifier );

        @Nonnull
        OptionalParametersBuilder withAdditionalParameter( @Nonnull final String key, @Nonnull final String value );

        @Nonnull
        OptionalParametersBuilder withAdditionalParameters( @Nonnull final Map<String, String> additionalParameters );

        @Nonnull
        OptionalParametersBuilder
            withTimeLimiter( @Nonnull final ResilienceConfiguration.TimeLimiterConfiguration timeLimiter );

        @Nonnull
        OAuth2Service build();
    }

    @Getter( AccessLevel.PACKAGE )
    static class Builder implements UriBuilder, IdentityBuilder, OptionalParametersBuilder
    {
        private static final String XSUAA_TOKEN_PATH = "/oauth/token";
        private static final Duration DEFAULT_TIME_OUT = Duration.ofSeconds(10);

        private URI tokenUri;
        private ClientIdentity identity;
        private OnBehalfOf onBehalfOf = OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
        private TenantPropagationStrategy tenantPropagationStrategy = TenantPropagationStrategy.ZID_HEADER;
        private final Map<String, String> additionalParameters = new HashMap<>();
        private ResilienceConfiguration.TimeLimiterConfiguration timeLimiter =
            ResilienceConfiguration.TimeLimiterConfiguration.of(DEFAULT_TIME_OUT);

        @Nonnull
        @Override
        public IdentityBuilder withTokenUri( @Nonnull final String tokenUri )
        {
            return withTokenUri(URI.create(tokenUri));
        }

        @Nonnull
        @Override
        public IdentityBuilder withTokenUri( @Nonnull final URI tokenUri )
        {
            final URI uri;
            if( tokenUri.getPath() == null || tokenUri.getPath().isBlank() ) {
                uri = expandPath(tokenUri, XSUAA_TOKEN_PATH);
            } else {
                uri = tokenUri;
            }

            this.tokenUri = uri;
            return this;
        }

        @Nonnull
        @Override
        public OptionalParametersBuilder withIdentity( @Nonnull final ClientIdentity identity )
        {
            this.identity = identity;
            return this;
        }

        @Nonnull
        @Override
        public OptionalParametersBuilder withOnBehalfOf( @Nonnull final OnBehalfOf onBehalfOf )
        {
            this.onBehalfOf = onBehalfOf;
            return this;
        }

        @Nonnull
        @Override
        public OptionalParametersBuilder withTenantPropagationStrategy(
            @Nonnull final TenantPropagationStrategy tenantPropagationStrategy )
        {
            this.tenantPropagationStrategy = tenantPropagationStrategy;
            return this;
        }

        @Nonnull
        @Override
        public OptionalParametersBuilder withTenantPropagationStrategyFrom(
            @Nullable final ServiceIdentifier serviceIdentifier )
        {
            final TenantPropagationStrategy tenantPropagationStrategy;
            if( ServiceBindingLibWorkarounds.IAS_IDENTIFIER.equals(serviceIdentifier) ) {
                tenantPropagationStrategy = TenantPropagationStrategy.TENANT_SUBDOMAIN;
            } else {
                tenantPropagationStrategy = TenantPropagationStrategy.ZID_HEADER;
            }

            this.tenantPropagationStrategy = tenantPropagationStrategy;
            return this;
        }

        @Nonnull
        @Override
        public
            OptionalParametersBuilder
            withAdditionalParameter( @Nonnull final String key, @Nonnull final String value )
        {
            additionalParameters.put(key, value);
            return this;
        }

        @Nonnull
        @Override
        public OptionalParametersBuilder withAdditionalParameters(
            @Nonnull final Map<String, String> additionalParameters )
        {
            this.additionalParameters.putAll(additionalParameters);
            return this;
        }

        @Nonnull
        @Override
        public OptionalParametersBuilder withTimeLimiter(
            @Nonnull final ResilienceConfiguration.TimeLimiterConfiguration timeLimiter )
        {
            this.timeLimiter = timeLimiter;
            return this;
        }

        @Nonnull
        @Override
        public OAuth2Service build()
        {
            if( tokenUri == null || identity == null ) {
                throw new ShouldNotHappenException("Some required parameters for the OAuth2Service are null.");
            }

            /*
             * Reasoning for always using ResilienceIsolationMode.TENANT_OPTIONAL, regardless of onBehalfOf:
             * - for TECHNICAL_USER_CURRENT_TENANT this is trivially correct
             * - for NAMED_USER_CURRENT_TENANT the resilience should still be applied per-tenant only
             * - for TECHNICAL_USER_PROVIDER && current tenant != provider the isolation is stronger than it needs to be,
             *   but the downside is arguably not worth keeping a second configuration for this case only
             */
            final var resilienceConfig =
                ResilienceConfiguration
                    .of(tokenUri.getHost() + "-" + identity.getId())
                    .isolationMode(ResilienceIsolationMode.TENANT_OPTIONAL)
                    .timeLimiterConfiguration(timeLimiter);

            // copy the additional parameters to prevent accidental manipulation after the `OAuth2Service` instance has been created.
            final var additionalParameters = new HashMap<>(this.additionalParameters);
            return new OAuth2Service(
                tokenUri,
                identity,
                onBehalfOf,
                tenantPropagationStrategy,
                additionalParameters,
                resilienceConfig);
        }
    }

    enum TenantPropagationStrategy
    {
        TENANT_SUBDOMAIN,
        ZID_HEADER;
    }
}
