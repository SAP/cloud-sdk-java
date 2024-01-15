/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
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

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity("{}", ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).isEmpty();
    }

    @Test
    void testEmptyDeltaLinkV4()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity("{}", ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).isEmpty();
    }

    @Test
    void testNotParsedDeltaLinkV2()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(PAYLOAD_DELTA_LINK, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).isEmpty();
    }

    @Test
    void testParsedDeltaLinkV4()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
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

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(emptyToken, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getDeltaLink()).containsExactly("/v1/foo/bar/endpoint?$deltatoken=&foo=bar");
        assertThat(result.getDeltaLink().flatMap(ODataUriFactory::extractDeltaToken)).isEmpty();
    }
}
