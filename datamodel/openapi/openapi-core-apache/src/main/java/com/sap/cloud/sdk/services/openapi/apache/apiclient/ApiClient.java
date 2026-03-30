/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.apache.apiclient;

import static com.sap.cloud.sdk.services.openapi.apache.apiclient.DefaultApiResponseHandler.isJsonMime;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_ENCODING;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
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
import org.apache.hc.core5.http.message.BasicNameValuePair;

/**
 * API client for executing HTTP requests using Apache HttpClient 5.
 */
@AllArgsConstructor( access = PRIVATE )
@EqualsAndHashCode
@ToString
@Slf4j
public class ApiClient
{
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

    // Methods that can have a request body
    private static final Set<Method> BODY_METHODS = Set.of(Method.POST, Method.PUT, Method.PATCH, Method.DELETE);

    // At runtime "localhost" will be replaced as basepath by the Destination's URI.
    private static final String DEFAULT_BASE_PATH = "http://localhost";

    @With( onMethod_ = @Beta )
    @Nonnull
    private final UnaryOperator<ClassicRequestBuilder> requestCustomizer;

    private static final OpenApiResponseListener EMPTY_RESPONSE_LISTENER = r -> {
    };
    private static final UnaryOperator<ClassicRequestBuilder> EMPTY_REQUEST_CUSTOMIZER = r -> r;

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
        if( !httpClient.getClass().getName().startsWith("com.sap.cloud.sdk.cloudplatform.connectivity") ) {
            val msg = "Creating ApiClient from HttpClient of type {}. The default base-path is \"{}\".";
            log.debug(msg, httpClient.getClass().getName(), DEFAULT_BASE_PATH);
        }
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
    public static
        ApiClient
        fromHttpClient( @Nonnull final CloseableHttpClient httpClient, @Nonnull final String basePath )
    {
        return new ApiClient(
            httpClient,
            basePath,
            createDefaultObjectMapper(),
            null,
            EMPTY_RESPONSE_LISTENER,
            EMPTY_REQUEST_CUSTOMIZER);
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
        final HttpClient httpClient = ApacheHttpClient5Accessor.getHttpClient(destination);
        return fromHttpClient((CloseableHttpClient) httpClient, destination.asHttp().getUri().toString());
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

    /**
     * Parse content type object from header value
     */
    @Nonnull
    private static ContentType getContentType( @Nonnull final String headerValue )
        throws OpenApiRequestException
    {
        try {
            return ContentType.parse(headerValue);
        }
        catch( UnsupportedCharsetException e ) {
            throw new OpenApiRequestException("Could not parse content type " + headerValue, e);
        }
    }

    /**
     * Build full URL by concatenating base URL, the given sub path and query parameters.
     *
     * @param path
     *            The sub path
     * @param queryParams
     *            The query parameters
     * @param collectionQueryParams
     *            The collection query parameters
     * @param urlQueryDeepObject
     *            URL query string of the deep object parameters
     * @return The full URL
     */
    @Nonnull
    private static String buildUrl(
        @Nonnull final String basePath,
        @Nonnull final String path,
        @Nullable final List<Pair> queryParams,
        @Nullable final List<Pair> collectionQueryParams,
        @Nullable final String urlQueryDeepObject )
    {
        final StringBuilder url = new StringBuilder();
        if( basePath.endsWith("/") && path.startsWith("/") ) {
            url.append(basePath, 0, basePath.length() - 1);
        } else {
            url.append(basePath);
        }
        url.append(path);

        if( queryParams != null && !queryParams.isEmpty() ) {
            // support (constant) query string in `path`, e.g. "/posts?draft=1"
            String prefix = path.contains("?") ? "&" : "?";
            for( final Pair param : queryParams ) {
                if( prefix != null ) {
                    url.append(prefix);
                    prefix = null;
                } else {
                    url.append("&");
                }
                final String value = parameterToString(param.getValue());
                // query parameter value already escaped as part of parameterToPair
                url.append(escapeString(param.getName())).append("=").append(value);
            }
        }

        if( collectionQueryParams != null && !collectionQueryParams.isEmpty() ) {
            String prefix = url.toString().contains("?") ? "&" : "?";
            for( final Pair param : collectionQueryParams ) {
                if( prefix != null ) {
                    url.append(prefix);
                    prefix = null;
                } else {
                    url.append("&");
                }
                final String value = parameterToString(param.getValue());
                // collection query parameter value already escaped as part of parameterToPairs
                url.append(escapeString(param.getName())).append("=").append(value);
            }
        }

        if( urlQueryDeepObject != null && !urlQueryDeepObject.isEmpty() ) {
            url.append(url.toString().contains("?") ? "&" : "?");
            url.append(urlQueryDeepObject);
        }

        return url.toString();
    }

    private static boolean isBodyAllowed( @Nonnull final Method method )
    {
        return BODY_METHODS.contains(method);
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
            buildClassicRequest(
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
                contentType);

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
    private ClassicRequestBuilder buildClassicRequest(
        @Nonnull final String basePath,
        @Nonnull final String path,
        @Nonnull final String method,
        @Nullable final List<Pair> queryParams,
        @Nullable final List<Pair> collectionQueryParams,
        @Nullable final String urlQueryDeepObject,
        @Nullable final Object body,
        @Nonnull final Map<String, String> headerParams,
        @Nonnull final Map<String, Object> formParams,
        @Nullable final String accept,
        @Nonnull final String contentType )
    {
        if( body != null && !formParams.isEmpty() ) {
            throw new OpenApiRequestException("Cannot have body and form params");
        }

        final String url = buildUrl(basePath, path, queryParams, collectionQueryParams, urlQueryDeepObject);

        final ClassicRequestBuilder builder = ClassicRequestBuilder.create(method);
        builder.setUri(url);

        if( accept != null ) {
            builder.addHeader("Accept", accept);
        }
        for( final Entry<String, String> keyValue : headerParams.entrySet() ) {
            builder.addHeader(keyValue.getKey(), keyValue.getValue());
        }
        final ContentType contentTypeObj = getContentType(contentType);
        if( body != null || !formParams.isEmpty() ) {
            if( isBodyAllowed(Method.valueOf(method)) ) {
                // Add entity if we have content and a valid method
                builder.setEntity(serialize(body, formParams, contentTypeObj, headerParams));
            } else {
                throw new OpenApiRequestException("method " + method + " does not support a request body");
            }
        } else {
            // for empty body
            builder.setEntity(new StringEntity("", contentTypeObj));
        }
        return builder;
    }

    @Nonnull
    private HttpEntity serialize(
        @Nullable final Object body,
        @Nonnull final Map<String, Object> formParams,
        @Nonnull final ContentType contentType,
        @Nonnull final Map<String, String> headerParams )
        throws OpenApiRequestException
    {
        final String mimeType = contentType.getMimeType();
        if( isJsonMime(mimeType) ) {
            return serializeJson(body, contentType, headerParams);
        } else if( mimeType.equals(ContentType.MULTIPART_FORM_DATA.getMimeType()) ) {
            return serializeMultipart(formParams, contentType);
        } else if( mimeType.equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType()) ) {
            return serializeFormUrlEncoded(formParams, contentType);
        } else if( body instanceof File file ) {
            return new FileEntity(file, contentType);
        } else if( body instanceof byte[] byteArray ) {
            return new ByteArrayEntity(byteArray, contentType);
        }
        throw new OpenApiRequestException("Serialization for content type '" + contentType + "' not supported");
    }

    @Nonnull
    private HttpEntity serializeJson(
        @Nullable final Object body,
        @Nonnull final ContentType contentType,
        @Nonnull final Map<String, String> headerParams )
        throws OpenApiRequestException
    {
        if( "gzip".equalsIgnoreCase(headerParams.get(CONTENT_ENCODING))
            || "gzip".equalsIgnoreCase(headerParams.get(CONTENT_ENCODING.toLowerCase())) ) {
            val outputStream = new ByteArrayOutputStream();
            try( val gzip = new GZIPOutputStream(outputStream) ) {
                gzip.write(objectMapper.writeValueAsBytes(body));
            }
            catch( IOException e ) {
                throw new OpenApiRequestException("Failed to GZIP compress request body", e);
            }
            return new ByteArrayEntity(
                outputStream.toByteArray(),
                contentType.withCharset(StandardCharsets.UTF_8),
                "gzip");
        }
        try {
            return new StringEntity(
                objectMapper.writeValueAsString(body),
                contentType.withCharset(StandardCharsets.UTF_8));
        }
        catch( JsonProcessingException e ) {
            throw new OpenApiRequestException(e);
        }
    }

    @Nonnull
    private
        HttpEntity
        serializeMultipart( @Nonnull final Map<String, Object> formParams, @Nonnull final ContentType contentType )
    {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for( final Entry<String, Object> entry : formParams.entrySet() ) {
            final Object value = entry.getValue();
            if( value instanceof File file ) {
                builder.addBinaryBody(entry.getKey(), file);
            } else if( value instanceof byte[] byteArray ) {
                builder.addBinaryBody(entry.getKey(), byteArray);
            } else {
                addMultipartTextField(builder, entry, contentType);
            }
        }
        return builder.build();
    }

    private void addMultipartTextField(
        @Nonnull final MultipartEntityBuilder builder,
        @Nonnull final Entry<String, Object> entry,
        @Nonnull final ContentType contentType )
    {
        final Charset charset = contentType.getCharset();
        if( charset != null ) {
            final ContentType textContentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset);
            builder.addTextBody(entry.getKey(), parameterToString(entry.getValue()), textContentType);
        } else {
            builder.addTextBody(entry.getKey(), parameterToString(entry.getValue()));
        }
    }

    @Nonnull
    private
        HttpEntity
        serializeFormUrlEncoded( @Nonnull final Map<String, Object> formParams, @Nonnull final ContentType contentType )
    {
        final List<NameValuePair> formValues = new ArrayList<>();
        for( final Entry<String, Object> entry : formParams.entrySet() ) {
            formValues.add(new BasicNameValuePair(entry.getKey(), parameterToString(entry.getValue())));
        }
        return new UrlEncodedFormEntity(formValues, contentType.getCharset());
    }
}
