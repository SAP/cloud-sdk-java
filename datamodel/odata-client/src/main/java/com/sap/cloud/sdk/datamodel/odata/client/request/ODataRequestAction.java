/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The executable OData action request.
 */
@Getter
@EqualsAndHashCode( callSuper = true )
public class ODataRequestAction extends ODataRequestGeneric
{
    @Nonnull
    private final String actionParameters;

    @Nonnull
    private final String query;

    {
        addHeader("Content-Type", "application/json");
    }

    /**
     * Convenience constructor for invocations of unbound actions. For bound actions use
     * {@link #ODataRequestAction(String, ODataResourcePath, String, ODataProtocol)}.
     *
     * @param servicePath
     *            The OData service path.
     * @param actionName
     *            The action name.
     * @param actionParameters
     *            Optional: The action parameters HTTP payload.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestAction(
        @Nonnull final String servicePath,
        @Nonnull final String actionName,
        @Nullable final String actionParameters,
        @Nonnull final ODataProtocol protocol )
    {
        this(servicePath, ODataResourcePath.of(actionName), actionParameters, protocol);
    }

    /**
     * Default constructor for OData Action request.
     *
     * @param servicePath
     *            The OData service path.
     * @param actionPath
     *            The {@link ODataResourcePath path} identifying the action. In case of an <strong>unbound</strong>
     *            action this is simply the action name. If this is a <strong>bound</strong> action the path must also
     *            contain the full path to the action.
     * @param actionParameters
     *            Optional: The action parameters as HTTP payload. This is expected to be a JSON formatted String.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestAction(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath actionPath,
        @Nullable final String actionParameters,
        @Nonnull final ODataProtocol protocol )
    {
        this(servicePath, actionPath, actionParameters, null, protocol);
    }

    /**
     * Constructor with StructuredQuery for OData Function request.
     *
     * @param servicePath
     *            The OData service path.
     * @param actionPath
     *            The full {@link ODataResourcePath} containing the action name, its parameters and possible further
     *            path segments. If this is a <strong>bound</strong> action the path must also contain the full path to
     *            the action.
     * @param actionParameters
     *            Optional: The action parameters as HTTP payload. This is expected to be a JSON formatted String.
     * @param encodedQuery
     *            Optional: An encodedQuery HTTP request query.
     * @param protocol
     *            The OData protocol to use.
     */
    public ODataRequestAction(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath actionPath,
        @Nullable final String actionParameters,
        @Nullable final String encodedQuery,
        @Nonnull final ODataProtocol protocol )
    {
        super(servicePath, actionPath, protocol);
        this.actionParameters = actionParameters != null ? actionParameters : "{}";
        this.query = encodedQuery != null ? encodedQuery : "";
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
        if( !genericQueryString.isEmpty() && !query.isEmpty() ) {
            return query + "&" + genericQueryString;
        }
        return query + genericQueryString;
    }

    @Override
    @Nonnull
    public ODataRequestResultGeneric execute( @Nonnull final HttpClient httpClient )
    {
        final ODataHttpRequest request = ODataHttpRequest.forBodyJson(this, httpClient, actionParameters);
        return tryExecuteWithCsrfToken(httpClient, request::requestPost).get();
    }
}
