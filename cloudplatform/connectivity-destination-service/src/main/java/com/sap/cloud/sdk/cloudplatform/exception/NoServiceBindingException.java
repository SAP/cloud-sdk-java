package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown if no binding is found to a service on SAP Business Technology Platform Cloud Foundry.
 */
@NoArgsConstructor
public class NoServiceBindingException extends CloudPlatformException
{
    private static final long serialVersionUID = -2801123460779872810L;

    /**
     * Initializes a new {@link NoServiceBindingException} instance.
     *
     * @param message
     *            The exception message.
     */
    public NoServiceBindingException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes a new {@link NoServiceBindingException} instance.
     *
     * @param cause
     *            The exception cause.
     */
    public NoServiceBindingException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Initializes a new {@link NoServiceBindingException} instance.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     */
    public NoServiceBindingException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
