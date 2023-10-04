/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown for issues that indicate problems due to a missing dependency.
 */
@NoArgsConstructor
public class DependencyNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = -3340082727416182209L;

    /**
     * Exception constructor.
     *
     * @param message
     *            The exception message.
     */
    public DependencyNotFoundException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Exception constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public DependencyNotFoundException( @Nullable final Throwable cause )
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
    public DependencyNotFoundException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
