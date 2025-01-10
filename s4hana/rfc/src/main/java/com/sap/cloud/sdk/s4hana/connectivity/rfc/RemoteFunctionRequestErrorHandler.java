package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

import io.vavr.control.Option;

/**
 * Gets invoked after the execution of an {@link AbstractRemoteFunctionRequest} and allows to inspect and react on the
 * {@link AbstractRemoteFunctionRequestResult}.
 *
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * You may implement and register your own {@link RemoteFunctionRequestErrorHandler} for the invocation of BAPIs. You
 * want to introduce special treament for a certain error message of the invoked BAPI. In your own
 * {@link RemoteFunctionRequestErrorHandler} you gain access to the request result and can react accordingly.
 * </p>
 *
 * <p>
 * <b>Note:</b> The request execution logic of {@link AbstractRemoteFunctionRequest} using a {@link CommitStrategy} that
 * includes a commit in the remote system ({@link CommitStrategy#COMMIT_SYNC}, {@link CommitStrategy#COMMIT_ASYNC})
 * invokes a rollback in the remote system if the used {@link RemoteFunctionRequestErrorHandler} throws a
 * {@link RemoteFunctionException}. Otherwise, no rollback is invoked the remote system.
 * </p>
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public interface RemoteFunctionRequestErrorHandler
{

    /**
     * Allows to inspect the request result after the request execution and react accordingly.
     *
     * In order to indicate an error situation, you may return a {@link RemoteFunctionException} or one subclass
     * encapsulated in an {@link Option}. Otherwise, return an empty {@link Option}.
     *
     * In case of an error situation, the request exceution logic triggers a rollback in the remote system if a
     * {@link CommitStrategy} including commit behaviour is used.
     *
     * @param <RequestT>
     *            The type of the request
     * @param <RequestResultT>
     *            the type of the request result
     * @param requestResult
     *            The request result to inspect
     * @return {@link Option} containing a {@link RemoteFunctionException} or being empty.
     */
    @Nonnull
    <
        RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
        Option<RemoteFunctionException>
        handleRequestResult( @Nonnull final RequestResultT requestResult );
}
