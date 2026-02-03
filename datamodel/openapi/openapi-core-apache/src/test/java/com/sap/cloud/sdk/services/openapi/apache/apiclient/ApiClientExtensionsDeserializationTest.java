package com.sap.cloud.sdk.services.openapi.apache.apiclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.services.openapi.apache.core.OpenApiRequestException;

import jdk.jfr.Description;
import lombok.Getter;
import lombok.Setter;

@WireMockTest
class ApiClientExtensionsDeserializationTest
{
    @Nonnull
    private static final String BASE_PATH = "http://localhost:8080";
    @Nonnull
    private static final String RELATIVE_PATH = "/outer";
    @Nonnull
    private static final String RESPONSE = """
        {
          "message": "Hello from the outer level.",
          "code": 1337,
          "inner": {
            "message": "Hello from the inner level.",
            "code": 7331,
            "innerComplexExtension": {
              "innerString": "inner",
              "innerInteger": 24
            },
            "innerListExtension": [
              "oof",
              "rab",
              "zab"
            ],
            "innerPrimitiveExtension": "additionalInnerValue"
          },
          "outerComplexExtension": {
            "outerString": "outer",
            "outerInteger": 42
          },
          "outerListExtension": [
            "foo",
            "bar",
            "baz"
          ],
          "outerPrimitiveExtension": "additionalOuterValue"
        }
        """;

    @Test
    @Description( "Tests deserialization of responses with nested extensions using the Apache HTTP client based ApiClient." )
    void testDeserializeResponseWithNestedExtensionsApache( WireMockRuntimeInfo wmInfo )
    {

        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo(RELATIVE_PATH)).willReturn(WireMock.okJson(RESPONSE)));
        final var apiClient = ApiClient.create().withBasePath(wmInfo.getHttpBaseUrl());

        final TestApacheApi api = new TestApacheApi(apiClient);
        final Outer result = api.getOuter();

        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo(RELATIVE_PATH)));
        assertResult(result);
    }

    private static void assertResult( Outer result )
    {
        assertThat(result.getMessage()).isEqualTo("Hello from the outer level.");
        assertThat(result.getCode()).isEqualTo(1337);
        assertThat(result.getCustomFieldNames())
            .containsExactlyInAnyOrder("outerComplexExtension", "outerListExtension", "outerPrimitiveExtension");
        assertThat(result.getCustomField("outerPrimitiveExtension")).isEqualTo("additionalOuterValue");
        assertThat(result.getCustomField("outerListExtension")).satisfies(outerList -> {
            assertThat(outerList).isInstanceOf(List.class);
            assertThat(outerList)
                .asInstanceOf(InstanceOfAssertFactories.list(String.class))
                .containsExactlyInAnyOrder("foo", "bar", "baz");
        });
        assertThat(result.getCustomField("outerComplexExtension")).satisfies(outerExtensions -> {
            assertThat(outerExtensions).isInstanceOf(Map.class);
            assertThat(outerExtensions)
                .asInstanceOf(InstanceOfAssertFactories.map(String.class, Object.class))
                .containsOnly(entry("outerString", "outer"), entry("outerInteger", 42));
        });

        assertThat(result.getInner()).satisfies(inner -> {
            assertThat(inner.getMessage()).isEqualTo("Hello from the inner level.");
            assertThat(inner.getCode()).isEqualTo(7331);
            assertThat(inner.getCustomFieldNames())
                .containsExactlyInAnyOrder("innerComplexExtension", "innerListExtension", "innerPrimitiveExtension");
            assertThat(inner.getCustomField("innerPrimitiveExtension")).isEqualTo("additionalInnerValue");
            assertThat(inner.getCustomField("innerListExtension")).satisfies(innerList -> {
                assertThat(innerList).isInstanceOf(List.class);
                assertThat(innerList)
                    .asInstanceOf(InstanceOfAssertFactories.list(String.class))
                    .containsExactlyInAnyOrder("oof", "rab", "zab");
            });
            assertThat(inner.getCustomField("innerComplexExtension")).satisfies(innerExtensions -> {
                assertThat(innerExtensions).isInstanceOf(Map.class);
                assertThat(innerExtensions)
                    .asInstanceOf(InstanceOfAssertFactories.map(String.class, Object.class))
                    .containsOnly(entry("innerString", "inner"), entry("innerInteger", 24));
            });
        });
    }

    private static class TestApacheApi extends BaseApi
    {
        public TestApacheApi( final ApiClient apiClient )
        {
            super(apiClient);
        }

        @Nonnull
        public Outer getOuter()
            throws OpenApiRequestException
        {
            final Object localVarPostBody = null;

            // create path and map variables
            final String localVarPath = RELATIVE_PATH;

            final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
            String localVarQueryParameterBaseName;
            final List<Pair> localVarQueryParams = new ArrayList<Pair>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
            final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
            final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

            final String[] localVarAccepts = { "application/json" };
            final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);

            final String[] localVarContentTypes = {

            };
            final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

            final TypeReference<Outer> localVarReturnType = new TypeReference<Outer>()
            {
            };
            return apiClient
                .invokeAPI(
                    localVarPath,
                    "GET",
                    localVarQueryParams,
                    localVarCollectionQueryParams,
                    localVarQueryStringJoiner.toString(),
                    localVarPostBody,
                    localVarHeaderParams,
                    localVarFormParams,
                    localVarAccept,
                    localVarContentType,
                    localVarReturnType);
        }
    }

    private static class Inner
    {
        @JsonProperty( "message" )
        @Getter
        @Setter
        private String message;

        @JsonProperty( "code" )
        @Getter
        @Setter
        private Integer code;

        @JsonAnySetter
        private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

        @Nonnull
        public Set<String> getCustomFieldNames()
        {
            return cloudSdkCustomFields.keySet();
        }

        @Nullable
        public Object getCustomField( @Nonnull final String name )
            throws NoSuchElementException
        {
            if( !cloudSdkCustomFields.containsKey(name) ) {
                throw new NoSuchElementException("Error has no field with name '" + name + "'.");
            }
            return cloudSdkCustomFields.get(name);
        }
    }

    private static class Outer
    {
        @JsonProperty( "message" )
        @Getter
        @Setter
        private String message;

        @JsonProperty( "code" )
        @Getter
        @Setter
        private Integer code;

        @JsonProperty( "inner" )
        @Getter
        @Setter
        private Inner inner;

        @JsonAnySetter
        private final Map<String, Object> cloudSdkCustomFields = new LinkedHashMap<>();

        @Nonnull
        public Set<String> getCustomFieldNames()
        {
            return cloudSdkCustomFields.keySet();
        }

        @Nullable
        public Object getCustomField( @Nonnull final String name )
            throws NoSuchElementException
        {
            if( !cloudSdkCustomFields.containsKey(name) ) {
                throw new NoSuchElementException("Error has no field with name '" + name + "'.");
            }
            return cloudSdkCustomFields.get(name);
        }
    }
}
