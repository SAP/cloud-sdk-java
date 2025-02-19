package com.sap.cloud.sdk.services.openapi.apiclient;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder.TlsUpgrade;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;

@WireMockTest
class ApiClientViaConstructorTest
{
    private static final String RELATIVE_PATH = "/apiEndpoint";
    private static final String SUCCESS_BODY = "success";
    private static final String BASE_PATH = "http://localhost:8080";

    @Test
    void testApiClientDoesNotChangeTheGivenRestTemplate()
    {
        final MyDto myTestDto = new MyDto();
        myTestDto.setReturn("Hello, World!");

        // the default RestTemplate considers both the private Java field and the getter.
        // this is because the field name ("_return") doesn't match the expected getter name (which would be "get_return").
        // in consequence, the serializer is unable to determine that the field belongs to the getter.
        // therefore, both the field (named "Return") and the getter (named "return") will be serialized
        final RestTemplate restTemplate = new RestTemplate();
        final MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        // we expect the service to be invoked twice:
        // firstly by the plain RestTemplate request and
        // secondly by the request created by our test service class.
        // the payload should be the exact same in both cases.
        server
            .expect(ExpectedCount.twice(), requestTo(BASE_PATH + RELATIVE_PATH))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().string("{\"return\":\"Hello, World!\",\"Return\":\"Hello, World!\"}"))
            .andRespond(MockRestResponseCreators.withSuccess(SUCCESS_BODY, MediaType.TEXT_PLAIN));

        // first service invocation
        restTemplate.postForObject(BASE_PATH + RELATIVE_PATH, myTestDto, String.class);

        final ApiClient apiClient = new ApiClient(restTemplate).setBasePath(BASE_PATH);
        final MyTestOpenApiService myTestOpenApiService = new MyTestOpenApiService(apiClient);

        // second service invocation
        myTestOpenApiService.invokeApiEndpoint(myTestDto);

        server.verify();
    }

    @Test
    void testApiClientWithoutExplicitRestTemplateDoesNotConsiderGetters()
    {
        final MyDto myTestDto = new MyDto();
        myTestDto.setReturn("Hello, World!");

        final ApiClient apiClient = new ApiClient().setBasePath(BASE_PATH);
        final RestTemplate restTemplate = apiClient.getRestTemplate();
        final MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server
            .expect(ExpectedCount.once(), requestTo(BASE_PATH + RELATIVE_PATH))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().string("{\"Return\":\"Hello, World!\"}"))
            .andRespond(MockRestResponseCreators.withSuccess(SUCCESS_BODY, MediaType.TEXT_PLAIN));

        final MyTestOpenApiService myTestOpenApiService = new MyTestOpenApiService(apiClient);
        myTestOpenApiService.invokeApiEndpoint(myTestDto);

        server.verify();
    }

    @Test
    void testApiClientWithQueryParams()
    {
        final String filterQueryValue = "emails.value eq \"my.email@test.com\"";
        final String expectedFilterQuery = "emails.value%20eq%20%22my.email@test.com%22";
        final String filterQueryParam = "filter";
        MultiValueMap<String, String> execQueryParams = new LinkedMultiValueMap<>();
        List<String> values = new ArrayList<>();
        values.add(filterQueryValue);
        execQueryParams.put("filter", values);

        final ApiClient apiClient = new ApiClient().setBasePath(BASE_PATH);
        final RestTemplate restTemplate = apiClient.getRestTemplate();
        final MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server
            .expect(
                ExpectedCount.once(),
                requestTo(BASE_PATH + RELATIVE_PATH + "?" + filterQueryParam + "=" + expectedFilterQuery))
            .andExpect(method(HttpMethod.GET))
            .andExpect(queryParam(filterQueryParam, expectedFilterQuery))
            .andRespond(MockRestResponseCreators.withSuccess(SUCCESS_BODY, MediaType.TEXT_PLAIN));

        final MyTestOpenApiService myTestOpenApiService = new MyTestOpenApiService(apiClient);
        myTestOpenApiService.invokeApiEndpoint(HttpMethod.GET, null, execQueryParams);

        server.verify();
    }

    @Test
    void testHttpRequestConfigIsTransmitted( WireMockRuntimeInfo wm )
    {
        httpRequest(TlsUpgrade.DISABLED, wm.getHttpBaseUrl());
        verify(getRequestedFor(anyUrl()).withoutHeader("Upgrade"));

        httpRequest(TlsUpgrade.AUTOMATIC, wm.getHttpBaseUrl());
        verify(getRequestedFor(anyUrl()).withoutHeader("Upgrade"));
    }

    private static void httpRequest( TlsUpgrade toggle, String url )
    {
        var sut = new ApacheHttpClient5FactoryBuilder().tlsUpgrade(toggle).build();
        var httpClient = sut.createHttpClient(DefaultHttpDestination.builder(url).build());
        var clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        var restTemplate = new RestTemplate(clientHttpRequestFactory);
        var apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(url);

        stubFor(get(anyUrl()).willReturn(ok("success")));

        assertThat(
            apiClient
                .invokeAPI(
                    "/apiEndpoint",
                    HttpMethod.GET,
                    null,
                    null,
                    new HttpHeaders(),
                    null,
                    null,
                    null,
                    null,
                    new ParameterizedTypeReference<String>()
                    {
                    }))
            .isEqualTo("success");
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

    private static class MyTestOpenApiService extends AbstractOpenApiService
    {
        MyTestOpenApiService( @Nonnull final ApiClient apiClient )
        {
            super(apiClient);
        }

        void invokeApiEndpoint( @Nullable Object body )
        {
            invokeApiEndpoint(HttpMethod.POST, body, null);
        }

        void invokeApiEndpoint(
            HttpMethod method,
            @Nullable Object body,
            @Nullable MultiValueMap<String, String> queryParams )
        {
            assertThat(apiClient.getBasePath()).isEqualTo(BASE_PATH);

            final ParameterizedTypeReference<String> returnType = new ParameterizedTypeReference<>()
            {
            };

            final String s =
                apiClient
                    .invokeAPI(
                        UriComponentsBuilder.fromPath(RELATIVE_PATH).toUriString(),
                        method,
                        queryParams,
                        body,
                        new HttpHeaders(),
                        null,
                        null,
                        null,
                        null,
                        returnType);

            assertThat(s).isEqualTo(SUCCESS_BODY);
        }
    }
}
