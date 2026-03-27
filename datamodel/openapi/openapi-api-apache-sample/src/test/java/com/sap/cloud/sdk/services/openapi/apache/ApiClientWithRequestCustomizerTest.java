package com.sap.cloud.sdk.services.openapi.apache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.UnaryOperator;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.openapi.apache.petstore.api.DefaultApi;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.ApiClient;

import io.vavr.control.Try;
import lombok.SneakyThrows;

public class ApiClientWithRequestCustomizerTest
{
    @Test
    @SneakyThrows
    void testRequestCustomizer()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        // Use the withRequestCustomizer API to add a custom header
        final UnaryOperator<ClassicRequestBuilder> customizer =
            builder -> builder.setPath("/custom-path").addHeader("X-Custom", "custom");

        final var apiClient = ApiClient.fromHttpClient(httpClient).withRequestCustomizer(customizer);
        new DefaultApi(apiClient).findPets();

        verify(httpClient).execute(argThat(req -> Try.run(() -> {
            assertThat(req.getRequestUri()).isEqualTo("/custom-path");
            assertThat(req.getMethod()).isEqualTo("GET");
            assertThat(req.getHeaders()).extracting(Header::getName).containsExactlyInAnyOrder("Accept", "X-Custom");
            assertThat(req.getFirstHeader("X-Custom").getValue()).isEqualTo("custom");
            assertThat(req.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
        }).isSuccess()), any(HttpContext.class), any());
    }

    @Test
    @SneakyThrows
    void testRequestCustomizerWithEntity()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        final var apiClient = ApiClient.fromHttpClient(httpClient).withRequestCustomizer(b -> b.setEntity("foo"));
        new DefaultApi(apiClient).findPets();

        verify(httpClient).execute(argThat(req -> Try.run(() -> {
            assertThat(EntityUtils.toString(req.getEntity())).isEqualTo("foo");
        }).isSuccess()), any(HttpContext.class), any());
    }

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
        }).isSuccess()), any(HttpContext.class), any());
    }

    @Test
    @SneakyThrows
    void testWithBasePath()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        final var apiClient = ApiClient.fromHttpClient(httpClient).withBasePath("https://api.example.com");
        new DefaultApi(apiClient).findPets();

        verify(httpClient).execute(argThat(req -> Try.run(() -> {
            assertThat(req.getUri().toString()).isEqualTo("https://api.example.com/pets");
        }).isSuccess()), any(HttpContext.class), any());
    }
}
