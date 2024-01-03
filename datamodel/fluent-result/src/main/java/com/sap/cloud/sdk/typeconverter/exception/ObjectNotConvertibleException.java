/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.typeconverter.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Constructor used to indicate that the access to the contained value in a {@code ConvertedObject} failed, as the value
 * could not be converted.
 */
@NoArgsConstructor
public class ObjectNotConvertibleException extends RuntimeException
{
    private static final long serialVersionUID = 8788906964482980106L;

    /**
     * Creates the exception by delegating the exception message to the super class.
     *
     * @param message
     *            The message to create the exception with.
     */
    public ObjectNotConvertibleException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Creates the exception by delegating the exception cause to the super class.
     *
     * @param cause
     *            The exception that caused this {@code ObjectNotConvertableException}.
     */
    public ObjectNotConvertibleException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Creates the exception by delegating the exception message and cause to the super class.
     *
     * @param message
     *            The message to create the exception with.
     * @param cause
     *            The exception that caused this {@code ObjectNotConvertableException}.
     */
    public ObjectNotConvertibleException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
