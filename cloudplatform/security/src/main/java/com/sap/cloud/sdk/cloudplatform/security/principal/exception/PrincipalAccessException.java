/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.principal.exception;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalFacade;

import lombok.NoArgsConstructor;

/**
 * An exception that is thrown in case of any errors in the {@link PrincipalFacade}.
 */
@NoArgsConstructor
public class PrincipalAccessException extends RuntimeException
{
    private static final long serialVersionUID = 5840394077284810685L;

    /**
     * Creates a new {@code PrincipalAccessException} with the given message.
     *
     * @param message
     *            The message of this {@code PrincipalAccessException}.
     */
    public PrincipalAccessException( @Nonnull final String message )
    {
        super(message);
    }

    /**
     * Creates a new {@code PrincipalAccessException} with the given cause.
     *
     * @param cause
     *            The reason this exception is thrown.
     */
    public PrincipalAccessException( @Nonnull final Throwable cause )
    {
        super(cause);
    }

    /**
     * Creates a new {@code PrincipalAccessException} with the given message and the given cause.
     *
     * @param message
     *            The message of this {@code PrincipalAccessException}.
     * @param cause
     *            The reason this exception is thrown.
     */
    public PrincipalAccessException( @Nonnull final String message, @Nonnull final Throwable cause )
    {
        super(message, cause);
    }
}
