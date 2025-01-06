/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * For internal use only by data model classes
 */
@Slf4j
public class ODataBinaryAdapter extends TypeAdapter<byte[]>
{
    @Override
    public void write( @Nonnull final JsonWriter jsonWriter, @Nullable final byte[] bytes )
        throws IOException
    {
        if( bytes == null ) {
            jsonWriter.nullValue();
            return;
        }
        final String result = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
        jsonWriter.value(result);
    }

    @Override
    @Nullable
    public byte[] read( @Nonnull final JsonReader jsonReader )
        throws IOException
    {
        if( jsonReader.peek() != JsonToken.STRING ) {
            jsonReader.skipValue();
            return null;
        }

        final String jsonValue = jsonReader.nextString();
        try {
            return Base64.getDecoder().decode(jsonValue.getBytes(StandardCharsets.UTF_8));
        }
        catch( final IllegalArgumentException e ) {
            log.debug("Cannot decode String as byte array: " + e.getMessage());
            return null;
        }
    }
}
