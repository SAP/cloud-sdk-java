package com.sap.cloud.sdk.services.openapi.apache.apiclient;

import static com.sap.cloud.sdk.services.openapi.apache.apiclient.DefaultApiResponseHandler.isJsonMime;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import org.apache.hc.core5.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiResponseListener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

@RequiredArgsConstructor
class ApiClientInvokerDefault implements ApiClientInvoker
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
    @Nullable
    private final OpenApiResponseListener openApiResponseListener;

    // Methods that can have a request body
    private static final Set<Method> BODY_METHODS = Set.of(Method.POST, Method.PUT, Method.PATCH, Method.DELETE);

    @Nullable
    @Override
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
        if( body != null && !formParams.isEmpty() ) {
            throw new OpenApiRequestException("Cannot have body and form params");
        }

        final String url = buildUrl(path, queryParams, collectionQueryParams, urlQueryDeepObject);

        final ClassicRequestBuilder builder = ClassicRequestBuilder.create(method);
        builder.setUri(url);

        if( accept != null ) {
            builder.addHeader("Accept", accept);
        }

        for( final Map.Entry<String, String> keyValue : headerParams.entrySet() ) {
            builder.addHeader(keyValue.getKey(), keyValue.getValue());
        }

        final HttpClientContext context = HttpClientContext.create();

        final ContentType contentTypeObj = getContentType(contentType);
        if( body != null || !formParams.isEmpty() ) {
            if( isBodyAllowed(Method.valueOf(method)) ) {
                // Add entity if we have content and a valid method
                builder.setEntity(serialize(body, formParams, contentTypeObj));
            } else {
                throw new OpenApiRequestException("method " + method + " does not support a request body");
            }
        } else {
            // for empty body
            builder.setEntity(new StringEntity("", contentTypeObj));
        }

        try {
            final HttpClientResponseHandler<T> responseHandler =
                new DefaultApiResponseHandler<>(objectMapper, tempFolderPath, returnType, openApiResponseListener);
            return httpClient.execute(builder.build(), context, responseHandler);
        }
        catch( final IOException e ) {
            throw new OpenApiRequestException(e);
        }
    }

    /**
     * Parse content type object from header value
     */
    @Nonnull
    private ContentType getContentType( @Nonnull final String headerValue )
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
     *
     * @param obj
     *            Object
     * @param contentType
     *            Content type
     * @param formParams
     *            Form parameters
     * @return Object
     * @throws OpenApiRequestException
     *             API exception
     */
    @Nonnull
    private HttpEntity serialize(
        @Nullable final Object obj,
        @Nonnull final Map<String, Object> formParams,
        @Nonnull final ContentType contentType )
        throws OpenApiRequestException
    {
        final String mimeType = contentType.getMimeType();
        if( isJsonMime(mimeType) ) {
            try {
                return new StringEntity(
                    objectMapper.writeValueAsString(obj),
                    contentType.withCharset(StandardCharsets.UTF_8));
            }
            catch( final JsonProcessingException e ) {
                throw new OpenApiRequestException(e);
            }
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
    private String buildUrl(
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
