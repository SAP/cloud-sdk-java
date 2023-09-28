package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Data;

/**
 * A {@link PrincipalAttribute} holding a simple generic value.
 *
 * @param <T>
 *            The type of the attribute.
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
@Data
public class SimplePrincipalAttribute<T> implements PrincipalAttribute
{
    @Nonnull
    private final String name;

    @Nullable
    private final T value;
}
