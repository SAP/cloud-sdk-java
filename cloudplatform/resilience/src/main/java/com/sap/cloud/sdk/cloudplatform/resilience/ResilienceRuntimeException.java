/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Exception class dedicated to failing resilient functions.
 */
@NoArgsConstructor
public class ResilienceRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 8256471661873163966L;

    /**
     * Throw exception with another caused-by exception.
     *
     * @param cause
     *            Exception to use as cause.
     */
    public ResilienceRuntimeException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Throw exception with custom string message.
     *
     * @param message
     *            User message to add to the exception.
     */
    public ResilienceRuntimeException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Throw exception with custom string message and another caused-by exception.
     *
     * @param message
     *            User message to add to the exception.
     * @param cause
     *            Exception to use as cause.
     */
    public ResilienceRuntimeException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
