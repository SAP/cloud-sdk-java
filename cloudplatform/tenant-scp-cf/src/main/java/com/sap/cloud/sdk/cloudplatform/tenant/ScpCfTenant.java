package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Implementation of {@link Tenant} for SAP Business Technology Platform Cloud Foundry.
 */
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
public class ScpCfTenant extends DefaultTenant implements TenantWithSubdomain
{
    /**
     * The subdomain of the tenant.
     */
    @EqualsAndHashCode.Exclude
    @Getter
    @Nonnull
    private final String subdomain;

    /**
     * Creates a new {@link ScpCfTenant}.
     *
     * @param tenantId
     *            The identifier of the tenant or zone.
     * @param subdomain
     *            The subdomain of the tenant.
     */
    public ScpCfTenant( @Nonnull final String tenantId, @Nonnull final String subdomain )
    {
        super(tenantId);
        this.subdomain = subdomain;
    }

    /**
     * Creates a mocked {@link ScpCfTenant} with an empty tenant identifier.
     */
    public ScpCfTenant()
    {
        this("", "");
    }
}
