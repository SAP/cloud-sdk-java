/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Exception that is thrown in case the request to receive an authentication token fails due to authentication issues
 * with the XSUAA service. In all other exceptional cases a {@link TokenRequestFailedException} is thrown.
 */
public class TokenRequestDeniedException extends RuntimeException
{
    private static final long serialVersionUID = 8476681960548337544L;

    /**
     * Creates a new exception based on the given message.
     *
     * @param message
     *            The message describing the cause of the exception to be created.
     */
    public TokenRequestDeniedException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Creates a new exception based on the given message.
     *
     * @param message
     *            The message describing the cause of the exception to be created.
     * @param cause
     *            The exception cause.
     */
    public TokenRequestDeniedException( @Nullable final String message, @Nonnull final Throwable cause )
    {
        super(message, cause);
    }
}
