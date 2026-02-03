package com.sap.cloud.sdk.services.openapi.apache.core;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Response object for Apache HTTP client OpenAPI calls containing status code and headers
 */
@Getter
@RequiredArgsConstructor
public class OpenApiResponse
{

    private final int statusCode;

    @Nonnull
    private final Map<String, List<String>> headers;
}
