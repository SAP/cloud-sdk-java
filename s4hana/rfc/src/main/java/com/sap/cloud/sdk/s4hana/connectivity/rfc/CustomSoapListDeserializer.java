/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class CustomSoapListDeserializer implements JsonDeserializer<List<?>>
{
    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    @Nullable
    public List<?> deserialize(
        @Nonnull final JsonElement jsonList,
        @Nonnull final Type typeOfList,
        @Nonnull final JsonDeserializationContext context )
        throws JsonParseException
    {
        // null value
        if( jsonList.isJsonNull() ) {
            return null;
        }

        // if empty list coming through XML representation, e.g. <MESSAGES />
        if( jsonList.isJsonPrimitive()
            && jsonList.getAsJsonPrimitive().isString()
            && jsonList.getAsString().isEmpty() ) {
            return Collections.emptyList();
        }

        // the happy path
        if( jsonList.isJsonArray() && typeOfList instanceof ParameterizedType ) {
            final Type listElementType = ((ParameterizedType) typeOfList).getActualTypeArguments()[0];
            return Streams
                .stream(jsonList.getAsJsonArray())
                .map(element -> context.deserialize(element, listElementType))
                .collect(Collectors.toList());
        }
        // the not so happy path
        log.error("Failed to deserialize {} to {}.", jsonList, typeOfList);
        throw new JsonParseException("List of type " + typeOfList + " could not be deserialized.");
    }
}
