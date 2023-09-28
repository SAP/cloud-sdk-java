package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Test;

import com.google.gson.TypeAdapter;

import lombok.SneakyThrows;

public class LocalDateTimeAdapterTest
{
    @Test
    @SneakyThrows
    public void standardFormatCases()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.fromJson("\"/Date(1649669575192)/\"")).isEqualTo("2022-04-11T09:32:55.192");
    }

    @Test
    @SneakyThrows
    public void wrongFormatReturnsNull()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.fromJson("\"Something that is not a date\"")).isNull();
    }

    @Test
    @SneakyThrows
    public void excessivelyLongNumberReturnsNull()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.fromJson("\"/Date(100000000000000000000000)/\"")).isNull();
    }

    @Test
    @SneakyThrows
    public void nonStringValueReturnsNull()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.fromJson("1234")).isNull();
    }

    @Test
    public void writeLocalDateTime()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.toJson(LocalDateTime.of(2022, Month.APRIL, 11, 11, 36, 55, 123000000)))
            .isEqualTo("\"/Date(1649677015123)/\"");
    }
}
