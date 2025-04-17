/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.builder.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.builder.model.Soda;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.nio.charset.StandardCharsets;
import java.net.URI;

import com.fasterxml.jackson.core.type.TypeReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.ai.sdk.core.AiCoreService;

/**
 * Soda Store API in version 1.0.0.
 *
 * API for managing sodas in a soda store
 */
public class AwesomeSodasApi extends AbstractOpenApiService {

    /**
     * Instantiates this API class to invoke operations on the Soda Store API
     */
    public AwesomeSodasApi()
    {
         super(new AiCoreService().getApiClient());
    }

    /**
     * Instantiates this API class to invoke operations on the Soda Store API
     *
     * @param aiCoreService The configured connectivity instance to AI Core
     */
    public AwesomeSodasApi( @Nonnull final AiCoreService aiCoreService )
    {
        super(aiCoreService.getApiClient());
    }

        /**
     * <p>Get a list of all sodas</p>
     * <p></p>
     * <p><b>200</b> - A list of sodas
     * @return List&lt;Soda&gt;
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public List<Soda> getSodas() throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        String path = "/sodas";
        final String localVarPath = URI.create(path).toString();

        final Map<String, List<String>> localVarQueryParams = new LinkedHashMap<>();
        final Map<String, List<String>> localVarHeaderParams = new LinkedHashMap<>();
        final Map<String, List<Object>> localVarFormParams = new LinkedHashMap<>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<String> localVarAccept = apiClient.getHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final String localVarContentType = apiClient.getHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final TypeReference<List<Soda>> localVarReturnType = new TypeReference<>() {};
        return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
}
