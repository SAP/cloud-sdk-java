/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SuppressWarnings( "deprecation" )
abstract class AbstractTransactionFactory<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
{
    @Nonnull
    protected abstract SoapTransaction<RequestT, RequestResultT> createSoapTransaction();

    /**
     * Retrieves the {@link com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor} to use.
     *
     * @return The {@link com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor} instance.
     */
    @Nonnull
    Transaction<RequestT, RequestResultT> createTransaction( @Nonnull final Destination destination )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            DestinationNotFoundException,
            DestinationAccessException,
            RemoteFunctionException
    {
        if( destination.isHttp() ) {
            return createSoapTransaction();
        } else if( destination.isRfc() ) {
            return createJCoTransaction(destination.asRfc().get("Name", String.class::cast).get());
        } else {
            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException(
                "Failed to create "
                    + com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor.class.getSimpleName()
                    + ": Destination must be of type HTTP or RFC, but was none of them.");

        }
    }

    @Nonnull
    protected abstract JCoTransaction<RequestT, RequestResultT> createJCoTransaction(
        @Nonnull final String destinationName )
        throws RemoteFunctionException;
}
