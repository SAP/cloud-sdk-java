/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown when some constraint is violated.
 */
@NoArgsConstructor
public class ConstraintViolationException extends RuntimeException
{
    private static final long serialVersionUID = -7592885145390648224L;

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     */
    public ConstraintViolationException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Exception constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public ConstraintViolationException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     */
    public ConstraintViolationException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
