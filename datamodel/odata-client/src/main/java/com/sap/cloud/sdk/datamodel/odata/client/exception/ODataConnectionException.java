package com.sap.cloud.sdk.datamodel.odata.client.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.methods.HttpUriRequest;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * OData connection exception indicating errors when trying to establish a service connection.
 */
@EqualsAndHashCode( callSuper = true )
@Getter
public class ODataConnectionException extends ODataRequestException
{
    private static final long serialVersionUID = -1448569410628983663L;

    /**
     * The {@link HttpUriRequest} that was attempted.
     */
    @Nonnull
    private final HttpUriRequest httpRequest;

    /**
     * Default constructor.
     *
     * @param request
     *            The original OData request reference.
     * @param httpRequest
     *            The original HTTP request which was sent.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause.
     */
    public ODataConnectionException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final HttpUriRequest httpRequest,
        @Nonnull final String message,
        @Nullable final Throwable cause )
    {
        super(request, message, cause);
        this.httpRequest = httpRequest;
    }
}
