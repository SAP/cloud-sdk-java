/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.core;

import javax.annotation.Nonnull;

/**
 * Thrown if an error occurs during the invocation of a OpenAPI service.
 */
public class OpenApiRequestException extends RuntimeException
{
    private static final long serialVersionUID = -8248392392632616674L;

    /**
     * Thrown if an error occurs during the invocation of a OpenAPI service.
     *
     * @param message
     *            The message of this exception
     */
    public OpenApiRequestException( @Nonnull final String message )
    {
        super(message);
    }

    /**
     * Thrown if an error occurs during the invocation of a OpenAPI service.
     *
     * @param message
     *            The message of this exception
     * @param cause
     *            The cause of this exception
     */
    public OpenApiRequestException( @Nonnull final String message, @Nonnull final Throwable cause )
    {
        super(message, cause);
    }

    /**
     * Thrown if an error occurs during the invocation of a OpenAPI service.
     *
     * @param cause
     *            The cause of this exception
     */
    public OpenApiRequestException( @Nonnull final Throwable cause )
    {
        super(cause);
    }
}
