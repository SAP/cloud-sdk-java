package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantThreadContextListener;

public interface TenantContextApi extends TestContextApi
{
    /**
     * Set {@code default-test-tenant} as value for the tenant for the current thread.
     *
     * @return the tenant
     */
    default Tenant setTenant()
    {
        return setTenant("default-test-tenant");
    }

    /**
     * Set the given tenantid as value for the tenant for the current thread.
     *
     * @param tenant
     *            the tenant ID to use
     * @return the tenant
     */
    default Tenant setTenant( @Nonnull final String tenant )
    {
        return setTenant(new DefaultTenant(tenant));
    }

    /**
     * Set the given tenant as value for the tenant for the current thread.
     *
     * @param tenant
     *            the tenant to use. If {@code null}, the tenant will be cleared.
     * @return the tenant
     */
    default Tenant setTenant( @Nullable final Tenant tenant )
    {
        setProperty(TenantThreadContextListener.PROPERTY_TENANT, tenant);
        return tenant;
    }

    /**
     * Clear the tenant for the current thread.
     */
    default void clearTenant()
    {
        setTenant((Tenant) null);
    }
}
