/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

/**
 * Custom runtime exception that is thrown in cases of unrecoverable errors while reading and processing the given
 * input.
 */
class ODataGeneratorReadException extends ODataGeneratorException
{
    private static final long serialVersionUID = -3106708037097370631L;

    /**
     * Creates an exception based on the given message.
     *
     * @param message
     *            The error message,
     */
    ODataGeneratorReadException( final String message )
    {
        super(message);
    }

    /**
     * Creates an exception based on the actual thrown exception.
     *
     * @param cause
     *            The causing exception.
     */
    ODataGeneratorReadException( final Throwable cause )
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
    ODataGeneratorReadException( final String message, final Throwable cause )
    {
        super(message, cause);
    }
}
