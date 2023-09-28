package com.sap.cloud.sdk.datamodel.odata.client.adapter;

import java.io.IOException;
import java.time.OffsetDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import io.vavr.control.Try;

/**
 * GSON type adapter for parsing and serialization of {@link OffsetDateTime}.
 */
public class OffsetDateTimeTypeAdapter extends TypeAdapter<OffsetDateTime>
{
    /**
     * For internal use only by data model classes
     */
    @Override
    @Nullable
    public OffsetDateTime read( @Nonnull final JsonReader in )
        throws IOException
    {
        if( in.peek().equals(JsonToken.NULL) ) {
            in.nextNull();
            return null;
        }

        final String jsonDateValue = in.nextString();
        return Try.of(() -> OffsetDateTime.parse(jsonDateValue)).getOrNull();
    }

    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final OffsetDateTime entityValue )
        throws IOException
    {
        if( entityValue == null ) {
            out.nullValue();
        } else {
            out.value(entityValue.toString());
        }
    }

}
