/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataFunctionParameters;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@WireMockTest
class CollectionValueFunctionRequestBuilderTest
{
    private static final String SERVICE_PATH = "/odata/default";
    private static final String FUNCTION_NAME = "TestFunction";
    private static final ODataFunctionParameters FUNCTION_PARAMETERS;
    private static final Map<String, Object> FUNCTION_PARAMETER_MAP = new HashMap<>();

    static {
        FUNCTION_PARAMETER_MAP.put("stringParameter", "test");
        FUNCTION_PARAMETER_MAP.put("booleanParameter", true);
        FUNCTION_PARAMETER_MAP.put("integerParameter", 9000);
        FUNCTION_PARAMETER_MAP.put("decimalParameter", 3.14);
        FUNCTION_PARAMETER_MAP.put("durationParameter", Duration.ofHours(8));
        FUNCTION_PARAMETER_MAP.put("dateTimeParameter", LocalDateTime.of(2019, 12, 25, 8, 0, 0));

        FUNCTION_PARAMETERS = ODataFunctionParameters.of(FUNCTION_PARAMETER_MAP, ODataProtocol.V4);
    }

    private static final String ODATA_FUNCTION_PARAMETER_WITH_SPECIAL_CHARACTER = "(stringParameter='t''est')";

    private DefaultHttpDestination destination;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
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
            return SERVICE_PATH;
        }

        @ElementName( "Name" )
        @SerializedName( "Name" )
        @JsonProperty( "Name" )
        private String name;
    }

    @Test
    void testFunctionQueryWithoutParameters()
    {
        final CollectionValueFunctionRequestBuilder<Void> requestBuilder =
            new CollectionValueFunctionRequestBuilder<>(SERVICE_PATH, FUNCTION_NAME, Void.class);

        assertThat(requestBuilder.toRequest().getRelativeUri()).hasToString(SERVICE_PATH + '/' + FUNCTION_NAME + "()");
    }

    @Test
    void testFunctionQueryWithMethodParameters()
    {
        final CollectionValueFunctionRequestBuilder<Void> requestBuilder =
            new CollectionValueFunctionRequestBuilder<Void>(
                SERVICE_PATH,
                FUNCTION_NAME,
                FUNCTION_PARAMETER_MAP,
                Void.class);

        assertThat(requestBuilder.toRequest().getRelativeUri())
            .hasToString(SERVICE_PATH + '/' + FUNCTION_NAME + FUNCTION_PARAMETERS.toEncodedString());
    }

    @Test
    void testFunctionQueryWithSpecialCharactersInMethodParameters()
    {
        final ODataFunctionParameters parameters =
            new ODataFunctionParameters(ODataProtocol.V4).addParameter("stringParameter", "t'est");
        final ODataResourcePath functionPath = ODataResourcePath.of(FUNCTION_NAME, parameters);

        final CollectionValueFunctionRequestBuilder<Void> requestBuilder =
            new CollectionValueFunctionRequestBuilder<>(SERVICE_PATH, functionPath, Void.class);

        assertThat(requestBuilder.toRequest().getRelativeUri())
            .hasToString(SERVICE_PATH + '/' + FUNCTION_NAME + ODATA_FUNCTION_PARAMETER_WITH_SPECIAL_CHARACTER);
    }

    @Test
    void testFunctionQueryWithMapParameters()
    {
        final CollectionValueFunctionRequestBuilder<Void> requestBuilder =
            new CollectionValueFunctionRequestBuilder<>(
                SERVICE_PATH,
                FUNCTION_NAME,
                FUNCTION_PARAMETER_MAP,
                Void.class);

        assertThat(requestBuilder.toRequest().getRelativeUri())
            .hasToString(SERVICE_PATH + '/' + FUNCTION_NAME + FUNCTION_PARAMETERS.toEncodedString());
    }

    @Test
    void testFunctionWithPrimitiveResponse()
    {
        stubFor(
            get(urlPathEqualTo(SERVICE_PATH + '/' + FUNCTION_NAME + "()"))
                .willReturn(okJson("{" + "\"value\" : [ 3.14f, 9.81f ]" + "}")));

        final CollectionValueFunctionRequestBuilder<Float> requestBuilder =
            new CollectionValueFunctionRequestBuilder<>(SERVICE_PATH, FUNCTION_NAME, Float.class);

        final List<Float> actualResponse = requestBuilder.execute(destination);
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).hasSize(2);
        assertThat(actualResponse).contains(3.14f, 9.81f);
    }

    @Test
    void testFunctionWithEntityResponse()
    {

        stubFor(
            get(urlPathEqualTo(SERVICE_PATH + '/' + FUNCTION_NAME + "()"))
                .willReturn(
                    okJson("{ \"value\" : [" + "{ \"Name\" : \"Tester1\" }," + "{ \"Name\" : \"Tester2\" }" + "]}")));

        final CollectionValueFunctionRequestBuilder<TestEntity> requestBuilder =
            new CollectionValueFunctionRequestBuilder<>(SERVICE_PATH, FUNCTION_NAME, TestEntity.class);

        final List<TestEntity> actualResponse = requestBuilder.execute(destination);
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).hasSize(2);
        assertThat(actualResponse).contains(new TestEntity("Tester1"), new TestEntity("Tester2"));
    }
}
