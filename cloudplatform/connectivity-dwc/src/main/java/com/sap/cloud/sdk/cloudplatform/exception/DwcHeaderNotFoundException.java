/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.exception;

import javax.annotation.Nonnull;

/**
 * Represents an {@link Throwable} that is thrown if a specific Deploy with Confidence header was not found.
 */
public class DwcHeaderNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 1089454219933367075L;

    /**
     * Initializes a new instance of the {@link DwcHeaderNotFoundException} class.
     *
     * @param message
     *            The exception message.
     */
    public DwcHeaderNotFoundException( @Nonnull final String message )
    {
        super(message);
    }

    /**
     * Initializes a new instance of the {@link DwcHeaderNotFoundException} class.
     *
     * @param message
     *            The exception message.
     * @param throwable
     *            The {@link Throwable}, which caused this exception.
     */
    public DwcHeaderNotFoundException( @Nonnull final String message, @Nonnull final Throwable throwable )
    {
        super(message, throwable);
    }
}
