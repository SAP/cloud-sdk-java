package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.mockito.Mockito.mock;

@Deprecated
class BapiExceptionHandlingTest
    extends
    AbstractRemoteFunctionExceptionHandlingTest<BapiRequest, BapiRequestResult, BapiTransactionFactory, JCoTransaction<BapiRequest, BapiRequestResult>>
{
    @Override
    protected BapiTransactionFactory getNakedTransactionFactoryMock()
    {
        return mock(BapiTransactionFactory.class);
    }

    @Override
    protected BapiRequest getRequestInstance( final CommitStrategy commitStrategy )
    {
        return new BapiRequest("BAPI_TEST", commitStrategy);
    }

    @Override
    protected BapiRequestResult getMockedRequestResult()
    {
        return mock(BapiRequestResult.class);
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected JCoTransaction<BapiRequest, BapiRequestResult> getMockedTransactionLogic()
    {
        return (JCoTransaction<BapiRequest, BapiRequestResult>) mock(JCoTransaction.class);
    }
}
