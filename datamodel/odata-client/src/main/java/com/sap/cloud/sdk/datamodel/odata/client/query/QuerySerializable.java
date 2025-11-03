package com.sap.cloud.sdk.datamodel.odata.client.query;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.request.UriEncodingStrategy;

/**
 * A serializable query interface to serve an encoded and not-encoded String representation.
 */
public interface QuerySerializable
{
    /**
     * Compute the encoded string representation of this query with the following {@link UriEncodingStrategy#REGULAR}
     * strategy.
     *
     * @return A string representing the encoded request query.
     */
    @Nonnull
    String getEncodedQueryString();

    /**
     * Compute the string representation of this query.
     *
     * @return A string representing the request query.
     */
    @Nonnull
    String getQueryString();
}
