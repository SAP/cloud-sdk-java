package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import io.vavr.control.Try;

/**
 * Represents a specific {@link DefaultTenantFacade} that is used when running on the SAP Deploy with Confidence stack.
 */
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
            final String subdomain = DwcHeaderUtils.getDwCSubdomainOrNull();
            return new DefaultTenant(tenantId, subdomain);
        }
        catch( final Exception e ) {
            throw new TenantAccessException(e);
        }
    }
}
