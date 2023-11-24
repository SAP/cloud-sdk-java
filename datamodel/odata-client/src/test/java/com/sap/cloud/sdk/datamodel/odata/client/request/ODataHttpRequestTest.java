/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

class ODataHttpRequestTest
{

    private static final String SERVICE_PATH = "/service/";
    private static final String ENTITY_COLLECTION = "Entity";
    private static final ODataEntityKey ENTITY_KEY =
        new ODataEntityKey(ODataProtocol.V4).addKeyProperty("EntityKey", "key");
    private static final String QUERY_STRING = "$select=select1&$top=1";
    private final HttpClient httpClient = Mockito.mock(HttpClient.class);

    @Test
    void testAcceptHeaderForRead()
        throws IOException
    {
        final ODataRequestRead odataRequest =
            new ODataRequestRead(SERVICE_PATH, ENTITY_COLLECTION, QUERY_STRING, ODataProtocol.V2);

        final ODataHttpRequest httpRequest = ODataHttpRequest.withoutBody(odataRequest, httpClient);
        httpRequest.requestGet();

        final ArgumentCaptor<HttpRequestBase> argumentCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        Mockito.verify(httpClient).execute(argumentCaptor.capture());

        final Header[] acceptHeader = argumentCaptor.getValue().getHeaders("Accept");
        assertThat(acceptHeader).isNotEmpty();
        assertThat(acceptHeader[0].getValue()).isEqualTo("application/json");
    }

    @Test
    void testAcceptHeaderForReadByKey()
        throws IOException
    {
        final ODataRequestReadByKey odataRequest =
            new ODataRequestReadByKey(SERVICE_PATH, ENTITY_COLLECTION, ENTITY_KEY, QUERY_STRING, ODataProtocol.V2);

        final ODataHttpRequest httpRequest = ODataHttpRequest.withoutBody(odataRequest, httpClient);
        httpRequest.requestGet();

        final ArgumentCaptor<HttpRequestBase> argumentCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        Mockito.verify(httpClient).execute(argumentCaptor.capture());

        final Header[] acceptHeader = argumentCaptor.getValue().getHeaders("Accept");
        assertThat(acceptHeader).isNotEmpty();
        assertThat(acceptHeader[0].getValue()).isEqualTo("application/json");
    }

    @Test
    void testCustomAcceptHeaderPrecedesDefaultAcceptHeader()
        throws IOException
    {
        final ODataRequestReadByKey odataRequest =
            new ODataRequestReadByKey(SERVICE_PATH, ENTITY_COLLECTION, ENTITY_KEY, QUERY_STRING, ODataProtocol.V2);
        odataRequest.setHeader(HttpHeaders.ACCEPT, ODataFormat.XML.getHttpAccept());

        final ODataHttpRequest httpRequest = ODataHttpRequest.withoutBody(odataRequest, httpClient);

        httpRequest.requestGet();

        final ArgumentCaptor<HttpRequestBase> argumentCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        Mockito.verify(httpClient).execute(argumentCaptor.capture());

        final Header[] acceptHeader = argumentCaptor.getValue().getHeaders("Accept");
        assertThat(acceptHeader).isNotEmpty();
        assertThat(acceptHeader[0].getValue()).isEqualTo("application/xml");
    }

    @Test
    void testAddCustomHeaderExtendsExistingHeaders()
        throws IOException
    {
        final ODataRequestReadByKey odataRequest =
            new ODataRequestReadByKey(SERVICE_PATH, ENTITY_COLLECTION, ENTITY_KEY, QUERY_STRING, ODataProtocol.V2);
        // this header will be added with the existing Accept application/json header, because we used addHeader and not setHeader
        odataRequest.addHeader(HttpHeaders.ACCEPT, ODataFormat.XML.getHttpAccept());

        final ODataHttpRequest httpRequest = ODataHttpRequest.withoutBody(odataRequest, httpClient);

        httpRequest.requestGet();

        final ArgumentCaptor<HttpRequestBase> argumentCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        Mockito.verify(httpClient).execute(argumentCaptor.capture());

        final Header[] acceptHeader = argumentCaptor.getValue().getHeaders("Accept");
        assertThat(acceptHeader).isNotEmpty();
        assertThat(acceptHeader[0].getValue()).isEqualTo("application/json");
        assertThat(acceptHeader[1].getValue()).isEqualTo("application/xml");
    }

}
