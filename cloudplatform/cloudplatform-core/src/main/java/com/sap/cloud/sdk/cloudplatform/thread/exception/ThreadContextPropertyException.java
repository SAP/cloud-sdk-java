/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Exception indicating an issue with a ThreadContext property.
 */
@NoArgsConstructor
public class ThreadContextPropertyException extends RuntimeException
{
    private static final long serialVersionUID = -4099758265561452522L;

    /**
     * Initializes a new {@link ThreadContextPropertyException} instance.
     *
     * @param message
     *            The exception message.
     */
    public ThreadContextPropertyException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes a new {@link ThreadContextPropertyException} instance.
     *
     * @param cause
     *            The exception cause.
     */
    public ThreadContextPropertyException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Initializes a new {@link ThreadContextPropertyException} instance.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     */
    public ThreadContextPropertyException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
