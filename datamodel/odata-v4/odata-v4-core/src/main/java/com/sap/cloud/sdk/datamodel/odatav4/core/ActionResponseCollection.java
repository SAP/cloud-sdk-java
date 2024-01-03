/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic OData service response wrapper for action requests.
 *
 * @param <ResultT>
 *            The generic result type.
 */
@EqualsAndHashCode
@ToString( doNotUseGetters = true )
@RequiredArgsConstructor( staticName = "of", access = AccessLevel.PUBLIC )
@Slf4j
public final class ActionResponseCollection<ResultT>
{
    // lazily evaluated response entity
    private final AtomicReference<Option<List<ResultT>>> responseResult = new AtomicReference<>();

    @Nonnull
    private final ODataRequestResultGeneric result;

    @Getter
    @Nonnull
    private final Class<ResultT> actionResultClass;

    /**
     * Get the optional result parsed by the HTTP content.
     *
     * @return The optional result entity.
     */
    @Nonnull
    public Option<List<ResultT>> getResponseResult()
    {
        Option<List<ResultT>> value = responseResult.get();
        if( value == null ) {
            synchronized( responseResult ) {
                value = responseResult.get();
                if( value == null ) {
                    value = parseEntityFromResponse();
                    responseResult.set(value);
                }
            }
        }
        return value;
    }

    /**
     * Get the response status code.
     *
     * @return The integer representation of the HTTP status code.
     */
    public int getResponseStatusCode()
    {
        return result.getHttpResponse().getStatusLine().getStatusCode();
    }

    /**
     * Get the response headers.
     *
     * @return The headers of the HTTP status code.
     */
    @Nonnull
    public Map<String, Iterable<String>> getResponseHeaders()
    {
        return result.getAllHeaderValues();
    }

    @Nonnull
    private Option<List<ResultT>> parseEntityFromResponse()
    {
        if( actionResultClass.equals(Void.class) ) {
            return Option.none();
        }
        @SuppressWarnings( "unchecked" )
        final Try<List<ResultT>> parsedEntity = Try.of(() -> result.asList(actionResultClass));
        return parsedEntity.onFailure(e -> log.debug("Failed to parse entity from HTTP response.", e)).toOption();
    }
}
