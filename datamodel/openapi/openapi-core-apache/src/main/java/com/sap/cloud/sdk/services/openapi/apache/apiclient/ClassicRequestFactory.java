/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.apache.apiclient;

import static com.sap.cloud.sdk.services.openapi.apache.apiclient.DefaultApiResponseHandler.isJsonMime;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_ENCODING;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiRequestException;

import lombok.experimental.UtilityClass;

/**
 * Factory for building Apache HttpClient 5 classic requests from OpenAPI parameters.
 */
@UtilityClass
class ClassicRequestFactory
{
    private static final Set<Method> BODY_METHODS = Set.of(Method.POST, Method.PUT, Method.PATCH, Method.DELETE);

    /**
     * Builds a ClassicRequestBuilder with the specified parameters, including URL construction, content type parsing,
     * entity creation, and header population.
     *
     * @param basePath
     *            The base path for the URL
     * @param path
     *            The sub path for the URL
     * @param method
     *            The HTTP method (e.g., GET, POST, PUT, DELETE)
     * @param queryParams
     *            The query parameters
     * @param collectionQueryParams
     *            The collection query parameters
     * @param urlQueryDeepObject
     *            URL query string of the deep object parameters
     * @param body
     *            The request body object
     * @param headerParams
     *            Map of header names to values
     * @param formParams
     *            The form parameters
     * @param accept
     *            The Accept header value, or null if not specified
     * @param contentType
     *            The content type header value
     * @param objectMapper
     *            The ObjectMapper for JSON serialization
     * @return A configured ClassicRequestBuilder ready to be built
     */
    @Nonnull
    static ClassicRequestBuilder buildClassicRequest(
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
        @Nonnull final String contentType,
        @Nonnull final ObjectMapper objectMapper )
    {
        if( body != null && !formParams.isEmpty() ) {
            throw new OpenApiRequestException("Cannot have body and form params");
        }

        final String url = buildUrl(basePath, path, queryParams, collectionQueryParams, urlQueryDeepObject);
        final ContentType contentTypeObj = getContentType(contentType);

        @SuppressWarnings( "PMD.CloseResource" ) // constructed entity is not a closeable resource
        final HttpEntity entity = createEntity(method, body, formParams, contentTypeObj, headerParams, objectMapper);

        final ClassicRequestBuilder builder = ClassicRequestBuilder.create(method);
        builder.setUri(url);

        if( accept != null ) {
            builder.addHeader("Accept", accept);
        }

        for( final Map.Entry<String, String> keyValue : headerParams.entrySet() ) {
            builder.addHeader(keyValue.getKey(), keyValue.getValue());
        }

        builder.setEntity(entity);

        return builder;
    }

    /**
     * Creates the HTTP entity for the request based on the method, body, and form parameters.
     */
    @Nonnull
    private static HttpEntity createEntity(
        @Nonnull final String method,
        @Nullable final Object body,
        @Nonnull final Map<String, Object> formParams,
        @Nonnull final ContentType contentType,
        @Nonnull final Map<String, String> headerParams,
        @Nonnull final ObjectMapper objectMapper )
        throws OpenApiRequestException
    {
        if( body != null || !formParams.isEmpty() ) {
            if( isBodyAllowed(Method.valueOf(method)) ) {
                return serialize(body, formParams, contentType, headerParams, objectMapper);
            } else {
                throw new OpenApiRequestException("method " + method + " does not support a request body");
            }
        }
        // for empty body
        return new StringEntity("", contentType);
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
        catch( final UnsupportedCharsetException e ) {
            throw new OpenApiRequestException("Could not parse content type " + headerValue, e);
        }
    }

    /**
     * Serialize the given Java object into string according the given Content-Type (only JSON is supported for now).
     */
    @Nonnull
    private static HttpEntity serialize(
        @Nullable final Object obj,
        @Nonnull final Map<String, Object> formParams,
        @Nonnull final ContentType contentType,
        @Nonnull final Map<String, String> headerParams,
        @Nonnull final ObjectMapper objectMapper )
        throws OpenApiRequestException
    {
        final String mimeType = contentType.getMimeType();
        if( isJsonMime(mimeType) ) {
            return serializeJson(obj, contentType, headerParams, objectMapper);
        } else if( mimeType.equals(ContentType.MULTIPART_FORM_DATA.getMimeType()) ) {
            final MultipartEntityBuilder multiPartBuilder = MultipartEntityBuilder.create();
            for( final Map.Entry<String, Object> paramEntry : formParams.entrySet() ) {
                final Object value = paramEntry.getValue();
                if( value instanceof final File file ) {
                    multiPartBuilder.addBinaryBody(paramEntry.getKey(), file);
                } else if( value instanceof final byte[] byteArray ) {
                    multiPartBuilder.addBinaryBody(paramEntry.getKey(), byteArray);
                } else {
                    final Charset charset = contentType.getCharset();
                    if( charset != null ) {
                        final ContentType customContentType =
                            ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset);
                        multiPartBuilder
                            .addTextBody(
                                paramEntry.getKey(),
                                ApiClient.parameterToString(paramEntry.getValue()),
                                customContentType);
                    } else {
                        multiPartBuilder
                            .addTextBody(paramEntry.getKey(), ApiClient.parameterToString(paramEntry.getValue()));
                    }
                }
            }
            return multiPartBuilder.build();
        } else if( mimeType.equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType()) ) {
            final List<NameValuePair> formValues = new ArrayList<>();
            for( final Map.Entry<String, Object> paramEntry : formParams.entrySet() ) {
                formValues
                    .add(
                        new BasicNameValuePair(
                            paramEntry.getKey(),
                            ApiClient.parameterToString(paramEntry.getValue())));
            }
            return new UrlEncodedFormEntity(formValues, contentType.getCharset());
        } else {
            // Handle files with unknown content type
            if( obj instanceof final File file ) {
                return new FileEntity(file, contentType);
            } else if( obj instanceof final byte[] byteArray ) {
                return new ByteArrayEntity(byteArray, contentType);
            }
            throw new OpenApiRequestException("Serialization for content type '" + contentType + "' not supported");
        }
    }

    /**
     * Serialize JSON body, with optional GZIP compression if Content-Encoding header is set to "gzip".
     */
    @Nonnull
    private static HttpEntity serializeJson(
        @Nullable final Object body,
        @Nonnull final ContentType contentType,
        @Nonnull final Map<String, String> headerParams,
        @Nonnull final ObjectMapper objectMapper )
        throws OpenApiRequestException
    {
        if( "gzip".equalsIgnoreCase(headerParams.get(CONTENT_ENCODING))
            || "gzip".equalsIgnoreCase(headerParams.get(CONTENT_ENCODING.toLowerCase())) ) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try( GZIPOutputStream gzip = new GZIPOutputStream(outputStream) ) {
                gzip.write(objectMapper.writeValueAsBytes(body));
            }
            catch( final IOException e ) {
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
        catch( final JsonProcessingException e ) {
            throw new OpenApiRequestException(e);
        }
    }

    /**
     * Build full URL by concatenating base URL, the given sub path and query parameters.
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
                final String value = ApiClient.parameterToString(param.getValue());
                // query parameter value already escaped as part of parameterToPair
                url.append(ApiClient.escapeString(param.getName())).append("=").append(value);
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
                final String value = ApiClient.parameterToString(param.getValue());
                // collection query parameter value already escaped as part of parameterToPairs
                url.append(ApiClient.escapeString(param.getName())).append("=").append(value);
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
}
