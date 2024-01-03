/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeCalendarConverter;

class LocalTimeCalendarConverterTest
{
    @Test
    void testFromDomain()
    {
        final int someHour = 15;
        final int someMinute = 57;
        final int someSecond = 42;
        final int someMillisecond = 852;

        final Calendar input = Calendar.getInstance();
        input.clear();
        input.set(Calendar.HOUR_OF_DAY, someHour);
        input.set(Calendar.MINUTE, someMinute);
        input.set(Calendar.SECOND, someSecond);
        input.set(Calendar.MILLISECOND, someMillisecond);

        final LocalTime result = new LocalTimeCalendarConverter().fromDomain(input).get();

        assertThat(result).isNotNull();

        assertThat(result.get(ChronoField.HOUR_OF_DAY)).isEqualTo(someHour);
        assertThat(result.get(ChronoField.MINUTE_OF_HOUR)).isEqualTo(someMinute);
        assertThat(result.get(ChronoField.SECOND_OF_MINUTE)).isEqualTo(someSecond);
        assertThat(result.get(ChronoField.MILLI_OF_SECOND)).isEqualTo(someMillisecond);
    }

    @Test
    void testToDomain()
    {
        final int someHour = 15;
        final int someMinute = 57;
        final int someSecond = 42;
        final int someMillisecond = 852;
        final int millisAsNanoseconds = someMillisecond * 1_000_000;

        final LocalTime input = LocalTime.of(someHour, someMinute, someSecond, millisAsNanoseconds);

        final Calendar result = new LocalTimeCalendarConverter().toDomain(input).get();

        assertThat(result).isNotNull();

        assertThat(result.get(Calendar.HOUR_OF_DAY)).isEqualTo(someHour);
        assertThat(result.get(Calendar.MINUTE)).isEqualTo(someMinute);
        assertThat(result.get(Calendar.SECOND)).isEqualTo(someSecond);
        assertThat(result.get(Calendar.MILLISECOND)).isEqualTo(someMillisecond);
    }
}
