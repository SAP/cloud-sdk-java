package com.sap.cloud.sdk.s4hana.connectivity;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

/**
 * Common interface for execution of different types of queries.
 *
 * @param <RequestT>
 *            The generic request type.
 * @param <RequestResultT>
 *            The generic request result type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public interface RequestExecutor<RequestT extends Request<RequestT, RequestResultT>, RequestResultT extends RequestResult<RequestT, RequestResultT>>
{
    /**
     * Executes a request against an SAP S/4HANA system.
     *
     * @param destination
     *            The {@link Destination} to be used for execution.
     * @param request
     *            The subclass of {@link Request} to execute.
     *
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
     *             If there is an issue while serializing the request.
     *
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
     *             If there is an issue while executing the request.
     *
     * @throws DestinationNotFoundException
     *             If no destination with the name can be found.
     *
     * @throws DestinationAccessException
     *             If there is an issue while accessing destination information.
     *
     * @return The executed request result.
     */
    @Nonnull
    RequestResultT execute( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException,
            DestinationNotFoundException,
            DestinationAccessException;
}
