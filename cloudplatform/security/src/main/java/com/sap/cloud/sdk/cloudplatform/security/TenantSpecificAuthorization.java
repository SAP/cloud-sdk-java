package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@code Authorization} implementation based on a tenant identifier and another {@code Authorization}.
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
@EqualsAndHashCode( callSuper = true )
@Data
public class TenantSpecificAuthorization extends Authorization
{
    /**
     * The tenant/zone identifier.
     */
    @Nonnull
    protected final String tenantId;

    /**
     * The wrapped authorization.
     */
    @Nonnull
    protected final Authorization authorization;

    /**
     * Creates a new authorization based on the tenant/zone identifier and another {@code Authorization}.
     *
     * @param tenantId
     *            The tenant or zone identifier of this {@code Authorization}.
     * @param authorization
     *            The {@code Authorization} to be considered tenant-specific.
     */
    public TenantSpecificAuthorization( @Nonnull final String tenantId, @Nonnull final Authorization authorization )
    {
        super(tenantId + ":" + authorization.getName());

        this.tenantId = tenantId;
        this.authorization = authorization;
    }
}
