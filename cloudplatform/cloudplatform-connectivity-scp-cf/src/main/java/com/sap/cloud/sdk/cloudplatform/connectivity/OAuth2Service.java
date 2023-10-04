/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.OAuth2ServiceSettings;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestDeniedException;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestFailedException;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

/**
 * This interface handles the communication with an OAuth2 service.
 */
public interface OAuth2Service
{
    /**
     * Retrieves an {@code AccessToken} for a given pair of client credentials.
     *
     * @param authorizationServerSettings
     *            The settings for the Authorization Server.
     * @param credentials
     *            The client credentials to retrieve the token for.
     * @param useProviderTenant
     *            Boolean flag to control usage of provider tenant context. The value {@code true} will directly use the
     *            provider tenant, whereas the value {@code false} will use the currently available Tenant as provided
     *            by the {@link TenantAccessor#getCurrentTenant()}.
     * @return The {@code AccessToken} for the given client credentials
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     */
    @Nonnull
    AccessToken retrieveAccessTokenViaClientCredentialsGrant(
        @Nonnull final OAuth2ServiceSettings authorizationServerSettings,
        @Nonnull final Credentials credentials,
        final boolean useProviderTenant );

    /**
     * Retrieves an {@code AccessToken} for the currently active user token and the client id.
     *
     * @param authorizationServerSettings
     *            The settings for the Authorization Server.
     * @param credentials
     *            The client id is part of the credentials.
     * @return The {@code AccessToken} for the active user token received from the given OAuth2 service.
     * @see <a href= "https://docs.cloudfoundry.org/api/uaa/version/74.14.0/index.html#jwt-bearer-token-grant">Cloud
     *      Foundry UAA JWT Bearer Token Grant</a>
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     * @throws AuthTokenAccessException
     *             When the authorization token cannot be accessed.
     */
    @Nonnull
    AccessToken retrieveAccessTokenViaJwtBearerGrant(
        @Nonnull final OAuth2ServiceSettings authorizationServerSettings,
        @Nonnull final Credentials credentials );
}
