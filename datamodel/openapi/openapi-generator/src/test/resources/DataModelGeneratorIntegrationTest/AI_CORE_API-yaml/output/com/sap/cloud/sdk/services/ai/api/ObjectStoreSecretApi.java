/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.ErrorResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ObjectStoreSecretCreationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ObjectStoreSecretDeletionResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ObjectStoreSecretModificationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ObjectStoreSecretStatus ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ObjectStoreSecretStatusResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ObjectStoreSecretWithSensitiveDataRequest ; //NOPMD

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

public class ObjectStoreSecretApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public ObjectStoreSecretApi( @Nonnull final Destination httpDestination )
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
    public ObjectStoreSecretApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Create a secret</p>
     *<p>Create a secret based on the configuration in the request body </p>
     * <p><b>202</b> - The request to create a k8s secret based on the given configuration has been accepted. 
     * <p><b>400</b> - One of the following failure cases has occurred: &lt;ul&gt; &lt;li&gt; Neither JSON nor YAML was able to be parsed. &lt;li&gt; The request was invalidly formatted 
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param objectStoreSecretWithSensitiveDataRequest  (required)
        The value for the parameter objectStoreSecretWithSensitiveDataRequest
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param aiResourceGroup  (optional, default to default)
        Specify an existing resource group id to use. Uses \&quot;default\&quot; if value not provided.
     * @return ObjectStoreSecretCreationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ObjectStoreSecretCreationResponse storeSecretsCreate( @Nonnull final ObjectStoreSecretWithSensitiveDataRequest objectStoreSecretWithSensitiveDataRequest,  @Nullable final String authorization,  @Nullable final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = objectStoreSecretWithSensitiveDataRequest;
        
        // verify the required parameter 'objectStoreSecretWithSensitiveDataRequest' is set
        if (objectStoreSecretWithSensitiveDataRequest == null) {
            throw new OpenApiRequestException("Missing the required parameter 'objectStoreSecretWithSensitiveDataRequest' when calling storeSecretsCreate");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/objectStoreSecrets").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ObjectStoreSecretCreationResponse> localVarReturnType = new ParameterizedTypeReference<ObjectStoreSecretCreationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Create a secret</p>
     *<p>Create a secret based on the configuration in the request body </p>
     * <p><b>202</b> - The request to create a k8s secret based on the given configuration has been accepted. 
     * <p><b>400</b> - One of the following failure cases has occurred: &lt;ul&gt; &lt;li&gt; Neither JSON nor YAML was able to be parsed. &lt;li&gt; The request was invalidly formatted 
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param objectStoreSecretWithSensitiveDataRequest
            The value for the parameter objectStoreSecretWithSensitiveDataRequest
* @return ObjectStoreSecretCreationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ObjectStoreSecretCreationResponse storeSecretsCreate( @Nonnull final ObjectStoreSecretWithSensitiveDataRequest objectStoreSecretWithSensitiveDataRequest) throws OpenApiRequestException {
        return storeSecretsCreate(objectStoreSecretWithSensitiveDataRequest, null, null);
    }

    /**
     * <p>Delete object store secret</p>
     *<p>Delete a secret with the name of objectStoreName if it exists.</p>
     * <p><b>202</b> - The request to delete the secret has been accepted.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param objectStoreName  (required)
        Name of the object store for the secret.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param aiResourceGroup  (optional, default to default)
        Specify an existing resource group id to use. Uses \&quot;default\&quot; if value not provided.
     * @return ObjectStoreSecretDeletionResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ObjectStoreSecretDeletionResponse storeSecretsDelete( @Nonnull final String objectStoreName,  @Nullable final String authorization,  @Nullable final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'objectStoreName' is set
        if (objectStoreName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'objectStoreName' when calling storeSecretsDelete");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("objectStoreName", objectStoreName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/objectStoreSecrets/{objectStoreName}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ObjectStoreSecretDeletionResponse> localVarReturnType = new ParameterizedTypeReference<ObjectStoreSecretDeletionResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.DELETE, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Delete object store secret</p>
     *<p>Delete a secret with the name of objectStoreName if it exists.</p>
     * <p><b>202</b> - The request to delete the secret has been accepted.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param objectStoreName
        Name of the object store for the secret.
* @return ObjectStoreSecretDeletionResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ObjectStoreSecretDeletionResponse storeSecretsDelete( @Nonnull final String objectStoreName) throws OpenApiRequestException {
        return storeSecretsDelete(objectStoreName, null, null);
    }

    /**
     * <p>Returns the of metadata of secrets which match the query parameter.</p>
     *<p>This retrieves the metadata of the stored secret which match the parameter objectStoreName.  The fetched secret is constructed like objectStoreName-object-store-secret  The base64 encoded field for the stored secret is not returned. </p>
     * <p><b>200</b> - The request was processed successfully and the metadata of the of stored secrets wil be returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param objectStoreName  (required)
        Name of the object store for the secret.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param aiResourceGroup  (optional, default to default)
        Specify an existing resource group id to use. Uses \&quot;default\&quot; if value not provided.
     * @return ObjectStoreSecretStatus
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ObjectStoreSecretStatus storeSecretsGet( @Nonnull final String objectStoreName,  @Nullable final String authorization,  @Nullable final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'objectStoreName' is set
        if (objectStoreName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'objectStoreName' when calling storeSecretsGet");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("objectStoreName", objectStoreName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/objectStoreSecrets/{objectStoreName}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ObjectStoreSecretStatus> localVarReturnType = new ParameterizedTypeReference<ObjectStoreSecretStatus>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Returns the of metadata of secrets which match the query parameter.</p>
     *<p>This retrieves the metadata of the stored secret which match the parameter objectStoreName.  The fetched secret is constructed like objectStoreName-object-store-secret  The base64 encoded field for the stored secret is not returned. </p>
     * <p><b>200</b> - The request was processed successfully and the metadata of the of stored secrets wil be returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param objectStoreName
        Name of the object store for the secret.
* @return ObjectStoreSecretStatus
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ObjectStoreSecretStatus storeSecretsGet( @Nonnull final String objectStoreName) throws OpenApiRequestException {
        return storeSecretsGet(objectStoreName, null, null);
    }

    /**
     * <p>Update object store secret</p>
     *<p>Update a secret with name of objectStoreName if it exists. </p>
     * <p><b>202</b> - The request to update the secret based on the given configuration has been accepted. 
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param objectStoreName  (required)
        Name of the object store for the secret.
     * @param objectStoreSecretWithSensitiveDataRequest  (required)
        The value for the parameter objectStoreSecretWithSensitiveDataRequest
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param aiResourceGroup  (optional, default to default)
        Specify an existing resource group id to use. Uses \&quot;default\&quot; if value not provided.
     * @return ObjectStoreSecretModificationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ObjectStoreSecretModificationResponse storeSecretsPatch( @Nonnull final String objectStoreName,  @Nonnull final ObjectStoreSecretWithSensitiveDataRequest objectStoreSecretWithSensitiveDataRequest,  @Nullable final String authorization,  @Nullable final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = objectStoreSecretWithSensitiveDataRequest;
        
        // verify the required parameter 'objectStoreName' is set
        if (objectStoreName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'objectStoreName' when calling storeSecretsPatch");
        }
        
        // verify the required parameter 'objectStoreSecretWithSensitiveDataRequest' is set
        if (objectStoreSecretWithSensitiveDataRequest == null) {
            throw new OpenApiRequestException("Missing the required parameter 'objectStoreSecretWithSensitiveDataRequest' when calling storeSecretsPatch");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("objectStoreName", objectStoreName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/objectStoreSecrets/{objectStoreName}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ObjectStoreSecretModificationResponse> localVarReturnType = new ParameterizedTypeReference<ObjectStoreSecretModificationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.PATCH, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Update object store secret</p>
     *<p>Update a secret with name of objectStoreName if it exists. </p>
     * <p><b>202</b> - The request to update the secret based on the given configuration has been accepted. 
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param objectStoreName
        Name of the object store for the secret.
* @param objectStoreSecretWithSensitiveDataRequest
            The value for the parameter objectStoreSecretWithSensitiveDataRequest
* @return ObjectStoreSecretModificationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ObjectStoreSecretModificationResponse storeSecretsPatch( @Nonnull final String objectStoreName,  @Nonnull final ObjectStoreSecretWithSensitiveDataRequest objectStoreSecretWithSensitiveDataRequest) throws OpenApiRequestException {
        return storeSecretsPatch(objectStoreName, objectStoreSecretWithSensitiveDataRequest, null, null);
    }

    /**
     * <p>Get a list of metadata of available secrets.</p>
     *<p>Retrieve a list of metadata of the stored secrets. </p>
     * <p><b>200</b> - The request was successful and the requested metadata for the secret will be returned. This includes a list of attributes of the stored secret like - creationTimestamp, namespace etc. The secret&#39;s data field is not returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param $top  (optional)
        Number of results to display
     * @param $skip  (optional)
        Number of results to be skipped from the ordered list of results
     * @param $count  (optional)
        When the $count field is set to false, the response contains a count of the items present in the response. When the $count field is set to true, the response contains a count of all the items present on the server, and not just the ones in the response. When the $count field is not passed, it is false by default.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @param aiResourceGroup  (optional, default to default)
        Specify an existing resource group id to use. Uses \&quot;default\&quot; if value not provided.
     * @return ObjectStoreSecretStatusResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ObjectStoreSecretStatusResponse storeSecretsQuery( @Nullable final Integer $top,  @Nullable final Integer $skip,  @Nullable final Boolean $count,  @Nullable final String authorization,  @Nullable final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/objectStoreSecrets").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$top", $top));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$skip", $skip));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$count", $count));
        

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));
        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ObjectStoreSecretStatusResponse> localVarReturnType = new ParameterizedTypeReference<ObjectStoreSecretStatusResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get a list of metadata of available secrets.</p>
     *<p>Retrieve a list of metadata of the stored secrets. </p>
     * <p><b>200</b> - The request was successful and the requested metadata for the secret will be returned. This includes a list of attributes of the stored secret like - creationTimestamp, namespace etc. The secret&#39;s data field is not returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @return ObjectStoreSecretStatusResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ObjectStoreSecretStatusResponse storeSecretsQuery() throws OpenApiRequestException {
        return storeSecretsQuery(null, null, null, null, null);
    }
}
