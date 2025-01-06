/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The meta logic used to transactional-like handle the execution of {@link AbstractRemoteFunctionRequest}s.
 *
 * @param <RequestT>
 *            The type of the request to execute.
 * @param <RequestResultT>
 *            The type of the result to return.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class RemoteFunctionRequestExecutor<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
    implements
    com.sap.cloud.sdk.s4hana.connectivity.RequestExecutor<RequestT, RequestResultT>
{
    private final AbstractTransactionFactory<RequestT, RequestResultT> transactionFactory;

    @Override
    @Nonnull
    public RequestResultT execute( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final Transaction<RequestT, RequestResultT> transactionLogic =
            transactionFactory.createTransaction(destination);

        transactionLogic.before(destination, request);

        try {
            final RequestResultT requestResult = transactionLogic.execute(destination, request);

            if( requestResult.hasFailed() ) {
                handleFailedRequestResult(destination, request, transactionLogic, requestResult);
            } else {
                handleSuccessfulRequestResult(destination, request, transactionLogic);
            }

            return requestResult;
        }
        finally {
            transactionLogic.after();
        }
    }

    private void handleSuccessfulRequestResult(
        @Nonnull final Destination destination,
        @Nonnull final RequestT request,
        @Nonnull final Transaction<RequestT, RequestResultT> transactionLogic )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        if( request.isPerformingTransactionalCommit() ) {
            try {
                transactionLogic.commit(destination, request);
            }
            catch( final com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException e ) {
                transactionLogic.rollback(destination, request);
                throw e;
            }
        }
    }

    private void handleFailedRequestResult(
        @Nonnull final Destination destination,
        @Nonnull final RequestT request,
        @Nonnull final Transaction<RequestT, RequestResultT> transactionLogic,
        @Nonnull final RequestResultT requestResult )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        @Nullable
        final Option<RemoteFunctionException> exception =
            request.getRemoteFunctionRequestErrorHandler().handleRequestResult(requestResult);

        if( exception.isDefined() ) {
            if( log.isDebugEnabled() ) {
                log
                    .debug(
                        request.getClass().getSimpleName()
                            + " failed. Triggering rollback for request: "
                            + request
                            + ".",
                        exception);
            }

            if( request.isPerformingTransactionalCommit() ) {
                transactionLogic.rollback(destination, request);
            }

            throw exception.get();
        }

    }
}
