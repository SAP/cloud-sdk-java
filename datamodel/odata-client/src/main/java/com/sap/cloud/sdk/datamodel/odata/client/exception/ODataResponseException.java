/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.exception;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A generic {@link ODataException} representing an erroneous service response. This exception class comprises details
 * of the HTTP response.
 */
@EqualsAndHashCode( callSuper = true )
public class ODataResponseException extends ODataException
{
    private static final long serialVersionUID = 4615831202194546242L;

    /**
     * The HTTP status code of the response received.
     */
    @Getter
    private final int httpCode;

    /**
     * The HTTP headers returned with the response.
     */
    @Getter
    @Nonnull
    private final Collection<Header> httpHeaders;

    /**
     * The content of the HTTP response body as plain text or null, if the response did not contain a body.
     */
    @Getter
    @Nonnull
    private final Option<String> httpBody;

    /**
     * The number of the failed OData batch request. This number is the Content-ID of the response. -1 if not found.
     */
    @Getter
    private int failedBatchRequestNumber = -1;

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
    public ODataResponseException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final HttpResponse httpResponse,
        @Nonnull final String message,
        @Nullable final Throwable cause )
    {
        super(request, message, cause);
        httpCode = httpResponse.getStatusLine().getStatusCode();
        httpHeaders = Arrays.asList(httpResponse.getAllHeaders());
        httpBody =
            Try
                .of(() -> EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8))
                .onFailure(this::addSuppressed)
                .toOption();
    }

    /**
     * Default constructor for batch requests.
     *
     * @param request
     *            The original OData request reference.
     * @param httpResponse
     *            The {@link HttpResponse} that gave raise to this exception.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause.
     * @param failedBatchRequestNumber
     *            The Content-ID of the failed OData batch request.
     */
    public ODataResponseException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final HttpResponse httpResponse,
        @Nonnull final String message,
        @Nullable final Throwable cause,
        final int failedBatchRequestNumber )
    {
        this(request, httpResponse, message, cause);
        this.failedBatchRequestNumber = failedBatchRequestNumber;
    }
}
