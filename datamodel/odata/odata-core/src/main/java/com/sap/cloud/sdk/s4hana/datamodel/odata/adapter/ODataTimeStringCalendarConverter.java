/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ODataTimeStringCalendarConverter extends AbstractTypeConverter<String, Calendar>
{
    private static final Pattern PATTERN =
        Pattern
            .compile(
                "P(?:(\\p{Digit}{1,2})Y)?(?:(\\p{Digit}{1,2})M)?(?:(\\p{Digit}{1,2})D)?"
                    + "T(?:(\\p{Digit}{1,2})H)?(?:(\\p{Digit}{1,4})M)?(?:(\\p{Digit}{1,5})(?:\\.(\\p{Digit}+?)0*)?S)?");

    @Nonnull
    @Override
    public ConvertedObject<Calendar> toDomainNonNull( @Nonnull final String jsonValue )
    {
        final Matcher matcher = PATTERN.matcher(jsonValue);
        if( !matcher.matches() ) {
            return ConvertedObject.ofNotConvertible();
        }
        if( matcher.group(1) == null
            && matcher.group(2) == null
            && matcher.group(3) == null
            && matcher.group(4) == null
            && matcher.group(5) == null
            && matcher.group(6) == null ) {
            return ConvertedObject.ofNotConvertible();
        }

        final Calendar dateTimeValue = Calendar.getInstance();
        dateTimeValue.clear();

        if( matcher.group(1) != null ) {
            dateTimeValue.set(Calendar.YEAR, Integer.parseInt(matcher.group(1)));
        }
        if( matcher.group(2) != null ) {
            dateTimeValue.set(Calendar.MONTH, Integer.parseInt(matcher.group(2)));
        }
        if( matcher.group(3) != null ) {
            dateTimeValue.set(Calendar.DAY_OF_YEAR, Integer.parseInt(matcher.group(3)));
        }
        dateTimeValue.set(Calendar.HOUR_OF_DAY, matcher.group(4) == null ? 0 : Integer.parseInt(matcher.group(4)));
        dateTimeValue.set(Calendar.MINUTE, matcher.group(5) == null ? 0 : Integer.parseInt(matcher.group(5)));
        dateTimeValue.set(Calendar.SECOND, matcher.group(6) == null ? 0 : Integer.parseInt(matcher.group(6)));

        if( matcher.group(7) != null ) {
            final String decimals = matcher.group(7);
            final int nanoSeconds = Integer.parseInt(decimals + "000000000".substring(decimals.length()));
            if( nanoSeconds % (1000 * 1000) == 0 ) {
                dateTimeValue.set(Calendar.MILLISECOND, nanoSeconds / (1000 * 1000));
            } else {
                log
                    .debug(
                        "The given date has a precision that cannot be represented in milliseconds. Nanoseconds: "
                            + nanoSeconds);
                return ConvertedObject.ofNotConvertible();
            }
        }

        return ConvertedObject.of(dateTimeValue);
    }

    @Nonnull
    @Override
    public ConvertedObject<String> fromDomainNonNull( @Nonnull final Calendar calendar )
    {
        final StringBuilder result = new StringBuilder(21); // 21 characters are enough for nanosecond precision.
        result.append('P');
        result.append('T');
        result.append(calendar.get(Calendar.HOUR_OF_DAY));
        result.append('H');
        result.append(calendar.get(Calendar.MINUTE));
        result.append('M');
        result.append(calendar.get(Calendar.SECOND));

        final int fractionalSecs = calendar.get(Calendar.MILLISECOND);
        appendFractionalSeconds(result, fractionalSecs);
        result.append('S');

        return ConvertedObject.of(result.toString());
    }

    protected static void appendFractionalSeconds( final StringBuilder result, final int fractionalSeconds )
    {
        if( fractionalSeconds > 0 ) {
            // Determine the number of significant digits.
            int output = fractionalSeconds;
            while( output % 10 == 0 ) {
                output /= 10;
            }

            result.append('.');
            for( int d = 100; d > 0; d /= 10 ) {
                final byte digit = (byte) (fractionalSeconds % (d * 10) / d);
                if( digit > 0 || fractionalSeconds % d > 0 ) {
                    result.append((char) ('0' + digit));
                }
            }
        }
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
