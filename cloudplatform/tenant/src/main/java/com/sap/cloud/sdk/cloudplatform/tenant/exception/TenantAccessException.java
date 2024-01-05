/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Runtime exception indicating an issue while accessing a tenant.
 */
@NoArgsConstructor
public class TenantAccessException extends RuntimeException
{
    private static final long serialVersionUID = 8085254905690399283L;

    /**
     * Initializes the exception by delegating the message to the super constructor.
     *
     * @param message
     *            The exception message.
     */
    public TenantAccessException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes the exception by delegating the causing exception to the super constructor.
     *
     * @param cause
     *            The exception that caused the exception to be created.
     */
    public TenantAccessException( @Nullable final Throwable cause )
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
    public TenantAccessException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
