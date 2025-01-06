/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType;

import lombok.Getter;

/**
 * This exception is thrown if a {@code DestinationType} is not supported by a method.
 */
public class DestinationTypeNotSupportedException extends DestinationAccessException
{
    private static final long serialVersionUID = 883765958865763104L;

    @Getter
    @Nullable
    private final DestinationType destinationType;

    /**
     * Creates a new exception without any referenced destination name or type.
     */
    public DestinationTypeNotSupportedException()
    {
        this(null, null);
    }

    /**
     * Initializes the exception by setting a custom message based on the destination type and setting the name and the
     * type of the causing destination.
     *
     * @param destinationName
     *            The name of the causing destination.
     * @param destinationType
     *            The type of the causing destination.
     */
    public DestinationTypeNotSupportedException(
        @Nullable final String destinationName,
        @Nullable final DestinationType destinationType )
    {
        super(
            destinationName,
            DestinationType.class.getSimpleName()
                + " "
                + (destinationType != null ? "'" + destinationType + "'" : "")
                + " is not supported.");

        this.destinationType = destinationType;
    }

    /**
     * Initializes the exception by delegating the message to the super constructor and setting the name and the type of
     * the causing destination.
     *
     * @param destinationName
     *            The name of the causing destination.
     * @param destinationType
     *            The type of the causing destination.
     * @param message
     *            The exception message.
     */
    public DestinationTypeNotSupportedException(
        @Nullable final String destinationName,
        @Nullable final DestinationType destinationType,
        @Nullable final String message )
    {
        super(destinationName, message);
        this.destinationType = destinationType;
    }

    /**
     * Initializes the exception by delegating the causing exception to the super constructor and setting the name and
     * type of the causing destination.
     *
     * @param destinationName
     *            The name of the causing destination.
     * @param destinationType
     *            The type of the causing destination.
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationTypeNotSupportedException(
        @Nullable final String destinationName,
        @Nullable final DestinationType destinationType,
        @Nullable final Throwable cause )
    {
        super(destinationName, cause);
        this.destinationType = destinationType;
    }

    /**
     * Initializes the exception by delegating the message and the causing exception to the super constructor and
     * setting the name and the type of the causing destination.
     *
     * @param destinationName
     *            The name of the causing destination.
     * @param destinationType
     *            The type of the causing destination.
     * @param message
     *            The exception message.
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationTypeNotSupportedException(
        @Nullable final String destinationName,
        @Nullable final DestinationType destinationType,
        @Nullable final String message,
        @Nullable final Throwable cause )
    {
        super(destinationName, message, cause);
        this.destinationType = destinationType;
    }
}
