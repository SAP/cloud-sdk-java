/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Exception indicating an issue while running in a ThreadContext.
 */
@NoArgsConstructor
public class ThreadContextExecutionException extends RuntimeException
{
    private static final long serialVersionUID = -2247289423654592408L;

    public ThreadContextExecutionException( @Nullable final String message )
    {
        super(message);
    }

    public ThreadContextExecutionException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    public ThreadContextExecutionException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
