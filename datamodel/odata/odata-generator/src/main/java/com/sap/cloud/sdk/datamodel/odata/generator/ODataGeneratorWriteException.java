/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import com.sun.codemodel.JClassAlreadyExistsException;

/**
 * Custom runtime exception that is thrown in cases of unrecoverable errors while writing the generated code.
 */
class ODataGeneratorWriteException extends ODataGeneratorException
{
    private static final long serialVersionUID = -1789220812598560787L;

    /**
     * Creates an exception based on the given message.
     *
     * @param message
     *            The error message,
     */
    ODataGeneratorWriteException( final String message )
    {
        super(message);
    }

    /**
     * Creates an exception based on the actual thrown exception.
     *
     * @param cause
     *            The causing exception.
     */
    ODataGeneratorWriteException( final Throwable cause )
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
    ODataGeneratorWriteException( final String message, final Throwable cause )
    {
        super(message, cause);
    }

    /**
     * Creates an exception that wraps an instance of {@link JClassAlreadyExistsException}. Prepares the exception
     * message based on the wrapped {@link JClassAlreadyExistsException} instance.
     *
     * @param cause
     *            The causing exception.
     */
    ODataGeneratorWriteException( final JClassAlreadyExistsException cause )
    {

        super("Java class exists already: " + cause.getExistingClass().fullName(), cause);
    }
}
