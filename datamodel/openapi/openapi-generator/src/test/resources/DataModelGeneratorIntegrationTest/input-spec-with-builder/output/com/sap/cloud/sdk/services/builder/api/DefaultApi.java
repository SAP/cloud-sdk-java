/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.builder.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.builder.model.Soda;
import com.sap.cloud.sdk.services.builder.model.UpdateSoda;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.nio.charset.StandardCharsets;
import java.net.URI;

import com.fasterxml.jackson.core.type.TypeReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

/**
 * Soda Store API in version 1.0.0.
 *
 * API for managing sodas in a soda store
 */
public class DefaultApi extends AbstractOpenApiService {
    /**
     * Instantiates this API class to invoke operations on the Soda Store API.
     *
     * @param httpDestination The destination that API should be used with
     */
    public DefaultApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the Soda Store API based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    @com.google.common.annotations.Beta
    public DefaultApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

        /**
     * <p>Delete a specific soda from the store</p>
     * <p></p>
     * <p><b>204</b> - Soda successfully deleted
     * <p><b>404</b> - Soda not found
     * @param sodaId
     *      ID of the soda to delete
     * @return An OpenApiResponse containing the status code of the HttpResponse.
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public OpenApiResponse deleteSodaById( @Nonnull final Long sodaId) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'sodaId' is set
        if (sodaId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'sodaId' when calling deleteSodaById");
        }
        
        String path = "/sodas/{sodaId}";
        // alter path and map variables
        path = path.replaceAll("[{]sodaId[}]", sodaId+"");
        final String localVarPath = URI.create(path).toString();

        final Map<String, List<String>> localVarQueryParams = new LinkedHashMap<>();
        final Map<String, List<String>> localVarHeaderParams = new LinkedHashMap<>();
        final Map<String, List<Object>> localVarFormParams = new LinkedHashMap<>();

        final List<String> localVarAccept = Arrays.asList();
        final String[] localVarContentTypes = { };
        final String localVarContentType = apiClient.getHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final TypeReference<Void> localVarReturnType = new TypeReference<>() {};
        apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
        return new OpenApiResponse(apiClient);
    }
    /**
     * <p>Get details of a specific soda</p>
     * <p></p>
     * <p><b>200</b> - The requested soda
     * <p><b>404</b> - Soda not found
     * @param sodaId
     *      ID of the soda to retrieve
     * @return Soda
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public Soda getSodaById( @Nonnull final Long sodaId) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'sodaId' is set
        if (sodaId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'sodaId' when calling getSodaById");
        }
        
        String path = "/sodas/{sodaId}";
        // alter path and map variables
        path = path.replaceAll("[{]sodaId[}]", sodaId+"");
        final String localVarPath = URI.create(path).toString();

        final Map<String, List<String>> localVarQueryParams = new LinkedHashMap<>();
        final Map<String, List<String>> localVarHeaderParams = new LinkedHashMap<>();
        final Map<String, List<Object>> localVarFormParams = new LinkedHashMap<>();

        final List<String> localVarAccept = Arrays.asList(
            "application/json"
        );
        final String[] localVarContentTypes = { };
        final String localVarContentType = apiClient.getHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final TypeReference<Soda> localVarReturnType = new TypeReference<>() {};
        return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
    /**
     * <p>Update details of a specific soda</p>
     * <p></p>
     * <p><b>200</b> - The updated soda
     * <p><b>404</b> - Soda not found
     * @param sodaId
     *      ID of the soda to update
     * @param updateSoda
     *      The value for the parameter updateSoda
     * @return Soda
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public Soda updateSodaById( @Nonnull final Long sodaId,  @Nonnull final UpdateSoda updateSoda) throws OpenApiRequestException {
        final Object localVarPostBody = updateSoda;
        
        // verify the required parameter 'sodaId' is set
        if (sodaId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'sodaId' when calling updateSodaById");
        }
        
        // verify the required parameter 'updateSoda' is set
        if (updateSoda == null) {
            throw new OpenApiRequestException("Missing the required parameter 'updateSoda' when calling updateSodaById");
        }
        
        String path = "/sodas/{sodaId}";
        // alter path and map variables
        path = path.replaceAll("[{]sodaId[}]", sodaId+"");
        final String localVarPath = URI.create(path).toString();

        final Map<String, List<String>> localVarQueryParams = new LinkedHashMap<>();
        final Map<String, List<String>> localVarHeaderParams = new LinkedHashMap<>();
        final Map<String, List<Object>> localVarFormParams = new LinkedHashMap<>();

        final List<String> localVarAccept = Arrays.asList(
            "application/json"
        );
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final String localVarContentType = apiClient.getHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final TypeReference<Soda> localVarReturnType = new TypeReference<>() {};
        return apiClient.invokeAPI(localVarPath, "PUT", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
}
