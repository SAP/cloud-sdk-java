/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ZonedDateTimeCalendarConverter;

class ZonedDateTimeCalendarConverterTest
{
    @Test
    void testFromDomain()
    {
        final int someYear = 2017;
        final int someMonth = Calendar.MAY;
        final int someDay = 24;
        final int someHour = 15;
        final int someMinute = 57;
        final int someSecond = 42;
        final int someMillisecond = 852;
        final TimeZone someTimeZone = TimeZone.getTimeZone("America/Los_Angeles");

        final Calendar input = Calendar.getInstance(someTimeZone);
        input.clear();
        input.set(Calendar.YEAR, someYear);
        input.set(Calendar.MONTH, someMonth);
        input.set(Calendar.DAY_OF_MONTH, someDay);
        input.set(Calendar.HOUR_OF_DAY, someHour);
        input.set(Calendar.MINUTE, someMinute);
        input.set(Calendar.SECOND, someSecond);
        input.set(Calendar.MILLISECOND, someMillisecond);

        final ZonedDateTime result = new ZonedDateTimeCalendarConverter().fromDomain(input).get();

        assertThat(result).isNotNull();

        assertThat(result.getZone()).isEqualTo(someTimeZone.toZoneId());
        assertThat(result.get(ChronoField.YEAR)).isEqualTo(someYear);
        assertThat(result.get(ChronoField.MONTH_OF_YEAR)).isEqualTo(Month.MAY.getValue());
        assertThat(result.get(ChronoField.DAY_OF_MONTH)).isEqualTo(someDay);
        assertThat(result.get(ChronoField.HOUR_OF_DAY)).isEqualTo(someHour);
        assertThat(result.get(ChronoField.MINUTE_OF_HOUR)).isEqualTo(someMinute);
        assertThat(result.get(ChronoField.SECOND_OF_MINUTE)).isEqualTo(someSecond);
        assertThat(result.get(ChronoField.MILLI_OF_SECOND)).isEqualTo(someMillisecond);
    }

    @Test
    void testToDomain()
    {
        final int someYear = 2017;
        final int someMonth = Month.MAY.getValue();
        final int someDay = 24;
        final int someHour = 15;
        final int someMinute = 57;
        final int someSecond = 42;
        final int someMillisecond = 852;
        final int millisAsNanoseconds = someMillisecond * 1_000_000;
        final ZoneId someTimeZone = ZoneId.of("America/Los_Angeles");

        final ZonedDateTime input =
            ZonedDateTime
                .of(someYear, someMonth, someDay, someHour, someMinute, someSecond, millisAsNanoseconds, someTimeZone);

        final Calendar result = new ZonedDateTimeCalendarConverter().toDomain(input).get();

        assertThat(result).isNotNull();

        assertThat(result.getTimeZone()).isEqualTo(TimeZone.getTimeZone(someTimeZone));
        assertThat(result.get(Calendar.YEAR)).isEqualTo(someYear);
        assertThat(result.get(Calendar.MONTH)).isEqualTo(Calendar.MAY);
        assertThat(result.get(Calendar.DAY_OF_MONTH)).isEqualTo(someDay);
        assertThat(result.get(Calendar.HOUR_OF_DAY)).isEqualTo(someHour);
        assertThat(result.get(Calendar.MINUTE)).isEqualTo(someMinute);
        assertThat(result.get(Calendar.SECOND)).isEqualTo(someSecond);
        assertThat(result.get(Calendar.MILLISECOND)).isEqualTo(someMillisecond);
    }
}
