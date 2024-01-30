/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.Artifact ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArtifactCreationResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArtifactList ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ArtifactPostData ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.InlineResponse400 ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.Name ; //NOPMD

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

public class ArtifactApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public ArtifactApi( @Nonnull final Destination httpDestination )
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
    public ArtifactApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Get number of artifacts</p>
     *<p>Retrieve  the number of available artifacts that match the specified filter criteria. Filter criteria include a scenarioId, executionId, an artifact name, artifact kind, or artifact labels. Search by substring of artifact name or description is also possible. </p>
     * <p><b>200</b> - Number of artifacts
     * <p><b>400</b> - The specification of the resource was incorrect
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param scenarioId  (optional)
        Scenario identifier
     * @param executionId  (optional)
        Execution identifier
     * @param name  (optional)
        Artifact name
     * @param kind  (optional)
        Kind of the artifact
     * @param $search  (optional)
        Generic search term to be looked for in various attributes
     * @param searchCaseInsensitive  (optional, default to false)
        indicates whether the search should be case insensitive
     * @param artifactLabelSelector  (optional
        Filter artifact by labels. Pass in expressions in the form of \&quot;key&#x3D;value\&quot; or \&quot;key!&#x3D;value\&quot; separated by commas and the result will be filtered to only those resources that have labels that match all provided expressions (i.e. logical AND). The maximum number of labels permitted for filtering is 10 
     * @return Integer
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public Integer artifactCount( @Nonnull final String aiResourceGroup,  @Nullable final String scenarioId,  @Nullable final String executionId,  @Nullable final Name name,  @Nullable final String kind,  @Nullable final String $search,  @Nullable final Boolean searchCaseInsensitive,  @Nullable final List<String> artifactLabelSelector) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling artifactCount");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/artifacts/$count").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "scenarioId", scenarioId));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "executionId", executionId));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "name", name));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "kind", kind));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$search", $search));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "searchCaseInsensitive", searchCaseInsensitive));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "artifactLabelSelector", artifactLabelSelector));
        

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
    * <p>Get number of artifacts</p>
     *<p>Retrieve  the number of available artifacts that match the specified filter criteria. Filter criteria include a scenarioId, executionId, an artifact name, artifact kind, or artifact labels. Search by substring of artifact name or description is also possible. </p>
     * <p><b>200</b> - Number of artifacts
     * <p><b>400</b> - The specification of the resource was incorrect
* @param aiResourceGroup
        Specify a resource group id
