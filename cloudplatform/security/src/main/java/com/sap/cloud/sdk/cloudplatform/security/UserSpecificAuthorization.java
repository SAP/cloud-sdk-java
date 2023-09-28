package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@code Authorization} implementation based on a tenant identifier, a user name, and another {@code Authorization}.
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
@EqualsAndHashCode( callSuper = true )
@Data
public class UserSpecificAuthorization extends Authorization
{
    /**
     * The tenant or zone identifier.
     */
    @Nonnull
    protected final String tenantId;

    /**
     * The user name.
     */
    @Nonnull
    protected final String userName;

    /**
     * The wrapped authorization.
     */
    @Nonnull
    protected final Authorization authorization;

    /**
     * Creates a new authorization based on the tenant/zone identifier, the user name, and another
     * {@code Authorization}.
     *
     * @param tenantId
     *            The tenant or zone identifier of this {@code Authorization}.
     * @param userName
     *            The user name of this {@code Authorization}.
     * @param authorization
     *            The {@code Authorization} to be considered tenant- and user-specific.
     */
    public UserSpecificAuthorization(
        @Nonnull final String tenantId,
        @Nonnull final String userName,
        @Nonnull final Authorization authorization )
    {
        super(tenantId + ":" + userName + ":" + authorization.getName());

        this.tenantId = tenantId;
        this.userName = userName;
        this.authorization = authorization;
    }
}
