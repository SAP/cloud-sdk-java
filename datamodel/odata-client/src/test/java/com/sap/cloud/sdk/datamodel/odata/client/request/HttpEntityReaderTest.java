package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;

class HttpEntityReaderTest
{
    private final ODataRequestResult odataResult = mock(ODataRequestResult.class);
    private final ODataRequestGeneric odataRequest = mock(ODataRequestGeneric.class);
    private final HttpResponse httpResponse = mock(HttpResponse.class);
    private final StatusLine httpResponseStatusLine = mock(StatusLine.class);

    @BeforeEach
    void adjustMocks()
    {
        lenient().when(odataRequest.getProtocol()).thenReturn(ODataProtocol.V2);
        lenient().when(odataResult.getHttpResponse()).thenReturn(httpResponse);
        lenient().when(odataResult.getODataRequest()).thenReturn(odataRequest);
        lenient().when(httpResponse.getStatusLine()).thenReturn(httpResponseStatusLine);
        lenient().when(httpResponse.getAllHeaders()).thenReturn(new Header[0]);
        lenient().when(httpResponseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    }

    @Test
    void testSuccessStream()
    {
        when(httpResponse.getEntity()).thenReturn(new StringEntity("{\"foo\":\"bar\"}", StandardCharsets.UTF_8));

        final boolean result = HttpEntityReader.stream(odataResult, reader -> {
            reader.beginObject();
            assertThat(reader.nextName()).isEqualTo("foo");
            assertThat(reader.nextString()).isEqualTo("bar");
            reader.endObject();
            return true;
        });

        assertThat(result).isTrue();
    }

    @Test
    void testSuccessRead()
    {
        when(httpResponse.getEntity()).thenReturn(new StringEntity("{\"foo\":\"bar\"}", StandardCharsets.UTF_8));

        final boolean result = HttpEntityReader.read(odataResult, element -> {
            final JsonObject expected = new JsonObject();
            expected.addProperty("foo", "bar");
            assertThat(element).isEqualTo(expected);
            return true;
        });

        assertThat(result).isTrue();
    }

    @Test
    void testErrorStreamInside()
    {
        when(httpResponse.getEntity()).thenReturn(new StringEntity("{\"b ro ken", StandardCharsets.UTF_8));

        HttpEntityReader.stream(odataResult, reader -> {
            reader.beginObject();
            assertThatCode(reader::nextName).isInstanceOf(MalformedJsonException.class);
            assertThatCode(reader::nextString).isInstanceOf(IllegalStateException.class);
            assertThatCode(reader::endObject).isInstanceOf(IllegalStateException.class);
            return null;
        });
    }

    @Test
    void testErrorStreamOutside()
    {
        when(httpResponse.getEntity()).thenReturn(new StringEntity("{\"b ro ken", StandardCharsets.UTF_8));

        assertThatExceptionOfType(ODataDeserializationException.class)
            .isThrownBy(() -> HttpEntityReader.stream(odataResult, reader -> {
                reader.beginObject(); // success
                reader.nextName(); // failure
                reader.nextString(); // not reachable code
                reader.endObject(); // not reachable code
                return null;
            }))
            .withCauseExactlyInstanceOf(MalformedJsonException.class);
    }

    @Test
    void testErrorRead()
    {
        when(httpResponse.getEntity()).thenReturn(new StringEntity("{\"b ro ken", StandardCharsets.UTF_8));

        assertThatExceptionOfType(ODataDeserializationException.class)
            .isThrownBy(() -> HttpEntityReader.read(odataResult, element -> {
                // already failed
                return null;
            }))
            .withCauseExactlyInstanceOf(JsonSyntaxException.class)
            .withRootCauseExactlyInstanceOf(MalformedJsonException.class);
    }

    @Test
    void testNoEntityRead()
    {
        when(httpResponse.getEntity()).thenReturn(null);

        assertThatExceptionOfType(ODataDeserializationException.class)
            .isThrownBy(() -> HttpEntityReader.read(odataResult, element -> true))
            .withNoCause();
    }

    @Test
    void testNoEntityStream()
    {
        when(httpResponse.getEntity()).thenReturn(null);

        assertThatExceptionOfType(ODataDeserializationException.class)
            .isThrownBy(() -> HttpEntityReader.stream(odataResult, reader -> true))
            .withNoCause();
    }
}
