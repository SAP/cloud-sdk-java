/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol.V2;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;

import lombok.Data;
import lombok.SneakyThrows;

class ODataResponseExceptionTest
{
    private static final ODataRequestGeneric REQUEST = new ODataRequestRead("path", "collection", null, V2);
    private static final Throwable CAUSE = new Exception();
    private static final String MESSAGE = "message";
    private static final String INPUT = "Hêllö WØrld";

    @Data
    private static class TestParameters
    {
        @Nonnull
        Charset charset;
        @Nonnull
        String text;
        @Nonnull
        String textMissingCharset;
    }

    static List<TestParameters> getTestParameters()
    {
        return Arrays
            .asList(
                new TestParameters(StandardCharsets.ISO_8859_1, INPUT, "H�ll� W�rld"),
                new TestParameters(StandardCharsets.UTF_8, INPUT, INPUT));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource( "getTestParameters" )
    void testEncodingGiven( @Nonnull final TestParameters parameters )
    {
        final byte[] encodedString = parameters.text.getBytes(parameters.charset);

        final HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        response.setEntity(new ByteArrayEntity(encodedString, ContentType.create("text/plain", parameters.charset)));

        final ODataResponseException message = new ODataResponseException(REQUEST, response, MESSAGE, CAUSE);
        assertThat(message).hasMessage(MESSAGE).hasCause(CAUSE);
        assertThat(message.getHttpCode()).isEqualTo(200);
        assertThat(message.getHttpHeaders()).isEmpty();
        assertThat(message.getHttpBody()).containsExactly(parameters.text);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource( "getTestParameters" )
    void testEncodingUnknown( @Nonnull final TestParameters parameters )
    {
        final byte[] encodedString = parameters.text.getBytes(parameters.charset);

        final HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        response.setEntity(new ByteArrayEntity(encodedString, ContentType.create("text/plain"))); // no charset

        final ODataResponseException message = new ODataResponseException(REQUEST, response, MESSAGE, CAUSE);
        assertThat(message).hasMessage(MESSAGE).hasCause(CAUSE);
        assertThat(message.getHttpCode()).isEqualTo(200);
        assertThat(message.getHttpHeaders()).isEmpty();
        assertThat(message.getHttpBody()).containsExactly(parameters.textMissingCharset);
    }
}
