/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.google.common.net.UrlEscapers;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The result type of the OData Read request.
 */
@Getter
@EqualsAndHashCode( callSuper = true )
public class ODataRequestRead extends ODataRequestGeneric
{
    @Nonnull
    private final String queryString;

    /**
     * Convenience constructor for OData read requests on entity collections directly. For operations on nested entity
     * collections use {@link #ODataRequestRead(String, ODataResourcePath, String, ODataProtocol)}.
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
    public ODataRequestRead(
        @Nonnull final String servicePath,
        @Nonnull final String entityName,
        @Nullable final String encodedQuery,
        @Nonnull final ODataProtocol protocol )
    {
        this(servicePath, ODataResourcePath.of(entityName), encodedQuery, protocol);
    }

    /**
     * Default constructor for OData Read request.
     * <p>
     * Note: The query string {@link #queryString} must not contain characters that are forbidden in URLs, like spaces.
     * If forbidden characters are present, an {@link IllegalArgumentException} is thrown.
     *
     * <p>
     * Build an instance of {@link StructuredQuery} and pass the value of
     * {@link StructuredQuery#getEncodedQueryString()} as {@link #queryString} to this method.
     *
     * <p>
     * Alternatively, use {@link UrlEscapers#urlFragmentEscaper()} from the Guava library to escape the query string
     * before passing it here.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the collection of entities or properties to read.
     * @param encodedQuery
     *            Optional: The encoded HTTP query, if any.
     * @param protocol
     *            The OData protocol to use.
     *
     */
    public ODataRequestRead(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nullable final String encodedQuery,
        @Nonnull final ODataProtocol protocol )
    {
        super(servicePath, entityPath, protocol);
        this.queryString = encodedQuery != null ? encodedQuery : "";
    }

    /**
     * Constructor with StructuredQuery for OData read requests on entity collections directly. For operations on nested
     * entity collections use {@link #ODataRequestRead(String, ODataResourcePath, String, ODataProtocol)}.
     *
     * @param servicePath
     *            The OData service path.
     * @param entityPath
     *            The {@link ODataResourcePath} that identifies the collection of entities or properties to read.
     * @param query
     *            The structured query.
     */
    public ODataRequestRead(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final StructuredQuery query )
    {
        this(
            servicePath,
            entityPath.addSegment(query.getEntityOrPropertyName()),
            query.getEncodedQueryString(),
            query.getProtocol());
    }

    @Nonnull
    @Override
    public URI getRelativeUri( @Nonnull final UriEncodingStrategy strategy )
    {
        return ODataUriFactory.createAndEncodeUri(getServicePath(), getResourcePath(), getRequestQuery(), strategy);
    }

    @Override
    @Nonnull
    public String getRequestQuery()
    {
        final String genericQueryString = super.getRequestQuery();
        if( !genericQueryString.isEmpty() && !queryString.isEmpty() ) {
            return queryString + "&" + genericQueryString;
        }
        return queryString + genericQueryString;
    }

    @Override
    @Nonnull
    public ODataRequestResultGeneric execute( @Nonnull final HttpClient httpClient )
    {
        final ODataHttpRequest request = ODataHttpRequest.withoutBody(this, httpClient);
        final Try<ODataRequestResultGeneric> result =
            csrfTokenRetriever == null
                ? tryExecute(request::requestGet, httpClient)
                : tryExecuteWithCsrfToken(httpClient, request::requestGet);
        return result.get();
    }
}
