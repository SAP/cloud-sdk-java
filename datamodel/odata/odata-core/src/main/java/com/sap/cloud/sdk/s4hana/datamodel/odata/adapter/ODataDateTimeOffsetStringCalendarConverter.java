/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ODataDateTimeOffsetStringCalendarConverter extends AbstractTypeConverter<String, Calendar>
{
    private static final Pattern JSON_PATTERN =
        Pattern.compile("/Date\\((-?\\p{Digit}+)(?:(\\+|-)(\\p{Digit}{1,4}))?\\)/");

    @Nonnull
    @Override
    public ConvertedObject<Calendar> toDomainNonNull( @Nonnull final String value )
    {
        final Matcher jsonMatcher = JSON_PATTERN.matcher(value);
        if( jsonMatcher.matches() ) {
            long millis;
            try {
                millis = Long.parseLong(jsonMatcher.group(1));
            }
            catch( final NumberFormatException e ) {
                log.debug("The given date string cannot be converted to milliseconds: " + e.getMessage());
                return ConvertedObject.ofNotConvertible();
            }

            String timeZone = "GMT";
            if( jsonMatcher.group(2) != null ) {
                final int offsetInMinutes = Integer.parseInt(jsonMatcher.group(3));
                if( offsetInMinutes >= 24 * 60 ) {
                    log.debug("The given offset is higher than minutes in a day: " + offsetInMinutes);
                    return ConvertedObject.ofNotConvertible();
                }
                if( offsetInMinutes != 0 ) {
                    timeZone +=
                        jsonMatcher.group(2) + offsetInMinutes / 60 + ":" + String.format("%02d", offsetInMinutes % 60);
                    // Convert the local-time milliseconds to UTC.
                    millis -= ("+".equals(jsonMatcher.group(2)) ? 1 : -1) * offsetInMinutes * 60 * 1000L;
                }
            }
            final Calendar dateTimeValue = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
            dateTimeValue.clear();
            dateTimeValue.setTimeInMillis(millis);
            return ConvertedObject.of(dateTimeValue);
        }

        return ConvertedObject.ofNotConvertible();
    }

    @Nonnull
    @Override
    public ConvertedObject<String> fromDomainNonNull( @Nonnull final Calendar value )
    {
        // number of milliseconds since 1970-01-01T00:00:00Z
        long milliSeconds = value.getTimeInMillis();
        // offset in milliseconds from GMT to the requested time zone
        final int offset = value.get(Calendar.ZONE_OFFSET) + value.get(Calendar.DST_OFFSET);

        milliSeconds += offset; // Convert from UTC to local time.
        final int offsetInMinutes = offset / 60 / 1000;

        if( offset == 0 ) {
            return ConvertedObject.of("/Date(" + milliSeconds + ")/");
        }

        return ConvertedObject.of("/Date(" + milliSeconds + String.format("%+05d", offsetInMinutes) + ")/");
    }

    @Nonnull
    @Override
    public Class<String> getType()
    {
        return String.class;
    }

    @Nonnull
    @Override
    public Class<Calendar> getDomainType()
    {
        return Calendar.class;
    }
}
