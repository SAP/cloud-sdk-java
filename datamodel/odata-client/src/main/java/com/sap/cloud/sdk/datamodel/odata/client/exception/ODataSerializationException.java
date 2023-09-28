package com.sap.cloud.sdk.datamodel.odata.client.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * OData serialization exception type to focus on serialization errors when creating the service request.
 */
@EqualsAndHashCode( callSuper = true )
public class ODataSerializationException extends ODataRequestException
{
    private static final long serialVersionUID = -3620082691866789667L;

    /**
     * The object for which serialization failed.
     */
    @Nonnull
    @Getter
    private final Object nonSerializableObject;

    /**
     * Default constructor.
     *
     * @param request
     *            The original OData request reference.
     * @param nonSerializableObject
     *            The non serializable object.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause.
     */
    public ODataSerializationException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final Object nonSerializableObject,
        @Nonnull final String message,
        @Nullable final Throwable cause )
    {
        super(request, message, cause);
        this.nonSerializableObject = nonSerializableObject;
    }
}
