package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;

@RunWith( MockitoJUnitRunner.class )
public class HttpEntityReaderTest
{
    @Mock
    ODataRequestResult odataResult;

    @Mock
    ODataRequestGeneric odataRequest;

    @Mock
    HttpResponse httpResponse;

    @Mock
    StatusLine httpResponseStatusLine;

    @Before
    public void adjustMocks()
    {
        when(odataRequest.getProtocol()).thenReturn(ODataProtocol.V2);
        when(odataResult.getHttpResponse()).thenReturn(httpResponse);
        when(odataResult.getODataRequest()).thenReturn(odataRequest);
        when(httpResponse.getStatusLine()).thenReturn(httpResponseStatusLine);
        when(httpResponse.getAllHeaders()).thenReturn(new Header[0]);
        when(httpResponseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    }

    @Test
    public void testSuccessStream()
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
    public void testSuccessRead()
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
    public void testErrorStreamInside()
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
    public void testErrorStreamOutside()
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
    public void testErrorRead()
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
    public void testNoEntityRead()
    {
        when(httpResponse.getEntity()).thenReturn(null);

        assertThatExceptionOfType(ODataDeserializationException.class)
            .isThrownBy(() -> HttpEntityReader.read(odataResult, element -> true))
            .withNoCause();
    }

    @Test
    public void testNoEntityStream()
    {
        when(httpResponse.getEntity()).thenReturn(null);

        assertThatExceptionOfType(ODataDeserializationException.class)
            .isThrownBy(() -> HttpEntityReader.stream(odataResult, reader -> true))
            .withNoCause();
    }
}
