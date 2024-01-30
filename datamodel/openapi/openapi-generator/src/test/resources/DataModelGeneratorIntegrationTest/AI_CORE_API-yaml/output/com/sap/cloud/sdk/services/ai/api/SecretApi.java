/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.ErrorResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.GenericSecretDataResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.GenericSecretPatchBody ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.GenericSecretPostBody ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ListGenericSecretsResponse ; //NOPMD

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

public class SecretApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public SecretApi( @Nonnull final Destination httpDestination )
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
    public SecretApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Create a new generic secret</p>
     *<p>Create a new generic secret in the corresponding resource group or at main tenant level.</p>
     * <p><b>200</b> - Secret has been created
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param genericSecretPostBody  (required)
        The value for the parameter genericSecretPostBody
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param aiResourceGroup  (optional)
        Specify an existing resource group id to use
     * @param aiTenantScope  (optional)
        Specify whether the main tenant scope is to be used
     * @return GenericSecretDataResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public GenericSecretDataResponse secretsCreate( @Nonnull final GenericSecretPostBody genericSecretPostBody,  @Nullable final String authorization,  @Nullable final String aiResourceGroup,  @Nullable final Boolean aiTenantScope) throws OpenApiRequestException {
        final Object localVarPostBody = genericSecretPostBody;
        
        // verify the required parameter 'genericSecretPostBody' is set
        if (genericSecretPostBody == null) {
            throw new OpenApiRequestException("Missing the required parameter 'genericSecretPostBody' when calling secretsCreate");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/secrets").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));
        if (aiTenantScope != null)
            localVarHeaderParams.add("AI-Tenant-Scope", apiClient.parameterToString(aiTenantScope));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<GenericSecretDataResponse> localVarReturnType = new ParameterizedTypeReference<GenericSecretDataResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Create a new generic secret</p>
     *<p>Create a new generic secret in the corresponding resource group or at main tenant level.</p>
     * <p><b>200</b> - Secret has been created
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param genericSecretPostBody
            The value for the parameter genericSecretPostBody
* @return GenericSecretDataResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public GenericSecretDataResponse secretsCreate( @Nonnull final GenericSecretPostBody genericSecretPostBody) throws OpenApiRequestException {
        return secretsCreate(genericSecretPostBody, null, null, null);
    }

    /**
     * <p>Deletes the secret</p>
     *<p>Deletes the secret from provided resource group namespace</p>
     * <p><b>200</b> - The secret has been removed
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param secretName  (required)
        The value for the parameter secretName
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param aiResourceGroup  (optional)
        Specify an existing resource group id to use
     * @param aiTenantScope  (optional)
        Specify whether the main tenant scope is to be used
     * @return An OpenApiResponse containing the status code of the HttpResponse.
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
     @Nonnull public OpenApiResponse secretsDelete( @Nonnull final String secretName,  @Nullable final String authorization,  @Nullable final String aiResourceGroup,  @Nullable final Boolean aiTenantScope) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'secretName' is set
        if (secretName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'secretName' when calling secretsDelete");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("secretName", secretName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/secrets/{secretName}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));
        if (aiTenantScope != null)
            localVarHeaderParams.add("AI-Tenant-Scope", apiClient.parameterToString(aiTenantScope));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        apiClient.invokeAPI(localVarPath, HttpMethod.DELETE, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
        return new OpenApiResponse(apiClient);
    }

    /**
    * <p>Deletes the secret</p>
     *<p>Deletes the secret from provided resource group namespace</p>
     * <p><b>200</b> - The secret has been removed
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param secretName
            The value for the parameter secretName
* @return An OpenApiResponse containing the status code of the HttpResponse.
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
     @Nonnull  public OpenApiResponse secretsDelete( @Nonnull final String secretName) throws OpenApiRequestException {
        return secretsDelete(secretName, null, null, null);
    }

    /**
     * <p>Lists all secrets corresponding to tenant</p>
     *<p>Lists all secrets corresponding to tenant. This retrieves metadata only, not the secret data itself.</p>
     * <p><b>200</b> - The secrets were fetched
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param $top  (optional)
        Number of results to display
     * @param $skip  (optional)
        Number of results to be skipped from the ordered list of results
     * @param aiResourceGroup  (optional)
        Specify an existing resource group id to use
     * @param aiTenantScope  (optional)
        Specify whether the main tenant scope is to be used
     * @return ListGenericSecretsResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ListGenericSecretsResponse secretsGet( @Nullable final String authorization,  @Nullable final Integer $top,  @Nullable final Integer $skip,  @Nullable final String aiResourceGroup,  @Nullable final Boolean aiTenantScope) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/secrets").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$top", $top));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$skip", $skip));
        

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));
        if (aiTenantScope != null)
            localVarHeaderParams.add("AI-Tenant-Scope", apiClient.parameterToString(aiTenantScope));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ListGenericSecretsResponse> localVarReturnType = new ParameterizedTypeReference<ListGenericSecretsResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Lists all secrets corresponding to tenant</p>
     *<p>Lists all secrets corresponding to tenant. This retrieves metadata only, not the secret data itself.</p>
     * <p><b>200</b> - The secrets were fetched
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @return ListGenericSecretsResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ListGenericSecretsResponse secretsGet() throws OpenApiRequestException {
        return secretsGet(null, null, null, null, null);
    }

    /**
     * <p>Update secret credentials</p>
     *<p>Update secret credentials. Replace secret data with the provided data.</p>
     * <p><b>200</b> - The secret has been updated
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param secretName  (required)
        The value for the parameter secretName
     * @param genericSecretPatchBody  (required)
        The value for the parameter genericSecretPatchBody
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param aiResourceGroup  (optional)
        Specify an existing resource group id to use
     * @param aiTenantScope  (optional)
        Specify whether the main tenant scope is to be used
     * @return GenericSecretDataResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public GenericSecretDataResponse secretsUpdate( @Nonnull final String secretName,  @Nonnull final GenericSecretPatchBody genericSecretPatchBody,  @Nullable final String authorization,  @Nullable final String aiResourceGroup,  @Nullable final Boolean aiTenantScope) throws OpenApiRequestException {
        final Object localVarPostBody = genericSecretPatchBody;
        
        // verify the required parameter 'secretName' is set
        if (secretName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'secretName' when calling secretsUpdate");
        }
        
        // verify the required parameter 'genericSecretPatchBody' is set
        if (genericSecretPatchBody == null) {
            throw new OpenApiRequestException("Missing the required parameter 'genericSecretPatchBody' when calling secretsUpdate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("secretName", secretName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/secrets/{secretName}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));
        if (aiTenantScope != null)
            localVarHeaderParams.add("AI-Tenant-Scope", apiClient.parameterToString(aiTenantScope));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<GenericSecretDataResponse> localVarReturnType = new ParameterizedTypeReference<GenericSecretDataResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.PATCH, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Update secret credentials</p>
     *<p>Update secret credentials. Replace secret data with the provided data.</p>
     * <p><b>200</b> - The secret has been updated
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param secretName
            The value for the parameter secretName
* @param genericSecretPatchBody
            The value for the parameter genericSecretPatchBody
* @return GenericSecretDataResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public GenericSecretDataResponse secretsUpdate( @Nonnull final String secretName,  @Nonnull final GenericSecretPatchBody genericSecretPatchBody) throws OpenApiRequestException {
        return secretsUpdate(secretName, genericSecretPatchBody, null, null, null);
    }
}
