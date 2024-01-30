/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.InlineResponse400 ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.Scenario ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ScenarioList ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.VersionList ; //NOPMD

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

public class ScenarioApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public ScenarioApi( @Nonnull final Destination httpDestination )
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
    public ScenarioApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

        /**
    * <p>Get scenario by id</p>
     *<p>Retrieve details for a scenario specified by scenarioId.</p>
     * <p><b>200</b> - A scenario
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
* @param aiResourceGroup
        Specify a resource group id
* @param scenarioId
        Scenario identifier
* @return Scenario
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public Scenario scenarioGet( @Nonnull final String aiResourceGroup,  @Nonnull final String scenarioId) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling scenarioGet");
        }
        
        // verify the required parameter 'scenarioId' is set
        if (scenarioId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'scenarioId' when calling scenarioGet");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("scenarioId", scenarioId);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/scenarios/{scenarioId}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<Scenario> localVarReturnType = new ParameterizedTypeReference<Scenario>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
    /**
    * <p>Get list of scenarios</p>
     *<p>Retrieve a list of all available scenarios.</p>
     * <p><b>200</b> - A list of scenarios
* @param aiResourceGroup
        Specify a resource group id
* @return ScenarioList
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ScenarioList scenarioQuery( @Nonnull final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling scenarioQuery");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/scenarios").build().toUriString();

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

        final ParameterizedTypeReference<ScenarioList> localVarReturnType = new ParameterizedTypeReference<ScenarioList>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p>Get list of versions for scenario</p>
     *<p>Retrieve a list of scenario versions based on the versions of executables available within that scenario. </p>
     * <p><b>200</b> - A list of versions for the scenario
     * <p><b>400</b> - The specification of the resource was incorrect
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param scenarioId  (required)
        Scenario identifier
     * @param labelSelector  (optional
        filter by labels. Pass in expressions in the form of \&quot;key&#x3D;value\&quot; or \&quot;key!&#x3D;value\&quot; separated by commas and the result will be filtered to only those resources that have labels that match all provided expressions (i.e. logical AND) 
     * @return VersionList
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public VersionList versions( @Nonnull final String aiResourceGroup,  @Nonnull final String scenarioId,  @Nullable final List<String> labelSelector) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling versions");
        }
        
        // verify the required parameter 'scenarioId' is set
        if (scenarioId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'scenarioId' when calling versions");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("scenarioId", scenarioId);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/scenarios/{scenarioId}/versions").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "labelSelector", labelSelector));
        

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<VersionList> localVarReturnType = new ParameterizedTypeReference<VersionList>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get list of versions for scenario</p>
     *<p>Retrieve a list of scenario versions based on the versions of executables available within that scenario. </p>
     * <p><b>200</b> - A list of versions for the scenario
     * <p><b>400</b> - The specification of the resource was incorrect
* @param aiResourceGroup
        Specify a resource group id
* @param scenarioId
        Scenario identifier
* @return VersionList
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public VersionList versions( @Nonnull final String aiResourceGroup,  @Nonnull final String scenarioId) throws OpenApiRequestException {
        return versions(aiResourceGroup, scenarioId, null);
    }
}
