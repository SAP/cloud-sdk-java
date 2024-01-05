/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.core;

import javax.annotation.Nonnull;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import lombok.Getter;

/**
 * Response object for OpenAPI calls containing status code
 */
public class OpenApiResponse
{
    /**
     * Http status code of this response.
     */
    @Getter
    private final int statusCode;

    /**
     * Http headers of this response.
     */
    @Nonnull
    @Getter
    private final MultiValueMap<String, String> headers;

    /**
     * Create a new {@code OpenApiResponse} from an {@link ApiClient}
     *
     * @param client
     *            A client that was used to execute an HTTP request.
     */
    public OpenApiResponse( @Nonnull final ApiClient client )
    {
        statusCode = client.getStatusCode();
        headers = client.getResponseHeaders();
    }

    /**
     * Create a new {@code OpenApiResponse} from an http status code.
     *
     * @param statusCode
     *            The status code for this http response.
     */
    public OpenApiResponse( final int statusCode )
    {
        this.statusCode = statusCode;
        headers = new HttpHeaders();
    }
}
