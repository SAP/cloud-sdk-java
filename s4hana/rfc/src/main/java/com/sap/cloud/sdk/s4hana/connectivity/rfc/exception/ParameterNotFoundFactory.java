/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AllArgsConstructor;

/**
 * The Parameter Not Found Exception Factory.
 */
@AllArgsConstructor
public class ParameterNotFoundFactory implements RemoteFunctionExceptionFactory<ParameterNotFoundException>
{
    @Nullable
    private final String parameter;

    /**
     * Creates a Parameter Not Found Exception.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Nonnull
    @Override
    @Deprecated
    public ParameterNotFoundException create(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        return new ParameterNotFoundException(parameter, message);
    }
}
