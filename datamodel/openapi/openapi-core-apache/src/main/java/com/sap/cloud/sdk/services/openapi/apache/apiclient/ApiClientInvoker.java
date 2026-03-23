package com.sap.cloud.sdk.services.openapi.apache.apiclient;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiRequestException;

@FunctionalInterface
@Beta
public interface ApiClientInvoker
{
    /**
     * Invoke API by sending HTTP request with the given options.
     *
     * @param <T>
     *            Type
     * @param path
     *            The sub-path of the HTTP URL
     * @param method
     *            The request method, one of "GET", "POST", "PUT", and "DELETE"
     * @param queryParams
     *            The query parameters
     * @param collectionQueryParams
     *            The collection query parameters
     * @param urlQueryDeepObject
     *            A URL query string for deep object parameters
     * @param body
     *            The request body object - if it is not binary, otherwise null
     * @param headerParams
     *            The header parameters
     * @param formParams
     *            The form parameters
     * @param accept
     *            The request's Accept header
     * @param contentType
     *            The request's Content-Type header
     * @param returnType
     *            Return type
     * @return The response body in type of string
     * @throws OpenApiRequestException
     *             API exception
     */
    @Nullable
    <T> T invokeAPI(
        @Nonnull final String path,
        @Nonnull final String method,
        @Nullable final List<Pair> queryParams,
        @Nullable final List<Pair> collectionQueryParams,
        @Nullable final String urlQueryDeepObject,
        @Nullable final Object body,
        @Nonnull final Map<String, String> headerParams,
        @Nonnull final Map<String, Object> formParams,
        @Nullable final String accept,
        @Nonnull final String contentType,
        @Nonnull final TypeReference<T> returnType );
}
