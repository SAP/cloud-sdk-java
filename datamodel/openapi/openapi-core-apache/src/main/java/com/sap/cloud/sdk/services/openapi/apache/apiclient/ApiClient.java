/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.apache.apiclient;

import static com.sap.cloud.sdk.services.openapi.apache.apiclient.DefaultApiResponseHandler.isJsonMime;
import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiResponseListener;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

/**
 * API client for executing HTTP requests using Apache HttpClient 5.
 */
@AllArgsConstructor( access = PRIVATE )
@EqualsAndHashCode
@ToString
public class ApiClient
{
    private static final String DEFAULT_BASE_PATH = "http://localhost";

    @Nonnull
    private final CloseableHttpClient httpClient;

    @With
    @Getter
    @Nonnull
    private final String basePath;

    @With( onMethod_ = @Beta )
    @Nonnull
    private final ObjectMapper objectMapper;

    @With
    @Nullable
    private final String tempFolderPath;

    @With( onMethod_ = @Beta )
    @Nonnull
    private final OpenApiResponseListener openApiResponseListener;

    @With( onMethod_ = @Beta )
    @Nonnull
    private final UnaryOperator<ClassicRequestBuilder> requestCustomizer;

    /**
     * Creates an ApiClient instance from an existing HttpClient.
     *
     * @param httpClient
     *            The HttpClient to use for requests
     * @return A new ApiClient instance
     */
    @Nonnull
    public static ApiClient fromHttpClient( @Nonnull final CloseableHttpClient httpClient )
    {
        return fromHttpClient(httpClient, DEFAULT_BASE_PATH);
    }

    /**
     * Creates an ApiClient instance from an existing HttpClient.
     *
     * @param httpClient
     *            The HttpClient to use for requests
     * @param basePath
     *            The base path to use for requests
     * @return A new ApiClient instance
     */
    @Nonnull
    public static ApiClient fromHttpClient( @Nonnull final CloseableHttpClient httpClient, @Nonnull final String basePath )
    {
        return new ApiClient(httpClient, basePath, createDefaultObjectMapper(), null, r -> {
        }, r -> r);
    }

    /**
     * Creates an ApiClient instance configured for the given destination.
     *
     * @param destination
     *            The destination to use for requests
     * @return A new ApiClient instance configured with the destination
     */
    @Nonnull
    public static ApiClient create( @Nonnull final Destination destination )
    {
        final CloseableHttpClient httpClient =
            (CloseableHttpClient) ApacheHttpClient5Accessor.getHttpClient(destination);
        return fromHttpClient(httpClient, destination.asHttp().getUri().toString());
    }

    /**
     * Creates an ApiClient instance with default configuration.
     *
     * @return A new ApiClient instance
     */
    @Nonnull
    public static ApiClient create()
    {
        return fromHttpClient((CloseableHttpClient) ApacheHttpClient5Accessor.getHttpClient());
    }

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
    public <T> T invokeAPI(
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
        @Nonnull final TypeReference<T> returnType )
    {
        final ClassicRequestBuilder builder =
            ClassicRequestFactory
                .buildClassicRequest(
                    basePath,
                    path,
                    method,
                    queryParams,
                    collectionQueryParams,
                    urlQueryDeepObject,
                    body,
                    headerParams,
                    formParams,
                    accept,
                    contentType,
                    objectMapper);

        return invokeAPI(builder, returnType);
    }

    /**
     * Invoke API by sending HTTP request with the given request builder.
     *
     * @param <T>
     *            Type
     * @param requestBuilder
     *            The request builder with all parameters configured
     * @param resultType
     *            Return type
     * @return The response body
     * @throws OpenApiRequestException
     *             API exception
     */
    @Nonnull
    protected <T> T invokeAPI(
        @Nonnull final ClassicRequestBuilder requestBuilder,
        @Nonnull final TypeReference<T> resultType )
    {
        final ClassicRequestBuilder finalBuilder = requestCustomizer.apply(requestBuilder);

        final HttpClientContext context = HttpClientContext.create();
        try {
            final HttpClientResponseHandler<T> responseHandler =
                new DefaultApiResponseHandler<>(objectMapper, tempFolderPath, resultType, openApiResponseListener);
            return httpClient.execute(finalBuilder.build(), context, responseHandler);
        }
        catch( final IOException e ) {
            throw new OpenApiRequestException(e);
        }
    }

