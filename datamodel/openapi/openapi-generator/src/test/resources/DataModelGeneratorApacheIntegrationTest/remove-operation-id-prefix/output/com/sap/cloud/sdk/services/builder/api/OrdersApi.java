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


import com.sap.cloud.sdk.services.builder.model.Order;

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
public class OrdersApi extends BaseApi {

    /**
     * Instantiates this API class to invoke operations on the SodaStore API
     */
    public OrdersApi() {}

    /**
     * Instantiates this API class to invoke operations on the SodaStore API.
     *
     * @param httpDestination The destination that API should be used with
     */
    public OrdersApi( @Nonnull final Destination httpDestination )
    {
    super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the SodaStore API based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    public OrdersApi(@Nonnull final ApiClient apiClient) {
    super(apiClient);
    }


        /**
         * <p>Create a new order
         * <p>
         * <p><b>201</b> - The created order
         * @param order
         *      The order details
         * @return Order
         * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
         */
        @Nonnull
        public Order get(@Nonnull final Order order) throws OpenApiRequestException {
            
            // verify the required parameter 'order' is set
            if (order == null) {
            throw new OpenApiRequestException("Missing the required parameter 'order' when calling get")
            .statusCode(400);
            }
            
            // create path and map variables
            final String localVarPath = "/orders";
            
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
            
            final TypeReference<Order> localVarReturnType = new TypeReference<Order>() {};
                        
            return apiClient.invokeAPI(
                localVarPath,
                "POST",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                order,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType
            );
        }
        }
