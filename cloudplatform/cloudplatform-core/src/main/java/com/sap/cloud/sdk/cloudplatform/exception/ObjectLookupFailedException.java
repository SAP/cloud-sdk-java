/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown if the lookup of an object fails.
 */
@NoArgsConstructor
public class ObjectLookupFailedException extends RuntimeException
{
    private static final long serialVersionUID = 2710460816605775587L;

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     */
    public ObjectLookupFailedException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Exception constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public ObjectLookupFailedException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     */
    public ObjectLookupFailedException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
