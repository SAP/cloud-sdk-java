/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Abstract base class to be used to easily write fields which can be read as a {@link Calendar} object as a Jackson
 * {@link StdSerializer}.
 * <p>
 * This may be used to specify a deserializer for the new Java date API (e.g. {@link java.time.LocalDateTime}) based on
 * a common conversion logic. This way the logic is split the following way:
 * <p>
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
 * </p>
 *
 * @param <T>
 *            The type this serializer should read.
 */
public abstract class AbstractJacksonCalendarSerializer<T> extends StdSerializer<T>
{
    private static final long serialVersionUID = -7620182674695291969L;

    /**
     * Constructor needed by the super class.
     *
     * @param t
     *            The class to be written by this serializer.
     */
    protected AbstractJacksonCalendarSerializer( final Class<T> t )
    {
        super(t);
    }

    /**
     * Getter for an instance of the common conversion logic from and to {@code Calendar}.
     *
     * @return The conversion logic for this serializer.
     */
    @Nonnull
    protected abstract AbstractTypeConverter<T, Calendar> getConverterInstance();

    /**
     * Getter for the conversion of a Calendar object into a String. The selection of implementation depends on what
     * kind of date object the string value should represent.
     *
     * @return {@code AbstractTypeConverter} instance that converts a Calendar into a String.
     */
    @Nonnull
    protected abstract AbstractTypeConverter<String, Calendar> getStringCalendarConverterInstance();

    @Override
    public void serialize(
        @Nullable final T value,
        @Nonnull final JsonGenerator jsonGenerator,
        @Nonnull final SerializerProvider serializerProvider )
        throws IOException
    {
        final Calendar cal = getConverterInstance().toDomain(value).orNull();
        final ConvertedObject<String> maybeJson = getStringCalendarConverterInstance().fromDomain(cal);

        if( maybeJson.isConvertible() ) {
            new StringSerializer().serialize(maybeJson.get(), jsonGenerator, serializerProvider);
        }
    }
}
