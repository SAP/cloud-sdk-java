/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Abstract base class to be used to easily parse fields which can be read as a {@link Calendar} object as a Gson
 * {@link TypeAdapter}.
 * <p>
 * This may be used to specify an adapter for the new Java date API (e.g. {@link java.time.LocalDateTime}) based on a
 * common conversion logic. This way the logic is split the following way:
 * <table>
 * <tr>
 * <td>General Conversion logic</td>
 * <td>AbstractTypeConverter subclasses (*CalendarConverter)</td>
 * </tr>
 * <tr>
 * <td>Gson Adapter</td>
 * <td>AbstractCalendarAdapter subclasses</td>
 * </tr>
 * <tr>
 * <td>Jackson (De)Serializer</td>
 * <td>AbstractJacksonCalendar(De)Serializer and subclasses</td>
 * </tr>
 * </table>
 *
 * @param <T>
 *            The type this adapter should parse.
 */
public abstract class AbstractCalendarAdapter<T> extends TypeAdapter<T>
{
    /**
     * Converts a string value read from a json property as an instance of the type to be created by this adapter.
     *
     * @param jsonString
     *            The string value of a json property.
     * @return A {@code ConvertedObject} instance containing the resulting object, or is empty if conversion was not
     *         possible.
     */
    @Nonnull
    protected abstract ConvertedObject<T> convertStringToType( @Nonnull final String jsonString );

    /**
     * Converts an instance of the type handled by this adapter into a string that can be written as a json value.
     *
     * @param entity
     *            The entity to convert into a String
     * @return A {@code ConvertedObject} instance containing the resulting string, or is empty if conversion was not
     *         possible.
     */
    @Nonnull
    protected abstract ConvertedObject<String> convertTypeToString( @Nullable final T entity );

    @Override
    public void write( @Nonnull final JsonWriter out, @Nullable final T value )
        throws IOException
    {
        final ConvertedObject<String> maybeString = convertTypeToString(value);
        if( maybeString.isConvertible() ) {
            out.value(maybeString.get());
        }
    }

    @Override
    @Nullable
    public T read( @Nonnull final JsonReader jsonReader )
        throws IOException
    {
        if( jsonReader.peek() != JsonToken.STRING ) {
            jsonReader.skipValue();
            return null;
        }

        final String jsonValue = jsonReader.nextString();

        return convertStringToType(jsonValue).orNull();
    }
}
