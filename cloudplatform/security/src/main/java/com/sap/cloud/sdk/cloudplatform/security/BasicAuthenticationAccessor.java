/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Static accessor to retrieve the BasicCredentials of the currently incoming request.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class BasicAuthenticationAccessor
{
    /**
     * The actual implementation used to forward the business logic calls to.
     */
    @Getter
    @Nonnull
    private static BasicAuthenticationFacade basicAuthenticationFacade = new DefaultBasicAuthenticationFacade();

    /**
     * Overrides the currently set facade with the given one, potentially changing the behavior of all following calls
     * to the {@code BasicAuthenticationAccessor}.
     * <p>
     * In case the given facade is {@code null} the default implementation will be reinstated.
     *
     * @param facade
     *            The facade to use for all future calls of the {@code BasicAuthenticationAccessor}. If {@code null} is
     *            passed, the {@link DefaultBasicAuthenticationFacade} will be used instead.
     */
    public static void setBasicAuthenticationFacade( @Nullable final BasicAuthenticationFacade facade )
    {
        basicAuthenticationFacade = Option.of(facade).getOrElse(DefaultBasicAuthenticationFacade::new);
    }

    /**
     * Getter to retrieve the basic credentials given by the currently incoming request.
     * <p>
     * If the incoming request contains a basic authentication header the {@code Try} will contain this header as a
     * {@link BasicCredentials} object. If the header was not given, was no basic authentication header or could not be
     * decoded the {@code Try} will contain the corresponding exception.
     *
     * @return A {@code Try} containing either the {@code BasicCredentials} or an exception describing the error why the
     *         credentials could not be retrieved.
     */
    @Nonnull
    public static Try<BasicCredentials> tryGetCurrentBasicCredentials()
    {
        return basicAuthenticationFacade.tryGetBasicCredentials();
    }
}
