/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * Facade for retrieving the current {@link AuthToken}.
 */
@FunctionalInterface
public interface AuthTokenFacade
{
    /**
     * "Null" implementation of this interface, to be used in case no implementation could be determined with the
     * {@link java.util.ServiceLoader} pattern.
     * <p>
     * This method will return a failed {@code Try} in case the {@link #tryGetCurrentToken()} gets invoked.
     */
    AuthTokenFacade NULL =
        () -> Try
            .failure(
                new IllegalStateException(
                    "Method invocation on a \"Null\" implementation of the "
                        + AuthTokenFacade.class.getName()
                        + ". This implies that no actual implementations of this interface could be found via the "
                        + "ServiceLoader pattern on the classpath. Please make sure to provide exactly one "
                        + "implementation."));

    /**
     * Returns a {@link Try} of the current {@link AuthToken}. An {@link AuthToken} is not available if no request is
     * available or the request does not contain an "Authorization" header.
     *
     * @return A {@link Try} of the current {@link AuthToken}.
     */
    @Nonnull
    Try<AuthToken> tryGetCurrentToken();
}
