/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.AllArgoCDApplicationData ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDApplicationBaseData ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDApplicationCreationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDApplicationData ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDApplicationDeletionResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDApplicationModificationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDApplicationRefreshResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArgoCDApplicationStatus ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.Body ; //NOPMD
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

public class ApplicationApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public ApplicationApi( @Nonnull final Destination httpDestination )
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
    public ApplicationApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Return all applications</p>
     *<p>Return all Argo CD application data objects. </p>
     * <p><b>200</b> - All applications have been found and returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return AllArgoCDApplicationData
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public AllArgoCDApplicationData all( @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/applications").build().toUriString();

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

        final ParameterizedTypeReference<AllArgoCDApplicationData> localVarReturnType = new ParameterizedTypeReference<AllArgoCDApplicationData>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Return all applications</p>
     *<p>Return all Argo CD application data objects. </p>
     * <p><b>200</b> - All applications have been found and returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @return AllArgoCDApplicationData
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public AllArgoCDApplicationData all() throws OpenApiRequestException {
        return all(null);
    }

    /**
     * <p>Create an application</p>
     *<p>Create an ArgoCD application to synchronise a repository. </p>
     * <p><b>200</b> - The ArgoCD application has been created and will be eventually synchronised with the repository.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param body  (required)
        The value for the parameter body
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDApplicationCreationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDApplicationCreationResponse kubesubmitV4ApplicationsCreate( @Nonnull final Body body,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = body;
        
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new OpenApiRequestException("Missing the required parameter 'body' when calling kubesubmitV4ApplicationsCreate");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/applications").build().toUriString();

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

        final ParameterizedTypeReference<ArgoCDApplicationCreationResponse> localVarReturnType = new ParameterizedTypeReference<ArgoCDApplicationCreationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Create an application</p>
     *<p>Create an ArgoCD application to synchronise a repository. </p>
     * <p><b>200</b> - The ArgoCD application has been created and will be eventually synchronised with the repository.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param body
            The value for the parameter body
* @return ArgoCDApplicationCreationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDApplicationCreationResponse kubesubmitV4ApplicationsCreate( @Nonnull final Body body) throws OpenApiRequestException {
        return kubesubmitV4ApplicationsCreate(body, null);
    }

    /**
     * <p>Delete application</p>
     *<p>Delete an ArgoCD application</p>
     * <p><b>200</b> - The argoCD application has been deleted successfully.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param applicationName  (required)
        Name of the ArgoCD application
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDApplicationDeletionResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDApplicationDeletionResponse kubesubmitV4ApplicationsDelete( @Nonnull final String applicationName,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'applicationName' is set
        if (applicationName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'applicationName' when calling kubesubmitV4ApplicationsDelete");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("applicationName", applicationName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/applications/{applicationName}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<ArgoCDApplicationDeletionResponse> localVarReturnType = new ParameterizedTypeReference<ArgoCDApplicationDeletionResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.DELETE, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Delete application</p>
     *<p>Delete an ArgoCD application</p>
     * <p><b>200</b> - The argoCD application has been deleted successfully.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param applicationName
        Name of the ArgoCD application
* @return ArgoCDApplicationDeletionResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDApplicationDeletionResponse kubesubmitV4ApplicationsDelete( @Nonnull final String applicationName) throws OpenApiRequestException {
        return kubesubmitV4ApplicationsDelete(applicationName, null);
    }

    /**
     * <p>Get ArgoCD application</p>
     *<p>Retrieve the ArgoCD application details. </p>
     * <p><b>200</b> - The application has been found and returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param applicationName  (required)
        Name of the ArgoCD application
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDApplicationData
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDApplicationData kubesubmitV4ApplicationsGet( @Nonnull final String applicationName,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'applicationName' is set
        if (applicationName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'applicationName' when calling kubesubmitV4ApplicationsGet");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("applicationName", applicationName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/applications/{applicationName}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<ArgoCDApplicationData> localVarReturnType = new ParameterizedTypeReference<ArgoCDApplicationData>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get ArgoCD application</p>
     *<p>Retrieve the ArgoCD application details. </p>
     * <p><b>200</b> - The application has been found and returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param applicationName
        Name of the ArgoCD application
* @return ArgoCDApplicationData
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDApplicationData kubesubmitV4ApplicationsGet( @Nonnull final String applicationName) throws OpenApiRequestException {
        return kubesubmitV4ApplicationsGet(applicationName, null);
    }

    /**
     * <p>Makes ArgoDC refresh the specified application</p>
     *<p>Schedules a refresh of the specified application that will be picked up by ArgoCD asynchronously </p>
     * <p><b>202</b> - A refresh of the application has been scheduled
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param applicationName  (required)
        Name of the ArgoCD application
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDApplicationRefreshResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDApplicationRefreshResponse kubesubmitV4ApplicationsRefresh( @Nonnull final String applicationName,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'applicationName' is set
        if (applicationName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'applicationName' when calling kubesubmitV4ApplicationsRefresh");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("applicationName", applicationName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/applications/{applicationName}/refresh").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<ArgoCDApplicationRefreshResponse> localVarReturnType = new ParameterizedTypeReference<ArgoCDApplicationRefreshResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Makes ArgoDC refresh the specified application</p>
     *<p>Schedules a refresh of the specified application that will be picked up by ArgoCD asynchronously </p>
     * <p><b>202</b> - A refresh of the application has been scheduled
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param applicationName
        Name of the ArgoCD application
* @return ArgoCDApplicationRefreshResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDApplicationRefreshResponse kubesubmitV4ApplicationsRefresh( @Nonnull final String applicationName) throws OpenApiRequestException {
        return kubesubmitV4ApplicationsRefresh(applicationName, null);
    }

    /**
     * <p>Update the ArgoCD application.</p>
     *<p>Update the referenced ArgoCD application to synchronize the repository. </p>
     * <p><b>200</b> - The ArgoCD application has been created and will be eventually synchronised with the repository.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param applicationName  (required)
        Name of the ArgoCD application
     * @param argoCDApplicationBaseData  (required)
        The value for the parameter argoCDApplicationBaseData
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDApplicationModificationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDApplicationModificationResponse kubesubmitV4ApplicationsUpdate( @Nonnull final String applicationName,  @Nonnull final ArgoCDApplicationBaseData argoCDApplicationBaseData,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = argoCDApplicationBaseData;
        
        // verify the required parameter 'applicationName' is set
        if (applicationName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'applicationName' when calling kubesubmitV4ApplicationsUpdate");
        }
        
        // verify the required parameter 'argoCDApplicationBaseData' is set
        if (argoCDApplicationBaseData == null) {
            throw new OpenApiRequestException("Missing the required parameter 'argoCDApplicationBaseData' when calling kubesubmitV4ApplicationsUpdate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("applicationName", applicationName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/applications/{applicationName}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<ArgoCDApplicationModificationResponse> localVarReturnType = new ParameterizedTypeReference<ArgoCDApplicationModificationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.PATCH, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Update the ArgoCD application.</p>
     *<p>Update the referenced ArgoCD application to synchronize the repository. </p>
     * <p><b>200</b> - The ArgoCD application has been created and will be eventually synchronised with the repository.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param applicationName
        Name of the ArgoCD application
* @param argoCDApplicationBaseData
            The value for the parameter argoCDApplicationBaseData
* @return ArgoCDApplicationModificationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDApplicationModificationResponse kubesubmitV4ApplicationsUpdate( @Nonnull final String applicationName,  @Nonnull final ArgoCDApplicationBaseData argoCDApplicationBaseData) throws OpenApiRequestException {
        return kubesubmitV4ApplicationsUpdate(applicationName, argoCDApplicationBaseData, null);
    }

    /**
     * <p>Returns the ArgoCD application status</p>
     *<p>Returns the ArgoCD application health and sync status. </p>
     * <p><b>200</b> - The application status has been found and returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
     * @param applicationName  (required)
        Name of the ArgoCD application
     * @param authorization  (optional)
        Authorization bearer token containing a JWT token.
     * @return ArgoCDApplicationStatus
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArgoCDApplicationStatus status( @Nonnull final String applicationName,  @Nullable final String authorization) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'applicationName' is set
        if (applicationName == null) {
            throw new OpenApiRequestException("Missing the required parameter 'applicationName' when calling status");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("applicationName", applicationName);
        final String localVarPath = UriComponentsBuilder.fromPath("/admin/applications/{applicationName}/status").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<ArgoCDApplicationStatus> localVarReturnType = new ParameterizedTypeReference<ArgoCDApplicationStatus>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Returns the ArgoCD application status</p>
     *<p>Returns the ArgoCD application health and sync status. </p>
     * <p><b>200</b> - The application status has been found and returned.
     * <p><b>400</b> - The request was malformed and could thus not be processed.
     * <p><b>404</b> - The specified resource was not found
     * <p><b>0</b> - HTTP status codes 401, 403 or 500. Response body contains further details.
* @param applicationName
        Name of the ArgoCD application
* @return ArgoCDApplicationStatus
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArgoCDApplicationStatus status( @Nonnull final String applicationName) throws OpenApiRequestException {
        return status(applicationName, null);
    }
}
