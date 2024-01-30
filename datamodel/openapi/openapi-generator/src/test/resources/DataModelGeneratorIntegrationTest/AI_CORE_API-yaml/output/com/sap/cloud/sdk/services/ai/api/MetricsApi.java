/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.DeleteMetricsResponse ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ExecutionId2 ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.GetMetricResourceList ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.InlineResponse4001 ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.MetricResource ; //NOPMD

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

public class MetricsApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public MetricsApi( @Nonnull final Destination httpDestination )
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
    public MetricsApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

        /**
    * <p>Delete metrics, tags, or labels</p>
     *<p>Delete metrics, tags, or labels associated with an execution.</p>
     * <p><b>200</b> - Metric Resource was successfully deleted
     * <p><b>404</b> - The specified resource was not found
* @param aiResourceGroup
        Specify a resource group id
* @param executionId
        The Id of an execution
* @return DeleteMetricsResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public DeleteMetricsResponse metricsDelete( @Nonnull final String aiResourceGroup,  @Nonnull final ExecutionId2 executionId) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling metricsDelete");
        }
        
        // verify the required parameter 'executionId' is set
        if (executionId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'executionId' when calling metricsDelete");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/metrics").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "executionId", executionId));

        if (aiResourceGroup != null)
        localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<DeleteMetricsResponse> localVarReturnType = new ParameterizedTypeReference<DeleteMetricsResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.DELETE, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p>Get metrics according to specified filter conditions. </p>
     *<p>Retrieve metrics, labels, or tags according to filter conditions.  One query parameter is mandatory, either execution ID or filter.  Use up to 10 execution IDs in a query parameter. </p>
     * <p><b>200</b> - List of tracking metadata, where each item includes metrics, labels, tags and customInfo. If $select query parameter is specified, each item will include only the resources specified in $select.
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>501</b> - Operation is not Supported.
     * @param aiResourceGroup  (required)
        Specify a resource group id
     * @param $filter  (optional)
        Filter parameter allows filtering of metric resource using ExecutionId(s). User can only use in, eq operators in filter expression.
     * @param executionIds  (optional
        executionIds parameter allows filtering of metric resource using single or multiple ExecutionId(s).
     * @param $select  (optional
        returns only the resources that the client explicitly requests. User can also pass * as a value for $select,  which will behave same as that of not passing $select query param.
     * @return GetMetricResourceList
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public GetMetricResourceList metricsFind( @Nonnull final String aiResourceGroup,  @Nullable final String $filter,  @Nullable final List<String> executionIds,  @Nullable final List<String> $select) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling metricsFind");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/metrics").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "$filter", $filter));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "executionIds", executionIds));
                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "$select", $select));
        

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<GetMetricResourceList> localVarReturnType = new ParameterizedTypeReference<GetMetricResourceList>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get metrics according to specified filter conditions. </p>
     *<p>Retrieve metrics, labels, or tags according to filter conditions.  One query parameter is mandatory, either execution ID or filter.  Use up to 10 execution IDs in a query parameter. </p>
     * <p><b>200</b> - List of tracking metadata, where each item includes metrics, labels, tags and customInfo. If $select query parameter is specified, each item will include only the resources specified in $select.
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>501</b> - Operation is not Supported.
* @param aiResourceGroup
        Specify a resource group id
* @return GetMetricResourceList
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public GetMetricResourceList metricsFind( @Nonnull final String aiResourceGroup) throws OpenApiRequestException {
        return metricsFind(aiResourceGroup, null, null, null);
    }
    /**
    * <p>Create or update metrics, tags, or labels</p>
     *<p>Update or create metrics, tags, or labels associated with an execution. </p>
     * <p><b>204</b> - Metrics was successfully updated/created
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>413</b> - request entity is larger than limits defined by server.
* @param aiResourceGroup
        Specify a resource group id
* @param metricResource
            The value for the parameter metricResource
* @return An OpenApiResponse containing the status code of the HttpResponse.
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
     @Nonnull  public OpenApiResponse metricsPatch( @Nonnull final String aiResourceGroup,  @Nonnull final MetricResource metricResource) throws OpenApiRequestException {
        final Object localVarPostBody = metricResource;
        
        // verify the required parameter 'aiResourceGroup' is set
        if (aiResourceGroup == null) {
            throw new OpenApiRequestException("Missing the required parameter 'aiResourceGroup' when calling metricsPatch");
        }
        
        // verify the required parameter 'metricResource' is set
        if (metricResource == null) {
            throw new OpenApiRequestException("Missing the required parameter 'metricResource' when calling metricsPatch");
        }
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/metrics").build().toUriString();

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
            "application/merge-patch+json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        apiClient.invokeAPI(localVarPath, HttpMethod.PATCH, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
        return new OpenApiResponse(apiClient);
    }
}
