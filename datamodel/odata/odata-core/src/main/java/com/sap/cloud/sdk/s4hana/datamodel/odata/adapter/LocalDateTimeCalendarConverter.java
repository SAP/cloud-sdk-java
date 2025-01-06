/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Converts between the deprecated {@link Calendar} type and the new {@link LocalDateTime}.
 * <p>
 * In combination with the {@link ODataField} annotation this can be used to expose fields of OData value which would be
 * exposed as {@code Calendar} as {@code LocalDateTime}.
 */
public class LocalDateTimeCalendarConverter extends AbstractTypeConverter<LocalDateTime, Calendar>
{
    private static final int MILLI_TO_NANO_FACTOR = 1_000_000;

    @Override
    @Nonnull
    public ConvertedObject<Calendar> toDomainNonNull( @Nonnull final LocalDateTime object )
    {
        // Used with the EdmDateTime class of the service SDK this calender has to be in timezone UTC, see
        // Calendar.getTimeInMillis(), where the UTC usage is specified.
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        calendar.set(Calendar.YEAR, object.get(ChronoField.YEAR));
        calendar.set(Calendar.MONTH, object.get(ChronoField.MONTH_OF_YEAR) - 1); // convert 1-based to 0-based months
        calendar.set(Calendar.DAY_OF_MONTH, object.get(ChronoField.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, object.get(ChronoField.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, object.get(ChronoField.MINUTE_OF_HOUR));
        calendar.set(Calendar.SECOND, object.get(ChronoField.SECOND_OF_MINUTE));
        calendar.set(Calendar.MILLISECOND, object.get(ChronoField.MILLI_OF_SECOND));

        return ConvertedObject.of(calendar);
    }

    @Override
    @Nonnull
    public ConvertedObject<LocalDateTime> fromDomainNonNull( @Nonnull final Calendar domainObject )
    {
        final LocalDateTime localDateTime =
            LocalDateTime
                .of(
                    domainObject.get(Calendar.YEAR),
                    domainObject.get(Calendar.MONTH) + 1, // convert 0-based to 1-based months
                    domainObject.get(Calendar.DAY_OF_MONTH),
                    domainObject.get(Calendar.HOUR_OF_DAY),
                    domainObject.get(Calendar.MINUTE),
                    domainObject.get(Calendar.SECOND),
                    domainObject.get(Calendar.MILLISECOND) * MILLI_TO_NANO_FACTOR);

        return ConvertedObject.of(localDateTime);
    }

    @Override
    @Nonnull
    public Class<LocalDateTime> getType()
    {
        return LocalDateTime.class;
    }

    @Override
    @Nonnull
    public Class<Calendar> getDomainType()
    {
        return Calendar.class;
    }
}
