/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

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
    @Getter
    @Nonnull
    private final String subdomain;

    /**
     * Creates a mocked {@link DefaultTenant} with an empty tenant identifier.
     */
    public DefaultTenant()
    {
        this("", "");
    }
}
