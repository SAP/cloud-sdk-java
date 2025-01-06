/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.google.gson.GsonBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

class ODataRequestActionTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();

    private static final String ODATA_SERVICE_PATH = "/service/";
    private static final String ODATA_ACTION = "TestAction";

    @RegisterExtension
    static final WireMockExtension wireMockServer =
        WireMockExtension.newInstance().options(WIREMOCK_CONFIGURATION).build();
    private HttpClient client;

    @BeforeEach
    void setup()
    {
        final Destination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();
        client = HttpClientAccessor.getHttpClient(destination);
    }

    @Test
    void testActionWithoutParameters()
    {
        wireMockServer.stubFor(post(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ACTION)).willReturn(noContent()));

        final ODataRequestAction request =
            new ODataRequestAction(ODATA_SERVICE_PATH, ODATA_ACTION, null, ODataProtocol.V4);

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                postRequestedFor(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ACTION))
                    .withHeader("Content-Type", equalTo("application/json")));

        wireMockServer
            .verify(
                1,
                headRequestedFor(urlPathEqualTo(ODATA_SERVICE_PATH))
                    .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));
    }

    @Test
    void testActionWithParameters()
    {
        wireMockServer.stubFor(post(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ACTION)).willReturn(noContent()));

        final Map<String, Object> actionParameters = new LinkedHashMap<>();
        actionParameters.put("stringParameter", "test");
        actionParameters.put("booleanParameter", true);
        actionParameters.put("integerParameter", 9000);
        actionParameters.put("decimalParameter", 3.14d);
        actionParameters.put("nullParameter", null);
        final ODataRequestAction request =
            new ODataRequestAction(
                ODATA_SERVICE_PATH,
                ODATA_ACTION,
                new GsonBuilder().serializeNulls().create().toJson(actionParameters),
                ODataProtocol.V4);

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                postRequestedFor((urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ACTION)))
                    .withHeader("Content-Type", equalTo("application/json")));

        wireMockServer
            .verify(
                1,
                headRequestedFor(urlPathEqualTo(ODATA_SERVICE_PATH))
                    .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));
    }

    @Test
    void testBoundAction()
    {
        final ODataResourcePath actionPath = ODataResourcePath.of("Entity").addSegment(ODATA_ACTION);

        final ODataRequestAction requestAction =
            new ODataRequestAction(ODATA_SERVICE_PATH, actionPath, "{}", ODataProtocol.V4);

        assertThat(requestAction.getRelativeUri()).hasToString(ODATA_SERVICE_PATH + "Entity/" + ODATA_ACTION);
    }

    @Test
    void testActionWithoutCsrfTokenIfSkipped()
    {
        wireMockServer.stubFor(post(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ACTION)).willReturn(noContent()));

        final ODataRequestAction request =
            new ODataRequestAction(ODATA_SERVICE_PATH, ODataResourcePath.of(ODATA_ACTION), null, ODataProtocol.V4);

        request.setCsrfTokenRetriever(CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER);

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer
            .verify(
                0,
                headRequestedFor(anyUrl())
                    .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase("fetch")));
    }
}
