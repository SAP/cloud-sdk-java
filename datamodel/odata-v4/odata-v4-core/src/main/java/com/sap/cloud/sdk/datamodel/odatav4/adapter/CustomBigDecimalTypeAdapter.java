/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.io.IOException;
import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Custom type adapter for BigDecimal that doesn't serialise BigDecimal into it's scientific notation. For e.g. if the
 * value of the attribute is 0.000000002, using this serializer ensures that it doesn't get converted to 2E-9.
 */
class CustomBigDecimalTypeAdapter extends TypeAdapter<BigDecimal>
{
    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final BigDecimal value )
        throws IOException
    {
        if( value == null ) {
            out.nullValue();
        } else {
            final Number number = new LazilyParsedNumber(value.toPlainString());
            out.value(number);
        }
    }

    @Override
    @Nullable
    public BigDecimal read( @Nonnull final JsonReader in )
        throws IOException
    {
        if( in.peek() == JsonToken.NULL ) {
            in.nextNull();
            return null;
        }
        try {
            return new BigDecimal(in.nextString());
        }
        catch( final NumberFormatException e ) {
            throw new JsonSyntaxException("The string could not be parsed as decimal.", e);
        }
    }
}
