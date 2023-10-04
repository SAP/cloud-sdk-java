/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

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
    ModificationRequestBuilder<ResultT> withoutCsrfToken();
}
