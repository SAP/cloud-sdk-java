/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

import lombok.EqualsAndHashCode;

/**
 * The result type of the OData Count request.
 */
@EqualsAndHashCode( callSuper = true )
public class ODataRequestCount extends ODataRequestRead
{
    /**
     * Default constructor for OData Count request.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityName
     *            The OData entity name.
     * @param encodedQuery
     *            Optional: The encoded HTTP query, if any.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestCount(
        @Nonnull final String servicePath,
        @Nonnull final String entityName,
        @Nullable final String encodedQuery,
        @Nonnull final ODataProtocol protocol )
    {
        this(servicePath, ODataResourcePath.of(entityName), encodedQuery, protocol);
    }

    /**
     * Default constructor for OData Count request.
     *
     * @param servicePath
     *            The OData service path.
     * @param resourcePath
     *            The {@link ODataResourcePath} that identifies the collection to be counted.
     * @param encodedQuery
     *            Optional: The encoded HTTP query, if any.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestCount(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath resourcePath,
        @Nullable final String encodedQuery,
        @Nonnull final ODataProtocol protocol )
    {
        super(servicePath, resourcePath.addSegment("$count"), encodedQuery, protocol);
    }

    /**
     * Constructor with StructuredQuery for OData Count request.
     *
     * @param servicePath
     *            The OData service path.
     * @param resourcePath
     *            The {@link ODataResourcePath} that identifies the collection to be counted.
     * @param query
     *            The structured query.
     */
    public ODataRequestCount(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath resourcePath,
        @Nonnull final StructuredQuery query )
    {
        this(
            servicePath,
            resourcePath.addSegment(query.getEntityOrPropertyName()),
            query.getEncodedQueryString(),
            query.getProtocol());
    }
}
