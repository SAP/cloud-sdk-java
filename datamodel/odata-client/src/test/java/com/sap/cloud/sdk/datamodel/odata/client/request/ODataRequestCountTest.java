/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.expression.OrderExpression;
import com.sap.cloud.sdk.datamodel.odata.client.query.Order;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

@WireMockTest
class ODataRequestCountTest
{
    private static final String SERVICE_PATH = "/some/path/SOME_API";
    private static final String ENTITY_NAME = "A_EntityName";
    private static final String FULL_URL = SERVICE_PATH + "/" + ENTITY_NAME + "/$count";
    private static final long EXPECTED_LONG_VALUE = 1691L;
    private static final String EXPECTED_STRING_VALUE = String.valueOf(EXPECTED_LONG_VALUE);

    private Destination httpDestination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        httpDestination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        stubFor(
            get(urlPathEqualTo(FULL_URL))
                .willReturn(ok(EXPECTED_STRING_VALUE).withHeader("Content-Type", "text/plain; charset=utf-8")));
    }

    @Test
    void testCountWithV2Protocol()
    {
        final ODataProtocol protocol = ODataProtocol.V2;

        final ODataRequestCount requestCount = new ODataRequestCount(SERVICE_PATH, ENTITY_NAME, "", protocol);

        final ODataRequestResultGeneric result =
            requestCount.execute(HttpClientAccessor.getHttpClient(httpDestination));

        final Long count = result.as(Long.class);

        assertThat(count).isEqualTo(EXPECTED_LONG_VALUE);
    }

    @Test
    void testCountWithV4Protocol()
    {
        final ODataProtocol protocol = ODataProtocol.V4;

        final ODataRequestCount requestCount = new ODataRequestCount(SERVICE_PATH, ENTITY_NAME, "", protocol);

        final ODataRequestResultGeneric result =
            requestCount.execute(HttpClientAccessor.getHttpClient(httpDestination));

        final Long count = result.as(Long.class);

        assertThat(count).isEqualTo(EXPECTED_LONG_VALUE);
    }

    @Test
    void testConstructorWithStructuredQuery()
    {
        final StructuredQuery structuredQuery =
            StructuredQuery
                .onEntity(ENTITY_NAME, ODataProtocol.V4)
                .filter(FieldReference.of("philosphy").equalTo("Yin & Yang"))
                .orderBy(OrderExpression.of("name", Order.ASC).and("ID", Order.ASC))
                .withInlineCount();

        final ODataRequestCount expected =
            new ODataRequestCount(SERVICE_PATH, ENTITY_NAME, structuredQuery.getEncodedQueryString(), ODataProtocol.V4);

        final ODataRequestCount actual = new ODataRequestCount(SERVICE_PATH, new ODataResourcePath(), structuredQuery);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getQueryString()).isEqualTo(expected.getQueryString());
    }
}
