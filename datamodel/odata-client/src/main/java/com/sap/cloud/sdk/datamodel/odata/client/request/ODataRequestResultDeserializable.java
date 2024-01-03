/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.result.ResultElement;

/**
 * Generic type of an OData request result.
 */
public interface ODataRequestResultDeserializable
{
    /**
     * Converts ODataRequestResult into POJO.
     *
     * @param objectType
     *            type of POJO
     * @param <T>
     *            The generic type of POJO
     * @return T - POJO
     *
     * @throws ODataResponseException
     *             When the HTTP status indicates an erroneous response.
     * @throws ODataDeserializationException
     *             When deserialization process failed for the OData response object.
     */
    @Nonnull
    <T> T as( @Nonnull final Class<T> objectType );

    /**
     * Converts ODataRequestResult into list of POJOs.
     *
     * @param objectType
     *            type of POJO
     * @param <T>
     *            Generic type of the POJO
     * @return List<T> - list of POJOs
     *
     * @throws ODataResponseException
     *             When the HTTP status indicates an erroneous response.
     * @throws ODataDeserializationException
     *             When deserialization process failed for the OData response objects.
     */
    @Nonnull
    <T> List<T> asList( @Nonnull final Class<T> objectType );

    /**
     * Run a consumer for fluent API type ResultElement to iterate over the OData response with a continuous data
     * stream. The HttpEntity will be consumed.
     *
     * @param handler
     *            The consumer for generic ResultElement.
     *
     * @throws ODataResponseException
     *             When the HTTP status indicates an erroneous response.
     * @throws ODataDeserializationException
     *             When deserialization process failed for the OData response objects.
     */
    void streamElements( @Nonnull final Consumer<ResultElement> handler );

    /**
     * Get the count of elements in the result set.
     *
     * @return The number of elements.
     *
     * @throws ODataResponseException
     *             When the HTTP status indicates an erroneous response.
     */
    long getInlineCount();

    /**
     * Construct and get a key-value map from the OData response. The HttpEntity will be consumed.
     *
     * @return The key-value map.
     *
     * @throws ODataResponseException
     *             When the HTTP status indicates an erroneous response.
     * @throws ODataDeserializationException
     *             When deserialization process failed for the OData response object.
     */
    @Nonnull
    Map<String, Object> asMap();

    /**
     * Construct and get a list of key-value maps from the OData response. The HttpEntity will be consumed.
     *
     * @return The list of key-value maps.
     *
     * @throws ODataResponseException
     *             When the HTTP status indicates an erroneous response.
     * @throws ODataDeserializationException
     *             When deserialization process failed for the OData response objects.
     */
    @Nonnull
    List<Map<String, Object>> asListOfMaps();
}
