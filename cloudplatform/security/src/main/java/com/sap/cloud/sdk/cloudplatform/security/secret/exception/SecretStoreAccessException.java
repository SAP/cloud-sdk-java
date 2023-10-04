/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.secret.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Runtime exception indicating that there was an issue while accessing the secret store.
 */
@NoArgsConstructor
public class SecretStoreAccessException extends RuntimeException
{
    private static final long serialVersionUID = -6143038000179303999L;

    /**
     * Initializes the exception by delegating the message to the super constructor.
     *
     * @param message
     *            The exception message.
     */
    public SecretStoreAccessException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes the exception by delegating the causing exception to the super constructor.
     *
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public SecretStoreAccessException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Initializes the exception by delegating the message and the causing exception to the super constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public SecretStoreAccessException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
