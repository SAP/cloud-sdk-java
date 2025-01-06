/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * This exception is thrown if the HTTP Client could not be build.
 */
@NoArgsConstructor
public class HttpClientInstantiationException extends RuntimeException
{
    private static final long serialVersionUID = 772265653882716543L;

    /**
     * Initializes the exception by delegating the message to the super constructor.
     *
     * @param message
     *            The exception message.
     */
    public HttpClientInstantiationException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes the exception by delegating the causing exception to the super constructor.
     *
     * @param cause
     *            The causing exception.
     */
    public HttpClientInstantiationException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Initializes the exception by delegating the message and the causing exception to the super constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The causing exception.
     */
    public HttpClientInstantiationException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
