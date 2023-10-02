/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.annotation.Nonnull;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

import io.vavr.control.Option;

@Deprecated
public abstract class AbstractRemoteFunctionExceptionHandlingTest<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>, TransactionFactoryT extends AbstractTransactionFactory<RequestT, RequestResultT>, TransactionT extends Transaction<RequestT, RequestResultT>>
{
    private static final String WARNING_MESSAGE_TEXT = "Some special warning happened for test purposes.";
    private static final String ERROR_MESSAGE_TEXT = "Some suspicious error happened for test purposes.";
    private static final String INFORMATION_MESSAGE_TEXT = "Some interesting information here for test purposes.";

    private Destination destination;

    @Before
    public void before()
    {
        destination = DefaultDestination.builder().build();

        mockedTransactionLogic = getMockedTransactionLogic();
    }

    protected abstract TransactionFactoryT getNakedTransactionFactoryMock();

    protected abstract RequestT getRequestInstance( final CommitStrategy commitStrategy );

    protected abstract RequestResultT getMockedRequestResult();

    protected abstract TransactionT getMockedTransactionLogic();

    private TransactionT mockedTransactionLogic;

    @Test
    public void testRequestResultHasErrorsAndExceptionExpected()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final TransactionFactoryT mockedTransactionFactory = getMockedTransactionFactory(true);

        //explicit usage of strict result handler
        final RequestT request = getRequestInstance(CommitStrategy.COMMIT_SYNC);
        request.withImportingAsReturn("RETURN", "BAPIRET2");

        request.propagatingErrorsAsExceptions();

