/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import javax.annotation.Nonnull;

/**
 * Common interface for serialization of queries and deserialization of request results.
 *
 * @param <RequestT>
 *            The generic request type.
 * @param <RequestResultT>
 *            The generic request result type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public interface RequestSerializer<RequestT extends Request<RequestT, RequestResultT>, RequestResultT extends RequestResult<RequestT, RequestResultT>>
{
    /**
     * Serialize a request.
     *
     * @param request
     *            The request to serialize.
     * @return The serialized request.
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
     *             If there is an issue while serializing the request.
     */
    @Nonnull
    SerializedRequest<RequestT> serialize( @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException;

    /**
     * Deserialize a request result.
     *
     * @param requestResult
     *            The query result to deserialize.
     * @return The deserialized result object.
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
     *             If there is an issue while deserializing the request.
     */
    @Nonnull
    RequestResultT deserialize( @Nonnull final SerializedRequestResult<RequestT> requestResult )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException;
}
