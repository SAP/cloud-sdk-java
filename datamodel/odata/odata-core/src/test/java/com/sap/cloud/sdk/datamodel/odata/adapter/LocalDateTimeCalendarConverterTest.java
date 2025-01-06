/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalDateTimeCalendarConverter;

class LocalDateTimeCalendarConverterTest
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

        final Calendar input = Calendar.getInstance();
        input.clear();
        input.set(Calendar.YEAR, someYear);
        input.set(Calendar.MONTH, someMonth);
        input.set(Calendar.DAY_OF_MONTH, someDay);
        input.set(Calendar.HOUR_OF_DAY, someHour);
        input.set(Calendar.MINUTE, someMinute);
        input.set(Calendar.SECOND, someSecond);
        input.set(Calendar.MILLISECOND, someMillisecond);

        final LocalDateTime result = new LocalDateTimeCalendarConverter().fromDomain(input).get();

        assertThat(result).isNotNull();

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

        final LocalDateTime input =
            LocalDateTime.of(someYear, someMonth, someDay, someHour, someMinute, someSecond, millisAsNanoseconds);

        final Calendar result = new LocalDateTimeCalendarConverter().toDomain(input).get();

        assertThat(result).isNotNull();

        assertThat(result.get(Calendar.YEAR)).isEqualTo(someYear);
        assertThat(result.get(Calendar.MONTH)).isEqualTo(Calendar.MAY);
        assertThat(result.get(Calendar.DAY_OF_MONTH)).isEqualTo(someDay);
        assertThat(result.get(Calendar.HOUR_OF_DAY)).isEqualTo(someHour);
        assertThat(result.get(Calendar.MINUTE)).isEqualTo(someMinute);
        assertThat(result.get(Calendar.SECOND)).isEqualTo(someSecond);
        assertThat(result.get(Calendar.MILLISECOND)).isEqualTo(someMillisecond);
    }
}