        assertThatExceptionOfType(RemoteFunctionException.class)
            .isThrownBy(
                () -> new RemoteFunctionRequestExecutor<>(mockedTransactionFactory).execute(destination, request))
            .withMessageContaining(WARNING_MESSAGE_TEXT)
            .withMessageContaining(INFORMATION_MESSAGE_TEXT)
            .withMessageContaining(ERROR_MESSAGE_TEXT);
    }

    @Test
    public void testRequestResultHasErrorsAndExceptionExpectedWithoutExplicitlyChosenErrorHandler()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final TransactionFactoryT mockedTransactionFactory = getMockedTransactionFactory(true);

        //no explicit mentioning of result handler
        final RequestT request = getRequestInstance(CommitStrategy.COMMIT_SYNC);
        request.withImportingAsReturn("RETURN", "BAPIRET2");

        assertThatExceptionOfType(RemoteFunctionException.class)
            .isThrownBy(
                () -> new RemoteFunctionRequestExecutor<>(mockedTransactionFactory).execute(destination, request))
            .withMessageContaining(WARNING_MESSAGE_TEXT)
            .withMessageContaining(INFORMATION_MESSAGE_TEXT)
            .withMessageContaining(ERROR_MESSAGE_TEXT);
    }

    @Test
    public void testRequestResultHasErrorsAndExceptionNotThrown()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final TransactionFactoryT mockedTransactionFactory = getMockedTransactionFactory(true);

        final RequestT request = getRequestInstance(CommitStrategy.COMMIT_SYNC);
        request.withImportingAsReturn("RETURN", "BAPIRET2");

        request.ignoringErrors();

        final RequestResultT requestResult =
            new RemoteFunctionRequestExecutor<>(mockedTransactionFactory).execute(destination, request);

        assertThat(requestResult.getErrorMessages()).hasSize(1);
        assertThat(requestResult.getInformationMessages()).hasSize(1);
        assertThat(requestResult.getWarningMessages()).hasSize(1);
    }

    @Test
    public void testRequestResultHasNoErrors()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final TransactionFactoryT mockedTransactionFactory = getMockedTransactionFactory(false);

        final RequestT request = getRequestInstance(CommitStrategy.COMMIT_SYNC);
        request.withImportingAsReturn("RETURN", "BAPIRET2");

        final RequestResultT requestResult =
            new RemoteFunctionRequestExecutor<>(mockedTransactionFactory).execute(destination, request);

        assertThat(requestResult.getInformationMessages()).hasSize(1);
        assertThat(requestResult.getWarningMessages()).hasSize(1);
        assertThat(requestResult.getErrorMessages()).isEmpty();
    }

    @Test
    public void testRequestResultHasErrorsAndCustomExceptionExpected()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final TransactionFactoryT mockedTransactionFactory = getMockedTransactionFactory(true);

        final RequestT request = getRequestInstance(CommitStrategy.COMMIT_SYNC);
        request.withImportingAsReturn("RETURN", "BAPIRET2");

        request.withErrorHandler(new CustomExceptionRemoteFunctionErrorHandler());

        assertThatExceptionOfType(CustomRemoteFunctionException.class)
            .isThrownBy(
                () -> new RemoteFunctionRequestExecutor<>(mockedTransactionFactory).execute(destination, request));
    }

    @Test
    public void test_RequestWithCommitHasErrors_RollbackIsTriggered_UsingStrictResultHandler()
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final TransactionFactoryT mockedTransactionFactory = getMockedTransactionFactory(true);

        final RequestT request = getRequestInstance(CommitStrategy.COMMIT_SYNC);
        request.withImportingAsReturn("RETURN", "BAPIRET2");

        request.propagatingErrorsAsExceptions();

        assertThatExceptionOfType(RemoteFunctionException.class)
            .isThrownBy(
                () -> new RemoteFunctionRequestExecutor<>(mockedTransactionFactory).execute(destination, request));

        verify(mockedTransactionLogic).rollback(any(Destination.class), eq(request));
    }

    private static final class CustomExceptionRemoteFunctionErrorHandler implements RemoteFunctionRequestErrorHandler
    {
        @Nonnull
        @Override
        public <
            RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
            Option<RemoteFunctionException>
            handleRequestResult( @Nonnull final RequestResultT requestResult )
        {
            return Option.of(new CustomRemoteFunctionException());
        }
    }

    private static final class CustomRemoteFunctionException extends RemoteFunctionException
    {

        private static final long serialVersionUID = -3333414179855029448L;
    }

    @SuppressWarnings( "unchecked" )
    private TransactionFactoryT getMockedTransactionFactory( final boolean requestResultHasErrors )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final TransactionFactoryT mockedTransactionFactory = getNakedTransactionFactoryMock();

        final TransactionT mockedTransaction = mockedTransactionLogic;

        final RequestResultT mockedRequestResult = getMockedRequestResult();

        when(mockedTransactionFactory.createTransaction(any(Destination.class))).thenReturn(mockedTransaction);
        when(mockedTransaction.execute(any(Destination.class), any())).thenReturn(mockedRequestResult);
        when(mockedRequestResult.hasFailed()).thenReturn(requestResultHasErrors);

        final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> warningMessages =
            Lists
                .newArrayList(
                    new com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage(
                        com.sap.cloud.sdk.s4hana.serialization.MessageType.WARNING,
                        com.sap.cloud.sdk.s4hana.serialization.MessageClass.BK,
                        new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("123"),
                        WARNING_MESSAGE_TEXT));

        when(mockedRequestResult.getWarningMessages()).thenReturn(warningMessages);

        final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> informationMessages =
            Lists
                .newArrayList(
                    new com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage(
                        com.sap.cloud.sdk.s4hana.serialization.MessageType.INFORMATION,
                        com.sap.cloud.sdk.s4hana.serialization.MessageClass.KW,
                        new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("666"),
                        INFORMATION_MESSAGE_TEXT));

        when(mockedRequestResult.getInformationMessages()).thenReturn(informationMessages);

        if( requestResultHasErrors ) {
            final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> errorMessages =
                Lists
                    .newArrayList(
                        new com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage(
                            com.sap.cloud.sdk.s4hana.serialization.MessageType.ERROR,
                            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
                            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("999"),
                            ERROR_MESSAGE_TEXT));

            when(mockedRequestResult.getErrorMessages()).thenReturn(errorMessages);
        }

        return mockedTransactionFactory;
    }
}
