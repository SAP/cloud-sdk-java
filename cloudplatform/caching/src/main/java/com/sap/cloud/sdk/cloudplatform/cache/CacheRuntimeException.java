package com.sap.cloud.sdk.cloudplatform.cache;

import javax.annotation.Nonnull;

/**
 * Exception class dedicated to failing cache calls.
 */
public class CacheRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 4932920177304964469L;

    /**
     * Throw exception with custom string message.
     *
     * @param message
     *            User message to add to the exception.
     */
    public CacheRuntimeException( @Nonnull final String message )
    {
        super(message);
    }

    /**
     * Throw exception with another caused-by exception.
     *
     * @param cause
     *            Exception to use as cause.
     */
    public CacheRuntimeException( @Nonnull final Throwable cause )
    {
        super(cause);
    }
}
