package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nullable;

import lombok.RequiredArgsConstructor;

/**
 * Thrown if the request headers cannot be accessed.
 */
@RequiredArgsConstructor
public class RequestHeadersAccessException extends RuntimeException
{
    private static final long serialVersionUID = -4402302735368094185L;

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     */
    public RequestHeadersAccessException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Exception constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public RequestHeadersAccessException( @Nullable final Throwable cause )
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
    public RequestHeadersAccessException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
