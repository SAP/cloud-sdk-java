package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.ZonedDateTime;
import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Gson adapter to (de-)serialize fields of type {@link ZonedDateTime} from and to Json.
 */
public class ZonedDateTimeAdapter extends AbstractCalendarAdapter<ZonedDateTime>
{
    private static final ODataDateTimeOffsetStringCalendarConverter DATE_TIME_OFFSET_STRING_CALENDAR_CONVERTER =
        new ODataDateTimeOffsetStringCalendarConverter();
    private static final ZonedDateTimeCalendarConverter ZONED_DATE_TIME_CALENDAR_CONVERTER =
        new ZonedDateTimeCalendarConverter();

    @Nonnull
    @Override
    protected ConvertedObject<ZonedDateTime> convertStringToType( @Nonnull final String jsonString )
    {
        final ConvertedObject<Calendar> maybeCalendar = DATE_TIME_OFFSET_STRING_CALENDAR_CONVERTER.toDomain(jsonString);
        return ZONED_DATE_TIME_CALENDAR_CONVERTER.fromDomain(maybeCalendar.orNull());
    }

    @Nonnull
    @Override
    protected ConvertedObject<String> convertTypeToString( @Nullable final ZonedDateTime entity )
    {
        final ConvertedObject<Calendar> convertedCalendar = ZONED_DATE_TIME_CALENDAR_CONVERTER.toDomain(entity);
        return DATE_TIME_OFFSET_STRING_CALENDAR_CONVERTER.fromDomain(convertedCalendar.orNull());
    }
}
