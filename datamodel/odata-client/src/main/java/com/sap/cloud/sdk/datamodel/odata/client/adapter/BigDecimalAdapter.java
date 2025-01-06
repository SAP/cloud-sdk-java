/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.adapter;

import java.io.IOException;
import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * GSON type adapter for parsing and serialization of {@link BigDecimal}.
 */
public class BigDecimalAdapter extends TypeAdapter<BigDecimal>
{
    /**
     * For internal use only by data model classes
     */
    @Override
    @Nullable
    public BigDecimal read( @Nonnull final JsonReader in )
        throws IOException
    {
        if( in.peek().equals(JsonToken.NULL) ) {
            in.nextNull();
            return null;
        }

        final String jsonStringValue = in.nextString();
        return new BigDecimal(jsonStringValue);
    }

    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final BigDecimal entityValue )
        throws IOException
    {
        if( entityValue == null ) {
            out.nullValue();
        } else {
            out.value(entityValue.toPlainString());
        }
    }

}
