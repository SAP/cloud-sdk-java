/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Implementation of {@link Tenant} which can be used on SAP Business Technology Platform Cloud Foundry.
 */
@EqualsAndHashCode
@ToString
public class DefaultTenant implements TenantWithSubdomain
{
    /**
     * The identifier of the tenant.
     */
    @Getter
    @Nonnull
    private final String tenantId;

    /**
     * The subdomain of the tenant.
     */
    @EqualsAndHashCode.Exclude
    @Getter
    @Nonnull
    private final String subdomain;

    /**
     * Creates a new {@link DefaultTenant}.
     *
     * @param tenantId
     *            The identifier of the tenant or zone.
     * @param subdomain
     *            The subdomain of the tenant.
     */
    public DefaultTenant( @Nonnull final String tenantId, @Nonnull final String subdomain )
    {
        this.tenantId = tenantId;
        this.subdomain = subdomain;
    }

    /**
     * Creates a mocked {@link DefaultTenant} with an empty tenant identifier.
     */
    public DefaultTenant()
    {
        this("", "");
    }
}
