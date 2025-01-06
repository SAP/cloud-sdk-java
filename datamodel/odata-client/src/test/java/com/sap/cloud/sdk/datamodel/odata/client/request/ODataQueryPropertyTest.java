/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static java.nio.charset.StandardCharsets.UTF_8;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.sap.cloud.sdk.datamodel.odata.client.request.UpdateStrategy.REPLACE_WITH_PUT;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;

import javax.annotation.Nonnull;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import lombok.SneakyThrows;

@WireMockTest
class ODataQueryPropertyTest
{
    private static final String SERVICE_URL = "/service";
    private static final String JSON = "application/json";
    private static final String XML = "application/xml";

    private static final ODataResourcePath resourceV2 =
        ODataResourcePath
            .of("Products", new ODataEntityKey(ODataProtocol.V2).addKeyProperty("Id", 0))
            .addSegment("Description");
    private static final ODataResourcePath resourceV4 =
        ODataResourcePath
            .of("Products", new ODataEntityKey(ODataProtocol.V4).addKeyProperty("Id", 0))
            .addSegment("Description");

    private HttpClient httpClient;

    @BeforeEach
    void setupHttpClient( @Nonnull final WireMockRuntimeInfo wm )
    {

        final Destination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        httpClient = HttpClientAccessor.getHttpClient(destination);
    }

    @Test
    void getSimplePropertyV2()
    {
        final String payloadV2 = "{\"d\":{\"Description\": \"Whole grain bread\"}}";
        stubFor(get(WireMock.anyUrl()).willReturn(okJson(payloadV2)));

        // user code
        final ODataRequestReadByKey request = new ODataRequestReadByKey(SERVICE_URL, resourceV2, "", ODataProtocol.V2);
        final ODataRequestResultGeneric result = request.execute(httpClient);

        // assertions
        verify(getRequestedFor(urlEqualTo(SERVICE_URL + resourceV2)));

        final String data = result.as(String.class);
        assertThat(data).isEqualTo("Whole grain bread");
    }

    @Test
    void setSimplePropertyV2()
    {
        stubFor(put(WireMock.anyUrl()).willReturn(noContent()));
        stubFor(head(WireMock.anyUrl()).willReturn(ok()));

        // user code
        final String payloadV2 = "{\"Description\": \"Whole grain bread\"}";
        final ODataRequestUpdate request =
            new ODataRequestUpdate(SERVICE_URL, resourceV2, payloadV2, REPLACE_WITH_PUT, null, ODataProtocol.V2);
        final ODataRequestResultGeneric result = request.execute(httpClient);

        // assertions
        verify(putRequestedFor(urlEqualTo(SERVICE_URL + resourceV2)).withHeader(CONTENT_TYPE, containing(JSON)));
        assertThat(result).isNotNull();
    }

    @Test
    void deleteSimplePropertyV2()
    {
        stubFor(delete(WireMock.anyUrl()).willReturn(noContent()));
        stubFor(head(WireMock.anyUrl()).willReturn(ok()));

        // user code
        final ODataRequestDelete request = new ODataRequestDelete(SERVICE_URL, resourceV2, null, ODataProtocol.V2);
        final ODataRequestResultGeneric result = request.execute(httpClient);

        // assertions
        verify(deleteRequestedFor(urlEqualTo(SERVICE_URL + "/Products(0)/Description")));
        assertThat(result).isNotNull();
    }

    @SneakyThrows
    @Test
    void getStreamPropertyV2()
    {
        final String payloadV2 = "<xml>This is a large document</xml>";
        final byte[] payloadBytes = payloadV2.getBytes(UTF_8);
        stubFor(get(WireMock.anyUrl()).willReturn(ok().withBody(payloadBytes).withHeader(CONTENT_TYPE, XML)));

        // user code
        final ODataRequestReadByKey request = new ODataRequestReadByKey(SERVICE_URL, resourceV2, "", ODataProtocol.V2);
        request.addHeader(ACCEPT, null);

        final ODataRequestResultGeneric result = request.execute(httpClient);

        // assertions
        verify(getRequestedFor(urlEqualTo(SERVICE_URL + resourceV2)));

        final HttpEntity entity = result.getHttpResponse().getEntity();
        assertThat(entity.getContentType().getValue()).isEqualTo(XML);
        assertThat(entity.getContent()).hasSameContentAs(new ByteArrayInputStream(payloadBytes));
    }

