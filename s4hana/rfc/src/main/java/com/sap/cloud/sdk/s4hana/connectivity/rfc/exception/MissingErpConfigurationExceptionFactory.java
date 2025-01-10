package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AllArgsConstructor;

/**
 * The Missing ERP Configuration Exception Factory.
 */
@AllArgsConstructor
public class MissingErpConfigurationExceptionFactory
    implements
    RemoteFunctionExceptionFactory<MissingErpConfigException>
{
    @Nullable
    private final String parameter;

    /**
     * Creates a Missing ERP Configuration Exception.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Nonnull
    @Override
    @Deprecated
    public MissingErpConfigException create(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        return new MissingErpConfigException(parameter, message);
    }
}