* @return Integer
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public Integer artifactCount( @Nonnull final String aiResourceGroup) throws OpenApiRequestException {
        return artifactCount(aiResourceGroup, null, null, null, null, null, null, null);
    }
    /**
    * <p>Register artifact</p>
     *<p>Register an artifact for use in a configuration, for example a model or a dataset.</p>
     * <p><b>201</b> - The artifact has been registered successfully
     * <p><b>400</b> - The specification of the resource was incorrect
* @param aiResourceGroup
        Specify a resource group id
* @param artifactPostData
            The value for the parameter artifactPostData
* @return ArtifactCreationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArtifactCreationResponse artifactCreate( @Nonnull final String aiResourceGroup,  @Nonnull final ArtifactPostData artifactPostData) throws OpenApiRequestException {
        final Object localVarPostBody = artifactPostData;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling artifactCreate");
        }
        
        // verify the required parameter 'artifactPostData' is set
        if (artifactPostData == null) {
            throw new OpenApiRequestException("Missing the required parameter 'artifactPostData' when calling artifactCreate");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/artifacts").build().toUriString();

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

        final ParameterizedTypeReference<ArtifactCreationResponse> localVarReturnType = new ParameterizedTypeReference<ArtifactCreationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p>Get artifact by ID</p>
     *<p>Retrieve details for artifact with artifactId.</p>
     * <p><b>200</b> - An artifact
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param artifactId  (required)
        Artifact identifier
     * @param $expand  (optional)
        expand detailed information on scenario
     * @return Artifact
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public Artifact artifactGet( @Nonnull final String aiResourceGroup,  @Nonnull final String artifactId,  @Nullable final String $expand) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling artifactGet");
        }
        
        // verify the required parameter 'artifactId' is set
        if (artifactId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'artifactId' when calling artifactGet");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("artifactId", artifactId);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/artifacts/{artifactId}").buildAndExpand(localVarPathParams).toUriString();

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

        final ParameterizedTypeReference<Artifact> localVarReturnType = new ParameterizedTypeReference<Artifact>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get artifact by ID</p>
     *<p>Retrieve details for artifact with artifactId.</p>
     * <p><b>200</b> - An artifact
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
* @param aiResourceGroup
        Specify a resource group id
* @param artifactId
        Artifact identifier
* @return Artifact
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public Artifact artifactGet( @Nonnull final String aiResourceGroup,  @Nonnull final String artifactId) throws OpenApiRequestException {
        return artifactGet(aiResourceGroup, artifactId, null);
    }

    /**
     * <p>Get list of artifacts</p>
     *<p>Retrieve a list of artifacts that matches the specified filter criteria. Filter criteria include scenario ID, execution ID, an artifact name, artifact kind, or artifact labels. Use top/skip parameters to paginate the result list. Search by substring of artifact name or description, if required. </p>
     * <p><b>200</b> - A list of artifacts
     * <p><b>400</b> - The specification of the resource was incorrect
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param scenarioId  (optional)
        Scenario identifier
     * @param executionId  (optional)
        Execution identifier
     * @param name  (optional)
        Artifact name
     * @param kind  (optional)
        Kind of the artifact
     * @param artifactLabelSelector  (optional
        Filter artifact by labels. Pass in expressions in the form of \&quot;key&#x3D;value\&quot; or \&quot;key!&#x3D;value\&quot; separated by commas and the result will be filtered to only those resources that have labels that match all provided expressions (i.e. logical AND). The maximum number of labels permitted for filtering is 10 
     * @param $top  (optional, default to 10000)
        Number of results to display
     * @param $skip  (optional)
        Number of results to be skipped from the ordered list of results
     * @param $search  (optional)
        Generic search term to be looked for in various attributes
     * @param searchCaseInsensitive  (optional, default to false)
        indicates whether the search should be case insensitive
     * @param $expand  (optional)
        expand detailed information on scenario
     * @return ArtifactList
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ArtifactList artifactQuery( @Nonnull final String aiResourceGroup,  @Nullable final String scenarioId,  @Nullable final String executionId,  @Nullable final Name name,  @Nullable final String kind,  @Nullable final List<String> artifactLabelSelector,  @Nullable final Integer $top,  @Nullable final Integer $skip,  @Nullable final String $search,  @Nullable final Boolean searchCaseInsensitive,  @Nullable final String $expand) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling artifactQuery");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/artifacts").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "scenarioId", scenarioId));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "executionId", executionId));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "name", name));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "kind", kind));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "artifactLabelSelector", artifactLabelSelector));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$top", $top));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$skip", $skip));
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

        final ParameterizedTypeReference<ArtifactList> localVarReturnType = new ParameterizedTypeReference<ArtifactList>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get list of artifacts</p>
     *<p>Retrieve a list of artifacts that matches the specified filter criteria. Filter criteria include scenario ID, execution ID, an artifact name, artifact kind, or artifact labels. Use top/skip parameters to paginate the result list. Search by substring of artifact name or description, if required. </p>
     * <p><b>200</b> - A list of artifacts
     * <p><b>400</b> - The specification of the resource was incorrect
* @param aiResourceGroup
        Specify a resource group id
* @return ArtifactList
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ArtifactList artifactQuery( @Nonnull final String aiResourceGroup) throws OpenApiRequestException {
        return artifactQuery(aiResourceGroup, null, null, null, null, null, null, null, null, null, null);
    }
}
