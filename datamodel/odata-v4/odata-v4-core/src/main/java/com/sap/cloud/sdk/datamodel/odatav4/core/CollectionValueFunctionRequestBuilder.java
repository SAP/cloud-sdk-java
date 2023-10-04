/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataFunctionParameters;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Representation of an OData function request as a fluent interface for further configuring the request and
 * {@link #execute(Destination) executing} it. This one handles functions where the return type is a collection of
 * primitive or entity values.
 *
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
public class CollectionValueFunctionRequestBuilder<ResultT>
    extends
    FunctionRequestBuilder<CollectionValueFunctionRequestBuilder<ResultT>, List<ResultT>>
{
    @Nonnull
    @Getter( AccessLevel.PROTECTED )
    private final Class<ResultT> resultClass;

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param functionName
     *            The name of the unbound function
     * @param resultClass
     *            The expected collection return type of the function.
     */
    public CollectionValueFunctionRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final String functionName,
        @Nonnull final Class<ResultT> resultClass )
    {
        this(servicePath, functionName, Collections.emptyMap(), resultClass);
    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param functionName
     *            The name of the unbound function
     * @param parameters
     *            The parameters passed to the function.
     * @param resultClass
     *            The expected collection return type of the function.
     */
    public CollectionValueFunctionRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final String functionName,
        @Nonnull final Map<String, Object> parameters,
        @Nonnull final Class<ResultT> resultClass )
    {
        this(
            servicePath,
            ODataResourcePath.of(functionName, ODataFunctionParameters.of(parameters, ODataProtocol.V4)),
            resultClass);

    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param functionPath
     *            The {@link ODataResourcePath} identifying the function to invoke.
     * @param resultClass
     *            The expected collection return type of the function.
     */
    CollectionValueFunctionRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath functionPath,
        @Nonnull final Class<ResultT> resultClass )
    {
        super(servicePath, functionPath);
        this.resultClass = resultClass;
    }

    @Nonnull
    @Override
    public List<ResultT> execute( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final ODataRequestResultGeneric response = toRequest().execute(httpClient);
        return response.asList(getResultClass());
    }
}
