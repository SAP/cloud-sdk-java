/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;

/**
 * The VdmEnum type adapter for serializing and deserializing JSON payload.
 *
 * @param <T>
 *            The generic type for which the adapter is applied.
 */
class GsonVdmEnumAdapter<T extends VdmEnum> extends TypeAdapter<T>
{
    private final ImmutableMap<String, VdmEnum> nameEnumLookupMap;

    GsonVdmEnumAdapter( @Nonnull final Class<? extends VdmEnum> enumType )
    {
        final VdmEnum[] constants = enumType.getEnumConstants();
        if( constants == null ) {
            throw new IllegalArgumentException("Enum type " + enumType.getSimpleName() + " has no enum constants.");
        }
        this.nameEnumLookupMap = Maps.uniqueIndex(Arrays.asList(constants), VdmEnum::getName);
    }

    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final T value )
        throws IOException
    {
        if( value == null ) {
            out.nullValue();
        } else {
            final String name = value.getName();
            out.value(name);
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Nullable
    public T read( @Nonnull final JsonReader in )
        throws IOException
    {
        if( in.peek() == JsonToken.NULL ) {
            in.nextNull();
            return null;
        }
        final String name = in.nextString();
        final VdmEnum value = nameEnumLookupMap.getOrDefault(name, null);
        return (T) value;
    }
}
