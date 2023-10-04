/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

/**
 * BAPI specific implementation of the {@link AbstractTransactionFactory}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class BapiTransactionFactory extends AbstractTransactionFactory<BapiRequest, BapiRequestResult>
{
    @Override
    @Nonnull
    protected SoapTransaction<BapiRequest, BapiRequestResult> createSoapTransaction()
    {
        return new SoapTransaction<>(
            new SoapRemoteFunctionRequestSerializer<>(BapiRequestResult.class),
            new com.sap.cloud.sdk.s4hana.connectivity.ErpHttpRequestExecutor<>(),
            new com.sap.cloud.sdk.s4hana.connectivity.ErpHttpRequestExecutor<>());
    }

    @Override
    @Nonnull
    protected JCoTransaction<BapiRequest, BapiRequestResult> createJCoTransaction(
        @Nonnull final String destinationName )
        throws RemoteFunctionException
    {
        return new JCoTransaction<>(destinationName, BapiRequestResult::new);
    }
}
