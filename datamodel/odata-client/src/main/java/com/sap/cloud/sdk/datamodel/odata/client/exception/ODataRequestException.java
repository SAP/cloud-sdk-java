/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

import lombok.EqualsAndHashCode;

/**
 * Generic OData request exception indicating errors while trying to request a service resource.
 */
@EqualsAndHashCode( callSuper = true )
public class ODataRequestException extends ODataException
{
    private static final long serialVersionUID = 4615831202194546242L;

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
    public ODataRequestException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final String message,
        @Nullable final Throwable cause )
    {
        super(request, message, cause);
    }
}
