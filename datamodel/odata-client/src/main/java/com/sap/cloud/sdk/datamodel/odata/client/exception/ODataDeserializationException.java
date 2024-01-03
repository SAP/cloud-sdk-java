/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpResponse;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

import lombok.EqualsAndHashCode;

/**
 * OData deserialization exception type to focus on deserialization errors when parsing the service response.
 */
@EqualsAndHashCode( callSuper = true )
public class ODataDeserializationException extends ODataResponseException
{
    private static final long serialVersionUID = 8060933261779457372L;

    /**
     * Default constructor.
     *
     * @param request
     *            The original OData request reference.
     * @param httpResponse
     *            The {@link HttpResponse} that gave raise to this exception.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause.
     */
    public ODataDeserializationException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final HttpResponse httpResponse,
        @Nonnull final String message,
        @Nullable final Throwable cause )
    {
        super(request, httpResponse, message, cause);
    }
}
