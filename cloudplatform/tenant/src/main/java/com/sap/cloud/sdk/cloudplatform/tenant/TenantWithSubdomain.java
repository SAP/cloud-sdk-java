package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

/**
 * Represents a {@link Tenant) that can be accessed through a subdomain.
 */
public interface TenantWithSubdomain extends Tenant
{
    /**
     * The subdomain of the tenant.
     *
     * @return The tenant subdomain.
     */
    @Nonnull
    String getSubdomain();
}
