/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.LocalTime;
import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Gson adapter to (de-)serialize fields of type {@link LocalTime} from and to Json.
 */
public class LocalTimeAdapter extends AbstractCalendarAdapter<LocalTime>
{
    private static final ODataTimeStringCalendarConverter TIME_STRING_CALENDAR_CONVERTER =
        new ODataTimeStringCalendarConverter();
    private static final LocalTimeCalendarConverter LOCAL_TIME_CALENDAR_CONVERTER = new LocalTimeCalendarConverter();

    @Nonnull
    @Override
    protected ConvertedObject<LocalTime> convertStringToType( @Nonnull final String jsonString )
    {
        final ConvertedObject<Calendar> maybeCalendar = TIME_STRING_CALENDAR_CONVERTER.toDomain(jsonString);
        return LOCAL_TIME_CALENDAR_CONVERTER.fromDomain(maybeCalendar.orNull());
    }

    @Nonnull
    @Override
    protected ConvertedObject<String> convertTypeToString( @Nullable final LocalTime entity )
    {
        final ConvertedObject<Calendar> convertedCalendar = LOCAL_TIME_CALENDAR_CONVERTER.toDomain(entity);
        return TIME_STRING_CALENDAR_CONVERTER.fromDomain(convertedCalendar.orNull());
    }
}
