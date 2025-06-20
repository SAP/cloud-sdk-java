package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;

import lombok.SneakyThrows;

class ODataRequestResultGenericTest
{
    private static final Header[] headerWithEtag = { new BasicHeader("ETag", "value1") };
    private static final Header[] headerWithTwoEtags =
        { new BasicHeader("ETag", "value1"), new BasicHeader("ETag", "value2") };
    private static final Header[] headerWithEmptyEtag = { new BasicHeader("ETag", "") };
    private static final Header[] headerWithNullEtag = { new BasicHeader("ETag", null) };
    private static final Header[] headerWithoutEtag = {};

    @Test
    void testETagHeaderExtraction()
    {
        final ODataRequestGeneric mockClientRequest = mock(ODataRequestGeneric.class);
        when(mockClientRequest.getProtocol()).thenReturn(mock(ODataProtocol.class));

        final BasicHttpResponse httpResponse = new BasicHttpResponse(HTTP_1_1, 200, "Ok");
        final ODataRequestResultGeneric mockResult = new ODataRequestResultGeneric(mockClientRequest, httpResponse);

        assertSoftly(softly -> {
            httpResponse.setHeaders(headerWithEtag);
            softly.assertThat(mockResult.getVersionIdentifierFromHeader().get()).isEqualTo("value1");

            httpResponse.setHeaders(headerWithTwoEtags);
            softly.assertThat(mockResult.getVersionIdentifierFromHeader().get()).isEqualTo("value1");

            httpResponse.setHeaders(headerWithEmptyEtag);
            softly.assertThat(mockResult.getVersionIdentifierFromHeader()).isEmpty();

            httpResponse.setHeaders(headerWithNullEtag);
            softly.assertThat(mockResult.getVersionIdentifierFromHeader()).isEmpty();

            httpResponse.setHeaders(headerWithoutEtag);
            softly.assertThat(mockResult.getVersionIdentifierFromHeader()).isEmpty();
        });
    }

    @Test
    void testEmptyPayload()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        doReturn(ODataProtocol.V2).when(request).getProtocol();

        final HttpResponse mockHttpResponse = new BasicHttpResponse(HTTP_1_1, 200, "");
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, mockHttpResponse);

        assertThatThrownBy(() -> result.as(ODataRequestResultGenericTest.class))
            .isInstanceOf(ODataDeserializationException.class)
            .hasMessage("OData 2.0 response did not contain any payload.");
    }

    @SneakyThrows
    @Test
    void testBrokenHttpEntity()
    {
        // mock request
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        doReturn(ODataProtocol.V2).when(request).getProtocol();

        // mock broken HTTP entity
        final HttpEntity httpEntity = mock(HttpEntity.class);
        doReturn(1L).when(httpEntity).getContentLength();
        doReturn(false).when(httpEntity).isRepeatable();
        doThrow(SocketException.class).when(httpEntity).getContent();
        doThrow(SocketException.class).when(httpEntity).writeTo(any());

        // create HTTP response
        final BasicHttpResponse httpResponse = new BasicHttpResponse(HTTP_1_1, 200, "");
        httpResponse.setEntity(httpEntity);

        // create OData response
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        // provoke exception by internally parsing the HTTP response
        assertThatThrownBy(() -> result.as(ODataRequestResultGenericTest.class))
            .isInstanceOf(ODataDeserializationException.class)
            .hasMessageContaining("OData 2.0 response stream cannot be read for HTTP entity")
            .hasCauseInstanceOf(SocketException.class);
    }

    @Test
    void getHeaderValuesShouldHandleKeyInsensitivity()
    {
        final ODataRequestResult result = mock(ODataRequestResult.class);
        final HttpResponse mockedResponse = mock(HttpResponse.class);
        when(mockedResponse.getAllHeaders())
            .thenReturn(
                new BasicHeader[] {
                    new BasicHeader("CoNtEnT-TyPe", "someType"),
                    new BasicHeader("someKey", "someValue"),
                    new BasicHeader("someOtherKey", "someOtherValue") });

        when(result.getHttpResponse()).thenReturn(mockedResponse);
        when(result.getAllHeaderValues()).thenCallRealMethod();
        when(result.getHeaderValues(any())).thenCallRealMethod();

        assertThat(result.getHeaderValues("content-type")).containsExactly("someType");
        assertThat(result.getHeaderValues("Content-Type")).containsExactly("someType");

        assertThat(result.getHeaderValues("someKey")).containsExactly("someValue");
        assertThat(result.getHeaderValues("SOMEkey")).containsExactly("someValue");

        assertThat(result.getHeaderValues("someOtherKey")).containsExactly("someOtherValue");
        assertThat(result.getHeaderValues("SOMEotherKEY")).containsExactly("someOtherValue");
    }

    @Test
    @SneakyThrows
    void ensureNoRedundantHeadersForPaginatedRequests()
    {
        final ODataRequestGeneric oDataRequest =
            new ODataRequestRead("generic/service/path", "entity(123)", null, ODataProtocol.V4);

        final BasicHttpResponse httpResponse = new BasicHttpResponse(HTTP_1_1, 200, "OK");
        final String json = "{\"value\":[],\"@odata.nextLink\": \"Foo?$count=true&$select=BarID&$skiptoken='ABCD'\"}";
        httpResponse.setEntity(new StringEntity(json));

        final HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(any())).thenReturn(httpResponse);

        final ODataRequestResultGeneric testResult =
            new ODataRequestResultGeneric(oDataRequest, httpResponse, httpClient);

        ODataRequestResultGeneric nextResult = testResult.tryGetNextPage().get();
        nextResult = nextResult.tryGetNextPage().get();
        nextResult = nextResult.tryGetNextPage().get();
        nextResult = nextResult.tryGetNextPage().get();
        nextResult = nextResult.tryGetNextPage().get();
        nextResult = nextResult.tryGetNextPage().get();

        final Map<String, Collection<String>> lastRequestHeaders = nextResult.getODataRequest().getHeaders();
        assertThat(lastRequestHeaders).containsExactly(entry("Accept", Collections.singletonList("application/json")));
    }

    @Test
    @SneakyThrows
    void testDisabledBuffer()
    {
        // test setup for request
        final ODataRequestGeneric oDataRequest =
            new ODataRequestRead("generic/service/path", "entity(123)", null, ODataProtocol.V4);

        // test setup for streamed http response
        final BasicHttpResponse httpResponse = new BasicHttpResponse(HTTP_1_1, 200, "OK");
        final String json = "{\"value\":[]}";
        final InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        httpResponse.setEntity(new InputStreamEntity(inputStream, json.length()));

        // system under test
        final ODataRequestResultGeneric testResult = new ODataRequestResultGeneric(oDataRequest, httpResponse);
        testResult.disableBufferingHttpResponse();

        // sanity checks do not consume the response
        assertThat(testResult.getHeaderValues("Content-Length")).isEmpty();
        assertThat(testResult.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // true-positive, successfully read once
        assertThat(testResult.asListOfMaps()).isEmpty();

        // true-negative, no second read possible
        assertThatThrownBy(testResult::asListOfMaps)
            .isInstanceOf(ODataDeserializationException.class)
            .hasMessageContaining("Unable to read OData 4.0 response.");
    }
}
