/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown when a certain service refuses the logon attempt.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@NoArgsConstructor
@Deprecated
public class LogonErrorException extends RequestExecutionException
{
    private static final long serialVersionUID = 6813423684760237979L;

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     */
    public LogonErrorException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The error cause.
     */
    public LogonErrorException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     * @param cause
     *            The error cause.
     */
    public LogonErrorException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
