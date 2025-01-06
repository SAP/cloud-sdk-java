/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import javax.annotation.Nonnull;

import lombok.extern.slf4j.Slf4j;

/**
 * Common abstract class for serialization of queries and deserialization of request results. Handles exceptions
 * happened during serialization and deserialization.
 *
 * @param <RequestT>
 *            The generic request type.
 * @param <RequestResultT>
 *            The generic request result type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Slf4j
@Deprecated
public abstract class AbstractRequestSerializer<RequestT extends Request<RequestT, RequestResultT>, RequestResultT extends RequestResult<RequestT, RequestResultT>>
    implements
    RequestSerializer<RequestT, RequestResultT>
{
    @Nonnull
    @Override
    public final SerializedRequest<RequestT> serialize( @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
    {
        try {
            final SerializedRequest<RequestT> result = serializeRequest(request);
            result.getRequestHeaders().addAll(request.getCustomHttpHeaders());
            return result;
        }
        catch( final Exception e ) {
            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException(e);
        }
    }

    @Nonnull
    @Override
    public final RequestResultT deserialize( @Nonnull final SerializedRequestResult<RequestT> requestResult )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
    {
        try {
            return deserializeRequestResult(requestResult);
        }
        catch( final Exception e ) {
            if( log.isDebugEnabled() ) {
                log
                    .debug(
                        "Failed to deserialize request result from {}: {}",
                        requestResult.getRequest().getClass().getSimpleName(),
                        e.getMessage());
            }
            if( log.isTraceEnabled() ) {
                log.trace("Unable to deserialize result body: {}", requestResult.getBody());
            }
            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException(e);
        }
    }

    /**
     * Serialize a request. Allows to generically throw an exception which is converted to
     * {@link com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException} by
     * {@link #serialize(Request)}.
     *
     * @param request
     *            The request to serialize.
     * @return The serialized request.
     * @throws Exception
     *             If there is an issue while serializing the request.
     */
    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nonnull
    protected abstract SerializedRequest<RequestT> serializeRequest( @Nonnull final RequestT request )
        throws Exception;

    /**
     * Deserialize a request result. Allows to generically throw an exception which is converted to
     * {@link com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException} by
     * {@link #deserialize(SerializedRequestResult)}.
     *
     * @param serializedRequestResult
     *            The request result payload to deserialize.
     * @return The deserialized request result.
     * @throws Exception
     *             If there is an issue while serializing the request.
     */
    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nonnull
    protected abstract RequestResultT deserializeRequestResult(
        @Nonnull final SerializedRequestResult<RequestT> serializedRequestResult )
        throws Exception;
}
