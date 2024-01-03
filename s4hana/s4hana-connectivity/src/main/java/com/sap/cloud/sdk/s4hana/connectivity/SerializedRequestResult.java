/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Header;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents serialized request result with a request, result body, and headers.
 *
 * @param <RequestT>
 *            The generic request type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@AllArgsConstructor
@Data
@Deprecated
public class SerializedRequestResult<RequestT extends Request<RequestT, ?>>
{
    @Nonnull
    private final RequestT request;

    @Nonnull
    private final String body;

    @Nonnull
    private final List<Header> headers;

    /**
     * Default constructor.
     *
     * @param request
     *            The original request.
     * @param body
     *            The serialized request result.
     */
    public SerializedRequestResult( @Nonnull final RequestT request, @Nonnull final String body )
    {
        this(request, body, Collections.emptyList());
    }
}
