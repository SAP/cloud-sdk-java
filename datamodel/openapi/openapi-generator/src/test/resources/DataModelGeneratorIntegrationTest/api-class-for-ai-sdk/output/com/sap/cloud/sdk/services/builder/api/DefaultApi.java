/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
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

import com.sap.ai.sdk.core.AiCoreService;

/**
 * Soda Store API in version 1.0.0.
 *
 * API for managing sodas in a soda store
 */
public class DefaultApi extends AbstractOpenApiService {

    /**
     * Instantiates this API class to invoke operations on the Soda Store API
     */
    public DefaultApi()
    {
         super(new AiCoreService().getApiClient());
    }

    /**
     * Instantiates this API class to invoke operations on the Soda Store API
     *
     * @param aiCoreService The configured connectivity instance to AI Core
     */
    public DefaultApi( @Nonnull final AiCoreService aiCoreService )
    {
        super(aiCoreService.getApiClient());
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
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("sodaId", sodaId);
        final String localVarPath = UriComponentsBuilder.fromPath("/sodas/{sodaId}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        apiClient.invokeAPI(localVarPath, HttpMethod.DELETE, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
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
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("sodaId", sodaId);
        final String localVarPath = UriComponentsBuilder.fromPath("/sodas/{sodaId}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<Soda> localVarReturnType = new ParameterizedTypeReference<Soda>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
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
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("sodaId", sodaId);
        final String localVarPath = UriComponentsBuilder.fromPath("/sodas/{sodaId}").buildAndExpand(localVarPathParams).toUriString();

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
        return apiClient.invokeAPI(localVarPath, HttpMethod.PUT, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
}
