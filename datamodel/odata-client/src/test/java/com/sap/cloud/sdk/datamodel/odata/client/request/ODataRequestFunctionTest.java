/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
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

class ODataRequestFunctionTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();

    private static final String ODATA_SERVICE_PATH = "/service/";
    private static final String ODATA_FUNCTION = "TestFunction";
    private static final String ODATA_FUNCTION_PARAMETERS_V2 =
        """
        ?\
        stringParameter='foo/bar'&\
        booleanParameter=true&\
        integerParameter=9000&\
        doubleParameter=3.14d&\
        nullParameter=null&\
        durationParameter=duration'PT8H'&\
        dateTimeParameter=datetime'2019-12-25T08:00:00'\
        """;

    private static final String ODATA_FUNCTION_PARAMETERS_V4 =
        """
        (\
        stringParameter='foo%2Fbar',\
        booleanParameter=true,\
        integerParameter=9000,\
        doubleParameter=3.14,\
        nullParameter=null,\
        durationParameter=duration'PT8H',\
        dateTimeParameter=2019-12-25T08:00:00Z\
        )\
        """;

    private WireMockServer wireMockServer;
    private HttpClient client;

    @BeforeEach
    void setup()
    {
        wireMockServer = new WireMockServer(WIREMOCK_CONFIGURATION);
        wireMockServer.start();
        final Destination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();
        client = HttpClientAccessor.getHttpClient(destination);
    }

    @AfterEach
    void teardown()
    {
        wireMockServer.stop();
    }

    @Test
    void testFunctionWithoutParameters()
    {
        wireMockServer.stubFor(get(anyUrl()).willReturn(okJson("{}")));

        final ODataRequestFunction request =
            new ODataRequestFunction(
                ODATA_SERVICE_PATH,
                ODATA_FUNCTION,
                ODataFunctionParameters.empty(ODataProtocol.V4),
                ODataProtocol.V4);

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                getRequestedFor(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_FUNCTION + "()"))
                    .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void testFunctionWithParametersV2()
    {
        wireMockServer.stubFor(get(anyUrl()).willReturn(okJson("{}")));

        final ODataFunctionParameters functionParameters = new ODataFunctionParameters(ODataProtocol.V2);
        functionParameters.addParameter("stringParameter", "foo/bar");
        functionParameters.addParameter("booleanParameter", true);
        functionParameters.addParameter("integerParameter", 9000);
        functionParameters.addParameter("doubleParameter", 3.14d);
        functionParameters.addParameter("nullParameter", null);
        functionParameters.addParameter("durationParameter", Duration.ofHours(8));
        functionParameters.addParameter("dateTimeParameter", LocalDateTime.of(2019, 12, 25, 8, 0, 0));

        final ODataRequestFunction request =
            new ODataRequestFunction(ODATA_SERVICE_PATH, ODATA_FUNCTION, functionParameters, ODataProtocol.V2);

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                getRequestedFor(urlEqualTo(ODATA_SERVICE_PATH + ODATA_FUNCTION + ODATA_FUNCTION_PARAMETERS_V2))
                    .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void testFunctionWithParametersV4()
    {
        wireMockServer.stubFor(get(anyUrl()).willReturn(okJson("{}")));

        final ODataFunctionParameters functionParameters = new ODataFunctionParameters(ODataProtocol.V4);
        functionParameters.addParameter("stringParameter", "foo/bar");
        functionParameters.addParameter("booleanParameter", true);
        functionParameters.addParameter("integerParameter", 9000);
        functionParameters.addParameter("doubleParameter", 3.14d);
        functionParameters.addParameter("nullParameter", null);
        functionParameters.addParameter("durationParameter", Duration.ofHours(8));
        functionParameters.addParameter("dateTimeParameter", LocalDateTime.of(2019, 12, 25, 8, 0, 0));

        final ODataRequestFunction request =
            new ODataRequestFunction(ODATA_SERVICE_PATH, ODATA_FUNCTION, functionParameters, ODataProtocol.V4);

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                getRequestedFor(urlEqualTo(ODATA_SERVICE_PATH + ODATA_FUNCTION + ODATA_FUNCTION_PARAMETERS_V4))
                    .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void testAutomaticParameterHandling()
    {
        final ODataFunctionParameters parametersV2 =
            new ODataFunctionParameters(ODataProtocol.V2).addParameter("key", "val");
        final ODataFunctionParameters parametersV4 =
            new ODataFunctionParameters(ODataProtocol.V4).addParameter("key", "val");
        final String customQuery = "$foo=bar";
        final ODataResourcePath functionPath = ODataResourcePath.of("function");

        final ODataRequestFunction functionWithoutQueryV2 =
            new ODataRequestFunction("/path", functionPath, parametersV2, null, ODataProtocol.V2);
        final ODataRequestFunction functionWithQueryV2 =
            new ODataRequestFunction("/path", functionPath, parametersV2, customQuery, ODataProtocol.V2);
        final ODataRequestFunction functionWithoutQueryV4 =
            new ODataRequestFunction("/path", functionPath, parametersV4, null, ODataProtocol.V4);
        final ODataRequestFunction functionWithQueryV4 =
            new ODataRequestFunction("/path", functionPath, parametersV4, customQuery, ODataProtocol.V4);

        assertThat(functionWithoutQueryV2.getRelativeUri()).hasToString("/path/function?key='val'");
        assertThat(functionWithQueryV2.getRelativeUri()).hasToString("/path/function?key='val'&$foo=bar");
        assertThat(functionWithoutQueryV4.getRelativeUri()).hasToString("/path/function(key='val')");
        assertThat(functionWithQueryV4.getRelativeUri()).hasToString("/path/function(key='val')?$foo=bar");
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

        final ODataRequestFunction expected =
            new ODataRequestFunction(
                ODATA_SERVICE_PATH,
                ODataResourcePath.of(ODATA_FUNCTION).addSegment(structuredQuery.getEntityOrPropertyName()),
                structuredQuery.getEncodedQueryString(),
                ODataProtocol.V4);

        final ODataRequestFunction actual =
            new ODataRequestFunction(ODATA_SERVICE_PATH, ODataResourcePath.of(ODATA_FUNCTION), structuredQuery);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getRequestQuery()).isEqualTo(expected.getRequestQuery());
        assertThat(actual.getRelativeUri())
            .hasPath("/service/TestFunction/Authors")
            .hasParameter("$filter", "(philosphy eq 'Yin & Yang')")
            .hasParameter("$orderby", "name asc,ID asc")
            .hasParameter("$count", "true");
    }
}
