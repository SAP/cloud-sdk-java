/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.ConfigurationBaseData ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ConfigurationCreationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ConfigurationList ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.InlineResponse400 ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ModelConfiguration ; //NOPMD

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

public class ConfigurationApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public ConfigurationApi( @Nonnull final Destination httpDestination )
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
    public ConfigurationApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Get number of configurations</p>
     *<p>Retrieve the number of available configurations that match the specified filter criteria. Filter criteria include a scenarioId or executableIdsList. Search by substring of configuration name is also possible. </p>
     * <p><b>200</b> - Number of configurations
     * <p><b>400</b> - The specification of the resource was incorrect
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param scenarioId  (optional)
        Scenario identifier
     * @param $search  (optional)
        Generic search term to be looked for in various attributes
     * @param searchCaseInsensitive  (optional, default to false)
        indicates whether the search should be case insensitive
     * @param executableIds  (optional
        Limit query to only these executable IDs
     * @return Integer
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public Integer configurationCount( @Nonnull final String aiResourceGroup,  @Nullable final String scenarioId,  @Nullable final String $search,  @Nullable final Boolean searchCaseInsensitive,  @Nullable final List<String> executableIds) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling configurationCount");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/configurations/$count").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "scenarioId", scenarioId));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$search", $search));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "searchCaseInsensitive", searchCaseInsensitive));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "executableIds", executableIds));
        

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "text/plain", "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<Integer> localVarReturnType = new ParameterizedTypeReference<Integer>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get number of configurations</p>
     *<p>Retrieve the number of available configurations that match the specified filter criteria. Filter criteria include a scenarioId or executableIdsList. Search by substring of configuration name is also possible. </p>
     * <p><b>200</b> - Number of configurations
     * <p><b>400</b> - The specification of the resource was incorrect
* @param aiResourceGroup
        Specify a resource group id
* @return Integer
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public Integer configurationCount( @Nonnull final String aiResourceGroup) throws OpenApiRequestException {
        return configurationCount(aiResourceGroup, null, null, null, null);
    }
    /**
    * <p>Create configuration</p>
     *<p>Create a new configuration linked to a specific scenario and executable for use in an execution or deployment. </p>
     * <p><b>201</b> - The created configuration
     * <p><b>400</b> - The specification of the resource was incorrect
* @param aiResourceGroup
        Specify a resource group id
* @param configurationBaseData
            The value for the parameter configurationBaseData
* @return ConfigurationCreationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ConfigurationCreationResponse configurationCreate( @Nonnull final String aiResourceGroup,  @Nonnull final ConfigurationBaseData configurationBaseData) throws OpenApiRequestException {
        final Object localVarPostBody = configurationBaseData;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling configurationCreate");
        }
        
        // verify the required parameter 'configurationBaseData' is set
        if (configurationBaseData == null) {
            throw new OpenApiRequestException("Missing the required parameter 'configurationBaseData' when calling configurationCreate");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/configurations").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

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

        final ParameterizedTypeReference<ConfigurationCreationResponse> localVarReturnType = new ParameterizedTypeReference<ConfigurationCreationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p>Get configuration by ID</p>
     *<p>Retrieve details for configuration with configurationId.</p>
     * <p><b>200</b> - A configuration
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param configurationId  (required)
        Configuration identifier
     * @param $expand  (optional)
        expand detailed information on scenario
     * @return ModelConfiguration
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ModelConfiguration configurationGet( @Nonnull final String aiResourceGroup,  @Nonnull final String configurationId,  @Nullable final String $expand) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling configurationGet");
        }
        
        // verify the required parameter 'configurationId' is set
        if (configurationId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'configurationId' when calling configurationGet");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("configurationId", configurationId);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/configurations/{configurationId}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$expand", $expand));
        

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ModelConfiguration> localVarReturnType = new ParameterizedTypeReference<ModelConfiguration>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get configuration by ID</p>
     *<p>Retrieve details for configuration with configurationId.</p>
     * <p><b>200</b> - A configuration
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
* @param aiResourceGroup
        Specify a resource group id
* @param configurationId
        Configuration identifier
* @return ModelConfiguration
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ModelConfiguration configurationGet( @Nonnull final String aiResourceGroup,  @Nonnull final String configurationId) throws OpenApiRequestException {
        return configurationGet(aiResourceGroup, configurationId, null);
    }

    /**
     * <p>Get list of configurations</p>
     *<p>Retrieve a list of configurations. Filter results by scenario ID or a list of executable IDs. Search for configurations containing the search string as substring in the configuration name. </p>
     * <p><b>200</b> - A list of configurations
     * <p><b>400</b> - The specification of the resource was incorrect
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param scenarioId  (optional)
        Scenario identifier
     * @param $top  (optional, default to 10000)
        Number of results to display
     * @param $skip  (optional)
        Number of results to be skipped from the ordered list of results
     * @param executableIds  (optional
        Limit query to only these executable IDs
     * @param $search  (optional)
        Generic search term to be looked for in various attributes
     * @param searchCaseInsensitive  (optional, default to false)
        indicates whether the search should be case insensitive
     * @param $expand  (optional)
        expand detailed information on scenario
     * @return ConfigurationList
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ConfigurationList configurationQuery( @Nonnull final String aiResourceGroup,  @Nullable final String scenarioId,  @Nullable final Integer $top,  @Nullable final Integer $skip,  @Nullable final List<String> executableIds,  @Nullable final String $search,  @Nullable final Boolean searchCaseInsensitive,  @Nullable final String $expand) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling configurationQuery");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/configurations").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "scenarioId", scenarioId));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$top", $top));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$skip", $skip));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "executableIds", executableIds));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$search", $search));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "searchCaseInsensitive", searchCaseInsensitive));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$expand", $expand));
        

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ConfigurationList> localVarReturnType = new ParameterizedTypeReference<ConfigurationList>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get list of configurations</p>
     *<p>Retrieve a list of configurations. Filter results by scenario ID or a list of executable IDs. Search for configurations containing the search string as substring in the configuration name. </p>
     * <p><b>200</b> - A list of configurations
     * <p><b>400</b> - The specification of the resource was incorrect
* @param aiResourceGroup
        Specify a resource group id
* @return ConfigurationList
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ConfigurationList configurationQuery( @Nonnull final String aiResourceGroup) throws OpenApiRequestException {
        return configurationQuery(aiResourceGroup, null, null, null, null, null, null, null);
    }
}
