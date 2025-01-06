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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.net.UrlEscapers;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.expression.OrderExpression;
import com.sap.cloud.sdk.datamodel.odata.client.query.Order;
import com.sap.cloud.sdk.datamodel.odata.client.query.StructuredQuery;

class ODataRequestReadTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();
    private static final String ODATA_SERVICE_PATH = "/service/";
    private static final String ODATA_ENTITY_COLLECTION = "Entity";

    private WireMockServer wireMockServer;
    private Destination destination;

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
    void testQueryParameters()
    {
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);
        wireMockServer
            .stubFor(get(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ENTITY_COLLECTION)).willReturn(okJson("{}")));

        final String queryString = "$select=select1&$expand=expand1,expand2($select=nestedSelect;$top=10)&$top=1";

        final ODataRequestRead request =
            new ODataRequestRead(ODATA_SERVICE_PATH, ODATA_ENTITY_COLLECTION, queryString, ODataProtocol.V4);
        request.addQueryParameter("query-param1", "qp1");
        request.addQueryParameter("query-param2", "qp2");
        request.addHeader("header-key1", "hk1");
        // two headers with the same key
        request.addHeader("header-key1", "hk2");
        request.addHeader("header-key2", "hk2");
        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                getRequestedFor(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ENTITY_COLLECTION))
                    .withQueryParam("query-param1", equalTo("qp1"))
                    .withQueryParam("query-param2", equalTo("qp2"))
                    .withQueryParam("$expand", equalTo("expand1,expand2($select=nestedSelect;$top=10)"))
                    .withQueryParam("$select", equalTo("select1"))
                    .withQueryParam("$top", equalTo("1"))
                    .withHeader("header-key1", equalTo("hk1"))
                    // two headers with the same key
                    .withHeader("header-key1", equalTo("hk2"))
                    .withHeader("header-key2", equalTo("hk2"))
                    .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void testV4QueryExpand()
    {
        final StructuredQuery query = StructuredQuery.onEntity("Movies", ODataProtocol.V4);
        final StructuredQuery subQuery1 = StructuredQuery.asNestedQueryOnProperty("relatedBook", ODataProtocol.V4);
        final StructuredQuery subQuery2 = StructuredQuery.asNestedQueryOnProperty("relatedMovies", ODataProtocol.V4);
        final StructuredQuery subQuery3 = StructuredQuery.asNestedQueryOnProperty("relatedBook", ODataProtocol.V4);
        subQuery2.select(subQuery3);
        subQuery1.select(subQuery2);
        query.select(subQuery1);
        assertThat(query.getQueryString()).isEqualTo("$expand=relatedBook($expand=relatedMovies($expand=relatedBook))");
    }

    @Test
    void testV4QuerySelectAndFilter()
    {
        final StructuredQuery query = StructuredQuery.onEntity("Movies", ODataProtocol.V4);
        final StructuredQuery subQuery1 = StructuredQuery.asNestedQueryOnProperty("relatedBook", ODataProtocol.V4);
        final StructuredQuery subQuery2 = StructuredQuery.asNestedQueryOnProperty("relatedMovies", ODataProtocol.V4);
        subQuery1.select(subQuery2);
        query.select(subQuery1);

        subQuery2.select("Director", "Producer");
        subQuery2.filter(FieldReference.of("Duration").greaterThan(3600));
        query.filter(FieldReference.ofPath("relatedBook", "Name").equalTo("Forrest Gump"));

        assertThat(query.getQueryString().split("&"))
            .containsExactly(
                "$expand=relatedBook($expand=relatedMovies($select=Director,Producer;$filter=(Duration gt 3600)))",
                "$filter=(relatedBook/Name eq 'Forrest Gump')");

        assertThat(query.getEncodedQueryString().split("&"))
            .containsExactly(
                "$expand=relatedBook($expand=relatedMovies($select=Director,Producer;$filter=(Duration%20gt%203600)))",
                "$filter=(relatedBook/Name%20eq%20'Forrest%20Gump')");
    }

    @Test
    void testV2QueryExpand()
    {
        final StructuredQuery query = StructuredQuery.onEntity("Movies", ODataProtocol.V2);
        final StructuredQuery subQuery1 = StructuredQuery.asNestedQueryOnProperty("relatedBook", ODataProtocol.V2);
        final StructuredQuery subQuery2 = StructuredQuery.asNestedQueryOnProperty("relatedMovies", ODataProtocol.V2);
        final StructuredQuery subQuery3 = StructuredQuery.asNestedQueryOnProperty("relatedBook", ODataProtocol.V2);
        subQuery2.select(subQuery3);
        subQuery1.select(subQuery2);
        query.select(subQuery1);
        assertThat(query.getQueryString())
            .isEqualTo("$expand=relatedBook,relatedBook/relatedMovies,relatedBook/relatedMovies/relatedBook");
        /*
         * note: The query string above is semantically equivalent to this one without fully qualified properties:
         * $expand=relatedBook/relatedMovies/relatedBook
         */
    }

    @Test
    void testV2QuerySelectAndFilter()
    {
        final StructuredQuery query = StructuredQuery.onEntity("Movies", ODataProtocol.V2);
        final StructuredQuery subQuery1 = StructuredQuery.asNestedQueryOnProperty("relatedBook", ODataProtocol.V2);
        final StructuredQuery subQuery2 = StructuredQuery.asNestedQueryOnProperty("relatedMovies", ODataProtocol.V2);
        subQuery1.select(subQuery2);
        query.select(subQuery1);

        subQuery2.select("Director", "Producer");
        subQuery2.filter(FieldReference.of("Duration").greaterThan(3600));
        query.filter(FieldReference.ofPath("relatedBook", "Name").equalTo("Forrest Gump"));

        assertThat(query.getQueryString().split("&"))
            .containsExactly(
                "$select=relatedBook/relatedMovies/Director,relatedBook/relatedMovies/Producer",
                "$expand=relatedBook,relatedBook/relatedMovies",
                "$filter=(relatedBook/Name eq 'Forrest Gump')");

        assertThat(query.getEncodedQueryString().split("&"))
            .containsExactly(
                "$select=relatedBook/relatedMovies/Director,relatedBook/relatedMovies/Producer",
                "$expand=relatedBook,relatedBook/relatedMovies",
                "$filter=(relatedBook/Name%20eq%20'Forrest%20Gump')");
    }

    @Test
    void testExceptionWhenUnencodedQueryString()
    {
        final String servicePath = "/odata/v4/Service/";
        final String entityName = "Authors";

        final String unencodedQuery = "$orderby=name asc,ID";

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(
                () -> new ODataRequestRead(servicePath, entityName, unencodedQuery, ODataProtocol.V4).getRelativeUri());
    }

    @Test
    void testGuavaUrlEscaperEscapedQueryString()
    {
        final String servicePath = "/odata/v4/Service/";
        final String entityName = "Authors";

        final String unencodedQuery = "$orderby=name asc,ID";

        final String encodedQuery = UrlEscapers.urlFragmentEscaper().escape(unencodedQuery);

        final URI relativeUri =
            new ODataRequestRead(servicePath, entityName, encodedQuery, ODataProtocol.V4).getRelativeUri();

        final String expectedUri = "/odata/v4/Service/Authors?$orderby=name%20asc,ID";

        assertThat(relativeUri.toString()).isEqualTo(expectedUri);
    }

    @Test
    void testBuildQueryStringWithStructuredQueryV2()
    {
        final String servicePath = "/odata/v2/Service/";
        final String entityName = "Authors";

        final StructuredQuery structuredQuery =
            StructuredQuery
                .onEntity(entityName, ODataProtocol.V2)
                .filter(FieldReference.of("philosphy").equalTo("Yin & Yang"))
                .orderBy(OrderExpression.of("name", Order.ASC).and("ID", Order.ASC))
                .withInlineCount();

        final String encodedQuery = structuredQuery.getEncodedQueryString();

        final URI relativeUri =
            new ODataRequestRead(servicePath, entityName, encodedQuery, ODataProtocol.V2).getRelativeUri();

        final String expectedUri =
            "/odata/v2/Service/Authors?$filter=(philosphy%20eq%20'Yin%20%26%20Yang')&$orderby=name%20asc,ID%20asc&$inlinecount=allpages";

        assertThat(relativeUri.toString()).isEqualTo(expectedUri);
    }

    @Test
    void testBuildQueryStringWithStructuredQueryV4()
    {
        final String servicePath = "/odata/v4/Service/";
        final String entityName = "Authors";

        final StructuredQuery structuredQuery =
            StructuredQuery
                .onEntity(entityName, ODataProtocol.V4)
                .filter(FieldReference.of("philosphy").equalTo("Yin & Yang"))
                .orderBy(OrderExpression.of("name", Order.ASC).and("ID", Order.ASC))
                .withInlineCount();

        final String encodedQuery = structuredQuery.getEncodedQueryString();

        final URI relativeUri =
            new ODataRequestRead(servicePath, entityName, encodedQuery, ODataProtocol.V4).getRelativeUri();

        final String expectedUri =
            "/odata/v4/Service/Authors?$filter=(philosphy%20eq%20'Yin%20%26%20Yang')&$orderby=name%20asc,ID%20asc&$count=true";

        assertThat(relativeUri.toString()).isEqualTo(expectedUri);
    }

    @Test
    void testCustomParameterswithStructuredQuery()
    {
        final String entityName = "Authors";
        final String customKey = "$key";
        final String customValue = "$value";

        final StructuredQuery structuredQuery =
            StructuredQuery
                .onEntity(entityName, ODataProtocol.V4)
                .filter(FieldReference.of("philosophy").equalTo("Yin & Yang"))
                .withCustomParameter(customKey, customValue);

        final String actualQuery = structuredQuery.getEncodedQueryString();
        final String expectedQuery = "$filter=(philosophy%20eq%20'Yin%20%26%20Yang')&$key=%24value";

        assertThat(actualQuery).isEqualTo(expectedQuery);
    }

    @Test
    void testCustomParametersOnNestedQuery()
    {
        final StructuredQuery query = StructuredQuery.asNestedQueryOnProperty("name", ODataProtocol.V4);

        assertThatThrownBy(() -> query.withCustomParameter("key", "value")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testCustomParametersWithEmptyKey()
    {
        final StructuredQuery query = StructuredQuery.onEntity("name", ODataProtocol.V4);

        assertThatThrownBy(() -> query.withCustomParameter("", "value")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testConstructorWithStructuredQuery()
    {
        final StructuredQuery structuredQuery =
            StructuredQuery
                .onEntity("Authors", ODataProtocol.V4)
                .filter(FieldReference.of("philosphy").equalTo("Yin & Yang"))
                .orderBy(OrderExpression.of("name", Order.ASC).and("ID", Order.ASC))
                .withInlineCount();

        final ODataRequestRead expected =
            new ODataRequestRead(
                "/some/service/path",
                "Authors",
                structuredQuery.getEncodedQueryString(),
                ODataProtocol.V4);

        final ODataRequestRead actual =
            new ODataRequestRead("/some/service/path", new ODataResourcePath(), structuredQuery);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getQueryString()).isEqualTo(expected.getQueryString());
    }
}
