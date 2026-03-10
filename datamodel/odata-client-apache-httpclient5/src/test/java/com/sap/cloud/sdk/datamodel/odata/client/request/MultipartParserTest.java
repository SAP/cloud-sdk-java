package com.sap.cloud.sdk.datamodel.odata.client.request;

import static java.nio.charset.StandardCharsets.UTF_8;

import static com.sap.cloud.sdk.datamodel.odata.client.request.MultipartParser.Entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.io.Resources;

import lombok.SneakyThrows;

class MultipartParserTest
{
    static List<String> getNewLineDelimiters()
    {
        return Arrays.asList("\r\n", "\n");
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource( "getNewLineDelimiters" )
    void testSimpleReadSuccess( @Nonnull final String newLine )
    {
        final String responseText =
            newLine // sanity check
                + ("--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef" + newLine)
                + ("Content-Type: application/http" + newLine)
                + ("Content-Transfer-Encoding: binary" + newLine)
                + ("" + newLine)
                + ("HTTP/1.1 200 OK" + newLine)
                + ("Content-Type: application/json; odata.metadata=minimal; odata.streaming=true" + newLine)
                + ("OData-Version: 4.0" + newLine)
                + ("" + newLine)
                + ("{\"foØ\":\"bär\"}" + newLine)
                + ("--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef--" + newLine);

        final ByteArrayInputStream response = new ByteArrayInputStream(responseText.getBytes(UTF_8));
        final String delimiter = "--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";

        // user code
        final List<List<Entry>> result = MultipartParser.ofInputStream(response, UTF_8, delimiter).toList();
        final MultipartHttpResponse httpResponse = MultipartHttpResponse.ofHttpContent(result.get(0).get(0));

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).hasSize(1);
        assertThat(result.get(0).get(0).getPayload()).startsWith("HTTP/1.1 200 OK");

        assertThat(httpResponse.getAllHeaders()).satisfiesExactly(header -> {
            assertThat(header.getName()).isEqualTo("Content-Type");
            assertThat(header.getValue()).isEqualTo("application/json; odata.metadata=minimal; odata.streaming=true");
        }, header -> {
            assertThat(header.getName()).isEqualTo("OData-Version");
            assertThat(header.getValue()).isEqualTo("4.0");
        });

        assertThat(httpResponse.getStatusLine().getProtocolVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(httpResponse.getStatusLine().getReasonPhrase()).isEqualTo("OK");
        assertThat(httpResponse.getEntity().getContent()).hasContent("{\"foØ\":\"bär\"}");
        assertThat(httpResponse.getEntity().getContentType().getValue())
            .isEqualTo("application/json; odata.metadata=minimal; odata.streaming=true; charset=UTF-8");
    }

    @ParameterizedTest
    @MethodSource( "getNewLineDelimiters" )
    void testEmptyReadSuccess( @Nonnull final String newLine )
    {
        final String responseText = "--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef--" + newLine;

        final ByteArrayInputStream response = new ByteArrayInputStream(responseText.getBytes(UTF_8));
        final String delimiter = "--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";

        // user code
        final List<List<Entry>> result = MultipartParser.ofInputStream(response, UTF_8, delimiter).toList();
        assertThat(result).isEmpty();
    }

    @Test
    void testEmpty()
    {
        final String responseText = "";

        final ByteArrayInputStream response = new ByteArrayInputStream(responseText.getBytes(UTF_8));
        final String delimiter = "--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";

        // user code
        final List<List<Entry>> result = MultipartParser.ofInputStream(response, UTF_8, delimiter).toList();
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @MethodSource( "getNewLineDelimiters" )
    void testWrongDelimiterSuccess( @Nonnull final String newLine )
    {
        final String responseText = "--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef--" + newLine;

        final ByteArrayInputStream response = new ByteArrayInputStream(responseText.getBytes(UTF_8));
        final String delimiter = "--some-delimiter";

        // user code
        final List<List<Entry>> result = MultipartParser.ofInputStream(response, UTF_8, delimiter).toList();
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @MethodSource( "getNewLineDelimiters" )
    void testNewLineBeforeReadSuccess( @Nonnull final String newLine )
    {
        final String responseText = "\n--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef--" + newLine;

        final ByteArrayInputStream response = new ByteArrayInputStream(responseText.getBytes(UTF_8));
        final String delimiter = "--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";

        // user code
        final List<List<Entry>> result = MultipartParser.ofInputStream(response, UTF_8, delimiter).toList();
        assertThat(result).isEmpty();
    }

    @Test
    void testEmptyHttpResponse()
    {
        final HttpResponse httpResponse = mock(HttpResponse.class);
        assertThatCode(() -> MultipartParser.ofHttpResponse(httpResponse))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("HTTP response does not contain a content.");
    }

    @SneakyThrows
    @Test
    void testEmptyInputStream()
    {
        final HttpEntity httpEntity = mock(HttpEntity.class);
        when(httpEntity.getContent()).thenThrow(IOException.class);

        final HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        assertThatCode(() -> MultipartParser.ofHttpResponse(httpResponse))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Unable to read HTTP content.");
    }

    @Test
    void testMissingDelimiter()
    {
        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        httpResponse.setEntity(new StringEntity("", Charset.defaultCharset()));
        httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, "multipart/mixed");

        assertThatCode(() -> MultipartParser.ofHttpResponse(httpResponse))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("No delimiter found in HTTP header.");
    }

    @ParameterizedTest
    @MethodSource( "getNewLineDelimiters" )
    void testReadResultUncached( @Nonnull final String newLine )
    {
        final String responseText = readResourceFileClrf("BatchReadResponseBody.txt", newLine);
        final ByteArrayInputStream response = new ByteArrayInputStream(responseText.getBytes(UTF_8));

        final String delimiter = "--batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";

        // user code
        final Stream<Stream<Entry>> result = MultipartParser.ofInputStream(response, UTF_8, delimiter).toStream();

        // assert behavior
        assertThat(result.count()).isEqualTo(2); // first access successful

        assertThatCode(result::count) // second access error
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("stream has already been operated upon or closed");
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource( "getNewLineDelimiters" )
    void testWriteResultUncached( @Nonnull final String newLine )
    {
        final BasicHttpResponse resp = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        resp.setEntity(new StringEntity(readResourceFileClrf("BatchWriteResponseBody.txt", newLine)));
        resp.setHeader("Content-Type", "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef");

        // user code
        final Stream<Stream<Entry>> result = MultipartParser.ofHttpResponse(resp).toStream();

        // assert behavior
        assertThat(result.count()).isEqualTo(2); // first access successful

        assertThatCode(result::count) // second access error
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("stream has already been operated upon or closed");
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource( "getNewLineDelimiters" )
    void testWriteResultCached( @Nonnull final String newLine )
    {
        final BasicHttpResponse resp = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        resp.setEntity(new StringEntity(readResourceFileClrf("BatchWriteResponseBody.txt", newLine)));
        resp.setHeader("Content-Type", "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef");

        // user code
        final List<List<Entry>> result = MultipartParser.ofHttpResponse(resp).toList();

        // assert behavior
        assertThat(result).isNotEmpty();

        for( final List<Entry> segments : result ) {
            for( final Entry segment : segments ) {
                // ignore
            }
        }
        assertThat(result).isNotEmpty();

        assertThat(result)
            .satisfiesExactly(
                segments1 -> assertThat(segments1)
                    .satisfiesExactly(
                        entry1 -> assertThat(entry1.getPayload()).contains("HTTP/1.1 201 Created"),
                        entry2 -> assertThat(entry2.getPayload()).contains("HTTP/1.1 201 Created")),
                segments2 -> assertThat(segments2)
                    .satisfiesExactly(entry1 -> assertThat(entry1.getPayload()).contains("HTTP/1.1 404 Not Found")));
    }

    @SneakyThrows
    private String readResourceFileClrf( final String resourceFileName, @Nonnull final String newLine )
    {
        final Class<MultipartParserTest> cl = MultipartParserTest.class;
        final URL resourceUrl = cl.getClassLoader().getResource(cl.getSimpleName() + "/" + resourceFileName);
        final String fileText = Resources.toString(resourceUrl, UTF_8);
        return fileText.replaceAll("\\R", newLine);
    }
}
