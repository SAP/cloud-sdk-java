package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * For internal use only by data model classes
 */
public class ODataBooleanAdapter extends TypeAdapter<Boolean>
{
    /**
     * For internal use only by data model classes
     */
    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final Boolean entityValue )
        throws IOException
    {
        out.value(entityValue);
    }

    /**
     * For internal use only by data model classes
     */
    @Override
    @Nonnull
    public Boolean read( @Nonnull final JsonReader in )
        throws IOException
    {
        if( in.peek() == JsonToken.BOOLEAN ) {
            return in.nextBoolean();
        } else {
            in.skipValue();
            return Boolean.FALSE;
        }
    }
}
