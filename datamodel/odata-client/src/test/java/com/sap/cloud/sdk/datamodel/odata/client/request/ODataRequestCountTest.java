/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.expression.OrderExpression;
import com.sap.cloud.sdk.datamodel.odata.client.query.Order;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

public class ODataRequestCountTest
{
    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    private static final String SERVICE_PATH = "/some/path/SOME_API";
    private static final String ENTITY_NAME = "A_EntityName";
    private static final String FULL_URL = SERVICE_PATH + "/" + ENTITY_NAME + "/$count";
    private static final long EXPECTED_LONG_VALUE = 1691L;
    private static final String EXPECTED_STRING_VALUE = String.valueOf(EXPECTED_LONG_VALUE);

    private Destination httpDestination;

    @Before
    public void before()
    {
        httpDestination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();

        wireMockServer
            .stubFor(
                get(urlPathEqualTo(FULL_URL))
                    .willReturn(ok(EXPECTED_STRING_VALUE).withHeader("Content-Type", "text/plain; charset=utf-8")));
    }

    @Test
    public void testCountWithV2Protocol()
    {
        final ODataProtocol protocol = ODataProtocol.V2;

        final ODataRequestCount requestCount = new ODataRequestCount(SERVICE_PATH, ENTITY_NAME, "", protocol);

        final ODataRequestResultGeneric result =
            requestCount.execute(HttpClientAccessor.getHttpClient(httpDestination));

        final Long count = result.as(Long.class);

        assertThat(count).isEqualTo(EXPECTED_LONG_VALUE);
    }

    @Test
    public void testCountWithV4Protocol()
    {
        final ODataProtocol protocol = ODataProtocol.V4;

        final ODataRequestCount requestCount = new ODataRequestCount(SERVICE_PATH, ENTITY_NAME, "", protocol);

        final ODataRequestResultGeneric result =
            requestCount.execute(HttpClientAccessor.getHttpClient(httpDestination));

        final Long count = result.as(Long.class);

        assertThat(count).isEqualTo(EXPECTED_LONG_VALUE);
    }

    @Test
    public void testConstructorWithStructuredQuery()
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
