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

class ODataNextLinkTest
{
    private static final String PAYLOAD_NEXT_LINK =
        """
        {\
          "d": {\
            "results": [],\
            "__next": "/v1/foo/bar/endpoint?$skiptoken=s3cReT-t0k3n&foo=bar"\
          }\
        }\
        """;

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
