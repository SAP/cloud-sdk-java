package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nullable;

/**
 * Represents a {@link Tenant} that can be accessed through a subdomain.
 */
public interface TenantWithSubdomain extends Tenant
{
    /**
     * The subdomain of the tenant.
     *
     * @return The tenant subdomain.
     */
    @Nullable
    String getSubdomain();
}
