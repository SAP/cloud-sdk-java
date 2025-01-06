package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextListener;

/**
 * Implementation of {@link ThreadContextListener} that ensures the correct initialization of {@link Principal}s when
 * working with non-container managed threads on all supported Cloud platforms.
 */
public class PrincipalThreadContextListener implements ThreadContextListener
{
    /**
     * The ThreadContext key.
     */
    public static final String PROPERTY_PRINCIPAL = PrincipalThreadContextListener.class.getName() + ":principal";

    /**
     * The {@link Principal} to be used by this listener.
     */
    @Nullable
    private final Principal principal;

    /**
     * Default constructor.
     */
    public PrincipalThreadContextListener()
    {
        principal = null;
    }

    /**
     * Constructor for providing a {@link Principal} to be returned by this listener.
     *
     * @param principal
     *            The {@link Principal} to be used by this listener.
     */
    public PrincipalThreadContextListener( @Nonnull final Principal principal )
    {
        this.principal = principal;
    }

    @Override
    public int getPriority()
    {
        return DefaultPriorities.PRINCIPAL_LISTENER;
    }

    @Override
    public void afterInitialize( @Nonnull final ThreadContext threadContext )
    {
        if( principal != null ) {
            threadContext.setProperty(PROPERTY_PRINCIPAL, Property.of(principal));
        } else {
            threadContext
                .setPropertyIfAbsent(
                    PROPERTY_PRINCIPAL,
                    Property.decorateCallable(PrincipalAccessor::getCurrentPrincipal));
        }
    }
}
