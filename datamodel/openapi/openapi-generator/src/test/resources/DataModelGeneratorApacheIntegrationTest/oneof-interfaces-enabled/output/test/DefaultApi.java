/*
* Copyright (c) 2026 SAP SE or an SAP affiliate company. All rights reserved.
*/

package test;

import com.fasterxml.jackson.core.type.TypeReference;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.apache.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.apache.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.Pair;


import test.OneOf;

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
        * <p>Get a list of all sodas
        * <p>
        * <p><b>200</b> - A list of sodas
        * @return List&lt;OneOf&gt;
        * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
        */
        @Nonnull
        public List<OneOf> getSodas() throws OpenApiRequestException {
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
            
                    final TypeReference<List<OneOf>> localVarReturnType = new TypeReference<List<OneOf>>() {};
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
        }
