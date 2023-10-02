/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor( staticName = "of" )
class GsonODataConverterAdapter<JavaT> extends TypeAdapter<JavaT>
{
    private final AbstractTypeConverter<JavaT, String> converter;

    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final JavaT value )
        throws IOException
    {
        if( value == null ) {
            out.nullValue();
        } else {
            final ConvertedObject<String> jsonValue = converter.toDomain(value);
            if( jsonValue.isNotConvertible() ) {
                log.warn("Not serializable: {}", value);
            } else {
                out.value(jsonValue.get());
            }
        }
    }

    @Override
    @Nullable
    public JavaT read( @Nonnull final JsonReader in )
        throws IOException
    {
        if( in.peek() == JsonToken.NULL ) {
            in.nextNull();
            return null;
        }
        final String value = in.nextString();
        final ConvertedObject<JavaT> convertedObject = converter.fromDomain(value);
        if( convertedObject.isNotConvertible() ) {
            log.warn("Not deserializable: {}", value);
        }
        return convertedObject.orNull();
    }
}
