/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

/**
 * RFC specific implementation of the {@link AbstractTransactionFactory}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class RfmTransactionFactory extends AbstractTransactionFactory<RfmRequest, RfmRequestResult>
{
    @Override
    @Nonnull
    protected SoapTransaction<RfmRequest, RfmRequestResult> createSoapTransaction()
    {
        return new SoapTransaction<>(
            new SoapRemoteFunctionRequestSerializer<>(RfmRequestResult.class),
            new com.sap.cloud.sdk.s4hana.connectivity.ErpHttpRequestExecutor<>(),
            new com.sap.cloud.sdk.s4hana.connectivity.ErpHttpRequestExecutor<>());
    }

    @Override
    @Nonnull
    protected JCoTransaction<RfmRequest, RfmRequestResult> createJCoTransaction( @Nonnull final String destinationName )
        throws RemoteFunctionException
    {
        return new JCoTransaction<>(destinationName, RfmRequestResult::new);
    }
}
