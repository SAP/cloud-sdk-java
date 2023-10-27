/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import io.vavr.control.Try;

/**
 * Represents a specific {@link DefaultTenantFacade} that is used when running on the SAP Deploy with Confidence stack.
 */
@Beta
public class DwcTenantFacade extends DefaultTenantFacade
{
    private static final String TENANT = TenantThreadContextListener.PROPERTY_TENANT;

    @Nonnull
    @Override
    public Try<Tenant> tryGetCurrentTenant()
    {
        @Nullable
        final ThreadContext currentContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentContext != null && currentContext.containsProperty(TENANT) ) {
            return currentContext.getPropertyValue(TENANT);
        }
        return Try.of(DwcTenantFacade::extractTenantFromDwcHeaders);
    }

    @Nonnull
    private static Tenant extractTenantFromDwcHeaders()
    {
        try {
            final String tenantId = DwcHeaderUtils.getDwcTenantIdOrThrow();
            final String subdomain = DwcHeaderUtils.getDwCSubdomainOrThrow();
            return new DwcTenant(tenantId, subdomain);
        }
        catch( final Exception e ) {
            throw new TenantAccessException(e);
        }
    }
}
