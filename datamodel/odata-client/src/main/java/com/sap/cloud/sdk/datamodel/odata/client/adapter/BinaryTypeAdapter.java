/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.adapter;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBinary;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * GSON type adapter for parsing and serialization of {@code byte[]}.
 */
@Slf4j
public class BinaryTypeAdapter extends TypeAdapter<byte[]>
{
    /**
     * For internal use only by data model classes
     */
    @Override
    @Nullable
    public byte[] read( @Nonnull final JsonReader in )
        throws IOException
    {
        if( in.peek().equals(JsonToken.NULL) ) {
            in.nextNull();
            return null;
        }

        final String value = in.nextString();
        return Try
            .of(() -> ValueBinary.DECODE_FROM_STRING.apply(value))
            .onFailure(e -> log.warn("Failed to deserialize binary value.", e))
            .getOrNull();
    }

    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final byte[] entityValue )
        throws IOException
    {
        if( entityValue == null ) {
            out.nullValue();
        } else {
            final String value = ValueBinary.ENCODE_TO_STRING.apply(entityValue);
            out.value(value);
        }
    }

}