    @Nonnull
    static ObjectMapper createDefaultObjectMapper()
    {
        return JsonMapper
            .builder()
            .addModule(new JavaTimeModule())
            .defaultDateFormat(new RFC3339DateFormat())
            .visibility(PropertyAccessor.GETTER, Visibility.NONE)
            .visibility(PropertyAccessor.SETTER, Visibility.NONE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }

    /**
     * Format the given Date object into string.
     *
     * @param date
     *            Date
     * @return Date in string format
     */
    @Nonnull
    private static String formatDate( @Nonnull final Date date )
    {
        return new RFC3339DateFormat().format(date);
    }

    /**
     * Format the given parameter object into string.
     *
     * @param param
     *            Object
     * @return Object in string format
     */
    @Nonnull
    public static String parameterToString( @Nullable final Object param )
    {
        if( param == null ) {
            return "";
        } else if( param instanceof final Date date ) {
            return formatDate(date);
        } else if( param instanceof Collection ) {
            final StringBuilder b = new StringBuilder();
            for( final Object o : (Collection<?>) param ) {
                if( !b.isEmpty() ) {
                    b.append(',');
                }
                b.append(o);
            }
            return b.toString();
        } else {
            return String.valueOf(param);
        }
    }

    /**
     * Formats the specified query parameter to a list containing a single {@code Pair} object.
     * <p>
     * Note that {@code value} must not be a collection.
     *
     * @param name
     *            The name of the parameter.
     * @param value
     *            The value of the parameter.
     * @return A list containing a single {@code Pair} object.
     */
    @Nonnull
    public static List<Pair> parameterToPair( @Nullable final String name, @Nullable final Object value )
    {
        final List<Pair> params = new ArrayList<>();

        // preconditions
        if( name == null || name.isEmpty() || value == null || value instanceof Collection ) {
            return params;
        }

        params.add(new Pair(name, escapeString(parameterToString(value))));
        return params;
    }

    /**
     * Formats the specified collection query parameters to a list of {@code Pair} objects.
     * <p>
     * Note that the values of each of the returned Pair objects are percent-encoded.
     *
     * @param collectionFormat
     *            The collection format of the parameter.
     * @param name
     *            The name of the parameter.
     * @param value
     *            The value of the parameter.
     * @return A list of {@code Pair} objects.
     */
    @Nonnull
    public static List<Pair> parameterToPairs(
        @Nonnull final String collectionFormat,
        @Nullable final String name,
        @Nullable final Collection<?> value )
    {
        final List<Pair> params = new ArrayList<>();

        // preconditions
        if( name == null || name.isEmpty() || value == null || value.isEmpty() ) {
            return params;
        }

        // create the params based on the collection format
        if( "multi".equals(collectionFormat) ) {
            for( final Object item : value ) {
                params.add(new Pair(name, escapeString(parameterToString(item))));
            }
            return params;
        }

        // collectionFormat is assumed to be "csv" by default
        final String delimiter = switch( collectionFormat ) {
            case "ssv" -> escapeString(" ");
            case "tsv" -> escapeString("\t");
            case "pipes" -> escapeString("|");
            default -> ",";
            // escape all delimiters except commas, which are URI reserved characters
        };

        final StringBuilder sb = new StringBuilder();
        for( final Object item : value ) {
            sb.append(delimiter);
            sb.append(escapeString(parameterToString(item)));
        }

        params.add(new Pair(name, sb.substring(delimiter.length())));

        return params;
    }

    /**
     * Select the Accept header's value from the given accepts array: if JSON exists in the given array, use it;
     * otherwise use all of them (joining into a string)
     *
     * @param accepts
     *            The accepts array to select from
     * @return The Accept header to use. If the given array is empty, null will be returned (not to set the Accept
     *         header explicitly).
     */
    @Nullable
    public static String selectHeaderAccept( @Nonnull final String[] accepts )
    {
        if( accepts.length == 0 ) {
            return null;
        }
        for( final String accept : accepts ) {
            if( isJsonMime(accept) ) {
                return accept;
            }
        }
        return String.join(",", accepts);
    }

    /**
     * Select the Content-Type header's value from the given array: if JSON exists in the given array, use it; otherwise
     * use the first one of the array.
     *
     * @param contentTypes
     *            The Content-Type array to select from
     * @return The Content-Type header to use. If the given array is empty, or matches "any", JSON will be used.
     */
    @Nonnull
    public static String selectHeaderContentType( @Nonnull final String[] contentTypes )
    {
        if( contentTypes.length == 0 || contentTypes[0].equals("*/*") ) {
            return "application/json";
        }
        for( final String contentType : contentTypes ) {
            if( isJsonMime(contentType) ) {
                return contentType;
            }
        }
        return contentTypes[0];
    }

    /**
     * Escape the given string to be used as URL query value.
     *
     * @param str
     *            String
     * @return Escaped string
     */
    @Nonnull
    public static String escapeString( @Nonnull final String str )
    {
        return URLEncoder.encode(str, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
