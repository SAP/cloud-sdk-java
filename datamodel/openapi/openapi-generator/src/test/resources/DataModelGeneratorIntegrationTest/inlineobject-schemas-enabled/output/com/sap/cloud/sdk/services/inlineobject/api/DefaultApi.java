/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.inlineobject.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.inlineobject.model.NotFoundContent;
import com.sap.cloud.sdk.services.inlineobject.model.ServiceUnavailableApplicationJson;
import com.sap.cloud.sdk.services.inlineobject.model.ServiceUnavailableApplicationXml;
import com.sap.cloud.sdk.services.inlineobject.model.Soda;

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
    @Beta
    public DefaultApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

        /**
     * <p>Get details of a specific soda</p>
     * <p></p>
     * <p><b>200</b> - The requested soda
     * <p><b>404</b> - The specified resource was not found
     * <p><b>503</b> - The service is currently unavailable
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
            "application/json", "application/xml"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<Soda> localVarReturnType = new ParameterizedTypeReference<Soda>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
}
