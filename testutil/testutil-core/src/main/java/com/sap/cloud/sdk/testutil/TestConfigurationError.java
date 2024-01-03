/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown where there is an error in the test configuration.
 */
@NoArgsConstructor
public class TestConfigurationError extends Error //NOCHECKSTYLE
{
    private static final long serialVersionUID = -398795063502340763L;

    /**
     * Constructor.
     *
     * @param message
     *            The error error.
     */
    public TestConfigurationError( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The error cause.
     */
    public TestConfigurationError( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *            The error error.
     * @param cause
     *            The error cause.
     */
    public TestConfigurationError( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
