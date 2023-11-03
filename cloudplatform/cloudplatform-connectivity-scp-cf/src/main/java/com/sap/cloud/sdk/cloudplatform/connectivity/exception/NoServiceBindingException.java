/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity.exception;

import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import lombok.NoArgsConstructor;

/**
 * Thrown if no binding is found to a service on SAP Business Technology Platform Cloud Foundry.
 */
@NoArgsConstructor
public class NoServiceBindingException extends CloudPlatformException
{
    private static final long serialVersionUID = -2801123460779872810L;

    public NoServiceBindingException( @Nullable final String message )
    {
        super(message);
    }

    public NoServiceBindingException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    public NoServiceBindingException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
