/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.annotations.JsonAdapter;
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

@WireMockTest
class SingleValueActionRequestBuilderTest
{
    private static final String DEFAULT_SERVICE_PATH = "/odata/default";
    private static final String ODATA_ACTION = "TestAction";

    private DefaultHttpDestination destination;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        stubFor(head(anyUrl()).willReturn(ok()));
    }

    private static String readResourceFile( final String resourceFileName )
    {
        return TestUtility.readResourceFile(SingleValueActionRequestBuilderTest.class, resourceFileName);
    }

    @Data
    @EqualsAndHashCode( callSuper = true )
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
    @JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        @Getter
        private final String odataType = "com.sap.cloud.sdk.TestEntity";

        @Getter
        private final String entityCollection = "EntityCollection";

        @Getter
        private final Class<TestEntity> type = TestEntity.class;

        @Override
        protected String getDefaultServicePath()
        {
            return DEFAULT_SERVICE_PATH;
        }

        @ElementName( "Name" )
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
    public static class ComplexType extends VdmComplex<ComplexType>
    {

        @Getter
        private final String odataType = "com.sap.cloud.sdk.ComplexType";

        @Getter
        private final Class<ComplexType> type = ComplexType.class;

        @ElementName( "City" )
        private String city;

        @ElementName( "Country" )
        private String country;
    }

    @Test
    void testFunctionQueryWithParametersAndNoResponse()
    {
        final String actionRequestUrl = String.format("%s/%s", DEFAULT_SERVICE_PATH, ODATA_ACTION);
        final String actionRequestBody = readResourceFile("ActionParametersRequestBody.json");

        stubFor(post(urlPathEqualTo(actionRequestUrl)).willReturn(noContent()));

        final Map<String, Object> actionParameters = new LinkedHashMap<>();
        actionParameters.put("stringParameter", "test");
        actionParameters.put("booleanParameter", true);
        actionParameters.put("integerParameter", 9000);
        actionParameters.put("decimalParameter", 3.14d);
        actionParameters.put("durationParameter", Duration.ofHours(8));
        actionParameters
            .put("dateTimeOffsetParameter", OffsetDateTime.of(2020, 3, 12, 5, 2, 30, 0, ZoneOffset.of("Z")));
        actionParameters.put("timeOfDayParameter", LocalTime.of(13, 03, 39, 999000000));

        final SingleValueActionRequestBuilder<Void> sut =
            new SingleValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, actionParameters, Void.class);
        assertThat(sut.toRequest().getRelativeUri()).hasToString(actionRequestUrl);
        final ActionResponseSingle<Void> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))

                .withHeader("Content-Type", equalTo("application/json"))

                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult()).isEmpty();
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(204);
    }

    @Test
    void testActionWithNoParameterAndPrimitiveResponse()
    {
        final String actionRequestUrl = String.format("%s/%s", DEFAULT_SERVICE_PATH, ODATA_ACTION);
        final String actionRequestBody = readResourceFile("ActionNoParametersRequestBody.json");

        stubFor(post(urlPathEqualTo(actionRequestUrl)).willReturn(okJson("{" + "\"value\" : 3.14" + "}")));

        final SingleValueActionRequestBuilder<Float> sut =
            new SingleValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, Float.class);
        final ActionResponseSingle<Float> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(actionRequestUrl))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult().get()).isEqualTo(3.14f);
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(200);
    }

    @Test
    void testActionWithNoParameterAndStringResponse()
    {
        final String actionRequestUrl = String.format("%s/%s", DEFAULT_SERVICE_PATH, ODATA_ACTION);
        final String actionRequestBody = readResourceFile("ActionNoParametersRequestBody.json");

        stubFor(post(urlPathEqualTo(actionRequestUrl)).willReturn(okJson("{" + "\"value\" : \"Works\"" + "}")));

        final SingleValueActionRequestBuilder<String> sut =
            new SingleValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, String.class);
        final ActionResponseSingle<String> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_ACTION))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult().get()).isEqualTo("Works");
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(200);

    }

    @Test
    void testActionWithEntityParameterAndEntityResponse()
    {
        final String actionRequestUrl = String.format("%s/%s", DEFAULT_SERVICE_PATH, ODATA_ACTION);
        final String actionRequestBody = readResourceFile("ActionEntityRequestBody.json");

        stubFor(post(urlPathEqualTo(actionRequestUrl)).willReturn(okJson("{ \"Name\" : \"Tester\" }")));

        final Map<String, Object> actionParameters = new LinkedHashMap<>();
        final TestEntity testEntity = new TestEntity("Tester");
        actionParameters.put("entityParameter", testEntity);

        final SingleValueActionRequestBuilder<TestEntity> sut =
            new SingleValueActionRequestBuilder<>(
                DEFAULT_SERVICE_PATH,
                ODATA_ACTION,
                actionParameters,
                TestEntity.class);
        final ActionResponseSingle<TestEntity> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(actionRequestUrl))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult().get().getName()).isEqualTo("Tester");
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(200);
    }

    @Test
    void testActionWithComplexTypeParameterComplexTypeResponse()
    {
        final String actionRequestUrl = String.format("%s/%s", DEFAULT_SERVICE_PATH, ODATA_ACTION);
        final String actionRequestBody = readResourceFile("ActionComplexTypeRequestBody.json");

        stubFor(
            post(urlPathEqualTo(actionRequestUrl))
                .willReturn(okJson("{\"City\": \"Stockholm\",\"Country\": \"Sweden\"}")));

        final Map<String, Object> actionParameters = new LinkedHashMap<>();
        final ComplexType testComplexType = new ComplexType("Stockholm", "Sweden");
        actionParameters.put("complexEntity", testComplexType);

        final SingleValueActionRequestBuilder<ComplexType> sut =
            new SingleValueActionRequestBuilder<>(
                DEFAULT_SERVICE_PATH,
                ODATA_ACTION,
                actionParameters,
                ComplexType.class);
        final ActionResponseSingle<ComplexType> actualResponse = sut.execute(destination);

        verify(
            postRequestedFor(urlEqualTo(actionRequestUrl))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(actionRequestBody)));

        verify(
            1,
            headRequestedFor(urlEqualTo(DEFAULT_SERVICE_PATH))
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getResponseResult().get().getCity()).isEqualTo("Stockholm");
        assertThat(actualResponse.getResponseResult().get().getCountry()).isEqualTo("Sweden");
        assertThat(actualResponse.getResponseStatusCode()).isEqualTo(200);
    }

    @Test
    void testActionRequestWithoutCsrfTokenRetrievalIfSkipped()
    {
        final String actionRequestUrl = String.format("%s/%s", DEFAULT_SERVICE_PATH, ODATA_ACTION);

        stubFor(post(urlPathEqualTo(actionRequestUrl)).willReturn(okJson("{" + "\"value\" : 3.14" + "}")));

        final SingleValueActionRequestBuilder<Float> sut =
            new SingleValueActionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_ACTION, Float.class);

        sut.withoutCsrfToken().execute(destination);

        verify(
            0,
            headRequestedFor(anyUrl())
                .withHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase("fetch")));
    }
}
