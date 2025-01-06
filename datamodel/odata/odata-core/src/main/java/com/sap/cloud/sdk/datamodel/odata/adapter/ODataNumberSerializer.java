/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.adapter;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * GSON serializer that transforms numbers to their JSON representation according to the OData V2 standard. Meant for
 * internal use only.
 */
public class ODataNumberSerializer implements JsonSerializer<Number>
{
    @Override
    @Nonnull
    public JsonElement serialize(
        @Nonnull final Number src,
        @Nonnull final Type typeOfSrc,
        @Nonnull final JsonSerializationContext context )
    {
        /*
        Short is used both for Edm.Byte and Edm.Int16.
        Edm.Byte should be a string but Edm.Int16 should be a number.
        But we can't differentiate between that here because we only know it's a Short.
        So we serialize to the plain number because this worked in the past.
         */
        if( typeOfSrc == Integer.class || typeOfSrc == Short.class ) {
            return new JsonPrimitive(src);
        } else if( typeOfSrc == BigDecimal.class ) {
            return new JsonPrimitive(((BigDecimal) src).toPlainString());
        } else {
            return new JsonPrimitive(src.toString());
        }
    }
}
