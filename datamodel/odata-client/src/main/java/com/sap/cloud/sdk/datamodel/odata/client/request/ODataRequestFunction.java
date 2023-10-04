/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.google.common.base.Strings;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The executable OData function request.
 */
@Getter
@EqualsAndHashCode( callSuper = true )
public class ODataRequestFunction extends ODataRequestGeneric
{
    @Nonnull
    private final String query;

    /**
     * Convenience constructor for invocations of unbound functions. The function parameters will be added to the URL
     * path or URL query depending on the OData protocol verison. For composable or bound functions please use
     * {@link #ODataRequestFunction(String, ODataResourcePath, String, ODataProtocol)} instead.
     *
     * @param servicePath
     *            The OData service path.
     * @param functionName
     *            The name of the unbound OData function.
     * @param parameters
     *            The parameters of the function invocation.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestFunction(
        @Nonnull final String servicePath,
        @Nonnull final String functionName,
        @Nonnull final ODataFunctionParameters parameters,
        @Nonnull final ODataProtocol protocol )
    {
        this(
            servicePath,
            protocol.isEqualTo(ODataProtocol.V2)
                ? ODataResourcePath.of(functionName)
                : ODataResourcePath.of(functionName, parameters),
            protocol.isEqualTo(ODataProtocol.V2) ? parameters.toEncodedString() : null,
            protocol);
    }

    /**
     * Convenience constructor for invocations of unbound functions. The function parameters will be added to the URL
     * path or URL query depending on the OData protocol verison. For composable or bound functions please use
     * {@link #ODataRequestFunction(String, ODataResourcePath, String, ODataProtocol)} instead.
     *
     * @param servicePath
     *            The OData service path.
     * @param functionPath
     *            The full {@link ODataResourcePath} containing the function name, its parameters and possible further
     *            path segments. If this is a <strong>bound</strong> function the path must also contain the full path
     *            to the function.
     * @param parameters
     *            The parameters of the function invocation.
     * @param query
     *            Optional: The encoded HTTP query, if any.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestFunction(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath functionPath,
        @Nonnull final ODataFunctionParameters parameters,
        @Nullable final String query,
        @Nonnull final ODataProtocol protocol )
    {
        this(
            servicePath,
            appendResourcePathWithParameters(functionPath, parameters, protocol),
            appendQueryWithParameters(query, parameters, protocol),
            protocol);
    }

    /**
     * Default constructor for OData Function request.
     *
     * @param servicePath
     *            The OData service path.
     * @param functionPath
     *            The full {@link ODataResourcePath} containing the function name, its parameters and possible further
     *            path segments. If this is a <strong>bound</strong> function the path must also contain the full path
     *            to the function.
     * @param encodedQuery
     *            Optional: The encoded HTTP query, if any.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestFunction(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath functionPath,
        @Nullable final String encodedQuery,
        @Nonnull final ODataProtocol protocol )
    {
        super(servicePath, functionPath, protocol);
        this.query = encodedQuery != null ? encodedQuery : "";
    }

    /**
     * Constructor with StructuredQuery for OData Function request.
     *
     * @param servicePath
     *            The OData service path.
     * @param functionPath
     *            The full {@link ODataResourcePath} containing the function name, its parameters and possible further
     *            path segments. If this is a <strong>bound</strong> function the path must also contain the full path
     *            to the function.
     * @param structQuery
     *            The structured query.
     */
    public ODataRequestFunction(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath functionPath,
        @Nonnull final StructuredQuery structQuery )
    {
        super(servicePath, functionPath.addSegment(structQuery.getEntityOrPropertyName()), structQuery.getProtocol());
        this.query = structQuery.getEncodedQueryString();
    }

    @Nonnull
    @Override
    public URI getRelativeUri( @Nonnull final UriEncodingStrategy strategy )
    {
        return ODataUriFactory.createAndEncodeUri(getServicePath(), getResourcePath(), getRequestQuery(), strategy);
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

    @Override
    @Nonnull
    public String getRequestQuery()
    {
        final String genericQueryString = super.getRequestQuery();
        if( !genericQueryString.isEmpty() && !query.isEmpty() ) {
            return query + "&" + genericQueryString;
        }
        return query + genericQueryString;
    }

    @Nonnull
    private static ODataResourcePath appendResourcePathWithParameters(
        @Nonnull final ODataResourcePath path,
        @Nonnull final ODataFunctionParameters parameters,
        @Nonnull final ODataProtocol protocol )
    {
        if( protocol.isEqualTo(ODataProtocol.V2) ) {
            return path;
        }
        final ODataResourcePath appendedPath = new ODataResourcePath();
        path.getSegments().forEach(s -> appendedPath.addSegment(s._1, s._2));
        return appendedPath.addParameterToLastSegment(parameters);
    }

    @Nullable
    private static String appendQueryWithParameters(
        @Nullable final String query,
        @Nonnull final ODataFunctionParameters parameters,
        @Nonnull final ODataProtocol protocol )
    {
        if( protocol.isEqualTo(ODataProtocol.V4) ) {
            return query;
        }
        final String encodedParams = parameters.toEncodedString();
        return Strings.isNullOrEmpty(query) ? encodedParams : encodedParams + "&" + query;
    }
}
