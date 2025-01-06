package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nullable;

/**
 * Custom runtime exception that is thrown in cases of unrecoverable errors.
 */
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
