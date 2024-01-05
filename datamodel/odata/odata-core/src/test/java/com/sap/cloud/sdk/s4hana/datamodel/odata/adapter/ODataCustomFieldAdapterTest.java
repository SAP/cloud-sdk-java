/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

class ODataCustomFieldAdapterTest
{
    private static final Gson GSON = new Gson();

    @Test
    void testReadInteger()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader(String.valueOf(Integer.MAX_VALUE)));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void testReadLong()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader(String.valueOf(Long.MAX_VALUE)));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void testReadDouble()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader(String.valueOf(Double.MAX_VALUE)));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    void testReadBoolean()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader(String.valueOf(true)));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isEqualTo(true);
    }

    @Test
    void testReadParsesDateString()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader("\"/Date(1643775600000)/\""));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(Date.from(Instant.parse("2022-02-02T04:20:00Z")));

        assertThat(sut.read(jsonReader)).isEqualTo(calendar);
    }

    @Test
    void testReadMalformedDateStringLeadsToNull()
        throws IOException
    {
        // The given timestamp exceeds the allowed maximum while still conforming to the regex used to match Date strings.
        // ==> The input is a "malformed" Date string.
        // Therefore, parsing fails and the adapter should return null.
        final JsonReader jsonReader = new JsonReader(new StringReader("\"/Date(" + Long.MAX_VALUE + "9)/\""));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isNull();
    }

    @Test
    void testReadDoesntParseOffsetDateTimeString()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader("\"/Date(2022-02-02T04:20:00Z)/\""));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isEqualTo("/Date(2022-02-02T04:20:00Z)/");
    }

    @Test
    void testReadString()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader("\"foo\""));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isEqualTo("foo");
    }

    @Test
    void testReadArrayOfPrimitives()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader("[\"foo\", \"bar\"]"));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isEqualTo(Arrays.asList("foo", "bar"));
    }

    @Test
    void testReadObjectOfPrimitives()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader("{\"foo\": \"bar\", \"baz\": 42}"));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        final HashMap<String, Object> expected = new HashMap<>();
        expected.put("foo", "bar");
        expected.put("baz", 42);

        assertThat(sut.read(jsonReader)).isEqualTo(expected);
    }

    @Test
    void testReadDeferredObjectLeadsToNull()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader("{\"__deferred\": \"some value\"}"));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        assertThat(sut.read(jsonReader)).isNull();
    }

    @Test
    void testReadSkipsMetadata()
        throws IOException
    {
        final JsonReader jsonReader =
            new JsonReader(new StringReader("{\"__metadata\": {\"key\": \"value\"}, \"foo\": \"bar\", \"baz\": 42}"));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        final HashMap<String, Object> expected = new HashMap<>();
        expected.put("foo", "bar");
        expected.put("baz", 42);

        assertThat(sut.read(jsonReader)).isEqualTo(expected);
    }

    @Test
    void testReadReturnsResultsOnRootLevel()
        throws IOException
    {
        final JsonReader jsonReader = new JsonReader(new StringReader("{\"results\": {\"key\": \"value\"}}"));
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        final HashMap<String, Object> expected = new HashMap<>();
        expected.put("key", "value");

        assertThat(sut.read(jsonReader)).isEqualTo(expected);
    }

    @Test
    void testReadExpectsDeferredToBeTheLastObject()
    {
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        {
            // working example
            final JsonReader jsonReader =
                new JsonReader(new StringReader("{\"key\": \"value\", \"__deferred\": {\"foo\": \"bar\"}}"));
            assertThatNoException().isThrownBy(() -> sut.read(jsonReader));
        }

        {
            // failing example
            final JsonReader jsonReader =
                new JsonReader(new StringReader("{\"__deferred\": {\"foo\": \"bar\"}, \"key\": \"value\"}"));
            assertThatThrownBy(() -> sut.read(jsonReader))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Expected END_OBJECT but was NAME");
        }
    }

    @Test
    void testReadExpectsResultsToBeTheLastObject()
    {
        final ODataCustomFieldAdapter sut = new ODataCustomFieldAdapter(GSON);

        {
            // working example
            final JsonReader jsonReader =
                new JsonReader(new StringReader("{\"key\": \"value\", \"results\": {\"foo\": \"bar\"}}"));
            assertThatNoException().isThrownBy(() -> sut.read(jsonReader));
        }

        {
            // failing example
            final JsonReader jsonReader =
                new JsonReader(new StringReader("{\"results\": {\"foo\": \"bar\"}, \"key\": \"value\"}"));
            assertThatThrownBy(() -> sut.read(jsonReader))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Expected END_OBJECT but was NAME");
        }
    }
}
