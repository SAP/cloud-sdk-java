/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

/**
 * Representation of a generic OData request builder as a fluent interface.
 *
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
public interface RequestBuilder<ResultT> extends RequestBuilderExecutable<ResultT>
{
    /**
     * Assemble a generic, untyped request object that represents the request build up via this builder.
     *
     * @return A request object extending {@link ODataRequestGeneric}.
     */
    @Nonnull
    ODataRequestGeneric toRequest();

    /**
     * Gives the option to specify custom HTTP headers. Multiple headers with the same key can be specified. The
     * returned object allows to specify the requests the headers should be used in.
     *
     * @param key
     *            Name of the (first) desired HTTP header parameter.
     * @param value
     *            Value of the (first) desired HTTP header parameter.
     *
     * @return A request builder to specify further headers and their intended usage.
     */
    @Nonnull
    RequestBuilder<ResultT> withHeader( @Nonnull final String key, @Nullable final String value );

    /**
     * Gives the option to specify a map of custom HTTP headers. The returned object allows to specify the requests the
     * headers should be used in.
     *
     * @param map
     *            A map of HTTP header key/value pairs.
     * @return A request builder to specify further headers and their intended usage.
     */
    @Nonnull
    default RequestBuilder<ResultT> withHeaders( @Nonnull final Map<String, String> map )
    {
        map.forEach(this::withHeader);
        return this;
    }
}
