/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

import lombok.Getter;

/**
 * Thrown if a destination cannot be accessed, is not configured correctly, or does not fulfill certain prerequisites.
 */
public class DestinationAccessException extends RuntimeException
{
    private static final long serialVersionUID = 3557617589074638145L;

    @Nullable
    @Getter
    private final String destinationName;

    /**
     * Creates a new exception without any referenced destination name.
     */
    public DestinationAccessException()
    {
        destinationName = null;
    }

    /**
     * Initializes the exception by delegating the message to the super constructor.
     *
     * @param message
     *            The exception message.
     */
    public DestinationAccessException( @Nullable final String message )
    {
        super(message);
        destinationName = null;
    }

    /**
     * Initializes the exception by delegating the causing exception to the super constructor.
     *
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationAccessException( @Nullable final Throwable cause )
    {
        super(cause);
        destinationName = null;
    }

    /**
     * Initializes the exception by delegating the message and the causing exception to the super constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationAccessException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
        destinationName = null;
    }

    /**
     * Initializes the exception by delegating the message to the super constructor and setting the name of the
     * destination causing the exception.
     *
     * @param destinationName
     *            The name of the destination causing this exception.
     * @param message
     *            The exception message.
     */
    public DestinationAccessException( @Nullable final String destinationName, @Nullable final String message )
    {
        super(message);
        this.destinationName = destinationName;
    }

    /**
     * Initializes the exception by delegating the message and the causing exception to the super constructor and
     * setting the name of the destination causing the exception.
     *
     * @param destinationName
     *            The name of the destination causing this exception.
     * @param message
     *            The exception message.
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationAccessException(
        @Nullable final String destinationName,
        @Nullable final String message,
        @Nullable final Throwable cause )
    {
        super(message, cause);
        this.destinationName = destinationName;
    }
}
