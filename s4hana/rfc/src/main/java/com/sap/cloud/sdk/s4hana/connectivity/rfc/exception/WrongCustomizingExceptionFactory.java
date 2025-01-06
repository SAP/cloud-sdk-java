package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AllArgsConstructor;

/**
 * The Wrong Customizing Exception Factory.
 */
@AllArgsConstructor
public class WrongCustomizingExceptionFactory implements RemoteFunctionExceptionFactory<WrongCustomizingException>
{
    @Nullable
    private final String parameter;

    /**
     * Creates a Wrong Customizing Exception.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Nonnull
    @Override
    @Deprecated
    public WrongCustomizingException create(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        return new WrongCustomizingException(parameter, message);
    }
}
