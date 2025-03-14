/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.sample.api;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Soda;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.SodaWithId;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

/**
 * SodaStore API in version 1.0.0.
 *
 * API for managing soda products and orders in SodaStore.
 */
public class SodasApi extends AbstractOpenApiService
{
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
    @com.google.common.annotations.Beta
    public SodasApi( @Nonnull final ApiClient apiClient )
    {
        super(apiClient);
    }

    /**
     * <p>
     * Get all soda products
     * </p>
     * <p>
     * </p>
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

        String path = "/sodas";
        final String localVarPath = URI.create(path).toString();

        final Map<String, List<String>> localVarQueryParams = new LinkedHashMap<>();
        final Map<String, List<String>> localVarHeaderParams = new LinkedHashMap<>();
        final Map<String, List<Object>> localVarFormParams = new LinkedHashMap<>();

        final List<String> localVarAccept = Arrays.asList("application/json");
        final String[] localVarContentTypes = {};
        final String localVarContentType = apiClient.getHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKeyAuth" };

        final TypeReference<List<SodaWithId>> localVarReturnType = new TypeReference<>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarAuthNames,
                localVarReturnType);
    }

    /**
     * <p>
     * Get a specific soda product by ID
     * </p>
     * <p>
     * </p>
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
            throw new OpenApiRequestException("Missing the required parameter 'id' when calling sodasIdGet");
        }

        String path = "/sodas/{id}";
        // alter path and map variables
        path = path.replaceAll("[{]id[}]", id + "");
        final String localVarPath = URI.create(path).toString();

        final Map<String, List<String>> localVarQueryParams = new LinkedHashMap<>();
        final Map<String, List<String>> localVarHeaderParams = new LinkedHashMap<>();
        final Map<String, List<Object>> localVarFormParams = new LinkedHashMap<>();

        final List<String> localVarAccept = Arrays.asList("application/json");
        final String[] localVarContentTypes = {};
        final String localVarContentType = apiClient.getHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKeyAuth" };

        final TypeReference<SodaWithId> localVarReturnType = new TypeReference<>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarAuthNames,
                localVarReturnType);
    }

    /**
     * <p>
     * Update a specific soda product by ID
     * </p>
     * <p>
     * </p>
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
            throw new OpenApiRequestException("Missing the required parameter 'sodaWithId' when calling sodasPut");
        }

        String path = "/sodas";
        final String localVarPath = URI.create(path).toString();

        final Map<String, List<String>> localVarQueryParams = new LinkedHashMap<>();
        final Map<String, List<String>> localVarHeaderParams = new LinkedHashMap<>();
        final Map<String, List<Object>> localVarFormParams = new LinkedHashMap<>();

        final List<String> localVarAccept = Arrays.asList("application/json");
        final String[] localVarContentTypes = { "application/json" };
        final String localVarContentType = apiClient.getHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKeyAuth" };

        final TypeReference<Soda> localVarReturnType = new TypeReference<>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "PUT",
                localVarQueryParams,
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarAuthNames,
                localVarReturnType);
    }
}
