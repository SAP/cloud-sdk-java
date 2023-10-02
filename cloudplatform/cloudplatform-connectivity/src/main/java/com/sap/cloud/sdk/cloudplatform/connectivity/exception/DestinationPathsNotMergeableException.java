/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

/**
 * This exception is thrown if destination URI paths cannot be merged.
 */
public class DestinationPathsNotMergeableException extends DestinationAccessException
{
    private static final long serialVersionUID = -8810012403657501359L;

    /**
     * Creates a new exception without any referenced destination name.
     */
    public DestinationPathsNotMergeableException()
    {
    }

    /**
     * Initializes the exception by delegating the message to the super constructor.
     *
     * @param message
     *            The exception message.
     */
    public DestinationPathsNotMergeableException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes the exception by delegating the causing exception to the super constructor.
     *
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationPathsNotMergeableException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Initializes the exception by delegating the message and the causing exception to the super constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationPathsNotMergeableException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
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
    public DestinationPathsNotMergeableException(
        @Nullable final String destinationName,
        @Nullable final String message )
    {
        super(destinationName, message);
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
    public DestinationPathsNotMergeableException(
        @Nullable final String destinationName,
        @Nullable final String message,
        @Nullable final Throwable cause )
    {
        super(destinationName, message, cause);
    }
}
