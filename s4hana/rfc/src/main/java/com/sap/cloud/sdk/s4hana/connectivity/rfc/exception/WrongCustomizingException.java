/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;

/**
 * The Wrong Customizing Exception.
 */
public class WrongCustomizingException extends RemoteFunctionException
{
    private static final long serialVersionUID = 6378929701942369329L;

    @Getter
    @Nullable
    private final String parameter;

    /**
     * Constructor.
     *
     * @param parameter
     *            The customized parameter.
     * @param message
     *            The exception message.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public WrongCustomizingException(
        @Nullable final String parameter,
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        super(message);
        this.parameter = parameter;
    }
}
