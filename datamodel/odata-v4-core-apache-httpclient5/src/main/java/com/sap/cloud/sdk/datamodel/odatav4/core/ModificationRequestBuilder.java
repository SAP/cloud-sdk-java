package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor;

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
    /**
     * Deactivates the CSRF token retrieval for this OData request. This is useful if the server does not support or
     * require CSRF tokens as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    default ModificationRequestBuilder<ResultT> withoutCsrfToken()
    {
        withHeader(ApacheHttpClient5Accessor.SKIP_CSRF_TOKEN_HEADER, "true");
        return this;
    }
}
