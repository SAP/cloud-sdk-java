package com.sap.cloud.sdk.services.openapi.apache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.UnaryOperator;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.openapi.apache.petstore.api.DefaultApi;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.ApiClient;

import io.vavr.control.Try;
import lombok.SneakyThrows;

@SuppressWarnings( "unchecked" )
public class ApiClientWithRequestCustomizerTest
{
    @SuppressWarnings( "rawtypes" )
    @Test
    @SneakyThrows
    void testRequestCustomizer()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        // Use the withRequestCustomizer API to add a custom header
        final UnaryOperator<ClassicRequestBuilder> customizer =
            builder -> builder.addHeader("X-Custom-Header", "custom-value");

        final var apiClient = ApiClient.fromHttpClient(httpClient).withRequestCustomizer(customizer);
        new DefaultApi(apiClient).findPets();

        verify(httpClient).execute(argThat(req -> Try.run(() -> {
            assertThat(req.getRequestUri()).isEqualTo("/pets");
            assertThat(req.getMethod()).isEqualTo("GET");
            assertThat(req.getHeaders())
                .extracting(Header::getName)
                .containsExactlyInAnyOrder("Accept", "X-Custom-Header");
            assertThat(req.getFirstHeader("X-Custom-Header").getValue()).isEqualTo("custom-value");
            assertThat(req.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
        }).isSuccess()), any(HttpContext.class), any(HttpClientResponseHandler.class));
    }

    @SuppressWarnings( "rawtypes" )
    @Test
    @SneakyThrows
    void testRequestCustomizerWithMultipleHeaders()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        // Chain multiple customizations
        final UnaryOperator<ClassicRequestBuilder> customizer =
            builder -> builder.addHeader("X-Correlation-Id", "12345").addHeader("X-Tenant-Id", "tenant-abc");

        final var apiClient = ApiClient.fromHttpClient(httpClient).withRequestCustomizer(customizer);
        new DefaultApi(apiClient).findPets();

        verify(httpClient).execute(argThat(req -> Try.run(() -> {
            assertThat(req.getFirstHeader("X-Correlation-Id").getValue()).isEqualTo("12345");
            assertThat(req.getFirstHeader("X-Tenant-Id").getValue()).isEqualTo("tenant-abc");
        }).isSuccess()), any(HttpContext.class), any(HttpClientResponseHandler.class));
    }

    @SuppressWarnings( "rawtypes" )
    @Test
    @SneakyThrows
    void testBasicApiCall()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        final var apiClient = ApiClient.fromHttpClient(httpClient);
        new DefaultApi(apiClient).findPets();

        verify(httpClient).execute(argThat(req -> Try.run(() -> {
            assertThat(req.getRequestUri()).isEqualTo("/pets");
            assertThat(req.getMethod()).isEqualTo("GET");
            assertThat(req.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
        }).isSuccess()), any(HttpContext.class), any(HttpClientResponseHandler.class));
    }

    @SuppressWarnings( "rawtypes" )
    @Test
    @SneakyThrows
    void testWithBasePath()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        final var apiClient = ApiClient.fromHttpClient(httpClient).withBasePath("https://api.example.com");
        new DefaultApi(apiClient).findPets();

        verify(httpClient).execute(argThat(req -> Try.run(() -> {
            assertThat(req.getUri().toString()).isEqualTo("https://api.example.com/pets");
        }).isSuccess()), any(HttpContext.class), any(HttpClientResponseHandler.class));
    }
}
