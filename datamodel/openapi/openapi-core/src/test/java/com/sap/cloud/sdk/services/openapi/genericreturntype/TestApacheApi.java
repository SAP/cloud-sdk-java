package com.sap.cloud.sdk.services.openapi.genericreturntype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.Pair;

class TestApacheApi extends BaseApi
{

    TestApacheApi( @Nonnull final Destination destination )
    {
        super(destination);
    }

    Object testMethod()
    {
        final Object localVarPostBody = null;

        // create path and map variables
        final String localVarPath = "/endpoint";

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept =
            com.sap.cloud.sdk.services.openapi.apache.ApiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType =
            com.sap.cloud.sdk.services.openapi.apache.ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<Object> localVarReturnType = new TypeReference<Object>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }
}
