/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import javax.annotation.Nullable;

import com.google.common.annotations.Beta;

/**
 * Custom runtime exception that is thrown in cases of unrecoverable errors.
 */
@Beta
public class ODataGeneratorException extends RuntimeException
{
    private static final long serialVersionUID = 6937287713485851338L;

    /**
     * Creates an exception based on the given message.
     *
     * @param message
     *            The error message,
     */
    public ODataGeneratorException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Creates an exception based on the actual thrown exception.
     *
     * @param cause
     *            The causing exception.
     */
    public ODataGeneratorException( @Nullable final Throwable cause )
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
    public ODataGeneratorException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
