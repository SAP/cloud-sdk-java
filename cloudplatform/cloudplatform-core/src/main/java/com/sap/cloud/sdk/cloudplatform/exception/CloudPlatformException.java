package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown for issues that are related to a Cloud platform.
 */
@NoArgsConstructor
public class CloudPlatformException extends RuntimeException
{
    private static final long serialVersionUID = -7764991764802352339L;

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     */
    public CloudPlatformException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Exception constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public CloudPlatformException( @Nullable final Throwable cause )
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
    public CloudPlatformException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
