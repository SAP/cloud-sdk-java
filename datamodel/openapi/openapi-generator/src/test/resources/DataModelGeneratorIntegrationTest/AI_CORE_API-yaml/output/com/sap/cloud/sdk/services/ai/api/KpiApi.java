/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.ColumnName ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.InlineResponse400 ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ResourceGroup ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ResultSet ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.ResultSetDeprecated ; //NOPMD
import java.util.Set ; //NOPMD

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

public class KpiApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public KpiApi( @Nonnull final Destination httpDestination )
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
    public KpiApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Get KPIs</p>
     *<p>Retrieve the number of executions, artifacts, and deployments  for each resource group, scenario, and executable. The columns to be returned can be specified in a query parameter. </p>
     * <p><b>200</b> - KPIs
     * <p><b>400</b> - Invalid request
     * <p><b>404</b> - The specified resource was not found
     * <p><b>429</b> - Too many requests
     * @param $select  (optional
        Columns to select
     * @return ResultSet
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public ResultSet kpiGet( @Nullable final Set<ColumnName> $select) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/analytics/kpis").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "$select", $select));
        

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ResultSet> localVarReturnType = new ParameterizedTypeReference<ResultSet>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Get KPIs</p>
     *<p>Retrieve the number of executions, artifacts, and deployments  for each resource group, scenario, and executable. The columns to be returned can be specified in a query parameter. </p>
     * <p><b>200</b> - KPIs
     * <p><b>400</b> - Invalid request
     * <p><b>404</b> - The specified resource was not found
     * <p><b>429</b> - Too many requests
* @return ResultSet
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public ResultSet kpiGet() throws OpenApiRequestException {
        return kpiGet(null);
    }
    /**
    * <p>Get Kpi(s)</p>
     *<p>Fetch latest values of the total number of Executions, Artifacts and Deployments recorded against each ResourceGroup.</p>
     * <p><b>200</b> - OK
     * <p><b>404</b> - Not Found
     * <p><b>429</b> - Too Many Requests
* @return ResultSetDeprecated
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     * @deprecated
     */
    @Deprecated
    @Nullable   public ResultSetDeprecated kpisDeprecated() throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/kpis").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<ResultSetDeprecated> localVarReturnType = new ParameterizedTypeReference<ResultSetDeprecated>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
    /**
    * <p>List resourceGroups</p>
     *<p>Retrieve a list of the resourceGroups of the tenant</p>
     * <p><b>200</b> - OK
     * <p><b>404</b> - The specified resource was not found
     * <p><b>429</b> - Too many requests
* @return List&lt;ResourceGroup&gt;
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     * @deprecated
     */
    @Deprecated
    @Nullable   public List<ResourceGroup> resourceGroups() throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/analytics/resourceGroups").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<List<ResourceGroup>> localVarReturnType = new ParameterizedTypeReference<List<ResourceGroup>>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
}
