/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown when the version of a system or service is not supported.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@NoArgsConstructor
@Deprecated
public class VersionNotSupportedException extends Exception
{
    private static final long serialVersionUID = -1877899746719623383L;

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     */
    public VersionNotSupportedException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The error cause.
     */
    public VersionNotSupportedException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     * @param cause
     *            The error cause.
     */
    public VersionNotSupportedException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
