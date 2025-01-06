/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.expression.OrderExpression;
import com.sap.cloud.sdk.datamodel.odata.client.query.Order;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

class ODataQueryReadByKeyTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();
    private static final String SERVICE_PATH = "/service/";
    private static final String ENTITY_COLLECTION = "Entity";
    private static final ODataEntityKey ENTITY_KEY = new ODataEntityKey(ODataProtocol.V4);
    /*  "("
          + "stringKey='stringValue',"
          + "booleanKey=true,"
          + "numberKey=9000,"
          + "durationKey=duration'PT8H',"
          + "dateTimeKey=2019-12-25T08:00:00Z"
          + ")";*/

    private WireMockServer wireMockServer;
    private Destination destination;

    static {
        ENTITY_KEY.addKeyProperty("stringKey", "stringValue");
        ENTITY_KEY.addKeyProperty("booleanKey", true);
        ENTITY_KEY.addKeyProperty("numberKey", 9000);
        ENTITY_KEY.addKeyProperty("durationKey", Duration.ofHours(8));
        ENTITY_KEY.addKeyProperty("dateTimeKey", LocalDateTime.of(2019, 12, 25, 8, 0, 0));
    }

    @BeforeEach
    void setup()
    {
        wireMockServer = new WireMockServer(WIREMOCK_CONFIGURATION);
        wireMockServer.start();
        destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();
    }

    @AfterEach
    void teardown()
    {
        wireMockServer.stop();
    }

    @Test
    void testRequestByEntityKey()
    {
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);
        wireMockServer
            .stubFor(get(urlPathEqualTo(SERVICE_PATH + ENTITY_COLLECTION + ENTITY_KEY)).willReturn(okJson("{}")));

        final ODataRequestReadByKey request =
            new ODataRequestReadByKey(SERVICE_PATH, ENTITY_COLLECTION, ENTITY_KEY, "", ODataProtocol.V4);

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                getRequestedFor(urlPathEqualTo(SERVICE_PATH + ENTITY_COLLECTION + ENTITY_KEY))
                    .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void testQueryParameters()
    {
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);
        wireMockServer
            .stubFor(get(urlPathEqualTo(SERVICE_PATH + ENTITY_COLLECTION + ENTITY_KEY)).willReturn(okJson("{}")));

        final String queryString = "$select=select1&$expand=expand1,expand2($select=nestedSelect;$top=10)";

        final ODataRequestReadByKey request =
            new ODataRequestReadByKey(SERVICE_PATH, ENTITY_COLLECTION, ENTITY_KEY, queryString, ODataProtocol.V4);
        request.addQueryParameter("query-param1", "qp1");
        request.addQueryParameter("query-param2", "qp2");
        request.addHeader("header-key1", "hk1");
        request.addHeader("header-key2", "hk2");
        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                getRequestedFor(urlPathEqualTo(SERVICE_PATH + ENTITY_COLLECTION + ENTITY_KEY))
                    .withQueryParam("query-param1", equalTo("qp1"))
                    .withQueryParam("query-param2", equalTo("qp2"))
                    .withQueryParam("$expand", equalTo("expand1,expand2($select=nestedSelect;$top=10)"))
                    .withQueryParam("$select", equalTo("select1"))
                    .withHeader("header-key1", equalTo("hk1"))
                    .withHeader("header-key2", equalTo("hk2"))
                    .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void testConstructorWithStructuredQuery()
    {
        final StructuredQuery structuredQuery =
            StructuredQuery
                .onEntity(ENTITY_COLLECTION, ODataProtocol.V4)
                .filter(FieldReference.of("philosphy").equalTo("Yin & Yang"))
                .orderBy(OrderExpression.of("name", Order.ASC).and("ID", Order.ASC))
                .withInlineCount();

        final ODataRequestReadByKey expected =
            new ODataRequestReadByKey(
                SERVICE_PATH,
                ENTITY_COLLECTION,
                ENTITY_KEY,
                structuredQuery.getEncodedQueryString(),
                ODataProtocol.V4);

        final ODataRequestReadByKey actual =
            new ODataRequestReadByKey(
                SERVICE_PATH,
                ODataResourcePath.of(ENTITY_COLLECTION),
                ENTITY_KEY,
                structuredQuery);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getQueryString()).isEqualTo(expected.getQueryString());
    }
}
