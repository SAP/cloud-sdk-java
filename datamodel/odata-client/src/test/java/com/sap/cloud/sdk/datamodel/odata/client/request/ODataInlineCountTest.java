/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;

public class ODataInlineCountTest
{
    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Test
    public void testInlineCountODataV2()
    {
        final String response =
            readResourceFile(ODataInlineCountTest.class, "odata-v2-response-with-inline-count.json");
        stubFor(get(anyUrl()).willReturn(okJson(response)));

        final Destination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();

        final ODataRequestRead request =
            new ODataRequestRead(
                "V2/Northwind/Northwind.svc",
                "Customers",
                "$inlinecount=allpages&$format=json",
                ODataProtocol.V2);

        final ODataRequestResultGeneric result = request.execute(HttpClientAccessor.getHttpClient(destination));

        assertThat(result.getInlineCount()).isEqualTo(2);
    }

    @Test
    public void testInlineCountWithBufferedHttpEntityODataV2()
    {
        final String response =
            readResourceFile(ODataInlineCountTest.class, "odata-v2-response-with-inline-count.json");
        stubFor(get(anyUrl()).willReturn(okJson(response)));

        final Destination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();

        final ODataRequestRead request =
            new ODataRequestRead(
                "V2/Northwind/Northwind.svc",
                "Customers",
                "$inlinecount=allpages&$format=json",
                ODataProtocol.V2);

        final ODataRequestResultGeneric result = request.execute(HttpClientAccessor.getHttpClient(destination));
        assertThat(result.getInlineCount()).isEqualTo(2);
        assertThat(result.asMap()).containsKeys("results", "__count");
        assertThat(result.asMap().get("results")).isNotNull();
    }

    @Test
    public void testInlineCountODataV4()
    {
        final String response =
            readResourceFile(ODataInlineCountTest.class, "odata-v4-response-with-inline-count.json");
        stubFor(get(anyUrl()).willReturn(okJson(response)));

        final Destination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();

        final ODataRequestRead request =
            new ODataRequestRead("TripPinRESTierService", "People", "$count=true", ODataProtocol.V4);

        final ODataRequestResultGeneric result = request.execute(HttpClientAccessor.getHttpClient(destination));

        assertThat(result.getInlineCount()).isEqualTo(2);
    }

    @Test
    public void testInlineCountWithBufferedHttpEntityODataV4()
    {
        final String response =
            readResourceFile(ODataInlineCountTest.class, "odata-v4-response-with-inline-count.json");
        stubFor(get(anyUrl()).willReturn(okJson(response)));

        final Destination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();

        final ODataRequestRead request =
            new ODataRequestRead("TripPinRESTierService", "People", "$count=true", ODataProtocol.V4);

        final ODataRequestResultGeneric result = request.execute(HttpClientAccessor.getHttpClient(destination));
        assertThat(result.getInlineCount()).isEqualTo(2);
        assertThat(result.asMap()).containsKeys("value", "@odata.count");
        assertThat(result.asMap().get("value")).isNotNull();
    }

    @Test
    public void testNoInlineCountInResponseODataV4()
    {
        final String response =
            readResourceFile(ODataInlineCountTest.class, "odata-v4-response-without-inline-count.json");
        stubFor(get(anyUrl()).willReturn(okJson(response)));

        final Destination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();

        final ODataRequestRead request =
            new ODataRequestRead("TripPinRESTierService", "People", "$count=true", ODataProtocol.V4);

        final ODataRequestResultGeneric result = request.execute(HttpClientAccessor.getHttpClient(destination));

        assertThatExceptionOfType(ODataDeserializationException.class).isThrownBy(result::getInlineCount);
    }

    @Test
    public void testNoInlineCountInResponseODataV2()
    {
        final String response =
            readResourceFile(ODataInlineCountTest.class, "odata-v2-response-without-inline-count.json");
        stubFor(get(anyUrl()).willReturn(okJson(response)));

        final Destination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();

        final ODataRequestRead request =
            new ODataRequestRead(
                "V2/Northwind/Northwind.svc",
                "Customers",
                "$inlinecount=allpages&$format=json",
                ODataProtocol.V2);

        final ODataRequestResultGeneric result = request.execute(HttpClientAccessor.getHttpClient(destination));

        assertThatExceptionOfType(ODataDeserializationException.class).isThrownBy(result::getInlineCount);
    }

    private static String readResourceFile( final Class<?> cls, final String resourceFileName )
    {
        try {
            final URL resourceUrl = cls.getClassLoader().getResource(cls.getSimpleName() + "/" + resourceFileName);
            return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        }
        catch( final IOException e ) {
            throw new IllegalStateException(e);
        }
    }
}
