/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.Body1 ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.DockerRegistrySecretCreationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.DockerRegistrySecretDeletionResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.DockerRegistrySecretModificationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.DockerRegistrySecretStatus ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.DockerRegistrySecretStatusResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.DockerRegistrySecretWithSensitiveDataRequest ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ErrorResponse ; //NOPMD

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

public class DockerRegistrySecretApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public DockerRegistrySecretApi( @Nonnull final Destination httpDestination )
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
    public DockerRegistrySecretApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Create a secret</p>
     *<p>Create a secret based on the configuration in the request body. </p>
     * <p><b>202</b> - The request to create a k8s secret based on the given configuration has been accepted. 
     * <p><b>400</b> - One of the following failure cases has occurred: &lt;ul&gt; &lt;li&gt; Neither JSON nor YAML was able to be parsed. &lt;li&gt; The request was invalidly formatted 
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param body1  (required)
        The value for the parameter body1
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return DockerRegistrySecretCreationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public DockerRegistrySecretCreationResponse registrySecretsCreate( @Nonnull final Body1 body1,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = body1;
        
        // verify the required parameter 'body1' is set
        if (body1 == null) {
            throw new OpenApiRequestException("Missing the required parameter 'body1' when calling registrySecretsCreate");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/dockerRegistrySecrets").build().toUriString();

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

        final ParameterizedTypeReference<DockerRegistrySecretCreationResponse> localVarReturnType = new ParameterizedTypeReference<DockerRegistrySecretCreationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Create a secret</p>
     *<p>Create a secret based on the configuration in the request body. </p>
     * <p><b>202</b> - The request to create a k8s secret based on the given configuration has been accepted. 
     * <p><b>400</b> - One of the following failure cases has occurred: &lt;ul&gt; &lt;li&gt; Neither JSON nor YAML was able to be parsed. &lt;li&gt; The request was invalidly formatted 
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param body1
            The value for the parameter body1
* @return DockerRegistrySecretCreationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public DockerRegistrySecretCreationResponse registrySecretsCreate( @Nonnull final Body1 body1) throws OpenApiRequestException {
        return registrySecretsCreate(body1, null);
    }

    /**
     * <p>Delete docker registry secret</p>
     *<p>Delete a secret with the name of dockerRegistryName if it exists.</p>
     * <p><b>202</b> - The request to delete the secret has been accepted.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param dockerRegistryName  (required)
        Name of the docker Registry store for the secret.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return DockerRegistrySecretDeletionResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public DockerRegistrySecretDeletionResponse registrySecretsDelete( @Nonnull final String dockerRegistryName,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'dockerRegistryName' is set
        if (dockerRegistryName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'dockerRegistryName' when calling registrySecretsDelete");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("dockerRegistryName", dockerRegistryName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/dockerRegistrySecrets/{dockerRegistryName}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<DockerRegistrySecretDeletionResponse> localVarReturnType = new ParameterizedTypeReference<DockerRegistrySecretDeletionResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.DELETE, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Delete docker registry secret</p>
     *<p>Delete a secret with the name of dockerRegistryName if it exists.</p>
     * <p><b>202</b> - The request to delete the secret has been accepted.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param dockerRegistryName
        Name of the docker Registry store for the secret.
* @return DockerRegistrySecretDeletionResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public DockerRegistrySecretDeletionResponse registrySecretsDelete( @Nonnull final String dockerRegistryName) throws OpenApiRequestException {
        return registrySecretsDelete(dockerRegistryName, null);
    }

    /**
     * <p>Returns the of metadata of secrets which match the query parameter.</p>
     *<p>Retrieve the stored secret metadata which matches the parameter dockerRegistryName. The base64 encoded field for the stored secret is not returned. </p>
     * <p><b>200</b> - The request was processed successfully and the metadata of the of stored secrets wil be returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param dockerRegistryName  (required)
        Name of the docker Registry store for the secret.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return DockerRegistrySecretStatus
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public DockerRegistrySecretStatus registrySecretsGet( @Nonnull final String dockerRegistryName,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'dockerRegistryName' is set
        if (dockerRegistryName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'dockerRegistryName' when calling registrySecretsGet");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("dockerRegistryName", dockerRegistryName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/dockerRegistrySecrets/{dockerRegistryName}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<DockerRegistrySecretStatus> localVarReturnType = new ParameterizedTypeReference<DockerRegistrySecretStatus>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Returns the of metadata of secrets which match the query parameter.</p>
     *<p>Retrieve the stored secret metadata which matches the parameter dockerRegistryName. The base64 encoded field for the stored secret is not returned. </p>
     * <p><b>200</b> - The request was processed successfully and the metadata of the of stored secrets wil be returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param dockerRegistryName
        Name of the docker Registry store for the secret.
* @return DockerRegistrySecretStatus
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public DockerRegistrySecretStatus registrySecretsGet( @Nonnull final String dockerRegistryName) throws OpenApiRequestException {
        return registrySecretsGet(dockerRegistryName, null);
    }

    /**
     * <p>Update a secret</p>
     *<p>Update a secret with name of dockerRegistryName if it exists. </p>
     * <p><b>202</b> - The request to update the secret based on the the given configuration has been accepted. 
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param dockerRegistryName  (required)
        Name of the docker Registry store for the secret.
     * @param dockerRegistrySecretWithSensitiveDataRequest  (required)
        The value for the parameter dockerRegistrySecretWithSensitiveDataRequest
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return DockerRegistrySecretModificationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public DockerRegistrySecretModificationResponse registrySecretsPatch( @Nonnull final String dockerRegistryName,  @Nonnull final DockerRegistrySecretWithSensitiveDataRequest dockerRegistrySecretWithSensitiveDataRequest,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = dockerRegistrySecretWithSensitiveDataRequest;
        
        // verify the required parameter 'dockerRegistryName' is set
        if (dockerRegistryName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'dockerRegistryName' when calling registrySecretsPatch");
        }
        
        // verify the required parameter 'dockerRegistrySecretWithSensitiveDataRequest' is set
        if (dockerRegistrySecretWithSensitiveDataRequest == null) {
            throw new OpenApiRequestException("Missing the required parameter 'dockerRegistrySecretWithSensitiveDataRequest' when calling registrySecretsPatch");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("dockerRegistryName", dockerRegistryName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/dockerRegistrySecrets/{dockerRegistryName}").buildAndExpand(localVarPathParams).toUriString();

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
            "application/merge-patch+json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<DockerRegistrySecretModificationResponse> localVarReturnType = new ParameterizedTypeReference<DockerRegistrySecretModificationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.PATCH, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Update a secret</p>
     *<p>Update a secret with name of dockerRegistryName if it exists. </p>
     * <p><b>202</b> - The request to update the secret based on the the given configuration has been accepted. 
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param dockerRegistryName
        Name of the docker Registry store for the secret.
* @param dockerRegistrySecretWithSensitiveDataRequest
            The value for the parameter dockerRegistrySecretWithSensitiveDataRequest
* @return DockerRegistrySecretModificationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public DockerRegistrySecretModificationResponse registrySecretsPatch( @Nonnull final String dockerRegistryName,  @Nonnull final DockerRegistrySecretWithSensitiveDataRequest dockerRegistrySecretWithSensitiveDataRequest) throws OpenApiRequestException {
        return registrySecretsPatch(dockerRegistryName, dockerRegistrySecretWithSensitiveDataRequest, null);
    }

    /**
     * <p>Get a list of metadata of secrets.</p>
     *<p>Retrieve a list of metadata of the stored secrets </p>
     * <p><b>200</b> - The request was successful and the requested metadata for the secret will be returned. This includes a list of attributes of the stored secret like - creationTimestamp, namespace etc. The secret&#39;s data field is not returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param $top  (optional)
        Number of results to display
     * @param $skip  (optional)
        Number of results to be skipped from the ordered list of results
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return DockerRegistrySecretStatusResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public DockerRegistrySecretStatusResponse registrySecretsQuery( @Nullable final Integer $top,  @Nullable final Integer $skip,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/dockerRegistrySecrets").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$top", $top));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$skip", $skip));
        

        if (authorization != null)
            localVarHeaderParams.add("Authorization", apiClient.parameterToString(authorization));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<DockerRegistrySecretStatusResponse> localVarReturnType = new ParameterizedTypeReference<DockerRegistrySecretStatusResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get a list of metadata of secrets.</p>
     *<p>Retrieve a list of metadata of the stored secrets </p>
     * <p><b>200</b> - The request was successful and the requested metadata for the secret will be returned. This includes a list of attributes of the stored secret like - creationTimestamp, namespace etc. The secret&#39;s data field is not returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @return DockerRegistrySecretStatusResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public DockerRegistrySecretStatusResponse registrySecretsQuery() throws OpenApiRequestException {
        return registrySecretsQuery(null, null, null);
    }
}
