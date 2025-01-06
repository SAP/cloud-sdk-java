/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Indicates an exception related to the serialization of a request.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@NoArgsConstructor
@Deprecated
public class RequestSerializationException extends RuntimeException
{
    private static final long serialVersionUID = -8840298431856936141L;

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     */
    public RequestSerializationException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The error cause.
     */
    public RequestSerializationException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     * @param cause
     *            The error cause.
     */
    public RequestSerializationException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
