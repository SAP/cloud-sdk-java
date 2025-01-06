package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

@Deprecated
class RfmRequestCommitHandlingTest
{
    private static final String FUNCTION_NAME = "A";
    private static final Destination destination = mock(Destination.class);
    @SuppressWarnings( "unchecked" )
    private static final Transaction<RfmRequest, RfmRequestResult> jcoTransaction =
        (JCoTransaction<RfmRequest, RfmRequestResult>) mock(JCoTransaction.class);
    @SuppressWarnings( "unchecked" )
    private static final Transaction<RfmRequest, RfmRequestResult> soapTransaction =
        (SoapTransaction<RfmRequest, RfmRequestResult>) mock(SoapTransaction.class);

    private RfmTransactionFactory mockTransactionFactory(
        final Transaction<RfmRequest, RfmRequestResult> transaction,
        final RfmRequestResult result )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        when(transaction.execute(eq(destination), any())).thenReturn(result);

        final RfmTransactionFactory transactionFactory = mock(RfmTransactionFactory.class);
        when(transactionFactory.createTransaction(destination)).thenReturn(transaction);

        return transactionFactory;
    }

    private RfmRequestResult mockRequestResult( final boolean executeSucceeds )
    {
        final RfmRequestResult result = mock(RfmRequestResult.class);
        when(result.hasFailed()).thenReturn(!executeSucceeds);

        return result;
    }

    @Test
    void testSuccessfulSoapCommitHandling()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        testSuccessfulCommitHandling(soapTransaction);
    }

    @Test
    void testFailedSoapCommitHandling()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        testFailedCommitHandling(soapTransaction);
    }

    @Test
    void testSuccessfulSoapNonTransactionalCall()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        testSuccessfulNonTransactionalCall(soapTransaction);
    }

    @Test
    void testFailedSoapNonTransactionalCall()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        testFailedNonTransactionalCall(soapTransaction);
    }

    @Test
    void testSuccessfulJCoCommitHandling()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        testSuccessfulCommitHandling(jcoTransaction);
    }

    @Test
    void testFailedJCoCommitHandling()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        testFailedCommitHandling(jcoTransaction);
    }

    @Test
    void testSuccessfulJCoNonTransactionalCall()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        testSuccessfulNonTransactionalCall(jcoTransaction);
    }

    @Test
    void testFailedJCoNonTransactionalCall()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        testFailedNonTransactionalCall(jcoTransaction);
    }

    private void testSuccessfulCommitHandling( final Transaction<RfmRequest, RfmRequestResult> transaction )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest sut = new RfmRequest(FUNCTION_NAME);
        final RfmRequestResult result = mockRequestResult(true);

        sut.setTransactionFactory(mockTransactionFactory(transaction, result));

        final RfmRequestResult executionResult = sut.execute(destination);

        assertThat(executionResult).isSameAs(result);

        verify(transaction).execute(destination, sut);
        verify(transaction).commit(destination, sut);
        verify(transaction, never()).rollback(destination, sut);
    }

    private void testFailedCommitHandling( final Transaction<RfmRequest, RfmRequestResult> transaction )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest sut = new RfmRequest(FUNCTION_NAME);
        final RfmRequestResult result = mockRequestResult(false);

        sut.setTransactionFactory(mockTransactionFactory(transaction, result));

        try {
            sut.execute(destination);
        }
        catch( final RemoteFunctionException e ) {
            assertThat(e).isNotNull();
        }

        verify(transaction).execute(destination, sut);
        verify(transaction).rollback(destination, sut);
        verify(transaction, never()).commit(destination, sut);
    }

    private void testSuccessfulNonTransactionalCall( final Transaction<RfmRequest, RfmRequestResult> transaction )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest sut = new RfmRequest(FUNCTION_NAME, false);
        final RfmRequestResult result = mockRequestResult(true);

        sut.setTransactionFactory(mockTransactionFactory(transaction, result));

        final RfmRequestResult executionResult = sut.execute(destination);

        assertThat(executionResult).isSameAs(result);

        verify(transaction).execute(destination, sut);
        verify(transaction, never()).commit(destination, sut);
        verify(transaction, never()).rollback(destination, sut);
    }

    private void testFailedNonTransactionalCall( final Transaction<RfmRequest, RfmRequestResult> transaction )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest sut = new RfmRequest(FUNCTION_NAME, false);
        final RfmRequestResult result = mockRequestResult(false);

        sut.setTransactionFactory(mockTransactionFactory(transaction, result));

        try {
            sut.execute(destination);
        }
        catch( final RemoteFunctionException e ) {
            assertThat(e).isNotNull();
        }

        verify(transaction).execute(destination, sut);
        verify(transaction, never()).rollback(destination, sut);
        verify(transaction, never()).commit(destination, sut);
    }
}
