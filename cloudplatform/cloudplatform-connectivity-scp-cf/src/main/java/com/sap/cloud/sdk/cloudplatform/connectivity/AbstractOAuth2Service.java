package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;

import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.OAuth2ServiceSettings;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestDeniedException;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestFailedException;

/**
 * This class handles the communication (and the caching of the responses) with an OAuth2 service.
 */
abstract class AbstractOAuth2Service implements OAuth2Service
{
    /**
     * Retrieves an {@code AccessToken} for a given pair of client credentials and returns it as part of an
     * Authorization header.
     *
     * @param oauthUri
     *            The OAuth2 service URI to retrieve the token from.
     * @param credentials
     *            The client credentials to retrieve the token for.
     * @return A {@link HttpHeaders#AUTHORIZATION} header with the retrieved access token.
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     * @see #retrieveAccessTokenViaClientCredentialsGrant(URI, Credentials)
     */
    @Nonnull
    public Header retrieveAccessTokenHeaderViaClientCredentialsGrant(
        @Nonnull final URI oauthUri,
        @Nonnull final Credentials credentials )
    {
        final String value = retrieveAccessTokenViaClientCredentialsGrant(oauthUri, credentials).getValue();
        return new Header(HttpHeaders.AUTHORIZATION, "Bearer " + value);
    }

    /**
     * Retrieves an {@code AccessToken} for a given pair of client credentials in the name of an authenticated user (to
     * be found in the JWT of the current request) and returns it as part of an Authorization header. The method
     * performs an OAuth2 flow: JWT Bearer Token Grant.
     *
     * @param oauthUri
     *            The OAuth2 service URI to retrieve the token from.
     * @param credentials
     *            The client credentials to retrieve the token for.
     * @return A {@link HttpHeaders#AUTHORIZATION} header with the retrieved access token.
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     * @see #retrieveAccessTokenViaUserTokenGrant(URI, Credentials)
     */
    @Nonnull
    public Header retrieveAccessTokenHeaderViaUserTokenGrant(
        @Nonnull final URI oauthUri,
        @Nonnull final Credentials credentials )
    {
        final String value = retrieveAccessTokenViaUserTokenGrant(oauthUri, credentials).getValue();
        return new Header(HttpHeaders.AUTHORIZATION, "Bearer " + value);
    }

    /**
     * Retrieves an {@code AccessToken} for a given pair of client credentials.
     *
     * @param oauthUri
     *            The OAuth2 service URI to retrieve the token from.
     * @param credentials
     *            The client credentials to retrieve the token for.
     * @return The {@code AccessToken} for the given client credentials received from the given OAuth2 service.
     * @see <a href= "https://docs.cloudfoundry.org/api/uaa/version/74.14.0/index.html#client-credentials-grant">Cloud
     *      Foundry UAA Client Credentials Grant</a>
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     */
    @Nonnull
    public AccessToken retrieveAccessTokenViaClientCredentialsGrant(
        @Nonnull final URI oauthUri,
        @Nonnull final Credentials credentials )
    {
        return retrieveAccessTokenViaClientCredentialsGrant(
            OAuth2ServiceSettings.ofBaseUri(oauthUri).build(),
            credentials,
            false);
    }

    /**
     * Retrieves an {@code AccessToken} for a given pair of client credentials.
     *
     * @param authorizationServerSettings
     *            The settings for the Authorization Server
     * @param credentials
     *            The client credentials to retrieve the token for.
     * @return The {@code AccessToken} for the given client credentials
     */
    @Nonnull
    public AccessToken retrieveAccessTokenViaClientCredentialsGrant(
        @Nonnull final OAuth2ServiceSettings authorizationServerSettings,
        @Nonnull final Credentials credentials )
    {
        return retrieveAccessTokenViaClientCredentialsGrant(authorizationServerSettings, credentials, false);
    }

    /**
     * Retrieves an {@code AccessToken} for the currently active user token and the client id.
     *
     * @param oauthUri
     *            The OAuth2 service URI to retrieve the token from.
     * @param credentials
     *            The client id is part of the credentials.
     * @return The {@code AccessToken} for the active user token received from the given OAuth2 service.
     * @see <a href= "https://docs.cloudfoundry.org/api/uaa/version/74.14.0/index.html#jwt-bearer-token-grant">Cloud
     *      Foundry UAA JWT Bearer Token Grant</a>
     * @throws TokenRequestFailedException
     *             When the token cannot be requested or correctly parsed.
     * @throws TokenRequestDeniedException
     *             When the authorization for the token request was rejected.
     */
    @Nonnull
    public
        AccessToken
        retrieveAccessTokenViaUserTokenGrant( @Nonnull final URI oauthUri, @Nonnull final Credentials credentials )
    {
        return retrieveAccessTokenViaJwtBearerGrant(OAuth2ServiceSettings.ofBaseUri(oauthUri).build(), credentials);
    }
}
