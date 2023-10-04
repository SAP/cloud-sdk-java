/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

/**
 * An interface representing a Tenant on the SAP Business Technology Platform.
 */
public interface Tenant
{
    /**
     * Getter for the id of a tenant. <br>
     * In a zone enabled context this represents the zone id.
     *
     * @return The identifier of the tenant (or zone, if applicable).
     */
    @Nonnull
    String getTenantId();
}
