/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * OData format for responses and requests.
 */
@RequiredArgsConstructor
enum ODataFormat
{
    /**
     * JSON
     */
    JSON("json", "application/json"),

    /**
     * XML
     */
    XML("xml", "application/xml");

    private final String odataFormatValue;

    @Getter( AccessLevel.PACKAGE )
    @Nonnull
    private final String httpAccept;

    @Override
    public String toString()
    {
        return odataFormatValue;
    }

    /**
     * Returns the {@code ODataFormat} for the given identifier or throws and {@code IllegalArgumentException} if the
     * provided string is not a valid identifier. This operation is case-insensitive.
     *
     * @param value
     *            The string identifier of the {@code ODataFormat}.
     * @return The {@code ODataFormat} associated with the identifier.
     */
    public static ODataFormat getODataFormat( @Nonnull final String value )
    {
        for( final ODataFormat f : values() ) {
            if( f.toString().equalsIgnoreCase(value) ) {
                return f;
            }
        }
        throw new IllegalArgumentException();
    }
}
