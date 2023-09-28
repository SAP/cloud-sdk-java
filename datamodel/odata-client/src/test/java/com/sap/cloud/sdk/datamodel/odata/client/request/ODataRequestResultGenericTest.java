package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.SocketException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
    public void getHeaderValuesShouldHandleKeyInsensitivity()
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
}
