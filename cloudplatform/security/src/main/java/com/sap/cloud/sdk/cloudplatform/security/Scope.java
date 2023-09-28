package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

/**
 * Represents a scope as a form of {@link Authorization}.
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
public class Scope extends Authorization
{
    /**
     * Creates a new scope.
     *
     * @param name
     *            The name of the scope.
     */
    public Scope( @Nonnull final String name )
    {
        super(name);
    }
}
