/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

@Deprecated
class RemoteFunctionRequestExecutorTest
{
    private static final Destination destination = mock(Destination.class);

    @Test
    void testRfcWithCommitAndSuccess()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest commitRequest = new RfmRequest("SOME_NAME", true);
        final RfmRequestResult mockedResult = mockResult(false);
        final Transaction<RfmRequest, RfmRequestResult> transaction = mockTransactionFlow(mockedResult);
        final AbstractTransactionFactory<RfmRequest, RfmRequestResult> mockedFactory =
            mockRemoteFunctionRequestExecutionLogic(transaction);

        final com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor<RfmRequest, RfmRequestResult> sut =
            new RemoteFunctionRequestExecutor<>(mockedFactory);

        final RfmRequestResult result = sut.execute(destination, commitRequest);

        assertThat(result).isEqualTo(mockedResult);

        verify(transaction).before(destination, commitRequest);
        verify(transaction).execute(destination, commitRequest);
        verify(transaction).commit(destination, commitRequest);
        verify(transaction).after();
        verify(transaction, never()).rollback(any(), any());
    }

    @Test
    void testRfcWithCommitAndExecuteFailure()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest commitRequest = new RfmRequest("SOME_NAME", true);
        final RfmRequestResult mockedResult = mockResult(true);
        final Transaction<RfmRequest, RfmRequestResult> transaction = mockTransactionFlow(mockedResult);
        final AbstractTransactionFactory<RfmRequest, RfmRequestResult> mockedFactory =
            mockRemoteFunctionRequestExecutionLogic(transaction);

        final com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor<RfmRequest, RfmRequestResult> sut =
            new RemoteFunctionRequestExecutor<>(mockedFactory);

        try {
            sut.execute(destination, commitRequest);
        }
        catch( final RemoteFunctionException e ) {
            assertThat(e).isNotNull();
        }

        verify(transaction).before(destination, commitRequest);
        verify(transaction).execute(destination, commitRequest);
        verify(transaction).rollback(destination, commitRequest);
        verify(transaction).after();
        verify(transaction, never()).commit(destination, commitRequest);
    }

    @Test
    void testRfcWithCommitAndCommitFailure()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest commitRequest = new RfmRequest("SOME_NAME", true);
        final RfmRequestResult mockedResult = mockResult(false);
        final Transaction<RfmRequest, RfmRequestResult> transaction = mockTransactionFlow(mockedResult);
        final AbstractTransactionFactory<RfmRequest, RfmRequestResult> mockedFactory =
            mockRemoteFunctionRequestExecutionLogic(transaction);
        doThrow(RemoteFunctionException.class).when(transaction).commit(destination, commitRequest);

        final com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor<RfmRequest, RfmRequestResult> sut =
            new RemoteFunctionRequestExecutor<>(mockedFactory);

        try {
            sut.execute(destination, commitRequest);
        }
        catch( final RemoteFunctionException e ) {
            assertThat(e).isNotNull();
        }

        verify(transaction).before(destination, commitRequest);
        verify(transaction).execute(destination, commitRequest);
        verify(transaction).commit(destination, commitRequest);
        verify(transaction).rollback(destination, commitRequest);
        verify(transaction).after();
    }

    @Test
    void testRfcWithoutCommitAndSuccess()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest commitRequest = new RfmRequest("SOME_NAME", false);
        final RfmRequestResult mockedResult = mockResult(false);
        final Transaction<RfmRequest, RfmRequestResult> transaction = mockTransactionFlow(mockedResult);
        final AbstractTransactionFactory<RfmRequest, RfmRequestResult> mockedFactory =
            mockRemoteFunctionRequestExecutionLogic(transaction);

        final com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor<RfmRequest, RfmRequestResult> sut =
            new RemoteFunctionRequestExecutor<>(mockedFactory);

        final RfmRequestResult result = sut.execute(destination, commitRequest);

        assertThat(mockedResult).isEqualTo(result);

        verify(transaction).before(destination, commitRequest);
        verify(transaction).execute(destination, commitRequest);
        verify(transaction).after();
        verify(transaction, never()).commit(destination, commitRequest);
        verify(transaction, never()).rollback(destination, commitRequest);
    }

    @Test
    void testRfcWithoutCommitAndFailure()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest commitRequest = new RfmRequest("SOME_NAME", false);
        final RfmRequestResult mockedResult = mockResult(true);
        final Transaction<RfmRequest, RfmRequestResult> transaction = mockTransactionFlow(mockedResult);
        final AbstractTransactionFactory<RfmRequest, RfmRequestResult> mockedFactory =
            mockRemoteFunctionRequestExecutionLogic(transaction);

        final com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor<RfmRequest, RfmRequestResult> sut =
            new RemoteFunctionRequestExecutor<>(mockedFactory);

        try {
            sut.execute(destination, commitRequest);
        }
        catch( final RemoteFunctionException e ) {
            assertThat(e).isNotNull();
        }

        verify(transaction).before(destination, commitRequest);
        verify(transaction).execute(destination, commitRequest);
        verify(transaction).after();
        verify(transaction, never()).commit(destination, commitRequest);
        verify(transaction, never()).rollback(destination, commitRequest);
    }

    private static RfmRequestResult mockResult( final boolean hasFailed )
    {
        final RfmRequestResult mockedResult = mock(RfmRequestResult.class);
        when(mockedResult.hasFailed()).thenReturn(hasFailed);
        return mockedResult;
    }

    private static AbstractTransactionFactory<RfmRequest, RfmRequestResult> mockRemoteFunctionRequestExecutionLogic(
        final Transaction<RfmRequest, RfmRequestResult> transaction )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        @SuppressWarnings( "unchecked" )
        final AbstractTransactionFactory<RfmRequest, RfmRequestResult> mockedLogic =
            (AbstractTransactionFactory<RfmRequest, RfmRequestResult>) mock(AbstractTransactionFactory.class);

        when(mockedLogic.createTransaction(any())).thenReturn(transaction);

        return mockedLogic;
    }

    private static Transaction<RfmRequest, RfmRequestResult> mockTransactionFlow( final RfmRequestResult result )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        @SuppressWarnings( "unchecked" )
        final Transaction<RfmRequest, RfmRequestResult> mockedFlow =
            (Transaction<RfmRequest, RfmRequestResult>) mock(Transaction.class);
        when(mockedFlow.execute(any(), any())).thenReturn(result);

        return mockedFlow;
    }
}
