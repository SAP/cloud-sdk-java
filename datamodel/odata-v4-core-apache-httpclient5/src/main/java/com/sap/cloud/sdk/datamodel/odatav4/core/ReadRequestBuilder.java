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
     * Activates CSRF token retrieval for this OData request.
     *
     * @return The same request builder that will now fetch a CSRF token.
     * @deprecated CSRF token handling is now performed automatically by the underlying HTTP client. This method is a
     *             no-op retained only for source compatibility, as read requests never require a CSRF token. It is
     *             scheduled for removal.
     */
    @Deprecated
    @Nonnull
    default ReadRequestBuilder<ResultT> withCsrfToken()
    {
        return this;
    }
}
