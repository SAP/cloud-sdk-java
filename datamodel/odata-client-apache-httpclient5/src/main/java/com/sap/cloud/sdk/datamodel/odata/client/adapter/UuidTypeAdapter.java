package com.sap.cloud.sdk.datamodel.odata.client.adapter;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import io.vavr.control.Try;

/**
 * GSON type adapter for parsing and serialization of {@link UUID}.
 */
public class UuidTypeAdapter extends TypeAdapter<UUID>
{
    /**
     * For internal use only by data model classes
     */
    @Override
    @Nullable
    public UUID read( @Nonnull final JsonReader in )
        throws IOException
    {
        if( in.peek().equals(JsonToken.NULL) ) {
            in.nextNull();
            return null;
        }

        final String jsonDateValue = in.nextString();
        return Try.of(() -> UUID.fromString(jsonDateValue)).getOrNull();
    }

    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final UUID entityValue )
        throws IOException
    {
        if( entityValue == null ) {
            out.nullValue();
        } else {
            out.value(entityValue.toString());
        }
    }

}
