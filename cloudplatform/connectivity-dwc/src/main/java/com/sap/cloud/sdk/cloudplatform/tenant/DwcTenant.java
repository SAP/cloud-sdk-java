/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a specific {@link TenantWithSubdomain} that is used when running on the SAP Deploy with Confidence stack.
 */
@Beta
@ToString( callSuper = true )
@EqualsAndHashCode( callSuper = true )
public class DwcTenant extends DefaultTenant
{
    /**
     * Creates a new {@link DwcTenant}.
     *
     * @param tenantId
     *            The identifier of the tenant or zone.
     * @param subdomain
     *            The subdomain of the tenant.
     */
    public DwcTenant( @Nonnull final String tenantId, @Nonnull final String subdomain )
    {
        super(tenantId, subdomain);
    }

    /**
     * Creates a mocked {@link DwcTenant} with an empty tenant identifier.
     */
    public DwcTenant()
    {
        this("", "");
    }
}
