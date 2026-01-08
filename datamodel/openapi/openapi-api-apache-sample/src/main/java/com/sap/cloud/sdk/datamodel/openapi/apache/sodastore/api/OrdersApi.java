/*
* Copyright (c) 2026 SAP SE or an SAP affiliate company. All rights reserved.
*/

package com.sap.cloud.sdk.datamodel.openapi.apache.sodastore.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.openapi.apache.sodastore.model.Order;
import com.sap.cloud.sdk.services.openapi.apache.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.Pair;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

/**
 * SodaStore API in version 1.0.0.
 *
 * API for managing soda products and orders in SodaStore.
 */
public class OrdersApi extends BaseApi
{

    /**
     * Instantiates this API class to invoke operations on the SodaStore API
     */
    public OrdersApi()
    {
    }

    /**
     * Instantiates this API class to invoke operations on the SodaStore API.
     *
     * @param httpDestination
     *            The destination that API should be used with
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
    public OrdersApi( @Nonnull final ApiClient apiClient )
    {
        super(apiClient);
    }

    /**
     * <p>
     * Create a new order
     * <p>
     * <p>
     * <b>201</b> - The created order
     *
     * @param order
     *            The order details
     * @return Order
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public Order ordersPost( @Nonnull final Order order )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = order;

        // verify the required parameter 'order' is set
        if( order == null ) {
            throw new OpenApiRequestException("Missing the required parameter 'order' when calling ordersPost")
                .statusCode(400);
        }

        // create path and map variables
        final String localVarPath = "/orders";

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

        final TypeReference<Order> localVarReturnType = new TypeReference<Order>()
        {
        };

        return apiClient
            .invokeAPI(
                localVarPath,
                "POST",
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
