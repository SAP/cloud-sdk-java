/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;

import io.vavr.CheckedFunction1;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to leverage reading from an HTTP response.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
final class HttpEntityReader
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Nullable
    private final HttpEntity entity;

    @Nonnull
    private final BiFunction<String, Throwable, ? extends RuntimeException> errorHandler;

    @Nonnull
    private final ODataProtocol protocol;

    @Nullable
    private <T> T read( @Nonnull final CheckedFunction1<InputStream, T> streamConsumer )
    {
        if( entity == null ) {
            final String msg = protocol + " response does not contain an HTTP entity.";
            log.warn(msg);
            throw errorHandler.apply(msg, null);
        }

        try( InputStream content = entity.getContent() ) {
            return streamConsumer.apply(content);
        }
        catch( final IOException | JsonIOException e ) {
            final String msg = protocol + " response stream cannot be read for HTTP entity: ";
            log.debug(msg + entity, e);
            throw errorHandler.apply(msg + entity.getClass().getName(), e);
        }
        catch( final UnsupportedOperationException e ) {
            final String msg = protocol + " response entity content cannot be represented as stream object.";
            log.debug(msg, e);
            throw errorHandler.apply(msg, e);
        }
        // CHECKSTYLE:OFF
        catch( final Throwable e ) {
            final String msg = "A problem occurred while streaming the " + protocol + " response.";
            log.debug(msg, e);
            throw errorHandler.apply(msg, e);
        }
        // CHECKSTYLE:ON
    }

    /**
     * Protocol dependent method to consume the InputStream from the HTTP response entity. This method parses the whole
     * JSON tree eagerly.
     *
     * @param result
     *            The result object to read from.
     * @param elementConsumer
     *            Function to turn a GSON element to a generic result.
     * @param <T>
     *            The generic return type.
     * @return The response.
     * @throws ODataDeserializationException
     *             When streamed deserialization process failed.
     */
    static <T> T read(
        @Nonnull final ODataRequestResult result,
        @Nonnull final CheckedFunction1<JsonElement, T> elementConsumer )
    {
        return stream(result, reader -> {
            final JsonElement rootElement = JsonParser.parseReader(reader);
            return elementConsumer.apply(rootElement);
        });
    }

    /**
     * Protocol dependent method to consume the InputStream from the HTTP response entity. This method allows for lazy
     * consuming of the JSON tree.
     *
     * @param result
     *            The result object to read from.
     * @param readerConsumer
     *            The consumer of the JsonReader.
     * @param <T>
     *            The generic return type.
     * @return The response.
     * @throws ODataDeserializationException
     *             When streamed deserialization process failed.
     */
    static <T> T stream(
        @Nonnull final ODataRequestResult result,
        @Nonnull final CheckedFunction1<JsonReader, T> readerConsumer )
    {
        final HttpResponse httpResponse = result.getHttpResponse();
        final ODataRequestGeneric request = result.getODataRequest();
        final BiFunction<String, Throwable, RuntimeException> errorHandler =
            ( msg, e ) -> new ODataDeserializationException(request, httpResponse, msg, e);

        return new HttpEntityReader(httpResponse.getEntity(), errorHandler, request.getProtocol()).read(inputStream -> {
            final JsonReader reader = new JsonReader(new InputStreamReader(inputStream, DEFAULT_CHARSET));
            return readerConsumer.apply(reader);
        });
    }
}
