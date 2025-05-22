package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

class ODataNextLinkTest
{
    private static final String PAYLOAD_NEXT_LINK = """
        {
          "d": {
            "results": [],
            "__next": "/v1/foo/bar/endpoint?$skiptoken=s3cReT-t0k3n&foo=bar"
          }
        }
        """;

    @Test
    void testRemoveDuplicateQueryArguments()
    {
        final ODataRequestGeneric request =
            new ODataRequestRead("/v1/foo/bar/", "endpoint", "blub=42", ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(PAYLOAD_NEXT_LINK, ContentType.APPLICATION_JSON));

        final String baseUrl = "http://blub/?high=five";

        // case 1: query parameters are EQUAL in destination and in nextLink -> remove redundant query parameter
        final Destination dest1 = DefaultHttpDestination.builder(baseUrl).property("URL.queries.foo", "bar").build();
        final HttpClient client1 = HttpClientAccessor.getHttpClient(dest1);
        final ODataRequestResultGeneric result1 = new ODataRequestResultGeneric(request, httpResponse, client1);
        assertThat(result1.getNextLink()).contains("/v1/foo/bar/endpoint?$skiptoken=s3cReT-t0k3n");

        // case 2: query parameters are NOT EQUAL in destination and in nextLink -> retain query parameter
        final Destination dest2 = DefaultHttpDestination.builder(baseUrl).property("URL.queries.foo", "baz").build();
        final HttpClient client2 = HttpClientAccessor.getHttpClient(dest2);
        final ODataRequestResultGeneric result2 = new ODataRequestResultGeneric(request, httpResponse, client2);
        assertThat(result2.getNextLink()).contains("/v1/foo/bar/endpoint?$skiptoken=s3cReT-t0k3n&foo=bar");
    }

    @Test
    void testNotParsedNextLinkV4()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(PAYLOAD_NEXT_LINK, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getNextLink()).isEmpty();
    }

    @Test
    void testParsedNextLinkV2()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(PAYLOAD_NEXT_LINK, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getNextLink()).containsExactly("/v1/foo/bar/endpoint?$skiptoken=s3cReT-t0k3n&foo=bar");
        assertThat(result.getNextLink().flatMap(ODataUriFactory::extractSkipToken)).containsExactly("s3cReT-t0k3n");
    }

    @Test
    void testEmptySkipTokenV2()
    {
        final String emptyToken = "{\"d\": {\"__next\": \"/v1/foo/bar/endpoint?$skiptoken=&foo=bar\"}}";

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(emptyToken, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getNextLink()).containsExactly("/v1/foo/bar/endpoint?$skiptoken=&foo=bar");
        assertThat(result.getNextLink().flatMap(ODataUriFactory::extractSkipToken)).isEmpty();
    }

    @Test
    void testNoNextLinkV2()
    {
        final String noLink = "{\"d\": {}}";

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(noLink, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getNextLink()).isEmpty();
    }
}
