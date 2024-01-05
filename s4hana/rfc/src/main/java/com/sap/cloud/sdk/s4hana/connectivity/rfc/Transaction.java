/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

/**
 * The logic to be executed transactional-like by a {@link RemoteFunctionRequestExecutor}.
 *
 * @param <RequestT>
 *            The type of the request to execute.
 * @param <RequestResultT>
 *            The type of the result to return.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public interface Transaction<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
{
    /**
     * This method gets called before anything else gets called in the {@link RemoteFunctionRequestExecutor}.
     *
     * @param destination
     *            The {@code Destination} of this call.
     * @param request
     *            The {@code AbstractRemoteFunctionRequest} going to be executed.
     */
    void before( @Nonnull final Destination destination, @Nonnull final RequestT request );

    /**
     * The actual logic to be executed.
     *
     * @param destination
     *            The {@code Destination} of this call.
     * @param request
     *            The {@code AbstractRemoteFunctionRequest} going to execute.
     * @return The {@code AbstractRemoteFunctionRequestResult} created by the request.
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
     *             if an exception occurred during the {@code execute} implementation.
     */
    @Nonnull
    RequestResultT execute( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException;

    /**
     * If {@link #execute(Destination, AbstractRemoteFunctionRequest)} succeeded, this method is called to actually
     * commit the changes.
     *
     * @param destination
     *            The {@code Destination} of this call.
     * @param request
     *            The {@code AbstractRemoteFunctionRequest} that was executed.
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
     *             If an exception occurred during the {@code commit} implementation.
     */
    void commit( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException;

    /**
     * If there was a problem with {@link #execute(Destination, AbstractRemoteFunctionRequest)} or
     * {@link #commit(Destination, AbstractRemoteFunctionRequest)} this method will get called to rollback any unwanted
     * changes.
     *
     * @param destination
     *            The {@code Destination} of this call.
     * @param request
     *            The {@code AbstractRemoteFunctionRequest} that was executed.
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
     *             If an exception occurred during the {@code rollback} implementation.
     */
    void rollback( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException;

    /**
     * This method gets called *in any case* after the transactional calls are done.
     *
     * @throws RemoteFunctionException
     *             If an exception occurred during the {@code after} implementation.
     */
    void after()
        throws RemoteFunctionException;
}
