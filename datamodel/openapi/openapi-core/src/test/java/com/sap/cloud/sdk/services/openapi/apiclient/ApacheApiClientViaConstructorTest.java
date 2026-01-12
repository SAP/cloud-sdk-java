package com.sap.cloud.sdk.services.openapi.apiclient;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder.TlsUpgrade;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.services.openapi.apache.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.Pair;

import jdk.jfr.Description;

@WireMockTest
class ApacheApiClientViaConstructorTest
{

    private static final String RELATIVE_PATH = "/apiEndpoint";
    private static final String SUCCESS_BODY = "success";
    private static final String BASE_PATH = "http://localhost:8080";

    @Test
    @Description( "Verify ApiClient's ObjectMapper ignores getters to avoid duplicate JSON properties." )
    void testApiClientDefaultObjectMapperIgnoresGetters( WireMockRuntimeInfo wm )
    {
        final String baseUrl = wm.getHttpBaseUrl();
        final String requestBody = "{\"Return\":\"Hello, World!\"}";
        WireMock
            .stubFor(
                post(urlEqualTo(RELATIVE_PATH))
                    .withRequestBody(equalTo(requestBody))
                    .willReturn(ok().withHeader("Content-Type", "text/plain").withBody(SUCCESS_BODY)));

        final MyDto myTestDto = new MyDto();
        myTestDto.setReturn("Hello, World!");

        final var apiClient = ApiClient.create().withBasePath(baseUrl);
        final var testApi = new MyTestApacheOpenApiService(apiClient, baseUrl);
        testApi.invokeApiEndpoint(myTestDto);

        WireMock.verify(1, postRequestedFor(urlEqualTo(RELATIVE_PATH)).withRequestBody(equalTo(requestBody)));
    }

    @Test
    void testApiClientWithQueryParams( WireMockRuntimeInfo wm )
    {
        final String baseUrl = wm.getHttpBaseUrl();
        final String filterQueryValue = "emails.value eq \"my.email@test.com\"";
        final String filterQueryParam = "filter";

        final List<Pair> queryParams = ApiClient.parameterToPair(filterQueryParam, filterQueryValue);

        WireMock
            .stubFor(
                get(urlPathEqualTo(RELATIVE_PATH))
                    .withQueryParam(filterQueryParam, equalTo(filterQueryValue))
                    .willReturn(ok().withHeader("Content-Type", "text/plain").withBody(SUCCESS_BODY)));

        final var apiClient = ApiClient.create().withBasePath(baseUrl);
        final var testApi = new MyTestApacheOpenApiService(apiClient, baseUrl);
        testApi.invokeApiEndpoint("GET", null, queryParams);

        WireMock
            .verify(
                1,
                getRequestedFor(urlPathEqualTo(RELATIVE_PATH))
                    .withQueryParam(filterQueryParam, equalTo(filterQueryValue)));
    }

    @Test
    @Description( "Verify that the TlsUpgrade configuration is transmitted in HTTP requests." )
    void testHttpRequestConfigIsTransmitted( WireMockRuntimeInfo wm )
    {
        httpRequest(TlsUpgrade.DISABLED, wm.getHttpBaseUrl());
        verify(getRequestedFor(anyUrl()).withoutHeader("Upgrade"));

        httpRequest(TlsUpgrade.AUTOMATIC, wm.getHttpBaseUrl());
        verify(getRequestedFor(anyUrl()).withoutHeader("Upgrade"));
    }

    private static void httpRequest( TlsUpgrade toggle, String url )
    {
        final var httpClientFactory = new ApacheHttpClient5FactoryBuilder().tlsUpgrade(toggle).build();
        final var httpClient = httpClientFactory.createHttpClient(DefaultHttpDestination.builder(url).build());
        var apiClient = ApiClient.fromHttpClient((CloseableHttpClient) httpClient);
        apiClient = apiClient.withBasePath(url);

        stubFor(get(anyUrl()).willReturn(ok().withHeader("Content-Type", "text/plain").withBody(SUCCESS_BODY)));

        assertThat(
            apiClient
                .invokeAPI(
                    RELATIVE_PATH,
                    "GET",
                    List.of(),
                    List.of(),
                    "",
                    null,
                    Map.of(),
                    Map.of(),
                    "text/plain",
                    "text/plain",
                    new TypeReference<String>()
                    {
                    }))
            .isEqualTo(SUCCESS_BODY);
    }

    private static class MyDto
    {
        @JsonProperty( "Return" )
        private String _return;

        public String getReturn()
        {
            return _return;
        }

        public void setReturn( String _return )
        {
            this._return = _return;
        }
    }

    private static class MyTestApacheOpenApiService extends BaseApi
    {
        private final String expectedBasePath;

        MyTestApacheOpenApiService( ApiClient apiClient, String expectedBasePath )
        {
            super(apiClient);
            this.expectedBasePath = expectedBasePath;
        }

        void invokeApiEndpoint( @Nullable Object body )
        {
            invokeApiEndpoint("POST", body, List.of());
        }

        //make it apache reliant
        void invokeApiEndpoint( String method, Object body, List<Pair> queryParams )
        {
            assertThat(apiClient.getBasePath()).isEqualTo(expectedBasePath);

            final TypeReference<String> returnType = new TypeReference<String>()
            {
            };

            final String s =
                apiClient
                    .invokeAPI(
                        RELATIVE_PATH,
                        method,
                        queryParams,
                        List.of(),
                        "",
                        body,
                        Map.of(),
                        Map.of(),
                        "application/json",
                        "application/json",
                        returnType);

            assertThat(s).isEqualTo(SUCCESS_BODY);
        }
    }
}
