/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import javax.annotation.Nonnull;

import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.JsonSyntaxException;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.TrippinService;

@WireMockTest
class CountEntityUnitTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();

    private DefaultHttpDestination destination;
    private TrippinService service;

    private static final String COUNT_REQUEST_URL =
        String.format("%s/%s/$count", TrippinService.DEFAULT_SERVICE_PATH, "People");

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        service = new DefaultTrippinService().withServicePath(TrippinService.DEFAULT_SERVICE_PATH);
    }

    @Test
    void testSuccessfulCount()
    {
        stubFor(
            get(urlPathEqualTo(COUNT_REQUEST_URL))
                .withQueryParam("$filter", equalTo("contains(FirstName,'Bar')"))
                .withQueryParam("$search", equalTo("\"Foo\""))
                .willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "42")));

        final Long countResponse =
            service.countPeople().search("Foo").filter(Person.FIRST_NAME.contains("Bar")).execute(destination);

        verify(1, getRequestedFor(urlPathEqualTo(COUNT_REQUEST_URL)));

        assertThat(countResponse).isEqualTo(42L);
    }

    @Test
    void testFailCountNull()
    {
        stubFor(
            get(urlPathEqualTo(COUNT_REQUEST_URL))
                .willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "null")));

        assertThatCode(() -> service.countPeople().execute(destination))
            .isInstanceOf(ODataDeserializationException.class)
            .matches(e -> e.getCause().getMessage().contains("null"));
    }

    @Test
    void testFailCountEmpty()
    {
        stubFor(
            get(urlPathEqualTo(COUNT_REQUEST_URL))
                .willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "")));

        assertThatCode(() -> service.countPeople().execute(destination))
            .isInstanceOf(ODataDeserializationException.class)
            .matches(e -> e.getCause().getMessage().contains("null"));
    }

    @Test
    void testFailCountJson()
    {
        stubFor(get(urlPathEqualTo(COUNT_REQUEST_URL)).willReturn(okJson("{\"foo\":\"bar\"}")));
        assertThatCode(() -> service.countPeople().execute(destination))
            .isInstanceOf(ODataDeserializationException.class);
    }

    @Test
    void testFailCountNoInteger()
    {
        stubFor(
            get(urlPathEqualTo(COUNT_REQUEST_URL))
                .willReturn(okForContentType(ContentType.TEXT_PLAIN.getMimeType(), "a")));

        assertThatCode(() -> service.countPeople().execute(destination))
            .isInstanceOf(ODataDeserializationException.class)
            .hasCauseInstanceOf(JsonSyntaxException.class)
            .hasRootCauseExactlyInstanceOf(NumberFormatException.class);
    }
}
