package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

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
    /**
     * Activates the CSRF token retrieval for this OData request. This is useful if the server does require CSRF tokens
     * as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    ReadRequestBuilder<ResultT> withCsrfToken();
}
