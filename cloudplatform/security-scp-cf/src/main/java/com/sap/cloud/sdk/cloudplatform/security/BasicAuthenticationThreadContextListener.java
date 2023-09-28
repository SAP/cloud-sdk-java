package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextListener;

/**
 * {@link ThreadContextListener} implementation reading the Basic Authentication Header from the incoming request and
 * storing it in the current {@link ThreadContext}.
 */
public class BasicAuthenticationThreadContextListener implements ThreadContextListener
{
    /**
     * The key of the stored BasicCredentials in the current {@link ThreadContext}.
     */
    public static final String PROPERTY_BASIC_AUTH_HEADER =
        BasicAuthenticationThreadContextListener.class.getName() + ":basicAuthHeader";

    @Override
    public int getPriority()
    {
        return DefaultPriorities.BASIC_AUTH_LISTENER;
    }

    @Override
    public void afterInitialize( @Nonnull final ThreadContext threadContext )
    {
        threadContext
            .setPropertyIfAbsent(
                PROPERTY_BASIC_AUTH_HEADER,
                () -> Property.ofTry(BasicAuthenticationAccessor.tryGetCurrentBasicCredentials()));
    }
}
