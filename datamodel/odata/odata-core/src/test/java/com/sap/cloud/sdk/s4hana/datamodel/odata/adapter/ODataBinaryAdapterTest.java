package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.Test;

import com.google.gson.TypeAdapter;

import lombok.SneakyThrows;

public class ODataBinaryAdapterTest
{
    @Test
    @SneakyThrows
    public void readIntoByteArray()
    {
        final String testText = "Hello World";
        final String base64encodedString =
            Base64.getEncoder().encodeToString(testText.getBytes(StandardCharsets.UTF_8));

        final TypeAdapter<byte[]> binaryAdapter = new ODataBinaryAdapter();

        final byte[] parsedResult = binaryAdapter.fromJson("\"" + base64encodedString + "\"");

        assertThat(parsedResult).isEqualTo(testText.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @SneakyThrows
    public void readInvalidString()
    {
        final TypeAdapter<byte[]> binaryAdapter = new ODataBinaryAdapter();

        assertThat(binaryAdapter.fromJson("\"_-/!§$&/()\"")).isNull();
    }

    @Test
    public void writeFromByteArray()
    {
        final String testText = "Hello World";
        final String base64encodedString =
            Base64.getEncoder().encodeToString(testText.getBytes(StandardCharsets.UTF_8));

        final TypeAdapter<byte[]> binaryAdapter = new ODataBinaryAdapter();

        final String writtenResult = binaryAdapter.toJson(testText.getBytes(StandardCharsets.UTF_8));

        assertThat(writtenResult).isEqualTo("\"" + base64encodedString + "\"");
    }

    @Test
    public void nonStringValueGetsIgnored()
        throws IOException
    {
        final TypeAdapter<byte[]> binaryAdapter = new ODataBinaryAdapter();

        assertThat(binaryAdapter.fromJson("123")).isNull();
    }
}
