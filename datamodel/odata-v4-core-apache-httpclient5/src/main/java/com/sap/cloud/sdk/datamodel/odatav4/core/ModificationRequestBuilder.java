package com.sap.cloud.sdk.datamodel.odatav4.core;

/**
 * Interface to mark OData request types as modifying operation.
 *
 * @param <ResultT>
 *            The type of the request's result, if any.
 *
 * @see CreateRequestBuilder
 * @see DeleteRequestBuilder
 * @see UpdateRequestBuilder
 * @see ActionRequestBuilder
 */
public interface ModificationRequestBuilder<ResultT> extends RequestBuilder<ResultT>
{
}
