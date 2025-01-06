package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;

/**
 * The Remote Function Exception Factory.
 *
 * @param <T>
 *            Generic remote function exception type.
 */
public interface RemoteFunctionExceptionFactory<T extends RemoteFunctionException>
{
    /**
     * Create a new exception for a given remote function message.
     *
     * @param message
     *            The remote function message.
     * @return A new exception instance.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Nonnull
    T create( @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message );
}
