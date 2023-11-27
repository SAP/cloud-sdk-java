/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@WireMockTest
class FunctionImportSingleDeserializationTest
{
    private static final String SERVICE_PATH = "/some/path/service";
    private static final String FUNCTION_NAME = "SomeFunction";

    @ParameterizedTest
    @MethodSource
    void testDeserializeSingleResponse( @Nonnull final TestInput<?> testInput, @Nonnull final WireMockRuntimeInfo wm )
    {
        stub(testInput);

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final Object actualReturnValue = testInput.functionSingleGet().executeSingle(destination);

        if( testInput.expectedReturnValue == null ) {
            assertThat(actualReturnValue).isNull();
        } else {
            assertThat(actualReturnValue).isEqualTo(testInput.expectedReturnValue);
        }
    }

    @Nonnull
    static Stream<TestInput<?>> testDeserializeSingleResponse()
    {
        return Stream
            .of(
                new TestInput<>(
                    String.class,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<!DOCTYPE user-permission PUBLIC \"-//SuccessFactors, Inc.//DTD Permission Config//EN\" \"users-permissions.dtd\">\n"
                        + "<user-permission>\n"
                        + "<user id=\"sfadmin@SAPPHIRE17\"/>\n"
                        + "</user-permission>\n",
                    "{\"d\":{\""
                        + FUNCTION_NAME
                        + "\":\""
                        + "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n"
                        + "<!DOCTYPE user-permission PUBLIC \\\"-//SuccessFactors, Inc.//DTD Permission Config//EN\\\" \\\"users-permissions.dtd\\\">\\n"
                        + "<user-permission>\\n"
                        + "<user id=\\\"sfadmin@SAPPHIRE17\\\"/>\\n"
                        + "</user-permission>\\n\"}}"),
                new TestInput<>(Boolean.class, true, "{\"d\":{\"" + FUNCTION_NAME + "\":true}}"),
                new TestInput<>(Long.class, 42L, "{\"d\":{\"" + FUNCTION_NAME + "\":\"42\"}}"),
                new TestInput<>(Void.class, null, null),
                new TestInput<>(
                    TestingEntity.class,
                    new TestingEntity("id", "text"),
                    "{\"d\":{\"" + FUNCTION_NAME + "\":{\"id\":\"id\",\"text\":\"text\"}}}"));
    }

    private void stub( @Nonnull final TestInput<?> testInput )
    {
        stubFor(head(urlPathEqualTo(SERVICE_PATH)).willReturn(ok().withHeader("x-csrf-token", "some-csrf-token")));

        final ResponseDefinitionBuilder responseBuilder;
        if( testInput.serializedReturnValue == null ) {
            responseBuilder = noContent();
        } else {
            responseBuilder =
                ok()
                    .withBody(testInput.serializedReturnValue)
                    .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        }

        stubFor(get(urlPathEqualTo(SERVICE_PATH + "/" + FUNCTION_NAME)).willReturn(responseBuilder));
    }

    @AllArgsConstructor
    @Getter
    private static class TestInput<T>
    {
        @Nonnull
        private Class<? extends T> returnValueType;
        @Nullable
        private T expectedReturnValue;
        @Nullable
        private String serializedReturnValue;

        @Nonnull
        public FluentHelperFunction<?, ? extends T, ? extends T> functionSingleGet()
        {
            return FluentHelperFactory
                .withServicePath(SERVICE_PATH)
                .functionSingleGet(Collections.emptyMap(), FUNCTION_NAME, returnValueType);
        }

        @Override
        public String toString()
        {
            return "Single Function import with " + returnValueType.getSimpleName() + " return type";
        }
    }

    @EqualsAndHashCode( callSuper = true )
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class TestingEntity extends VdmEntity<TestingEntity>
    {
        @Getter( AccessLevel.PROTECTED )
        private final String entityCollection = "TestingCollection";

        @Getter
        private final Class<TestingEntity> type = TestingEntity.class;

        @SerializedName( "id" )
        @ODataField( odataName = "id" )
        private String id;

        @SerializedName( "text" )
        @ODataField( odataName = "text" )
        private String text;
    }
}
