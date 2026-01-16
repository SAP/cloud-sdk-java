package com.sap.cloud.sdk.services.openapi.apiclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.Pair;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

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
    void testDeserializeResponseWithNestedExtensions()
    {
        final ApiClient apiClient = new ApiClient().setBasePath(BASE_PATH);
        final RestTemplate restTemplate = apiClient.getRestTemplate();
        final MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server
            .expect(ExpectedCount.once(), requestTo(BASE_PATH + RELATIVE_PATH))
            .andExpect(method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(RESPONSE, MediaType.APPLICATION_JSON));

        final TestSpringApi api = new TestSpringApi(apiClient);
        final Outer result = api.getOuter();

        server.verify();
        assertResult(result);
    }

    @Test
    @Description( "Tests deserialization of responses with nested extensions using the Apache HTTP client based ApiClient." )
    void testDeserializeResponseWithNestedExtensionsApache( WireMockRuntimeInfo wmInfo )
    {

        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo(RELATIVE_PATH)).willReturn(WireMock.okJson(RESPONSE)));
        final var apiClient =
            com.sap.cloud.sdk.services.openapi.apache.ApiClient.create().withBasePath(wmInfo.getHttpBaseUrl());

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
        public TestApacheApi( final com.sap.cloud.sdk.services.openapi.apache.ApiClient apiClient )
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
            final String localVarAccept =
                com.sap.cloud.sdk.services.openapi.apache.ApiClient.selectHeaderAccept(localVarAccepts);

            final String[] localVarContentTypes = {

            };
            final String localVarContentType =
                com.sap.cloud.sdk.services.openapi.apache.ApiClient.selectHeaderContentType(localVarContentTypes);

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

    private static class TestSpringApi extends AbstractOpenApiService
    {
        public TestSpringApi( final ApiClient apiClient )
        {
            super(apiClient);
        }

        public Outer getOuter()
        {
            final Object localVarPostBody = null;
            // create path and map variables
            final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
            final String localVarPath =
                UriComponentsBuilder.fromPath("/outer").buildAndExpand(localVarPathParams).toUriString();

            final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
            final HttpHeaders localVarHeaderParams = new HttpHeaders();
            final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

            final String[] localVarAccepts = { "application/json" };
            final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
            final String[] localVarContentTypes = {};
            final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

            final String[] localVarAuthNames = new String[] {};

            final ParameterizedTypeReference<Outer> localVarReturnType = new ParameterizedTypeReference<Outer>()
            {
            };
            return apiClient
                .invokeAPI(
                    localVarPath,
                    HttpMethod.GET,
                    localVarQueryParams,
                    localVarPostBody,
                    localVarHeaderParams,
                    localVarFormParams,
                    localVarAccept,
                    localVarContentType,
                    localVarAuthNames,
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
