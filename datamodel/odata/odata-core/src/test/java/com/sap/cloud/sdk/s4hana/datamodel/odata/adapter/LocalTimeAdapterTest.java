/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.google.gson.TypeAdapter;

import lombok.SneakyThrows;

class LocalTimeAdapterTest
{
    @Test
    @SneakyThrows
    void standardFormatCases()
    {
        final TypeAdapter<LocalTime> adapter = new LocalTimeAdapter();

        assertThat(adapter.fromJson("\"PT13H20M\"")).isEqualTo("13:20");
        assertThat(adapter.fromJson("\"PT22M\"")).isEqualTo("00:22");
        assertThat(adapter.fromJson("\"PT15H\"")).isEqualTo("15:00");
        assertThat(adapter.fromJson("\"PT54S\"")).isEqualTo("00:00:54");
        assertThat(adapter.fromJson("\"PT54.123S\"")).isEqualTo("00:00:54.123");
    }

    @Test
    @SneakyThrows
    void adapterIgnoresDateFields()
    {
        final TypeAdapter<LocalTime> adapter = new LocalTimeAdapter();

        assertThat(adapter.fromJson("\"P11Y22M33DT13H22M12.345S\"")).isEqualTo("13:22:12.345");
        assertThat(adapter.fromJson("\"P11Y22M33DT\"")).isEqualTo("00:00");
    }

    @Test
    @SneakyThrows
    void adapterReturnsNullOnEmptyData()
    {
        final TypeAdapter<LocalTime> adapter = new LocalTimeAdapter();

        assertThat(adapter.fromJson("\"PT\"")).isNull();
    }

    @Test
    @SneakyThrows
    void nonStringValueReturnsNull()
    {
        final TypeAdapter<LocalTime> adapter = new LocalTimeAdapter();

        assertThat(adapter.fromJson("1234")).isNull();
    }

    @Test
    void writeLocalTime()
    {
        final TypeAdapter<LocalTime> adapter = new LocalTimeAdapter();

        assertThat(adapter.toJson(LocalTime.of(1, 2, 3))).isEqualTo("\"PT1H2M3S\"");
        assertThat(adapter.toJson(LocalTime.of(14, 54, 32, 123000000))).isEqualTo("\"PT14H54M32.123S\"");
    }
}
