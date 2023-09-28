package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.google.gson.TypeAdapter;

import lombok.SneakyThrows;

public class ZonedDateTimeAdapterTest
{
    @Test
    @SneakyThrows
    public void standardFormatCases()
    {
        final TypeAdapter<ZonedDateTime> adapter = new ZonedDateTimeAdapter();

        assertThat(adapter.fromJson("\"/Date(1649669575192)/\"")).isEqualTo("2022-04-11T09:32:55.192Z");
        assertThat(adapter.fromJson("\"/Date(1649669575192+0120)/\"")).isEqualTo("2022-04-11T09:32:55.192+02:00");
    }

    @Test
    @SneakyThrows
    public void declaredOffsetTooLargeReturnsNull()
    {
        final TypeAdapter<ZonedDateTime> adapter = new ZonedDateTimeAdapter();

        assertThat(adapter.fromJson("\"/Date(1649669575192+1441)/\"")).isNull();
    }

    @Test
    @SneakyThrows
    public void invalidPatternReturnsNull()
    {
        final TypeAdapter<ZonedDateTime> adapter = new ZonedDateTimeAdapter();

        assertThat(adapter.fromJson("\"Something that is not a date\"")).isNull();
    }

    @Test
    @SneakyThrows
    public void isoPatternReturnsNull()
    {
        final TypeAdapter<ZonedDateTime> adapter = new ZonedDateTimeAdapter();

        assertThat(adapter.fromJson("\"2022-04-11T12:47:14.1234567-02:30\"")).isNull();
        assertThat(adapter.fromJson("\"2022-04-11T12:47:14.1234567+4:30\"")).isNull();
        assertThat(adapter.fromJson("\"2022-04-11T12:47:14.1234567Z\"")).isNull();
        assertThat(adapter.fromJson("\"2022-04-11T12:47:14\"")).isNull();
        assertThat(adapter.fromJson("\"2022-04-11T12:47\"")).isNull();
        assertThat(adapter.fromJson("\"2-4-1T2:7\"")).isNull();
    }

    @Test
    @SneakyThrows
    public void excessivelyLongNumberReturnsNull()
    {
        final TypeAdapter<ZonedDateTime> adapter = new ZonedDateTimeAdapter();

        assertThat(adapter.fromJson("\"/Date(100000000000000000000000)/\"")).isNull();
    }

    @Test
    @SneakyThrows
    public void nonStringValueReturnsNull()
    {
        final TypeAdapter<ZonedDateTime> adapter = new ZonedDateTimeAdapter();

        assertThat(adapter.fromJson("1234")).isNull();
    }

    @Test
    public void writeZonedDateTime()
    {
        final TypeAdapter<ZonedDateTime> adapter = new ZonedDateTimeAdapter();

        assertThat(
            adapter.toJson(ZonedDateTime.of(2022, Month.APRIL.getValue(), 11, 11, 36, 55, 123000000, ZoneId.of("GMT"))))
            .isEqualTo("\"/Date(1649677015123)/\"");
        assertThat(
            adapter
                .toJson(ZonedDateTime.of(2022, Month.APRIL.getValue(), 11, 11, 36, 55, 123000000, ZoneId.of("GMT+2"))))
            .isEqualTo("\"/Date(1649677015123+0120)/\"");
    }
}
