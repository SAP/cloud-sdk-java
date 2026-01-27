/*
 * Copyright (c) 2026 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.builder.api;

import com.fasterxml.jackson.core.type.TypeReference;

import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.Pair;


import com.sap.cloud.sdk.services.builder.model.Soda;
import com.sap.cloud.sdk.services.builder.model.SodaWithId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;


/**
 * SodaStore API in version 1.0.0.
 * <p>
 * API for managing soda products and orders in SodaStore.
 */
public class SodasApi extends BaseApi {

    /**
     * Instantiates this API class to invoke operations on the SodaStore API
     */
    public SodasApi() {}

    /**
     * Instantiates this API class to invoke operations on the SodaStore API.
     *
     * @param httpDestination The destination that API should be used with
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
    public SodasApi(@Nonnull final ApiClient apiClient) {
    super(apiClient);
    }


        /**
         * <p>Get all soda products
         * <p>
         * <p><b>200</b> - A list of soda products
         * @return List&lt;SodaWithId&gt;
         * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
         */
        @Nonnull
        public List<SodaWithId> get() throws OpenApiRequestException {
            
            // create path and map variables
            final String localVarPath = "/sodas";
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            final List<Pair> localVarQueryParams = new ArrayList<Pair>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
            final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
            final Map<String, Object> localVarFormParams = new HashMap<String, Object>();
            
                                    
            final String[] localVarAccepts = {
            "application/json"
            };
            final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
            final String[] localVarContentTypes = {
            
            };
            final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);
            
            final TypeReference<List<SodaWithId>> localVarReturnType = new TypeReference<List<SodaWithId>>() {};
                        
            return apiClient.invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                null,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType
            );
        }

        /**
         * <p>Get a specific soda product by ID
         * <p>
         * <p><b>200</b> - The soda product
         * <p><b>404</b> - Soda product not found
         * @param id
         *      ID of the soda product to retrieve
         * @return SodaWithId
         * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
         */
        @Nonnull
        public SodaWithId sodasIdGet(@Nonnull final Long id) throws OpenApiRequestException {
            
            // verify the required parameter 'id' is set
            if (id == null) {
            throw new OpenApiRequestException("Missing the required parameter 'id' when calling sodasIdGet")
            .statusCode(400);
            }
            
            // create path and map variables
            final String localVarPath = "/sodas/{id}"
                .replaceAll("\\{" + "id" + "\\}", ApiClient.escapeString(ApiClient.parameterToString(id)));
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            final List<Pair> localVarQueryParams = new ArrayList<Pair>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
            final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
            final Map<String, Object> localVarFormParams = new HashMap<String, Object>();
            
                                    
            final String[] localVarAccepts = {
            "application/json"
            };
            final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
            final String[] localVarContentTypes = {
            
            };
            final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);
            
            final TypeReference<SodaWithId> localVarReturnType = new TypeReference<SodaWithId>() {};
                        
            return apiClient.invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                null,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType
            );
        }

        /**
         * <p>Update a specific soda product by ID
         * <p>
         * <p><b>200</b> - The updated soda product
         * <p><b>404</b> - Soda product not found
         * <p><b>204</b> - Nothing has changed
         * @param sodaWithId
         *      The updated soda product
         * @return Soda
         * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
         */
        @Nullable
        public Soda sodasPut(@Nonnull final SodaWithId sodaWithId) throws OpenApiRequestException {
            
            // verify the required parameter 'sodaWithId' is set
            if (sodaWithId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'sodaWithId' when calling sodasPut")
            .statusCode(400);
            }
            
            // create path and map variables
            final String localVarPath = "/sodas";
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            final List<Pair> localVarQueryParams = new ArrayList<Pair>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
            final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
            final Map<String, Object> localVarFormParams = new HashMap<String, Object>();
            
                                    
            final String[] localVarAccepts = {
            "application/json"
            };
            final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
            final String[] localVarContentTypes = {
            "application/json"
            };
            final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);
            
            final TypeReference<Soda> localVarReturnType = new TypeReference<Soda>() {};
                        
            return apiClient.invokeAPI(
                localVarPath,
                "PUT",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                sodaWithId,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType
            );
        }
        }
