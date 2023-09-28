package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A {@link SimplePrincipalAttribute} holding a String value.
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
public class StringPrincipalAttribute extends SimplePrincipalAttribute<String>
{
    /**
     * Creates an {@link PrincipalAttribute} containing a String value.
     *
     * @param name
     *            The name of this UserAttribute.
     * @param value
     *            The value of this attribute.
     */
    public StringPrincipalAttribute( @Nonnull final String name, @Nullable final String value )
    {
        super(name, value);
    }
}
