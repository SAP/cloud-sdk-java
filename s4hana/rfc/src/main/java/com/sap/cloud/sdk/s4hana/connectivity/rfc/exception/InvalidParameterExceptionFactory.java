/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AllArgsConstructor;

/**
 * The Invalid Parameter Exception Factory.
 */
@AllArgsConstructor
public class InvalidParameterExceptionFactory implements RemoteFunctionExceptionFactory<InvalidParameterException>
{
    @Nullable
    private final String parameter;

    /**
     * Creates an Invalid Parameter Exception.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Nonnull
    @Override
    @Deprecated
    public InvalidParameterException create(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        return new InvalidParameterException(parameter, message);
    }
}
