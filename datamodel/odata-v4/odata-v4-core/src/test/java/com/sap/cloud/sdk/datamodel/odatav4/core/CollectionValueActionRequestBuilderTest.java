/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odatav4.TestUtility;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class CollectionValueActionRequestBuilderTest
{
    private static final String DEFAULT_SERVICE_PATH = "/odata/default";
    private static final String ODATA_ACTION = "TestAction";

    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    private DefaultHttpDestination destination;

    @Before
    public void setup()
    {
        destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();
        wireMockServer.stubFor(head(anyUrl()).willReturn(ok()));
    }

    private static String readResourceFile( final String resourceFileName )
    {
        return TestUtility.readResourceFile(CollectionValueActionRequestBuilderTest.class, resourceFileName);
    }

    @Data
    @EqualsAndHashCode( callSuper = true )
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
    @JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
    public static class TestEntity extends VdmEntity<SingleValueFunctionRequestBuilderTest.TestEntity>
    {
        @Getter
        private final String odataType = "com.sap.cloud.sdk.TestEntity";

        @Getter
        private final String entityCollection = "EntityCollection";

        @Getter
        private final Class<SingleValueFunctionRequestBuilderTest.TestEntity> type =
            SingleValueFunctionRequestBuilderTest.TestEntity.class;

        @Override
        protected String getDefaultServicePath()
        {
            return DEFAULT_SERVICE_PATH;
        }

        @ElementName( "Name" )
        @SerializedName( "Name" )
        @JsonProperty( "Name" )
        private String name;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString( doNotUseGetters = true, callSuper = true )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
    @JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
    public static class ComplexType extends VdmComplex<SingleValueActionRequestBuilderTest.ComplexType>
    {

        @Getter
        private final String odataType = "com.sap.cloud.sdk.ComplexType";

        @Getter
        private final Class<SingleValueActionRequestBuilderTest.ComplexType> type =
            SingleValueActionRequestBuilderTest.ComplexType.class;

        @ElementName( "City" )
        private String city;

        @ElementName( "Country" )
        private String country;
    }

    @Test
    public void testActionWithPrimitiveResponse()
    {
        wireMockServer
            .stubFor(
                post(urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                    .willReturn(okJson("{\"value\" : [ 3.14, 9.81 ]}")));

        final CollectionValueActionRequestBuilder<Float> sut =
            new CollectionValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, Float.class);
        final String actionRequestBody = readResourceFile("ActionNoParametersRequestBody.json");

        final ActionResponseCollection<Float> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult().get()).hasSize(2);
        assertThat(actualResponse.getResponseResult().get()).contains(3.14f, 9.81f);
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(200);
    }

    @Test
    public void testActionWithStringResponse()
    {
        wireMockServer
            .stubFor(
                post(urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                    .willReturn(okJson("{" + "\"value\" : [ \"It\", \"works\",\"as\",\"expected\" ]" + "}")));

        final CollectionValueActionRequestBuilder<String> sut =
            new CollectionValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, String.class);
        final String actionRequestBody = readResourceFile("ActionNoParametersRequestBody.json");

        final ActionResponseCollection<String> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult().get()).hasSize(4);
        assertThat(actualResponse.getResponseResult().get()).contains("It", "works", "as", "expected");
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(200);
    }

    @Test
    public void testActionWithComplexTypeResponse()
    {
        wireMockServer
            .stubFor(
                post(urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                    .willReturn(
                        okJson(
                            "{ \"value\" : ["
                                + "{ \"City\" : \"Stockholm\" ,\"Country\" : \"Sweden\"},"
                                + "{ \"City\" : \"Dubrovnik\",\"Country\" : \"Croatia\" }"
                                + "]}")));

        final CollectionValueActionRequestBuilder<ComplexType> sut =
            new CollectionValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, ComplexType.class);
        final String actionRequestBody = readResourceFile("ActionNoParametersRequestBody.json");
        final ActionResponseCollection<ComplexType> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult().get()).hasSize(2);
        assertThat(actualResponse.getResponseResult().get())
            .contains(new ComplexType("Stockholm", "Sweden"), new ComplexType("Dubrovnik", "Croatia"));
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(200);
    }

    @Test
    public void testActionWithEntityResponse()
    {
        wireMockServer
            .stubFor(
                post(urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                    .willReturn(
                        okJson(
                            "{ \"value\" : [" + "{ \"Name\" : \"Tester1\" }," + "{ \"Name\" : \"Tester2\" }" + "]}")));

        final CollectionValueActionRequestBuilder<TestEntity> sut =
            new CollectionValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, TestEntity.class);
        final String actionRequestBody = readResourceFile("ActionNoParametersRequestBody.json");
        final ActionResponseCollection<TestEntity> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult().get()).hasSize(2);
        assertThat(actualResponse.getResponseResult().get())
            .contains(new TestEntity("Tester1"), new TestEntity("Tester2"));
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(200);
    }

    @Test
    public void testActionWithoutCsrfTokenRetrievalIfSkipped()
    {
        wireMockServer
            .stubFor(
                post(urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                    .willReturn(okJson("{\"value\" : [ 3.14, 9.81 ]}")));

        final CollectionValueActionRequestBuilder<Float> sut =
            new CollectionValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, Float.class);

        sut.withoutCsrfToken().execute(destination);

        verify(
            0,
            headRequestedFor(anyUrl())
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase("fetch")));
    }
}
