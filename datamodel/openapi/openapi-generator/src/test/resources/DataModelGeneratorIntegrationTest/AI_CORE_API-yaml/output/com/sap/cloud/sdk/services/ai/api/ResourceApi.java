/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.ErrorResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ResourceGetResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ResourcePatchBody ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ResourcePatchResponse ; //NOPMD

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
* AI Core in version 2.26.0.
*
* Provides tools to manage your scenarios and workflows in SAP AI Core. Execute pipelines as a batch job, for example to pre-process or train your models, or perform batch inference.  Serve inference requests of trained models. Deploy а trained machine learning model as a web service to serve inference requests with high performance.  Register your own Docker registry, synchronize your AI content from your own git repository, and register your own object store for training data and trained models. 
*/

public class ResourceApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public ResourceApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    /**
    * Instantiates this API class to invoke operations on the AI Core based on a given {@link ApiClient}.
    *
    * @param apiClient
    *            ApiClient to invoke the API on
    */
    @Beta
    public ResourceApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Get node request status corresponding to tenant</p>
     *<p>Lists all hot spare nodes, used nodes and total nodes corresponding to tenant.</p>
     * <p><b>200</b> - Reserved resource status were fetched
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ResourceGetResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ResourceGetResponse kubesubmitV4ResourcesGet( @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/resources/nodes").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ResourceGetResponse> localVarReturnType = new ParameterizedTypeReference<ResourceGetResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get node request status corresponding to tenant</p>
     *<p>Lists all hot spare nodes, used nodes and total nodes corresponding to tenant.</p>
     * <p><b>200</b> - Reserved resource status were fetched
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @return ResourceGetResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ResourceGetResponse kubesubmitV4ResourcesGet() throws OpenApiRequestException {
        return kubesubmitV4ResourcesGet(null);
    }

    /**
     * <p>Set reserved resources corresponding to tenant</p>
     *<p>Set hot spare nodes corresponding to tenant at main tenant level.</p>
     * <p><b>200</b> - Reserved resource has been set.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param resourcePatchBody  (required)
        The value for the parameter resourcePatchBody
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ResourcePatchResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ResourcePatchResponse kubesubmitV4ResourcesPatch( @Nonnull final ResourcePatchBody resourcePatchBody,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = resourcePatchBody;
        
        // verify the required parameter 'resourcePatchBody' is set
        if (resourcePatchBody == null) {
            throw new OpenApiRequestException("Missing the required parameter 'resourcePatchBody' when calling kubesubmitV4ResourcesPatch");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/resources/nodes").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ResourcePatchResponse> localVarReturnType = new ParameterizedTypeReference<ResourcePatchResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.PATCH, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Set reserved resources corresponding to tenant</p>
     *<p>Set hot spare nodes corresponding to tenant at main tenant level.</p>
     * <p><b>200</b> - Reserved resource has been set.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param resourcePatchBody
            The value for the parameter resourcePatchBody
* @return ResourcePatchResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ResourcePatchResponse kubesubmitV4ResourcesPatch( @Nonnull final ResourcePatchBody resourcePatchBody) throws OpenApiRequestException {
        return kubesubmitV4ResourcesPatch(resourcePatchBody, null);
    }
}
