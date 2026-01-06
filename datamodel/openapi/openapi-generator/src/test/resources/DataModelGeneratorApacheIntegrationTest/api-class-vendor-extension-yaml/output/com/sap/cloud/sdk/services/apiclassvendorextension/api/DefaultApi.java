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
import com.sap.cloud.sdk.services.apiclassvendorextension.model.UpdateSoda;

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
*
* API for managing sodas in a soda store
*/
public class DefaultApi extends BaseApi {

    /**
    * Instantiates this API class to invoke operations on the Soda Store API
    */
    public DefaultApi() {}

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
    public DefaultApi(@Nonnull final ApiClient apiClient) {
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
            final Object localVarPostBody = newSoda;
            
                // verify the required parameter 'newSoda' is set
                if (newSoda == null) {
                throw new OpenApiRequestException("Missing the required parameter 'newSoda' when calling addSoda")
                .statusCode(400);
                }
            
            // create path and map variables
            final String localVarPath = "/sodas";
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            String localVarQueryParameterBaseName;
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
                    localVarPostBody,
                    localVarHeaderParams,
                    localVarFormParams,
                    localVarAccept,
                    localVarContentType,
                    localVarReturnType
                    );
        }

        /**
        * <p>Delete a specific soda from the store
        * <p>
        * <p><b>204</b> - Soda successfully deleted
        * <p><b>404</b> - Soda not found
        * @param sodaId
        *      ID of the soda to delete
        * @return An OpenApiResponse containing the status code of the HttpResponse.
        * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
        */
        @Nonnull
        public OpenApiResponse deleteSodaById(@Nonnull final Long sodaId) throws OpenApiRequestException {
            final Object localVarPostBody = null;
            
                // verify the required parameter 'sodaId' is set
                if (sodaId == null) {
                throw new OpenApiRequestException("Missing the required parameter 'sodaId' when calling deleteSodaById")
                .statusCode(400);
                }
            
            // create path and map variables
            final String localVarPath = "/sodas/{sodaId}"
                .replaceAll("\\{" + "sodaId" + "\\}", ApiClient.escapeString(ApiClient.parameterToString(sodaId)));
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            String localVarQueryParameterBaseName;
            final List<Pair> localVarQueryParams = new ArrayList<Pair>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
            final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
            final Map<String, Object> localVarFormParams = new HashMap<String, Object>();
            
                                
                                
                    final String[] localVarAccepts = {
                    
                    };
                    final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);
            
                    final String[] localVarContentTypes = {
                    
                    };
                    final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);
            
                                final TypeReference<OpenApiResponse> localVarReturnType = new TypeReference<OpenApiResponse>() {};
                    return apiClient.invokeAPI(
                    localVarPath,
                    "DELETE",
                    localVarQueryParams,
                    localVarCollectionQueryParams,
                    localVarQueryStringJoiner.toString(),
                    localVarPostBody,
                    localVarHeaderParams,
                    localVarFormParams,
                    localVarAccept,
                    localVarContentType,
                    localVarReturnType
                    );
        }

        /**
        * <p>Get details of a specific soda
        * <p>
        * <p><b>200</b> - The requested soda
        * <p><b>404</b> - Soda not found
        * @param sodaId
        *      ID of the soda to retrieve
        * @return Soda
        * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
        */
        @Nonnull
        public Soda getSodaById(@Nonnull final Long sodaId) throws OpenApiRequestException {
            final Object localVarPostBody = null;
            
                // verify the required parameter 'sodaId' is set
                if (sodaId == null) {
                throw new OpenApiRequestException("Missing the required parameter 'sodaId' when calling getSodaById")
                .statusCode(400);
                }
            
            // create path and map variables
            final String localVarPath = "/sodas/{sodaId}"
                .replaceAll("\\{" + "sodaId" + "\\}", ApiClient.escapeString(ApiClient.parameterToString(sodaId)));
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            String localVarQueryParameterBaseName;
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
            
                    final TypeReference<Soda> localVarReturnType = new TypeReference<Soda>() {};
                                return apiClient.invokeAPI(
                    localVarPath,
                    "GET",
                    localVarQueryParams,
                    localVarCollectionQueryParams,
                    localVarQueryStringJoiner.toString(),
                    localVarPostBody,
                    localVarHeaderParams,
                    localVarFormParams,
                    localVarAccept,
                    localVarContentType,
                    localVarReturnType
                    );
        }

        /**
        * <p>Get a list of all sodas
        * <p>
        * <p><b>200</b> - A list of sodas
        * @return List&lt;Soda&gt;
        * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
        */
        @Nonnull
        public List<Soda> getSodas() throws OpenApiRequestException {
            final Object localVarPostBody = null;
            
            // create path and map variables
            final String localVarPath = "/sodas";
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            String localVarQueryParameterBaseName;
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
            
                    final TypeReference<List<Soda>> localVarReturnType = new TypeReference<List<Soda>>() {};
                                return apiClient.invokeAPI(
                    localVarPath,
                    "GET",
                    localVarQueryParams,
                    localVarCollectionQueryParams,
                    localVarQueryStringJoiner.toString(),
                    localVarPostBody,
                    localVarHeaderParams,
                    localVarFormParams,
                    localVarAccept,
                    localVarContentType,
                    localVarReturnType
                    );
        }

        /**
        * <p>Update details of a specific soda
        * <p>
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
        public Soda updateSodaById(@Nonnull final Long sodaId, @Nonnull final UpdateSoda updateSoda) throws OpenApiRequestException {
            final Object localVarPostBody = updateSoda;
            
                // verify the required parameter 'sodaId' is set
                if (sodaId == null) {
                throw new OpenApiRequestException("Missing the required parameter 'sodaId' when calling updateSodaById")
                .statusCode(400);
                }
            
                // verify the required parameter 'updateSoda' is set
                if (updateSoda == null) {
                throw new OpenApiRequestException("Missing the required parameter 'updateSoda' when calling updateSodaById")
                .statusCode(400);
                }
            
            // create path and map variables
            final String localVarPath = "/sodas/{sodaId}"
                .replaceAll("\\{" + "sodaId" + "\\}", ApiClient.escapeString(ApiClient.parameterToString(sodaId)));
            
            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            String localVarQueryParameterBaseName;
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
                    localVarPostBody,
                    localVarHeaderParams,
                    localVarFormParams,
                    localVarAccept,
                    localVarContentType,
                    localVarReturnType
                    );
        }
        }
