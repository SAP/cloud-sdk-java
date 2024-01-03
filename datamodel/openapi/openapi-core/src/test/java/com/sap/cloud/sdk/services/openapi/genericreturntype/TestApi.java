/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.genericreturntype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

class TestApi extends AbstractOpenApiService
{
    TestApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    Object testMethod()
        throws OpenApiRequestException
    {
        final Object postBody = null;

        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        final String path = UriComponentsBuilder.fromPath("/endpoint").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { "application/json" };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        final String[] authNames = new String[] {};

        final ParameterizedTypeReference<Object> returnType = new ParameterizedTypeReference<Object>()
        {
        };
        return apiClient
            .invokeAPI(
                path,
                HttpMethod.GET,
                queryParams,
                postBody,
                headerParams,
                formParams,
                accept,
                contentType,
                authNames,
                returnType);
    }
}
