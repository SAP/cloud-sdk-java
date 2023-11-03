/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import lombok.NoArgsConstructor;

/**
 * Thrown if multiple bindings are found to a service on SAP Business Technology Platform Cloud Foundry.
 */
@NoArgsConstructor
public class MultipleServiceBindingsException extends CloudPlatformException
{
    private static final long serialVersionUID = -8207497496475088999L;

    /**
     * Default constructor.
     *
     * @param message
     *            The exception message.
     */
    public MultipleServiceBindingsException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Default constructor.
     *
     * @param cause
     *            The exception cause.
     */
    public MultipleServiceBindingsException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Default constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     */
    public MultipleServiceBindingsException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
