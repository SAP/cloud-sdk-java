package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.auth0.jwt.JWT;
import com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import io.vavr.control.Try;

public class DwcAuthTokenFacade extends DefaultAuthTokenFacade
{
    private static final String AUTH_TOKEN = AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN;

    @Nonnull
    @Override
    public Try<AuthToken> tryGetCurrentToken()
    {
        @Nullable
        final ThreadContext currentContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentContext != null && currentContext.containsProperty(AUTH_TOKEN) ) {
            return currentContext.getPropertyValue(AUTH_TOKEN);
        }
        return Try.of(DwcAuthTokenFacade::extractAuthTokenFromDwcHeaders);
    }

    @Nonnull
    private static AuthToken extractAuthTokenFromDwcHeaders()
    {
        try {
            final String token = DwcHeaderUtils.getDwcTokenOrThrow();
            return new AuthToken(JWT.decode(token));
        }
        catch( final Exception e ) {
            throw new AuthTokenAccessException("Failed to extract auth token from DwC headers.", e);
        }
    }
}
