/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;

/**
 * Indicates an error during the retrieval of an CSRF token.
 */
@EqualsAndHashCode( callSuper = true )
public class CsrfTokenRetrievalException extends RuntimeException
{
    private static final long serialVersionUID = -1472826476874136669L;

    /**
     * Returns a new CsrfTokenRetrievalException instance.
     *
     * @param message
     *            The error message.
     * @param cause
     *            The exception causing the error.
     */
    public CsrfTokenRetrievalException( @Nullable final String message, @Nullable final Exception cause )
    {
        super(message, cause);
    }

    /**
     * Returns a new CsrfTokenRetrievalException instance.
     *
     * @param message
     *            The error message.
     */
    public CsrfTokenRetrievalException( @Nullable final String message )
    {
        super(message);
    }
}
