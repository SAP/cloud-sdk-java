/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.sample.api;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.datamodel.openapi.sample.model.Order; // NOPMD
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
    public OrdersApi( @Nonnull final HttpDestinationProperties httpDestination )
    {
        super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the SodaStore API based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    @Beta
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
    @Nullable
    public Order ordersPost( @Nonnull final Order order )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = order;

        // verify the required parameter 'order' is set
        if( order == null ) {
            throw new OpenApiRequestException("Missing the required parameter 'order' when calling ordersPost");
        }

        final String localVarPath = UriComponentsBuilder.fromPath("/orders").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { "application/json" };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKeyAuth" };

        final ParameterizedTypeReference<Order> localVarReturnType = new ParameterizedTypeReference<Order>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                HttpMethod.POST,
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
