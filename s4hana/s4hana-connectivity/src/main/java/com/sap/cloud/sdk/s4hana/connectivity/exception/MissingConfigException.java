package com.sap.cloud.sdk.s4hana.connectivity.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown when configuration for accessing a certain system is missing.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@NoArgsConstructor
@Deprecated
public class MissingConfigException extends RequestExecutionException
{
    private static final long serialVersionUID = 472053808276154534L;

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     */
    public MissingConfigException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The error cause.
     */
    public MissingConfigException( @Nullable final Throwable cause )
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
    public MissingConfigException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
