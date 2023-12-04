/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The generic OData exception.<br>
 * Its sub-types will be thrown in the following scenarios:
 * <ul>
 * <li><strong>{@link ODataSerializationException}</strong><br>
 * If entity cannot be serialized for HTTP request.</li>
 * <li><strong>{@link ODataConnectionException}</strong><br>
 * When the HTTP connection cannot be established.</li>
 * <li><strong>{@link ODataRequestException}</strong><br>
 * When the OData request could not be sent due to a generic reason.</li>
 * <li><strong>{@link ODataResponseException}</strong><br>
 * If the response code infers an unhealthy state, i.e. when >= 400</li>
 * <li><strong>{@link ODataDeserializationException}</strong><br>
 * When deserialization process failed for the OData response object.</li>
 * <li><strong>{@link ODataServiceErrorException}</strong><br>
 * If the response contains an OData error in the payload.</li>
 * </ul>
 */
@EqualsAndHashCode( callSuper = true )
@Getter
public class ODataException extends IllegalStateException
{
    private static final long serialVersionUID = -1264994793328207269L;

    /**
     * The OData request that was attempted while this exception occurred.
     */
    @Nonnull
    private final ODataRequestGeneric request;

    /**
     * Default constructor.
     *
     * @param request
     *            The original OData request reference.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause.
     */
    public ODataException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final String message,
        @Nullable final Throwable cause )
    {
        super(message, cause);
        this.request = request;
    }
}
