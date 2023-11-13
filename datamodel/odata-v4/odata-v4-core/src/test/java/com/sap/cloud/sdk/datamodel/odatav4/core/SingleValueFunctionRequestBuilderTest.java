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
class SingleValueFunctionRequestBuilderTest
{
    private static final String DEFAULT_SERVICE_PATH = "/odata/default";
    private static final String ODATA_FUNCTION = "TestFunction";
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
        @SerializedName( "Name" )
        @JsonProperty( "Name" )
        private String name;
    }

    @Test
    void testFunctionQueryWithoutParameters()
    {
        final SingleValueFunctionRequestBuilder<Void> requestBuilder =
            new SingleValueFunctionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_FUNCTION, Void.class);

        assertThat(requestBuilder.toRequest().getRelativeUri())
            .hasToString(DEFAULT_SERVICE_PATH + '/' + ODATA_FUNCTION + "()");
    }

    @Test
    void testFunctionQueryWithMethodParameters()
    {
        final SingleValueFunctionRequestBuilder<Void> requestBuilder =
            new SingleValueFunctionRequestBuilder<>(
                DEFAULT_SERVICE_PATH,
                ODATA_FUNCTION,
                FUNCTION_PARAMETER_MAP,
                Void.class);

        assertThat(requestBuilder.toRequest().getRelativeUri())
            .hasToString(DEFAULT_SERVICE_PATH + '/' + ODATA_FUNCTION + FUNCTION_PARAMETERS.toEncodedString());
    }

    @Test
    void testFunctionQueryWithSpecialCharactersInMethodParameters()
    {
        final ODataFunctionParameters parameters =
            new ODataFunctionParameters(ODataProtocol.V4).addParameter("stringParameter", "t'est");
        final ODataResourcePath functionPath = ODataResourcePath.of(ODATA_FUNCTION, parameters);

        final SingleValueFunctionRequestBuilder<Void> requestBuilder =
            new SingleValueFunctionRequestBuilder<>(DEFAULT_SERVICE_PATH, functionPath, Void.class);

        assertThat(requestBuilder.toRequest().getRelativeUri())
            .hasToString(DEFAULT_SERVICE_PATH + '/' + ODATA_FUNCTION + ODATA_FUNCTION_PARAMETER_WITH_SPECIAL_CHARACTER);
    }

    @Test
    void testFunctionQueryWithMapParameters()
    {
        final SingleValueFunctionRequestBuilder<Void> requestBuilder =
            new SingleValueFunctionRequestBuilder<>(
                DEFAULT_SERVICE_PATH,
                ODATA_FUNCTION,
                FUNCTION_PARAMETER_MAP,
                Void.class);
        assertThat(requestBuilder.toRequest().getRelativeUri())
            .hasToString(DEFAULT_SERVICE_PATH + '/' + ODATA_FUNCTION + FUNCTION_PARAMETERS.toEncodedString());
    }

    @Test
    void testFunctionWithPrimitiveResponse()
    {

        stubFor(
            get(urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_FUNCTION + "()"))
                .willReturn(okJson("{" + "\"value\" : 3.14" + "}")));

        final SingleValueFunctionRequestBuilder<Float> requestBuilder =
            new SingleValueFunctionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_FUNCTION, Float.class);

        final Float actualResponse = requestBuilder.execute(destination);
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).isEqualTo(3.14f);
    }

    @Test
    void testFunctionWithStringResponse()
    {

        stubFor(
            get(urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_FUNCTION + "()"))
                .willReturn(okJson("{" + "\"value\" : \"Works\"" + "}")));

        final SingleValueFunctionRequestBuilder<String> requestBuilder =
            new SingleValueFunctionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_FUNCTION, String.class);

        final String actualResponse = requestBuilder.execute(destination);
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).isEqualTo("Works");
    }

    @Test
    void testFunctionWithEntityResponse()
    {

        stubFor(
            get(urlPathEqualTo(DEFAULT_SERVICE_PATH + '/' + ODATA_FUNCTION + "()"))
                .willReturn(okJson("{" + "\"Name\" : \"Tester\"" + "}")));

        final SingleValueFunctionRequestBuilder<TestEntity> requestBuilder =
            new SingleValueFunctionRequestBuilder<>(DEFAULT_SERVICE_PATH, ODATA_FUNCTION, TestEntity.class);

        final TestEntity actualResponse = requestBuilder.execute(destination);
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getName()).isEqualTo("Tester");
    }
}
