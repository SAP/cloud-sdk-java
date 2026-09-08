package com.sap.cloud.sdk.datamodel.odatav4.core;

/**
 * Interface to mark OData request types as reading operation.
 *
 * @param <ResultT>
 *            The type of the request's result, if any.
 *
 * @see GetAllRequestBuilder
 * @see GetByKeyRequestBuilder
 * @see FunctionRequestBuilder
 */
public interface ReadRequestBuilder<ResultT> extends RequestBuilder<ResultT>
{
}
