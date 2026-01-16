package com.sap.cloud.sdk.services.openapi.apiclient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.services.openapi.apache.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.apache.Pair;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

import lombok.Data;

@WireMockTest
class ApacheApiClientResponseHandlingTest
{
    private static final String TEST_PATH = "/test";
    private static final String TEST_RESPONSE_BODY = "{\"message\": \"success\"}";

    @Test
    void testResponseMetadataListener( final WireMockRuntimeInfo wmInfo )
    {
        stubFor(
            get(urlEqualTo(TEST_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("x-custom-header", "some-value")
                        .withBody(TEST_RESPONSE_BODY)));

        final AtomicReference<OpenApiResponse> metadata = new AtomicReference<>();
        final ApiClient apiClient =
            ApiClient.create().withBasePath(wmInfo.getHttpBaseUrl()).withResponseMetadataListener(metadata::set);

        final TestApi api = new TestApi(apiClient);
        final TestResponse result = api.executeRequest();

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("success");
        assertThat(metadata.get()).isNotNull();
        assertThat(metadata.get().getStatusCode()).isEqualTo(200);
        assertThat(metadata.get().getHeaders()).isNotEmpty();
        assertThat(metadata.get().getHeaders()).containsKey("x-custom-header");

        verify(1, getRequestedFor(urlEqualTo(TEST_PATH)));
    }

    @Test
    void testCaseInsensitiveHeaderLookup( final WireMockRuntimeInfo wmInfo )
    {
        stubFor(
            get(urlEqualTo(TEST_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(TEST_RESPONSE_BODY)
                        .withHeader("x-custom-header", "some-value")));

        final AtomicReference<OpenApiResponse> capturedResponse = new AtomicReference<>();
        final ApiClient apiClient =
            ApiClient
                .create()
                .withBasePath(wmInfo.getHttpBaseUrl())
                .withResponseMetadataListener(capturedResponse::set);

        final TestApi api = new TestApi(apiClient);
        api.executeRequest();

        // Verify case-insensitive access works
        final Map<String, List<String>> headers = capturedResponse.get().getHeaders();
        assertThat(headers.get("x-custom-header")).contains("some-value");
        assertThat(headers.get("X-Custom-Header")).contains("some-value");
        assertThat(headers.get("X-CUSTOM-HEADER")).contains("some-value");
    }

    private static class TestApi extends BaseApi
    {
        private final String path;

        TestApi( final ApiClient apiClient )
        {
            this(apiClient, TEST_PATH);
        }

        TestApi( final ApiClient apiClient, final String path )
        {
            super(apiClient);
            this.path = path;
        }

        TestResponse executeRequest()
            throws OpenApiRequestException
        {
            final List<Pair> localVarQueryParams = new ArrayList<>();
            final List<Pair> localVarCollectionQueryParams = new ArrayList<>();
            final Map<String, String> localVarHeaderParams = new HashMap<>();
            final Map<String, Object> localVarFormParams = new HashMap<>();

            final String[] localVarAccepts = { "application/json" };
            final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);

            final String[] localVarContentTypes = {};
            final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

            final TypeReference<TestResponse> localVarReturnType = new TypeReference<TestResponse>()
            {
            };

            return apiClient
                .invokeAPI(
                    path,
                    "GET",
                    localVarQueryParams,
                    localVarCollectionQueryParams,
                    null,
                    null,
                    localVarHeaderParams,
                    localVarFormParams,
                    localVarAccept,
                    localVarContentType,
                    localVarReturnType);
        }
    }

    @Data
    private static class TestResponse
    {
        @JsonProperty( "message" )
        private String message;
    }
}
