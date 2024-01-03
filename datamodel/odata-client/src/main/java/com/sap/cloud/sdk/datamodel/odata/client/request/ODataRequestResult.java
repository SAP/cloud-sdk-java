/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

/**
 * Generic type of an OData request result.
 */
public interface ODataRequestResult
{
    /**
     * Get the original {@link ODataRequestExecutable} instance that was used for running the OData request.
     *
     * @return The original {@link ODataRequestExecutable} instance.
     */
    @Nonnull
    ODataRequestGeneric getODataRequest();

    /**
     * Get the original OData {@link HttpResponse} instance, which holds the HttpEntity and header information.
     *
     * @return The HttpResponse.
     */
    @Nonnull
    HttpResponse getHttpResponse();

    /**
     * Get the iterable list of HTTP response header names.
     *
     * @return An iterable set of header names.
     */
    @Nonnull
    default Iterable<String> getHeaderNames()
    {
        return getAllHeaderValues().keySet();
    }

    /**
     * Get the iterable HTTP header values for a specific header name. The lookup happens case-insensitively.
     *
     * @param headerName
     *            The header name to look for.
     * @return An iterable set of header values.
     */
    @Nonnull
    default Iterable<String> getHeaderValues( @Nonnull final String headerName )
    {
        return getAllHeaderValues().getOrDefault(headerName, Collections.emptyList());
    }

    /**
     * Get all HTTP header values, grouped by the name (<b>case insensitive</b>) of the HTTP header.
     *
     * @return A <b>case insensitive</b> map of HTTP header names, where each entry is an iterable set of values for the
     *         specific header name.
     */
    @Nonnull
    default Map<String, Iterable<String>> getAllHeaderValues()
    {
        final Header[] allHeaders = getHttpResponse().getAllHeaders();
        final Map<String, Collection<String>> result = new TreeMap<>(String::compareToIgnoreCase);

        for( final Header header : allHeaders ) {
            final String headerName = header.getName();
            final String headerValue = header.getValue();

            if( headerValue == null ) {
                continue;
            }

            result.computeIfAbsent(headerName, key -> new ArrayList<>()).add(headerValue);
        }
        return Collections.unmodifiableMap(result);
    }
}
