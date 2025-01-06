/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown if the BasicAuthentication credentials cannot be accessed.
 */
@NoArgsConstructor
public class BasicAuthenticationAccessException extends RuntimeException
{
    private static final long serialVersionUID = -833946322233768505L;

    /**
     * Creates a new exception based on the given message.
     *
     * @param message
     *            A message describing the exception cause.
     */
    public BasicAuthenticationAccessException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Creates a new exception based on the given message.
     *
     * @param message
     *            A message describing the exception cause.
     * @param t
     *            A cause that lead to the exception.
     */
    public BasicAuthenticationAccessException( @Nullable final String message, @Nonnull final Throwable t )
    {
        super(message, t);
    }
}
