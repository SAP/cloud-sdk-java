/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol.V2;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@RunWith( Parameterized.class )
@AllArgsConstructor
public class ODataResponseExceptionTest
{
    private static final ODataRequestGeneric REQUEST = new ODataRequestRead("path", "collection", null, V2);
    private static final Throwable CAUSE = new Exception();
    private static final String MESSAGE = "message";
    private static final String INPUT = "Hêllö WØrld";

    private Charset charset;
    private String text;
    private String textMissingCharset;

    @Parameterized.Parameters( name = "{0}" )
    public static List<Object[]> getCharsets()
    {
        return Arrays
            .asList(
                new Object[] { StandardCharsets.ISO_8859_1, INPUT, "H�ll� W�rld" },
                new Object[] { StandardCharsets.UTF_8, INPUT, INPUT });
    }

    @SneakyThrows
    @Test
    public void testEncodingGiven()
    {
        final byte[] encodedString = text.getBytes(charset);

        final HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        response.setEntity(new ByteArrayEntity(encodedString, ContentType.create("text/plain", charset)));

        final ODataResponseException message = new ODataResponseException(REQUEST, response, MESSAGE, CAUSE);
        assertThat(message).hasMessage(MESSAGE).hasCause(CAUSE);
        assertThat(message.getHttpCode()).isEqualTo(200);
        assertThat(message.getHttpHeaders()).isEmpty();
        assertThat(message.getHttpBody()).containsExactly(text);
    }

    @SneakyThrows
    @Test
    public void testEncodingUnknown()
    {
        final byte[] encodedString = text.getBytes(charset);

        final HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        response.setEntity(new ByteArrayEntity(encodedString, ContentType.create("text/plain"))); // no charset

        final ODataResponseException message = new ODataResponseException(REQUEST, response, MESSAGE, CAUSE);
        assertThat(message).hasMessage(MESSAGE).hasCause(CAUSE);
        assertThat(message.getHttpCode()).isEqualTo(200);
        assertThat(message.getHttpHeaders()).isEmpty();
        assertThat(message.getHttpBody()).containsExactly(textMissingCharset);
    }
}
