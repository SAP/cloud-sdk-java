/*
 * Copyright (c) 2026 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.apiclassvendorextension.api;

import com.fasterxml.jackson.core.type.TypeReference;

import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.Pair;


import com.sap.cloud.sdk.services.apiclassvendorextension.model.Soda;

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
 * Soda Store API in version 1.0.0.
 * <p>
 * API for managing sodas in a soda store
 */
public class OneOfStringTestApi extends BaseApi {

    /**
     * Instantiates this API class to invoke operations on the Soda Store API.
     *
     * @param httpDestination The destination that API should be used with
     */
    public OneOfStringTestApi( @Nonnull final Destination httpDestination )
    {
    super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the Soda Store API based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    public OneOfStringTestApi(@Nonnull final ApiClient apiClient) {
    super(apiClient);
    }

    /**
    * Creates a new API instance with additional default headers.
    *
    * @param defaultHeaders Additional headers to include in all requests
    * @return A new API instance with the combined headers
    */
    public OneOfStringTestApi withDefaultHeaders(@Nonnull final Map<String, String> defaultHeaders) {
        final var api = new OneOfStringTestApi(apiClient);
        api.defaultHeaders.putAll(this.defaultHeaders);
        api.defaultHeaders.putAll(defaultHeaders);
        return api;
    }


        /**
         * <p>String parameter test
         * <p>Should simplify the parameter to a string
         * <p><b>200</b> - A list of soda products
         * @param executionId
         *      The Id of an execution
         * @return List&lt;Soda&gt;
         * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
         */
        @Nonnull
        public List<Soda> sodasDelete(@Nonnull final String executionId) throws OpenApiRequestException {
            
            // verify the required parameter 'executionId' is set
            if (executionId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'executionId' when calling sodasDelete")
            .statusCode(400);
            }
            
            // create path and map variables
            final String localVarPath = "/sodas";
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            final List<Pair> localVarQueryParams = new ArrayList<Pair>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
            final Map<String, String> localVarHeaderParams = new HashMap<String, String>(defaultHeaders);
            final Map<String, Object> localVarFormParams = new HashMap<String, Object>();
            
                                                                                                                    localVarQueryParams.addAll(ApiClient.parameterToPair("executionId", executionId));
                                                                                                
            final String[] localVarAccepts = {
            "application/json"
            };
            final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
            final String[] localVarContentTypes = {
            
            };
            final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);
            
            final TypeReference<List<Soda>> localVarReturnType = new TypeReference<List<Soda>>() {};
                        
            return apiClient.invokeAPI(
                localVarPath,
                "DELETE",
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
        }
