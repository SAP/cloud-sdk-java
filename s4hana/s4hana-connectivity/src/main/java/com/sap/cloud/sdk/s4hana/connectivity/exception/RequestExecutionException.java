/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Thrown when something goes wrong during the execution of a request.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@NoArgsConstructor
@Deprecated
public class RequestExecutionException extends Exception
{
    private static final long serialVersionUID = 1407979994822578835L;

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     */
    public RequestExecutionException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The error cause.
     */
    public RequestExecutionException( @Nullable final Throwable cause )
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
    public RequestExecutionException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
