/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Exception indicating an issue while accessing a ThreadContext.
 */
@NoArgsConstructor
public class ThreadContextAccessException extends RuntimeException
{
    private static final long serialVersionUID = -3333880351273201038L;

    /**
     * Initializes a new {@link ThreadContextAccessException} instance.
     *
     * @param message
     *            The exception message.
     */
    public ThreadContextAccessException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes a new {@link ThreadContextAccessException} instance.
     *
     * @param cause
     *            The exception cause.
     */
    public ThreadContextAccessException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Initializes a new {@link ThreadContextAccessException} instance.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     */
    public ThreadContextAccessException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
