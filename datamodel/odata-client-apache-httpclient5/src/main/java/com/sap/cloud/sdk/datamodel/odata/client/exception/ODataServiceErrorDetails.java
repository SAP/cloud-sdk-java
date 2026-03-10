package com.sap.cloud.sdk.datamodel.odata.client.exception;

import javax.annotation.Nonnull;

import io.vavr.control.Option;

/**
 * Interface that resembles which information OData errors must contain and which information is optional.
 */
public interface ODataServiceErrorDetails
{
    /**
     * Language independent OData error response code.
     *
     * @return The OData error code.
     */
    @Nonnull
    String getODataCode();

    /**
     * Language dependent OData error message. The language used is reflected by the "Content-Language" header in the
     * HTTP response.
     *
     * @return The OData error message.
     */
    @Nonnull
    String getODataMessage();

    /**
     * Optional OData service specific hint for origin of the error.
     *
     * @return An {@link Option optional} target.
     */
    @Nonnull
    Option<String> getTarget();
}
