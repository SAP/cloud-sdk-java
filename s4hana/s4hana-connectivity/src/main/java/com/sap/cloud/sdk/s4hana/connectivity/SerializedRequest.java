/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Header;

import lombok.Data;

/**
 * Represents a serialized request, including request method, path, headers, and body.
 *
 * @param <RequestT>
 *            The generic request type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Data
@Deprecated
public class SerializedRequest<RequestT extends Request<RequestT, ?>>
{
    @Nonnull
    private final RequestT request;

    private final RequestMethod requestMethod;
    private final String requestPath;
    private final List<Header> requestHeaders;
    private final String requestBody;
}
