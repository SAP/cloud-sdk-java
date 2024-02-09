package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenThreadContextListener;

/**
 * API for setting and clearing the {@link AuthToken} for the current thread.
 */
public interface AuthTokenContext extends TestContextApi
{
    /**
     * Set an empty JWT token as the current {@link AuthToken} in the thread context.
     *
     * @return the given {@link AuthToken}
     */
    default AuthToken setAuthToken()
    {
        return setAuthToken(JWT.decode(JWT.create().sign(Algorithm.none())));
    }

    /**
     * Set the given {@link DecodedJWT} as the current {@link AuthToken} in the thread context.
     *
     * @param decodedJWT
     *            the JWT to set.
     * @return the given {@link AuthToken}
     */
    default AuthToken setAuthToken( @Nonnull final DecodedJWT decodedJWT )
    {
        return setAuthToken(new AuthToken(decodedJWT));
    }

    /**
     * Set the given {@link AuthToken} as the current {@link AuthToken} in the thread context.
     *
     * @param authToken
     *            the token to set. If {@code null}, the current token will be cleared.
     * @return the given {@link AuthToken}
     */
    default AuthToken setAuthToken( @Nullable final AuthToken authToken )
    {
        setProperty(AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN, authToken);
        return authToken;
    }

    /**
     * Clear the current {@link AuthToken} in the thread context.
     */
    default void clearAuthToken()
    {
        setAuthToken((AuthToken) null);
    }
}
