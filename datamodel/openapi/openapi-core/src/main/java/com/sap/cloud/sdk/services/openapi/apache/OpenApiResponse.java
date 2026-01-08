package com.sap.cloud.sdk.services.openapi.apache;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.Getter;

/**
 * Response object for Apache HTTP client OpenAPI calls containing status code and headers
 */
@Getter
public class OpenApiResponse
{

    private final int statusCode;

    @Nonnull
    private final Map<String, List<String>> headers;

    /**
     * Create a new OpenApiResponse with status code and headers.
     */
    OpenApiResponse( final int statusCode, @Nonnull final Map<String, List<String>> headers )
    {
        this.statusCode = statusCode;
        this.headers = Map.copyOf(headers);
    }
}
