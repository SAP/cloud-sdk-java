package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown if a feature is not supported by the current Cloud platform.
 */
@NoArgsConstructor
public class UnsupportedCloudFeatureException extends RuntimeException
{
    private static final long serialVersionUID = -214417440234165614L;

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     */
    public UnsupportedCloudFeatureException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Exception constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public UnsupportedCloudFeatureException( @Nullable final Throwable cause )
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
    public UnsupportedCloudFeatureException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
