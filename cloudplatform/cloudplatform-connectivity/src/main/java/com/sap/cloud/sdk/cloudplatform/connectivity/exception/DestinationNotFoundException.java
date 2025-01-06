/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

import lombok.Getter;

/**
 * Runtime exception indicating that a destination cannot be found.
 */
public class DestinationNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 8880115609598781458L;

    @Getter
    @Nullable
    private final String destinationName;

    /**
     * Creates a new exception without any referenced destination name.
     */
    public DestinationNotFoundException()
    {
        this((String) null);
    }

    /**
     * Initializes the exception by setting a custom message based on the name of the causing destination.
     *
     * @param destinationName
     *            The name of the destination causing this exception.
     */
    public DestinationNotFoundException( @Nullable final String destinationName )
    {
        super("Destination " + (destinationName != null ? "'" + destinationName + "'" : "") + " not found.");
        this.destinationName = destinationName;
    }

    /**
     * Initializes the exception by delegating the causing exception to the super constructor.
     *
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationNotFoundException( @Nullable final Throwable cause )
    {
        super(cause);
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
    public DestinationNotFoundException( @Nullable final String destinationName, @Nullable final String message )
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
    public DestinationNotFoundException(
        @Nullable final String destinationName,
        @Nullable final String message,
        @Nullable final Throwable cause )
    {
        super(message, cause);
        this.destinationName = destinationName;
    }
}
