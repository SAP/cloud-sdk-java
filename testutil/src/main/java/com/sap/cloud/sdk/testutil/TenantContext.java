package com.sap.cloud.sdk.testutil;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantThreadContextListener;

/**
 * API for setting and clearing the tenant for the current thread.
 */
public interface TenantContext extends TestContextApi
{
    /**
     * Set {@code default-test-tenant} as value for the tenant for the current thread.
     *
     * @return the tenant
     */
    @Nonnull
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
    @Nonnull
    default Tenant setTenant( @Nonnull final String tenant )
    {
        Objects.requireNonNull(tenant, "Tenant ID must not be null.");
        return setTenant(new DefaultTenant(tenant, "default-test-subdomain"));
    }

    /**
     * Set the given tenant as value for the tenant for the current thread.
     *
     * @param tenant
     *            the tenant to use. If {@code null}, the tenant will be cleared.
     * @return the tenant
     */
    @Nullable
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
