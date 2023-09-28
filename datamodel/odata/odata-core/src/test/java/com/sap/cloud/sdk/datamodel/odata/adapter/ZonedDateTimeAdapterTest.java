package com.sap.cloud.sdk.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.google.gson.stream.JsonReader;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ZonedDateTimeAdapter;

public class ZonedDateTimeAdapterTest
{

    @Test
    public void testDeserialization()
        throws IOException
    {
        final String jsonInput = "\"/Date(1525730400000-0120)/\"";

        final ZonedDateTimeAdapter sut = new ZonedDateTimeAdapter();
        final JsonReader reader = new JsonReader(new StringReader(jsonInput));
        reader.setLenient(true);
        final ZonedDateTime result = sut.read(reader);

        assertThat(result).isEqualTo(ZonedDateTime.of(2018, 5, 8, 2, 0, 0, 0, ZoneId.of("UTC+2")));
    }
}
