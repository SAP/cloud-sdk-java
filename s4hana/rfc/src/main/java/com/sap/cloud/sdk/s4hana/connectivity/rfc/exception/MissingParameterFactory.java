/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AllArgsConstructor;

/**
 * The Missing Parameter Exception Factory.
 */
@AllArgsConstructor
public class MissingParameterFactory implements RemoteFunctionExceptionFactory<MissingParameterException>
{
    @Nullable
    private final String parameter;

    /**
     * Creates a Missing Parameter Exception.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Nonnull
    @Override
    @Deprecated
    public MissingParameterException create(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        return new MissingParameterException(parameter, message);
    }
}
