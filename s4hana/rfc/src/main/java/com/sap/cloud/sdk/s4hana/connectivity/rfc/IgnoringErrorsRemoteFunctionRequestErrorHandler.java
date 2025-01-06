/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings( "deprecation" )
class IgnoringErrorsRemoteFunctionRequestErrorHandler implements RemoteFunctionRequestErrorHandler
{
    /**
     * <p>
     * Does not inspect the request result and returns an empty {@link Option} not containing an exception.
     * </p>
     * <p>
     * Therefore, this result handler is <b>tolerant</b> with regards to errors messages in the request result.
     * </p>
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    @Nonnull
    public <
        RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
        Option<RemoteFunctionException>
        handleRequestResult( @Nonnull final RequestResultT requestResult )
    {
        if( log.isDebugEnabled() ) {
            log
                .debug(
                    "Invoking "
                        + getClass().getSimpleName()
                        + " upon processing of remote function. Request result: "
                        + requestResult);
        }

        return Option.none();
    }
}
