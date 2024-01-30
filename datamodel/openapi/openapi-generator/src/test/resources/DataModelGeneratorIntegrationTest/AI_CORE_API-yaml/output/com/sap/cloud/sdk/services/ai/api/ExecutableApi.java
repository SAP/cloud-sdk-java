/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.Executable ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ExecutableList ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.InlineResponse400 ; //NOPMD

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

public class ExecutableApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public ExecutableApi( @Nonnull final Destination httpDestination )
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
    public ExecutableApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

        /**
    * <p>Get details about specific executable</p>
     *<p>Retrieve details about an executable identified by executableId belonging to a scenario identified by scenarioId. </p>
     * <p><b>200</b> - An Executable
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
* @param scenarioId
        Scenario identifier
* @param executableId
        Executable identifier
* @param aiResourceGroup
        Specify a resource group id
* @return Executable
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public Executable executableGet( @Nonnull final String scenarioId,  @Nonnull final String executableId,  @Nonnull final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'scenarioId' is set
        if (scenarioId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'scenarioId' when calling executableGet");
        }
        
        // verify the required parameter 'executableId' is set
        if (executableId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'executableId' when calling executableGet");
        }
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling executableGet");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("scenarioId", scenarioId);
        localVarPathParams.put("executableId", executableId);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/scenarios/{scenarioId}/executables/{executableId}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (aiResourceGroup != null)
        localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<Executable> localVarReturnType = new ParameterizedTypeReference<Executable>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p>Get list of executables</p>
     *<p>Retrieve a list of executables for a scenario. Filter by version ID, if required. </p>
     * <p><b>200</b> - A list of executables
     * <p><b>400</b> - The specification of the resource was incorrect
     * @param scenarioId  (required)
        Scenario identifier
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param versionId  (optional)
        Version ID, if defined - returns the specified version
     * @return ExecutableList
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ExecutableList executableQuery( @Nonnull final String scenarioId,  @Nonnull final String aiResourceGroup,  @Nullable final String versionId) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'scenarioId' is set
        if (scenarioId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'scenarioId' when calling executableQuery");
        }
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling executableQuery");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("scenarioId", scenarioId);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/scenarios/{scenarioId}/executables").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "versionId", versionId));
        

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ExecutableList> localVarReturnType = new ParameterizedTypeReference<ExecutableList>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get list of executables</p>
     *<p>Retrieve a list of executables for a scenario. Filter by version ID, if required. </p>
     * <p><b>200</b> - A list of executables
     * <p><b>400</b> - The specification of the resource was incorrect
* @param scenarioId
        Scenario identifier
* @param aiResourceGroup
        Specify a resource group id
* @return ExecutableList
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ExecutableList executableQuery( @Nonnull final String scenarioId,  @Nonnull final String aiResourceGroup) throws OpenApiRequestException {
        return executableQuery(scenarioId, aiResourceGroup, null);
    }
}
