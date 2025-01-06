/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.LocalDateTime;
import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Gson adapter to (de-)serialize fields of type {@link LocalDateTime} from and to Json.
 */
public class LocalDateTimeAdapter extends AbstractCalendarAdapter<LocalDateTime>
{
    private static final ODataDateTimeStringCalendarConverter STRING_CALENDAR_CONVERTER =
        new ODataDateTimeStringCalendarConverter();
    private static final LocalDateTimeCalendarConverter LOCAL_DATE_TIME_CALENDAR_CONVERTER =
        new LocalDateTimeCalendarConverter();

    @Nonnull
    @Override
    protected ConvertedObject<LocalDateTime> convertStringToType( @Nonnull final String jsonString )
    {
        final ConvertedObject<Calendar> maybeCalendar = STRING_CALENDAR_CONVERTER.toDomain(jsonString);
        return LOCAL_DATE_TIME_CALENDAR_CONVERTER.fromDomain(maybeCalendar.orNull());
    }

    @Nonnull
    @Override
    protected ConvertedObject<String> convertTypeToString( @Nullable final LocalDateTime entity )
    {
        final ConvertedObject<Calendar> convertedCalendar = LOCAL_DATE_TIME_CALENDAR_CONVERTER.toDomain(entity);
        return STRING_CALENDAR_CONVERTER.fromDomain(convertedCalendar.orNull());
    }
}
