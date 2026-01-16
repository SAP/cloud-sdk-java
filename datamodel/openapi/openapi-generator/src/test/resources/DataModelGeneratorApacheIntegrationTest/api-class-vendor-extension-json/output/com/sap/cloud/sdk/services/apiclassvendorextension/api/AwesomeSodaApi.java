/*
 * Copyright (c) 2026 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.apiclassvendorextension.api;

import com.fasterxml.jackson.core.type.TypeReference;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.apache.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.apache.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.Pair;


import com.sap.cloud.sdk.services.apiclassvendorextension.model.NewSoda;
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
public class AwesomeSodaApi extends BaseApi {

    /**
     * Instantiates this API class to invoke operations on the Soda Store API
     */
    public AwesomeSodaApi() {}

    /**
     * Instantiates this API class to invoke operations on the Soda Store API.
     *
     * @param httpDestination The destination that API should be used with
     */
    public AwesomeSodaApi( @Nonnull final Destination httpDestination )
    {
    super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the Soda Store API based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    public AwesomeSodaApi(@Nonnull final ApiClient apiClient) {
    super(apiClient);
    }


        /**
         * <p>Add a new soda to the store
         * <p>
         * <p><b>201</b> - The newly added soda
         * @param newSoda
             *      The value for the parameter newSoda
         * @return Soda
         * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
         */
        @Nonnull
        public Soda addSoda(@Nonnull final NewSoda newSoda) throws OpenApiRequestException {
            
            // verify the required parameter 'newSoda' is set
            if (newSoda == null) {
            throw new OpenApiRequestException("Missing the required parameter 'newSoda' when calling addSoda")
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
                "POST",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                newSoda,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType
            );
        }
        }
