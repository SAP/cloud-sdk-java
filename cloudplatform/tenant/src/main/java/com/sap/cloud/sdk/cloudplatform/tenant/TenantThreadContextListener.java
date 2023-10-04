/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextListener;

/**
 * Implementation of {@link ThreadContextListener} that ensures the correct initialization of {@link Tenant}s when
 * working with non-container managed threads on all supported Cloud platforms.
 */
public class TenantThreadContextListener implements ThreadContextListener
{
    /**
     * The ThreadContext key.
     */
    public static final String PROPERTY_TENANT = TenantThreadContextListener.class.getName() + ":tenant";

    /**
     * The {@link Tenant} to be used by this listener.
     */
    @Nullable
    private final Tenant tenant;

    /**
     * Default constructor.
     */
    public TenantThreadContextListener()
    {
        tenant = null;
    }

    /**
     * Constructor for providing a {@link Tenant} to be returned by this listener.
     *
     * @param tenant
     *            The {@link Tenant} to be used by this listener.
     */
    public TenantThreadContextListener( @Nonnull final Tenant tenant )
    {
        this.tenant = tenant;
    }

    @Override
    public int getPriority()
    {
        return DefaultPriorities.TENANT_LISTENER;
    }

    @Override
    public void afterInitialize( @Nonnull final ThreadContext threadContext )
    {
        if( tenant != null ) {
            threadContext.setProperty(PROPERTY_TENANT, Property.of(tenant));
        } else {
            threadContext
                .setPropertyIfAbsent(PROPERTY_TENANT, Property.decorateCallable(TenantAccessor::getCurrentTenant));
        }
    }
}
