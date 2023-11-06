/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Implementation of {@link Tenant} which can be used on SAP Business Technology Platform Cloud Foundry.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
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
    @Nullable
    private final String subdomain;

    /**
     * Creates a {@link DefaultTenant} without subdomain.
     */
    public DefaultTenant( @Nonnull final String tenantId )
    {
        this(tenantId, null);
    }

    @Nonnull
    @Override
    public String getSubdomain()
    {
        return Objects.requireNonNull(subdomain, "Subdomain is not available for this tenant.");
    }
}