    @Test
    void setStreamPropertyV2()
    {
        stubFor(put(WireMock.anyUrl()).willReturn(noContent()));
        stubFor(head(WireMock.anyUrl()).willReturn(ok()));

        // user code
        final String payloadV2 = "<xml>This is a large document</xml>";
        final ByteArrayInputStream payloadBytes = new ByteArrayInputStream(payloadV2.getBytes(UTF_8));
        final HttpEntity httpEntity = new InputStreamEntity(payloadBytes, ContentType.create(XML, UTF_8));

        final ODataRequestResultGeneric result =
            new ODataRequestUpdate(SERVICE_URL, resourceV2, httpEntity, REPLACE_WITH_PUT, null, ODataProtocol.V2)
                .execute(httpClient);

        // assertions

        verify(
            putRequestedFor(urlEqualTo(SERVICE_URL + resourceV2))
                .withHeader(CONTENT_TYPE, equalTo("application/xml; charset=UTF-8"))
                .withRequestBody(equalTo(payloadV2)));
        assertThat(result).isNotNull();
    }

    @Test
    void getSimplePropertyV4()
    {
        final String payloadV4 =
            "{\"@odata.context\":\"https://services.odata.org/V4/OData/(S())/OData.svc/$metadata#Products(0)/Description\",\"value\":\"Whole grain bread\"}";
        stubFor(get(WireMock.anyUrl()).willReturn(okJson(payloadV4)));

        // user code
        final ODataRequestReadByKey request = new ODataRequestReadByKey(SERVICE_URL, resourceV4, "", ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);

        // assertions
        verify(getRequestedFor(urlEqualTo(SERVICE_URL + resourceV2)));

        final String data = result.as(String.class);
        assertThat(data).isEqualTo("Whole grain bread");
    }

    @Test
    void setSimplePropertyV4()
    {
        stubFor(put(WireMock.anyUrl()).willReturn(noContent()));
        stubFor(head(WireMock.anyUrl()).willReturn(ok()));

        // user code
        final String payloadV4 = "{\"value\": \"Whole grain bread\"}";
        final ODataRequestUpdate request =
            new ODataRequestUpdate(SERVICE_URL, resourceV4, payloadV4, REPLACE_WITH_PUT, null, ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);

        // assertions
        verify(putRequestedFor(urlEqualTo(SERVICE_URL + resourceV2)).withHeader(CONTENT_TYPE, containing(JSON)));
        assertThat(result).isNotNull();
    }

    @Test
    void deleteSimplePropertyV4()
    {
        stubFor(delete(WireMock.anyUrl()).willReturn(noContent()));
        stubFor(head(WireMock.anyUrl()).willReturn(ok()));

        // user code
        final ODataRequestDelete request = new ODataRequestDelete(SERVICE_URL, resourceV4, null, ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(httpClient);

        // assertions
        verify(deleteRequestedFor(urlEqualTo(SERVICE_URL + resourceV2)));
        assertThat(result).isNotNull();
    }

    @SneakyThrows
    @Test
    void getStreamPropertyV4()
    {
        final String payloadV4 = "<xml>This is a large document</xml>";
        final byte[] payloadBytes = payloadV4.getBytes(UTF_8);
        stubFor(get(WireMock.anyUrl()).willReturn(ok().withBody(payloadBytes).withHeader(CONTENT_TYPE, XML)));

        // user code
        final ODataRequestReadByKey request = new ODataRequestReadByKey(SERVICE_URL, resourceV4, "", ODataProtocol.V4);
        request.addHeader(ACCEPT, null);

        final ODataRequestResultGeneric result = request.execute(httpClient);

        // assertions
        verify(getRequestedFor(urlEqualTo(SERVICE_URL + resourceV4)));

        final HttpEntity entity = result.getHttpResponse().getEntity();
        assertThat(entity.getContentType().getValue()).isEqualTo(XML);
        assertThat(entity.getContent()).hasSameContentAs(new ByteArrayInputStream(payloadBytes));
    }

    @Test
    void setStreamPropertyV4()
    {
        stubFor(put(WireMock.anyUrl()).willReturn(noContent()));
        stubFor(head(WireMock.anyUrl()).willReturn(ok()));

        // user code
        final String payloadV4 = "<xml>This is a large document</xml>";
        final ByteArrayInputStream payloadBytes = new ByteArrayInputStream(payloadV4.getBytes(UTF_8));
        final HttpEntity httpEntity = new InputStreamEntity(payloadBytes, ContentType.create(XML, UTF_8));

        final ODataRequestResultGeneric result =
            new ODataRequestUpdate(SERVICE_URL, resourceV4, httpEntity, REPLACE_WITH_PUT, null, ODataProtocol.V4)
                .execute(httpClient);

        // assertions
        verify(
            putRequestedFor(urlEqualTo(SERVICE_URL + resourceV4))
                .withHeader(CONTENT_TYPE, equalTo("application/xml; charset=UTF-8"))
                .withRequestBody(equalTo(payloadV4)));
        assertThat(result).isNotNull();
    }
}
