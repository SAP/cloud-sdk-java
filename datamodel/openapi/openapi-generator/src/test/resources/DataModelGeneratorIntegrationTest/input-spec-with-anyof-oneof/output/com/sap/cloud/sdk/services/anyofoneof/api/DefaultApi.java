/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.anyofoneof.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.anyofoneof.model.RootObject;

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
* Sample API in version 1.0.0.
*
* API for managing root and child objects
*/

public class DefaultApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the Sample API.
    *
    * @param httpDestination The destination that API should be used with
    */
    public DefaultApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    /**
    * Instantiates this API class to invoke operations on the Sample API based on a given {@link ApiClient}.
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
     * <p></p>
     *<p></p>
     * <p><b>201</b> - Something was created.
     * <p><b>400</b> - Request was invalid.
     * <p><b>401</b> - Authentication Error
     * @param rootObject  (optional)
        The value for the parameter rootObject
     * @return RootObject
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull public RootObject someEndpointPost( @Nullable final RootObject rootObject) throws OpenApiRequestException {
        final Object localVarPostBody = rootObject;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/some/endpoint").build().toUriString();

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

        final ParameterizedTypeReference<RootObject> localVarReturnType = new ParameterizedTypeReference<RootObject>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p></p>
     *<p></p>
     * <p><b>201</b> - Something was created.
     * <p><b>400</b> - Request was invalid.
     * <p><b>401</b> - Authentication Error
* @return RootObject
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public RootObject someEndpointPost() throws OpenApiRequestException {
        return someEndpointPost(null);
    }
}
