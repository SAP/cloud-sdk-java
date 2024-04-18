/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestAction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an Action OData request as a fluent interface for further configuring the request and
 * {@link #execute(com.sap.cloud.sdk.cloudplatform.connectivity.Destination) executing} it.
 *
 * @param <BuilderT>
 *            The request builder type.
 * @param <ResultT>
 *            The type of the result, if any.
 */
@Slf4j
public abstract class ActionRequestBuilder<BuilderT extends ActionRequestBuilder<BuilderT, ResultT>, ResultT>
    extends
    AbstractRequestBuilder<BuilderT, ResultT>
    implements
    ModificationRequestBuilder<ResultT>
{
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    private static final GsonVdmAdapterFactory GSON_VDM_ADAPTER_FACTORY = new GsonVdmAdapterFactory();
    private final Map<String, Object> parameters;

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param actionName
     *            The name of the unbound action
     */
    public ActionRequestBuilder( @Nonnull final String servicePath, @Nonnull final String actionName )
    {
        this(servicePath, actionName, Collections.emptyMap());
    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param actionName
     *            The name of the unbound action.
     * @param parameters
     *            The parameters passed to the function.
     */
    public ActionRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final String actionName,
        @Nonnull final Map<String, Object> parameters )
    {
        this(servicePath, ODataResourcePath.of(actionName), parameters);
    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param actionPath
     *            The path to the unbound action.
     * @param parameters
     *            The parameters passed to the function.
     */
    public ActionRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath actionPath,
        @Nonnull final Map<String, Object> parameters )
    {
        super(servicePath, actionPath);
        this.parameters = parameters;
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
    }

    /**
     * Serializes the passed parameter with custom type adapters and returns a JsonElement.
     */
    @Nonnull
    private <T> JsonElement serialize( @Nullable final T parameter )
    {
        if( parameter == null ) {
            return JsonNull.INSTANCE;
        }

        @SuppressWarnings( "unchecked" )
        final TypeToken<T> typeToken = TypeToken.get((Class<T>) parameter.getClass());
        final TypeAdapter<T> typeAdapter = GSON_VDM_ADAPTER_FACTORY.create(gson, typeToken);

        final JsonElement jsonObject;
        if( typeAdapter != null ) {
            jsonObject = typeAdapter.toJsonTree(parameter);
        } else {
            jsonObject = gson.toJsonTree(parameter);
        }
        return jsonObject;
    }

    /**
     * Creates an instance of {@link ODataRequestAction} based on the Entity class.
     * <p>
     * The following settings are used:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the action name</li>
     * <li>the parameters if applicable as JSON string</li>
     * </ul>
     *
     * @return An initialized {@link ODataRequestFunction}.
     */
    @Override
    @Nonnull
    public ODataRequestAction toRequest()
    {
        final ODataRequestAction request =
            new ODataRequestAction(getServicePath(), getResourcePath(), serializeParameters(), ODataProtocol.V4);
        return super.toRequest(request);
    }

    @Nonnull
    private String serializeParameters()
    {
        final JsonObject o = new JsonObject();
        parameters.forEach(( key, value ) -> o.add(key, serialize(value)));

        return gson.toJson(o);
    }

    /**
     * Deactivates the CSRF token retrieval for this OData request. This is useful if the server does not support or
     * require CSRF tokens as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    @Override
    public ModificationRequestBuilder<ResultT> withoutCsrfToken()
    {
        this.csrfTokenRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;
        return this;
    }
}
