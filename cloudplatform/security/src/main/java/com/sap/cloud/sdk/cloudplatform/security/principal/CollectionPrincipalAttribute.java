package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.Collection;

import javax.annotation.Nonnull;

import lombok.Data;

/**
 * A {@link PrincipalAttribute} holding a generic collection of values.
 *
 * @param <T>
 *            The type of the collection.
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
@Data
public class CollectionPrincipalAttribute<T> implements PrincipalAttribute
{
    @Nonnull
    private final String name;

    @Nonnull
    private final Collection<T> values;
}
