/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Implementation of {@link Tenant} which can be used on SAP Business Technology Platform Cloud Foundry.
 */
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class DefaultTenant implements TenantWithSubdomain
{
    /**
     * The identifier of the tenant.
     */
    @Nonnull
    private final String tenantId;

    /**
     * The subdomain of the tenant.
     */
    @Nullable
    private final String subdomain;

    /**
     * Creates a {@link DefaultTenant} without subdomain.
     *
     * @param tenantId
     *            The identifier of the tenant.
     */
    public DefaultTenant( @Nonnull final String tenantId )
    {
        this(tenantId, null);
    }
}
