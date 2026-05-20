package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

class ODataDeltaLinkTest
{
    private static final String PAYLOAD_DELTA_LINK = """
        {
          "@odata.context": "$metadata#FooBar",
          "value": [],
          "@odata.deltaLink": "/v1/foo/bar/endpoint?$deltatoken=s3cReT-t0k3n&foo=bar"
        }
        """;

    @Test
    void testEmptyDeltaLinkV2()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final BasicClassicHttpResponse httpResponse = new BasicClassicHttpResponse(200, "Ok");
        httpResponse.setVersion(HttpVersion.HTTP_1_1);
        httpResponse.setEntity(new StringEntity("{}", ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).isEmpty();
    }

    @Test
    void testEmptyDeltaLinkV4()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final BasicClassicHttpResponse httpResponse = new BasicClassicHttpResponse(200, "Ok");
        httpResponse.setVersion(HttpVersion.HTTP_1_1);
        httpResponse.setEntity(new StringEntity("{}", ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).isEmpty();
    }

    @Test
    void testNotParsedDeltaLinkV2()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final BasicClassicHttpResponse httpResponse = new BasicClassicHttpResponse(200, "Ok");
        httpResponse.setVersion(HttpVersion.HTTP_1_1);
        httpResponse.setEntity(new StringEntity(PAYLOAD_DELTA_LINK, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).isEmpty();
    }

    @Test
    void testParsedDeltaLinkV4()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final BasicClassicHttpResponse httpResponse = new BasicClassicHttpResponse(200, "Ok");
        httpResponse.setVersion(HttpVersion.HTTP_1_1);
        httpResponse.setEntity(new StringEntity(PAYLOAD_DELTA_LINK, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).containsExactly("/v1/foo/bar/endpoint?$deltatoken=s3cReT-t0k3n&foo=bar");
        assertThat(result.getDeltaLink().flatMap(ODataUriFactory::extractDeltaToken)).containsExactly("s3cReT-t0k3n");
    }

    @Test
    void testEmptyDeltaTokenV4()
    {
        final String emptyToken = "{\"@odata.deltaLink\": \"/v1/foo/bar/endpoint?$deltatoken=&foo=bar\"}";

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final BasicClassicHttpResponse httpResponse = new BasicClassicHttpResponse(200, "Ok");
        httpResponse.setVersion(HttpVersion.HTTP_1_1);
        httpResponse.setEntity(new StringEntity(emptyToken, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).containsExactly("/v1/foo/bar/endpoint?$deltatoken=&foo=bar");
        assertThat(result.getDeltaLink().flatMap(ODataUriFactory::extractDeltaToken)).isEmpty();
    }
}
