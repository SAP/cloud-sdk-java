/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.exception;

import javax.annotation.Nullable;

import lombok.RequiredArgsConstructor;

/**
 * Thrown if the authorization token cannot be accessed.
 */
@RequiredArgsConstructor
public class AuthTokenAccessException extends RuntimeException
{
    private static final long serialVersionUID = -4945693952502604406L;

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     */
    public AuthTokenAccessException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Exception constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public AuthTokenAccessException( @Nullable final Throwable cause )
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
    public AuthTokenAccessException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
