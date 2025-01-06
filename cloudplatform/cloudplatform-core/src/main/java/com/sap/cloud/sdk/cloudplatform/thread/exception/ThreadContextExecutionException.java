/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Exception indicating an issue while running in a ThreadContext.
 */
@NoArgsConstructor
public class ThreadContextExecutionException extends RuntimeException
{
    private static final long serialVersionUID = -2247289423654592408L;

    /**
     * Initializes a new {@link ThreadContextExecutionException} instance.
     *
     * @param message
     *            The exception message.
     */
    public ThreadContextExecutionException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes a new {@link ThreadContextExecutionException} instance.
     *
     * @param cause
     *            The exception cause.
     */
    public ThreadContextExecutionException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Initializes a new {@link ThreadContextExecutionException} instance.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     */
    public ThreadContextExecutionException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
