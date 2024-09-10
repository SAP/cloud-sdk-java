/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpResponse;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * An {@link ODataException} representing an erroneous response from the service where the payload contained detailed
 * OData error information.
 */
@EqualsAndHashCode( callSuper = true )
@Getter
public class ODataServiceErrorException extends ODataResponseException
{
    private static final long serialVersionUID = 8060933261779457372L;

    /**
     * The parsed {@link ODataServiceError} that was found in the HTTP response body.
     */
    @Nonnull
    private final transient ODataServiceError odataError;

    /**
     * Default constructor.
     *
     * @param request
     *            The original OData request reference.
     * @param httpResponse
     *            The failing HTTP response reference.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause, if any.
     * @param odataError
     *            The parsed {@link ODataServiceError odata error} contained in the HTTP response.
     */
    public ODataServiceErrorException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final HttpResponse httpResponse,
        @Nonnull final String message,
        @Nullable final Throwable cause,
        @Nonnull final ODataServiceError odataError )
    {
        super(request, httpResponse, message, cause);
        this.odataError = odataError;
    }
}
