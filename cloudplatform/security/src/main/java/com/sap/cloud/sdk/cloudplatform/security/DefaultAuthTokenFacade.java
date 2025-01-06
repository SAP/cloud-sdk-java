/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Facade for retrieving the current {@link AuthToken}.
 */
@Slf4j
@AllArgsConstructor( access = AccessLevel.PACKAGE )
public class DefaultAuthTokenFacade extends ExecutableAuthTokenFacade
{
    @Nonnull
    private AuthTokenDecoder tokenDecoder;

    /**
     * Default constructor.
     */
    public DefaultAuthTokenFacade()
    {
        this(new AuthTokenDecoderDefault());
    }

    @Nonnull
    @Override
    public Try<AuthToken> tryGetCurrentToken()
    {
        final ThreadContext currentContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentContext != null
            && currentContext.containsProperty(AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN) ) {
            return currentContext.getPropertyValue(AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN);
        }
        return RequestHeaderAccessor.tryGetHeaderContainer().flatMap(tokenDecoder::decode);
    }

    @Nullable
    @Override
    protected <T> T executeWithAuthToken( @Nonnull final AuthToken authToken, @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        return ThreadContextExecutor
            .fromCurrentOrNewContext()
            .withListeners(new AuthTokenThreadContextListener(authToken))
            .execute(callable);
    }
}
