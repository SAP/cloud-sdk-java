package com.sap.cloud.sdk.testutil;

import static org.mockito.Mockito.mock;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenThreadContextListener;

public interface AuthTokenContextApi extends TestContextApi
{
    default AuthToken setAuthToken()
    {
        return setAuthToken(mock(AuthToken.class));
    }

    default AuthToken setAuthToken( final DecodedJWT decodedJWT )
    {
        return setAuthToken(new AuthToken(decodedJWT));
    }

    default AuthToken setAuthToken( final AuthToken authToken )
    {
        setProperty(AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN, authToken);
        return authToken;
    }

    default void clearAuthToken()
    {
        setAuthToken((AuthToken) null);
    }
}
