/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.exception;

import javax.annotation.Nullable;

/**
 * Exception that is thrown in case the request to receive an authentication token fails for any reason that is not
 * related with authentication issues with the XSUAA service. In those cases an {@link TokenRequestDeniedException} is
 * thrown.
 */
public class TokenRequestFailedException extends RuntimeException
{
    private static final long serialVersionUID = 3662837475107402856L;

    /**
     * Creates a new exception based on the given message.
     *
     * @param message
     *            The message describing the cause of the exception to be created.
     */
    public TokenRequestFailedException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Creates a new exception based on the given causing exception.
     *
     * @param cause
     *            The exception that is the cause for the exception to be created.
     */
    public TokenRequestFailedException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Creates a new exception based on the given messagen and causing exception.
     *
     * @param message
     *            The message describing the cause of the exception to be created.
     * @param cause
     *            The exception that is the cause for the exception to be created.
     */
    public TokenRequestFailedException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
