/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Representation of an OData action request as a fluent interface for further configuring the request and
 * {@link #execute(Destination) executing} it. This one handles actions where the return type is a collection of
 * primitive or entity or complex type values.
 *
 * @param <ResultT>
 *            The type of the result entity, or primitive or complex type
 */
public class CollectionValueActionRequestBuilder<ResultT>
    extends
    ActionRequestBuilder<CollectionValueActionRequestBuilder<ResultT>, ActionResponseCollection<ResultT>>
{
    @Getter( AccessLevel.PROTECTED )
    private final Class<ResultT> resultClass;

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param actionName
     *            The name of the unbound action
     * @param resultClass
     *            The expected collection return type of the action.
     */
    public CollectionValueActionRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final String actionName,
        @Nonnull final Class<ResultT> resultClass )
    {
        super(servicePath, actionName);
        this.resultClass = resultClass;
    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param actionName
     *            The name of the unbound action
     * @param parameters
     *            The parameters passed to the action.
     * @param resultClass
     *            The expected collection return type of the action.
     */
    public CollectionValueActionRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final String actionName,
        @Nonnull final Map<String, Object> parameters,
        @Nonnull final Class<ResultT> resultClass )
    {
        super(servicePath, actionName, parameters);
        this.resultClass = resultClass;
    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param actionPath
     *            The path to the unbound action
     * @param parameters
     *            The parameters passed to the action.
     * @param resultClass
     *            The expected collection return type of the action.
     */
    public CollectionValueActionRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath actionPath,
        @Nonnull final Map<String, Object> parameters,
        @Nonnull final Class<ResultT> resultClass )
    {
        super(servicePath, actionPath, parameters);
        this.resultClass = resultClass;
    }

    @Nonnull
    @Override
    public ActionResponseCollection<ResultT> execute( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final ODataRequestResultGeneric response = toRequest().execute(httpClient);
        return ActionResponseCollection.of(response, resultClass);
    }
}
