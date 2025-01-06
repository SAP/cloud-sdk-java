/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import javax.annotation.Nonnull;

/**
 * Common interface for ERP request results.
 *
 * @param <RequestT>
 *            The generic request type.
 * @param <RequestResultT>
 *            The generic request result type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public interface RequestResult<RequestT extends Request<RequestT, RequestResultT>, RequestResultT extends RequestResult<RequestT, RequestResultT>>
{
    /**
     * ERP request from which this result originates.
     *
     * @return The ERP request.
     */
    @Nonnull
    RequestT getRequest();
}
