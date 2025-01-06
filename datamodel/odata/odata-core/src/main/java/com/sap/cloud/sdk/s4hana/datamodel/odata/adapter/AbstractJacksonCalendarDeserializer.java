/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Abstract base class to be used to easily read fields which can be read as a {@link Calendar} object as a Jackson
 * {@link StdDeserializer}.
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
 *            The type this deserializer should read.
 */
public abstract class AbstractJacksonCalendarDeserializer<T> extends StdDeserializer<T>
{
    private static final long serialVersionUID = -3931503330406820250L;

    /**
     * Constructor needed by the super class.
     *
     * @param vc
     *            The class to be read by this deserializer.
     */
    protected AbstractJacksonCalendarDeserializer( final Class<?> vc )
    {
        super(vc);
    }

    /**
     * Getter for an instance of the common conversion logic from and to {@code Calendar}.
     *
     * @return The conversion logic for this deserializer.
     */
    @Nonnull
    protected abstract AbstractTypeConverter<T, Calendar> getCalendarConverterInstance();

    /**
     * Getter for the conversion of a String into a Calendar object. The selection of implementation depends on what
     * kind of date object the string value should represent.
     *
     * @return {@code AbstractTypeConverter} instance that converts a String into a Calendar.
     */
    @Nonnull
    protected abstract AbstractTypeConverter<String, Calendar> getStringCalendarConverterInstance();

    @Override
    @Nullable
    public T deserialize(
        @Nonnull final JsonParser jsonParser,
        @Nonnull final DeserializationContext deserializationContext )
        throws IOException
    {
        final ConvertedObject<Calendar> cal = getStringCalendarConverterInstance().toDomain(jsonParser.getText());

        try {
            return getCalendarConverterInstance().fromDomain(cal.orNull()).orNull();
        }
        catch( final Exception e ) {
            throw new IOException("Could not convert the read Calendar: " + cal, e);
        }
    }
}
