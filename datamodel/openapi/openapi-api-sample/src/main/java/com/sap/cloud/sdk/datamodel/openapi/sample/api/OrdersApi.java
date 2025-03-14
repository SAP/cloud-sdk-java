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

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Order;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

/**
 * SodaStore API in version 1.0.0.
 *
 * API for managing soda products and orders in SodaStore.
 */
public class OrdersApi extends AbstractOpenApiService
{
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
    @com.google.common.annotations.Beta
    public OrdersApi( @Nonnull final ApiClient apiClient )
    {
        super(apiClient);
    }

    /**
     * <p>
     * Create a new order
     * </p>
     * <p>
     * </p>
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
            throw new OpenApiRequestException("Missing the required parameter 'order' when calling ordersPost");
        }

        String path = "/orders";
        final String localVarPath = URI.create(path).toString();

        final Map<String, List<String>> localVarQueryParams = new LinkedHashMap<>();
        final Map<String, List<String>> localVarHeaderParams = new LinkedHashMap<>();
        final Map<String, List<Object>> localVarFormParams = new LinkedHashMap<>();

        final List<String> localVarAccept = Arrays.asList("application/json");
        final String[] localVarContentTypes = { "application/json" };
        final String localVarContentType = apiClient.getHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKeyAuth" };

        final TypeReference<Order> localVarReturnType = new TypeReference<>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "POST",
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
