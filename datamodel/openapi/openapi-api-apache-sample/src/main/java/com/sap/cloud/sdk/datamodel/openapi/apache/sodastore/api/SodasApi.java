/*
* Copyright (c) 2026 SAP SE or an SAP affiliate company. All rights reserved.
*/

package com.sap.cloud.sdk.datamodel.openapi.apache.sodastore.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.openapi.apache.sodastore.model.Soda;
import com.sap.cloud.sdk.datamodel.openapi.apache.sodastore.model.SodaWithId;
import com.sap.cloud.sdk.services.openapi.apache.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.Pair;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

/**
 * SodaStore API in version 1.0.0.
 *
 * API for managing soda products and orders in SodaStore.
 */
public class SodasApi extends BaseApi
{

    /**
     * Instantiates this API class to invoke operations on the SodaStore API
     */
    public SodasApi()
    {
    }

    /**
     * Instantiates this API class to invoke operations on the SodaStore API.
     *
     * @param httpDestination
     *            The destination that API should be used with
     */
    public SodasApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the SodaStore API based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    public SodasApi( @Nonnull final ApiClient apiClient )
    {
        super(apiClient);
    }

    /**
     * <p>
     * Download soda product data as binary
     * <p>
     * <p>
     * <b>200</b> - Successful response
     * <p>
     * <b>404</b> - Soda product not found
     *
     * @param id
     *            ID of the soda product to download
     * @return File
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public File sodasDownloadIdGet( @Nonnull final Long id )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = null;

        // verify the required parameter 'id' is set
        if( id == null ) {
            throw new OpenApiRequestException("Missing the required parameter 'id' when calling sodasDownloadIdGet")
                .statusCode(400);
        }

        // create path and map variables
        final String localVarPath =
            "/sodas/download/{id}"
                .replaceAll("\\{" + "id" + "\\}", ApiClient.escapeString(ApiClient.parameterToString(id)));

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/octet-stream" };
        final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {

        };
        final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<File> localVarReturnType = new TypeReference<File>()
        {
        };

        return apiClient
            .invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }

    /**
     * <p>
     * Get all soda products
     * <p>
     * <p>
     * <b>200</b> - A list of soda products
     *
     * @return List&lt;SodaWithId&gt;
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public List<SodaWithId> sodasGet()
        throws OpenApiRequestException
    {
        final Object localVarPostBody = null;

        // create path and map variables
        final String localVarPath = "/sodas";

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {

        };
        final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<List<SodaWithId>> localVarReturnType = new TypeReference<List<SodaWithId>>()
        {
        };

        return apiClient
            .invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }

    /**
     * <p>
     * Get a specific soda product by ID
     * <p>
     * <p>
     * <b>200</b> - The soda product
     * <p>
     * <b>404</b> - Soda product not found
     *
     * @param id
     *            ID of the soda product to retrieve
     * @return SodaWithId
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public SodaWithId sodasIdGet( @Nonnull final Long id )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = null;

        // verify the required parameter 'id' is set
        if( id == null ) {
            throw new OpenApiRequestException("Missing the required parameter 'id' when calling sodasIdGet")
                .statusCode(400);
        }

        // create path and map variables
        final String localVarPath =
            "/sodas/{id}".replaceAll("\\{" + "id" + "\\}", ApiClient.escapeString(ApiClient.parameterToString(id)));

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {

        };
        final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<SodaWithId> localVarReturnType = new TypeReference<SodaWithId>()
        {
        };

        return apiClient
            .invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }

    /**
     * <p>
     * Update a specific soda product by ID
     * <p>
     * <p>
     * <b>200</b> - The updated soda product
     * <p>
     * <b>204</b> - Nothing has changed
     * <p>
     * <b>404</b> - Soda product not found
     *
     * @param sodaWithId
     *            The updated soda product
     * @return Soda
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nullable
    public Soda sodasPut( @Nonnull final SodaWithId sodaWithId )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = sodaWithId;

        // verify the required parameter 'sodaWithId' is set
        if( sodaWithId == null ) {
            throw new OpenApiRequestException("Missing the required parameter 'sodaWithId' when calling sodasPut")
                .statusCode(400);
        }

        // create path and map variables
        final String localVarPath = "/sodas";

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { "application/json" };
        final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<Soda> localVarReturnType = new TypeReference<Soda>()
        {
        };

        return apiClient
            .invokeAPI(
                localVarPath,
                "PUT",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }
}
