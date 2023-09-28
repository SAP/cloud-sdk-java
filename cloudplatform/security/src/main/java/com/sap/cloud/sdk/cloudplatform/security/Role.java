package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

/**
 * A role a user can assume as a form of {@code Authorization}.
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
public class Role extends Authorization
{
    /**
     * Creates a new role.
     *
     * @param name
     *            The name of the role.
     */
    public Role( @Nonnull final String name )
    {
        super(name);
    }
}
