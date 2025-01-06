/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.query;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataUriFactory;

/**
 * A serializable query interface to serve an encoded and not-encoded String representation.
 */
public interface QuerySerializable
{
    /**
     * Compute the encoded string representation of this query. All characters except the ones listed in
     * {@link ODataUriFactory#SAFE_CHARS_IN_QUERY} are encoded
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
