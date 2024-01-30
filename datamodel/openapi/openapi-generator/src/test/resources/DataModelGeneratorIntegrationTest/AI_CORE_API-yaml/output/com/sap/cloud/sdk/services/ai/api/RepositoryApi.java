/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.ArgoCDRepositoryCreationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDRepositoryCredentials ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDRepositoryData ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDRepositoryDataResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDRepositoryDeletionResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDRepositoryDetails ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDRepositoryModificationResponse ; //NOPMD
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

public class RepositoryApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public RepositoryApi( @Nonnull final Destination httpDestination )
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
    public RepositoryApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>List all GitOps repositories for a tenant</p>
     *<p>Retrieve a list of all GitOps repositories for a tenant.</p>
     * <p><b>200</b> - Returns a list of all GitOps repositories for the tenant.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDRepositoryDataResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDRepositoryDataResponse all( @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/repositories").build().toUriString();

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

        final ParameterizedTypeReference<ArgoCDRepositoryDataResponse> localVarReturnType = new ParameterizedTypeReference<ArgoCDRepositoryDataResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>List all GitOps repositories for a tenant</p>
     *<p>Retrieve a list of all GitOps repositories for a tenant.</p>
     * <p><b>200</b> - Returns a list of all GitOps repositories for the tenant.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @return ArgoCDRepositoryDataResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDRepositoryDataResponse all() throws OpenApiRequestException {
        return all(null);
    }

    /**
     * <p>On-board a new GitOps repository</p>
     *<p>On-board a new GitOps repository as specified in the content payload</p>
     * <p><b>200</b> - The repository has been on-boarded
     * <p><b>409</b> - The provided repository already exists
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param argoCDRepositoryData  (required)
        The value for the parameter argoCDRepositoryData
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDRepositoryCreationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDRepositoryCreationResponse kubesubmitV4RepositoriesCreate( @Nonnull final ArgoCDRepositoryData argoCDRepositoryData,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = argoCDRepositoryData;
        
        // verify the required parameter 'argoCDRepositoryData' is set
        if (argoCDRepositoryData == null) {
            throw new OpenApiRequestException("Missing the required parameter 'argoCDRepositoryData' when calling kubesubmitV4RepositoriesCreate");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/repositories").build().toUriString();

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

        final ParameterizedTypeReference<ArgoCDRepositoryCreationResponse> localVarReturnType = new ParameterizedTypeReference<ArgoCDRepositoryCreationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>On-board a new GitOps repository</p>
     *<p>On-board a new GitOps repository as specified in the content payload</p>
     * <p><b>200</b> - The repository has been on-boarded
     * <p><b>409</b> - The provided repository already exists
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param argoCDRepositoryData
            The value for the parameter argoCDRepositoryData
* @return ArgoCDRepositoryCreationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDRepositoryCreationResponse kubesubmitV4RepositoriesCreate( @Nonnull final ArgoCDRepositoryData argoCDRepositoryData) throws OpenApiRequestException {
        return kubesubmitV4RepositoriesCreate(argoCDRepositoryData, null);
    }

    /**
     * <p>Off-board a repository.</p>
     *<p>Remove a repository from GitOps.</p>
     * <p><b>200</b> - The repository has been off-boarded successfully.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param repositoryName  (required)
        Name of the repository
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDRepositoryDeletionResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDRepositoryDeletionResponse kubesubmitV4RepositoriesDelete( @Nonnull final String repositoryName,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'repositoryName' is set
        if (repositoryName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'repositoryName' when calling kubesubmitV4RepositoriesDelete");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("repositoryName", repositoryName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/repositories/{repositoryName}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<ArgoCDRepositoryDeletionResponse> localVarReturnType = new ParameterizedTypeReference<ArgoCDRepositoryDeletionResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.DELETE, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Off-board a repository.</p>
     *<p>Remove a repository from GitOps.</p>
     * <p><b>200</b> - The repository has been off-boarded successfully.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param repositoryName
        Name of the repository
* @return ArgoCDRepositoryDeletionResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDRepositoryDeletionResponse kubesubmitV4RepositoriesDelete( @Nonnull final String repositoryName) throws OpenApiRequestException {
        return kubesubmitV4RepositoriesDelete(repositoryName, null);
    }

    /**
     * <p>Get the access details for a repository</p>
     *<p>Retrieve the access details for a repository if it exists.</p>
     * <p><b>200</b> - The repository details have been found and returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param repositoryName  (required)
        Name of the repository
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDRepositoryDetails
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDRepositoryDetails kubesubmitV4RepositoriesGet( @Nonnull final String repositoryName,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'repositoryName' is set
        if (repositoryName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'repositoryName' when calling kubesubmitV4RepositoriesGet");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("repositoryName", repositoryName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/repositories/{repositoryName}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<ArgoCDRepositoryDetails> localVarReturnType = new ParameterizedTypeReference<ArgoCDRepositoryDetails>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get the access details for a repository</p>
     *<p>Retrieve the access details for a repository if it exists.</p>
     * <p><b>200</b> - The repository details have been found and returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param repositoryName
        Name of the repository
* @return ArgoCDRepositoryDetails
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDRepositoryDetails kubesubmitV4RepositoriesGet( @Nonnull final String repositoryName) throws OpenApiRequestException {
        return kubesubmitV4RepositoriesGet(repositoryName, null);
    }

    /**
     * <p>Update the repository credentials.</p>
     *<p>Update the referenced repository credentials to synchronize a repository. </p>
     * <p><b>200</b> - The repository credentials have been updated and will eventually be synced.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param repositoryName  (required)
        Name of the repository
     * @param argoCDRepositoryCredentials  (required)
        The value for the parameter argoCDRepositoryCredentials
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDRepositoryModificationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDRepositoryModificationResponse kubesubmitV4RepositoriesUpdate( @Nonnull final String repositoryName,  @Nonnull final ArgoCDRepositoryCredentials argoCDRepositoryCredentials,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = argoCDRepositoryCredentials;
        
        // verify the required parameter 'repositoryName' is set
        if (repositoryName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'repositoryName' when calling kubesubmitV4RepositoriesUpdate");
        }
        
        // verify the required parameter 'argoCDRepositoryCredentials' is set
        if (argoCDRepositoryCredentials == null) {
            throw new OpenApiRequestException("Missing the required parameter 'argoCDRepositoryCredentials' when calling kubesubmitV4RepositoriesUpdate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("repositoryName", repositoryName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/repositories/{repositoryName}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<ArgoCDRepositoryModificationResponse> localVarReturnType = new ParameterizedTypeReference<ArgoCDRepositoryModificationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.PATCH, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Update the repository credentials.</p>
     *<p>Update the referenced repository credentials to synchronize a repository. </p>
     * <p><b>200</b> - The repository credentials have been updated and will eventually be synced.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param repositoryName
        Name of the repository
* @param argoCDRepositoryCredentials
            The value for the parameter argoCDRepositoryCredentials
* @return ArgoCDRepositoryModificationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDRepositoryModificationResponse kubesubmitV4RepositoriesUpdate( @Nonnull final String repositoryName,  @Nonnull final ArgoCDRepositoryCredentials argoCDRepositoryCredentials) throws OpenApiRequestException {
        return kubesubmitV4RepositoriesUpdate(repositoryName, argoCDRepositoryCredentials, null);
    }
}
