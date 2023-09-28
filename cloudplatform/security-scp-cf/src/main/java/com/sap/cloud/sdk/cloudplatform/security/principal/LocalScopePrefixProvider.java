package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * The interface representing the strategy used by the {@link LocalScopePrefixExtractor} to determine the local scope
 * prefix (i.e. prefix used to decide whether the given scope is local to an application). You can provide your own
 * implementation of this interface to an {@link ScpCfPrincipalFacade} or {@link ScpCfPrincipal} to change the behavior
 * at runtime.
 *
 * @deprecated To be removed without replacement. Please refer to release notes for more information.
 */
@Deprecated
public interface LocalScopePrefixProvider
{
    /**
     * Returns the local scope prefix determined by the given implementation.
     *
     * @return A {@link Try} if the String representing the local scope prefix.
     */
    @Nonnull
    Try<String> getLocalScopePrefix();
}
