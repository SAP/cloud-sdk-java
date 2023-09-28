package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.Collection;

import javax.annotation.Nonnull;

/**
 * A {@link CollectionPrincipalAttribute} holding String values.
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
public class StringCollectionPrincipalAttribute extends CollectionPrincipalAttribute<String>
{
    /**
     * Creates an {@link CollectionPrincipalAttribute} containing String values.
     *
     * @param name
     *            The name of this CollectionUserAttribute.
     * @param values
     *            The values of this attribute.
     */
    public StringCollectionPrincipalAttribute( @Nonnull final String name, @Nonnull final Collection<String> values )
    {
        super(name, values);
    }
}
