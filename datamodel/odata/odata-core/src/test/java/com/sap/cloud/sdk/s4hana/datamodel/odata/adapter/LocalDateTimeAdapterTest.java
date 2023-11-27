/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.google.gson.TypeAdapter;

import lombok.SneakyThrows;

class LocalDateTimeAdapterTest
{
    @Test
    @SneakyThrows
    void standardFormatCases()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.fromJson("\"/Date(1649669575192)/\"")).isEqualTo("2022-04-11T09:32:55.192");
    }

    @Test
    @SneakyThrows
    void wrongFormatReturnsNull()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.fromJson("\"Something that is not a date\"")).isNull();
    }

    @Test
    @SneakyThrows
    void excessivelyLongNumberReturnsNull()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.fromJson("\"/Date(100000000000000000000000)/\"")).isNull();
    }

    @Test
    @SneakyThrows
    void nonStringValueReturnsNull()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.fromJson("1234")).isNull();
    }

    @Test
    void writeLocalDateTime()
    {
        final TypeAdapter<LocalDateTime> adapter = new LocalDateTimeAdapter();

        assertThat(adapter.toJson(LocalDateTime.of(2022, Month.APRIL, 11, 11, 36, 55, 123000000)))
            .isEqualTo("\"/Date(1649677015123)/\"");
    }
}
