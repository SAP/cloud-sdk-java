/*
 * Copyright (c) 2026 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.filehandling.api;

import com.fasterxml.jackson.core.type.TypeReference;

import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.Pair;


import java.io.File;

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
public class FilesApi extends BaseApi {

    /**
     * Instantiates this API class to invoke operations on the Soda Store API.
     *
     * @param httpDestination The destination that API should be used with
     */
    public FilesApi( @Nonnull final Destination httpDestination )
    {
    super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the Soda Store API based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    public FilesApi(@Nonnull final ApiClient apiClient) {
    super(apiClient);
    }

    /**
    * Creates a new API instance with additional default headers.
    *
    * @param defaultHeaders Additional headers to include in all requests
    * @return A new API instance with the combined headers
    */
    public FilesApi withDefaultHeaders(@Nonnull final Map<String, String> defaultHeaders) {
        final var api = new FilesApi(apiClient);
        api.defaultHeaders.putAll(this.defaultHeaders);
        api.defaultHeaders.putAll(defaultHeaders);
        return api;
    }


        /**
         * <p>Download a file
         * <p>
         * <p><b>200</b> - File content as binary
         * @return byte[]
         * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
         */
        @Nonnull
        public byte[] exportFile() throws OpenApiRequestException {
            
            // create path and map variables
            final String localVarPath = "/files/export";
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            final List<Pair> localVarQueryParams = new ArrayList<Pair>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
            final Map<String, String> localVarHeaderParams = new HashMap<String, String>(defaultHeaders);
            final Map<String, Object> localVarFormParams = new HashMap<String, Object>();
            
                                    
            final String[] localVarAccepts = {
            "application/octet-stream"
            };
            final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
            final String[] localVarContentTypes = {
            
            };
            final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);
            
            final TypeReference<byte[]> localVarReturnType = new TypeReference<byte[]>() {};
                        
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
         * <p>Upload a file
         * <p>
         * <p><b>200</b> - File imported successfully
         * @param _file
             *      The value for the parameter _file
         * @return An OpenApiResponse containing the status code of the HttpResponse.
         * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
         */
        @Nonnull
        public OpenApiResponse importFile(@Nonnull final File _file) throws OpenApiRequestException {
            
            // verify the required parameter '_file' is set
            if (_file == null) {
            throw new OpenApiRequestException("Missing the required parameter '_file' when calling importFile")
            .statusCode(400);
            }
            
            // create path and map variables
            final String localVarPath = "/files/import";
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            final List<Pair> localVarQueryParams = new ArrayList<Pair>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
            final Map<String, String> localVarHeaderParams = new HashMap<String, String>(defaultHeaders);
            final Map<String, Object> localVarFormParams = new HashMap<String, Object>();
            
                        if (_file != null)
                localVarFormParams.put("file", _file);
            
            final String[] localVarAccepts = {
            
            };
            final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
            final String[] localVarContentTypes = {
            "multipart/form-data"
            };
            final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);
            
                        final TypeReference<OpenApiResponse> localVarReturnType = new TypeReference<OpenApiResponse>() {};
            
            return apiClient.invokeAPI(
                localVarPath,
                "POST",
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
