package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import lombok.RequiredArgsConstructor;

/**
 * For internal use only by data model classes.
 */
@RequiredArgsConstructor
public class GsonCustomFieldAdapter extends TypeAdapter<Object>
{
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>()
    {
    }.getType();

    @Nonnull
    private final Gson gson;

    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final Object value )
    {
        // TODO missing implementation for array.
        if( value instanceof String ) {
            gson.toJson(new JsonPrimitive((String) value), out);
        }
        if( value instanceof Number ) {
            gson.toJson(new JsonPrimitive((Number) value), out);
        }
        if( value instanceof Boolean ) {
            gson.toJson(new JsonPrimitive((Boolean) value), out);
        }
        gson.toJson(value, MAP_TYPE, out);
    }

    @Override
    @Nullable
    public Object read( @Nonnull final JsonReader in )
        throws IOException
    {
        // TODO missing implementation for array.
        if( JsonToken.STRING == in.peek() ) {
            return in.nextString();
        }
        if( JsonToken.NUMBER == in.peek() ) {
            return in.nextDouble();
        }
        if( JsonToken.BOOLEAN == in.peek() ) {
            return in.nextBoolean();
        }
        return gson.fromJson(in, MAP_TYPE);
    }
}
