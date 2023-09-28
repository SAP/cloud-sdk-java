package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextListener;

/**
 * Implementation of {@link ThreadContextListener} that ensures the correct initialization of {@link AuthToken}s when
 * working with non-container managed threads on all supported Cloud platforms.
 */
public class AuthTokenThreadContextListener implements ThreadContextListener
{
    /**
     * The ThreadContext key.
     */
    public static final String PROPERTY_AUTH_TOKEN = AuthTokenThreadContextListener.class.getName() + ":authToken";

    /**
     * The {@link AuthToken} to be used by this listener.
     */
    @Nullable
    private final AuthToken authToken;

    /**
     * Default constructor.
     */
    public AuthTokenThreadContextListener()
    {
        authToken = null;
    }

    /**
     * Constructor for providing a {@link AuthToken} to be returned by this listener.
     *
     * @param authToken
     *            The {@link AuthToken} to be used by this listener.
     */
    public AuthTokenThreadContextListener( @Nonnull final AuthToken authToken )
    {
        this.authToken = authToken;
    }

    @Override
    public int getPriority()
    {
        return DefaultPriorities.AUTH_TOKEN_LISTENER;
    }

    @Override
    public void afterInitialize( @Nonnull final ThreadContext threadContext )
    {
        if( authToken != null ) {
            threadContext.setProperty(PROPERTY_AUTH_TOKEN, Property.of(authToken));
        } else {
            threadContext
                .setPropertyIfAbsent(
                    PROPERTY_AUTH_TOKEN,
                    Property.decorateCallable(AuthTokenAccessor::getCurrentToken));
        }
    }
}
