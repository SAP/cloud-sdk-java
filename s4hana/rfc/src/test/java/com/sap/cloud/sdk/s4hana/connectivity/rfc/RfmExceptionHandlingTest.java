/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.mockito.Mockito.mock;

@Deprecated
class RfmExceptionHandlingTest
    extends
    AbstractRemoteFunctionExceptionHandlingTest<RfmRequest, RfmRequestResult, RfmTransactionFactory, JCoTransaction<RfmRequest, RfmRequestResult>>
{
    @Override
    protected RfmTransactionFactory getNakedTransactionFactoryMock()
    {
        return mock(RfmTransactionFactory.class);
    }

    @Override
    protected RfmRequest getRequestInstance( final CommitStrategy commitStrategy )
    {
        return new RfmRequest("RFC_TEST", commitStrategy);
    }

    @Override
    protected RfmRequestResult getMockedRequestResult()
    {
        return mock(RfmRequestResult.class);
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected JCoTransaction<RfmRequest, RfmRequestResult> getMockedTransactionLogic()
    {
        return (JCoTransaction<RfmRequest, RfmRequestResult>) mock(JCoTransaction.class);
    }
}
