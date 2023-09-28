package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

/**
 * A simple attribute of a {@link Principal}.
 * <p>
 * An attribute of a user is identified by its name. By this name it can be identified in the
 * {@link Principal#getAttribute(String)} method.
 *
 * @deprecated This is deprecated and should no longer be used. It will remain for compatibility reasons but will no
 *             longer be supported. Please see release notes for more information.
 */
@Deprecated
public interface PrincipalAttribute
{
    /**
     * The name that identifies this attribute.
     *
     * @return The identifying attribute name.
     */
    @Nonnull
    String getName();
}
