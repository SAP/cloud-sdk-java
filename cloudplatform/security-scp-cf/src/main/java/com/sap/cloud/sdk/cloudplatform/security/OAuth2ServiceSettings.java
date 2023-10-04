/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import static com.sap.cloud.security.xsuaa.util.UriUtil.expandPath;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Settings about the Authorization Server.
 */
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor( access = AccessLevel.PROTECTED )
public class OAuth2ServiceSettings
{
    private static final String TOKEN_PATH = "/oauth/token";
    private static final String AUTHORIZE_PATH = "/oauth/authorize";
    private static final String KEYSET_PATH = "/token_keys";

    @Nonnull
    @Getter
    private final URI baseUri;

    @Nullable
    @Getter
    private final String tokenPath;

    @Nullable
    @Getter
    private final String authorizePath;

    @Nullable
    @Getter
    private final String keysetPath;

    /**
     * Helps in constructing the OAuth2 endpoints; the tokenEndpoint, authorize and key set URI (JWKS) from the
     * {@code baseUri}.
     *
     * @return an implementation of {@link com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider }
     *
     * @since 4.14.0
     */
    @Nonnull
    @Beta
    public OAuth2ServiceEndpointsProvider toOAuth2Endpoints()
    {
        return new OAuth2ServiceEndpointsProvider()
        {
            @Getter
            @Nonnull
            final URI tokenEndpoint = expandPath(baseUri, tokenPath != null ? tokenPath : TOKEN_PATH);

            @Getter
            @Nonnull
            final URI authorizeEndpoint = expandPath(baseUri, authorizePath != null ? authorizePath : AUTHORIZE_PATH);

            @Getter
            @Nonnull
            final URI jwksUri = expandPath(baseUri, keysetPath != null ? keysetPath : KEYSET_PATH);
        };
    }

    /**
     * Invoke a builder for this type.
     *
     * @param baseUri
     *            The required OAuth2 service base URI.
     * @return An instance of the fluent builder API.
     */
    @Nonnull
    public static OAuth2ServiceSettings.OAuth2ServiceSettingsBuilder ofBaseUri( @Nonnull final URI baseUri )
    {
        return builder().baseUri(baseUri);
    }

    @Nonnull
    private static OAuth2ServiceSettings.OAuth2ServiceSettingsBuilder builder()
    {
        return new OAuth2ServiceSettings.OAuth2ServiceSettingsBuilder();
    }
}
