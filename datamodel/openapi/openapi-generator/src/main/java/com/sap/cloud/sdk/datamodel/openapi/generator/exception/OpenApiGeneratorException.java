/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator.exception;

import javax.annotation.Nullable;

/**
 * Custom runtime exception that is thrown in cases of unrecoverable errors.
 */
public class OpenApiGeneratorException extends RuntimeException
{
    private static final long serialVersionUID = -4639591118092112703L;

    /**
     * Creates an exception based on the given message.
     *
     * @param message
     *            The error message,
     */
    public OpenApiGeneratorException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Creates an exception based on the actual thrown exception.
     *
     * @param cause
     *            The causing exception.
     */
    public OpenApiGeneratorException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Creates an exception based on a more specific message and the actual thrown exception.
     *
     * @param message
     *            The more specific message.
     * @param cause
     *            The causing exception.
     */
    public OpenApiGeneratorException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
