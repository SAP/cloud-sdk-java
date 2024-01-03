/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Converts between the deprecated {@link Calendar} type and the new {@link LocalTime}.
 * <p>
 * The year, month, and day fields on the {@code Calendar} instance are ignored/cleared, as the {@code LocalTime} does
 * not contain any values for those
 * <p>
 * In combination with the {@link ODataField} annotation this can be used to expose fields of OData value which would be
 * exposed as {@code Calendar} as {@code LocalTime}.
 */
public class LocalTimeCalendarConverter extends AbstractTypeConverter<LocalTime, Calendar>
{
    private static final int MILLI_TO_NANO_FACTOR = 1_000_000;

    @Override
    @Nonnull
    public ConvertedObject<Calendar> toDomainNonNull( @Nonnull final LocalTime object )
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        calendar.clear();
        calendar.set(Calendar.HOUR_OF_DAY, object.getHour());
        calendar.set(Calendar.MINUTE, object.getMinute());
        calendar.set(Calendar.SECOND, object.getSecond());
        if( object.isSupported(ChronoField.MILLI_OF_SECOND) ) {
            calendar.set(Calendar.MILLISECOND, object.get(ChronoField.MILLI_OF_SECOND));
        }

        return ConvertedObject.of(calendar);
    }

    @Override
    @Nonnull
    public ConvertedObject<LocalTime> fromDomainNonNull( @Nonnull final Calendar domainObject )
    {
        final LocalTime localDateTime =
            LocalTime
                .of(
                    domainObject.get(Calendar.HOUR_OF_DAY),
                    domainObject.get(Calendar.MINUTE),
                    domainObject.get(Calendar.SECOND),
                    domainObject.get(Calendar.MILLISECOND) * MILLI_TO_NANO_FACTOR);

        return ConvertedObject.of(localDateTime);
    }

    @Override
    @Nonnull
    public Class<LocalTime> getType()
    {
        return LocalTime.class;
    }

    @Override
    @Nonnull
    public Class<Calendar> getDomainType()
    {
        return Calendar.class;
    }
}
