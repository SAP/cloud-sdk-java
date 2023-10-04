/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown, when the authentication failed because of problems in OAuth.
 */
@NoArgsConstructor
public class DestinationOAuthTokenException extends DestinationAccessException
{
    private static final long serialVersionUID = 883765958865763104L;

    /**
     * Initializes the exception by setting a custom message based on the name of the causing destination.
     *
     * @param destinationName
     *            The name of the destination causing this exception.
     */
    public DestinationOAuthTokenException( @Nullable final String destinationName )
    {
        super(
            destinationName,
            "Failed to access or use OAuth token for destination '"
                + destinationName
                + "'. "
                + "Please make sure that the destination configuration is correct.");
    }

    /**
     * Initializes the exception by delegating the message and the name of the destination causing the exception to the
     * super constructor.
     *
     * @param destinationName
     *            The name of the destination causing this exception.
     * @param message
     *            The exception message.
     */
    public DestinationOAuthTokenException( @Nullable final String destinationName, @Nullable final String message )
    {
        super(destinationName, message);
    }

    /**
     * Initializes the exception by delegating the causing exception and the name of the destination causing the
     * exception to the super constructor.
     *
     * @param destinationName
     *            The name of the destination causing this exception.
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationOAuthTokenException( @Nullable final String destinationName, @Nullable final Throwable cause )
    {
        super(destinationName, cause);
    }

    /**
     * Initializes the exception by delegating the causing exception, the message, and the name of the destination
     * causing the exception to the super constructor.
     *
     * @param destinationName
     *            The name of the destination causing this exception.
     * @param message
     *            The exception message.
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public DestinationOAuthTokenException(
        @Nullable final String destinationName,
        @Nullable final String message,
        @Nullable final Throwable cause )
    {
        super(destinationName, message, cause);
    }
}
