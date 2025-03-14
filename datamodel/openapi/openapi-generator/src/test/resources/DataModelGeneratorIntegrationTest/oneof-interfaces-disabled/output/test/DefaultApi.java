/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package test;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import test.OneOf;

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

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

/**
 * Soda Store API in version 1.0.0.
 *
 * API for managing sodas in a soda store
 */
public class DefaultApi extends AbstractOpenApiService {
    /**
     * Instantiates this API class to invoke operations on the Soda Store API.
     *
     * @param httpDestination The destination that API should be used with
     */
    public DefaultApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the Soda Store API based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    @com.google.common.annotations.Beta
    public DefaultApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

        /**
     * <p>Get a list of all sodas</p>
     * <p></p>
     * <p><b>200</b> - A list of sodas
     * @return List&lt;OneOf&gt;
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public List<OneOf> getSodas() throws OpenApiRequestException {
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

        final TypeReference<List<OneOf>> localVarReturnType = new TypeReference<>() {};
        return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
}
