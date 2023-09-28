package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * Facade interface encapsulating the logic to access {@link Principal} information.
 */
public interface PrincipalFacade
{
    /**
     * Returns a {@link Try} of the current {@link Principal}.
     *
     * @return A {@link Try} of the current {@link Principal}.
     */
    @Nonnull
    Try<Principal> tryGetCurrentPrincipal();
}
