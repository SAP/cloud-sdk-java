package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

/**
 * An interface representing a Tenant on the SAP Business Technology Platform.
 */
public interface Tenant
{
    /**
     * Getter for the id of a tenant. <br>
     *
     * @return The identifier of the tenant.
     */
    @Nonnull
    String getTenantId();
}
