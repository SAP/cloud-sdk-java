/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.apiclassvendorextension.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.apiclassvendorextension.model.NewSoda ; //NOPMD
import com.sap.cloud.sdk.services.apiclassvendorextension.model.Soda ; //NOPMD

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.annotations.Beta;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

/**
* Soda Store API in version 1.0.0.
*
* API for managing sodas in a soda store
*/

public class AwesomeSodaApi extends AbstractOpenApiService {
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
    @Beta
    public AwesomeSodaApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

        /**
    * <p>Add a new soda to the store</p>
     *<p></p>
     * <p><b>201</b> - The newly added soda
* @param newSoda
            The value for the parameter newSoda
* @return Soda
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public Soda addSoda( @Nonnull final NewSoda newSoda) throws OpenApiRequestException {
        final Object localVarPostBody = newSoda;
        
        // verify the required parameter 'newSoda' is set
        if (newSoda == null) {
            throw new OpenApiRequestException("Missing the required parameter 'newSoda' when calling addSoda");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/sodas").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<Soda> localVarReturnType = new ParameterizedTypeReference<Soda>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
}
