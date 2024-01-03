/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;

/**
 * The Parameter Not Found Exception.
 */
public class ParameterNotFoundException extends RemoteFunctionException
{
    private static final long serialVersionUID = 2648164770398903288L;

    @Getter
    @Nullable
    private final String parameter;

    /**
     * Constructor.
     *
     * @param parameter
     *            The parameter.
     * @param message
     *            The remote function message.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public ParameterNotFoundException(
        @Nullable final String parameter,
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        super(message);
        this.parameter = parameter;
    }
}
