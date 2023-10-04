/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown in case of an exception that should not happen. Indicates that a critical assertion is violated.
 */
@NoArgsConstructor
public class ShouldNotHappenException extends RuntimeException
{
    private static final long serialVersionUID = -4649892790941627059L;

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     */
    public ShouldNotHappenException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Exception constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public ShouldNotHappenException( @Nullable final Throwable cause )
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
    public ShouldNotHappenException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
