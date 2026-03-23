package com.sap.cloud.sdk.services.openapi.apache;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cloud.sdk.datamodel.openapi.apache.petstore.api.DefaultApi;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.ApiClientInvoker;
import com.sap.cloud.sdk.services.openapi.apache.apiclient.Pair;

import io.vavr.control.Try;
import lombok.SneakyThrows;

public class ApiClientWithInvokerTest
{
    @Test
    void testApiClientWithInvoker()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        final ApiClientInvoker invoker = mock(ApiClientInvoker.class);

        final var apiClient = ApiClient.fromHttpClient(httpClient).withInvoker(invoker);
        new DefaultApi(apiClient).findPets();

        verify(invoker)
            .invokeAPI(
                eq("/pets"),
                eq("GET"),
                eq(emptyList()),
                eq(emptyList()),
                eq(""),
                eq(null),
                eq(emptyMap()),
                eq(emptyMap()),
                eq("application/json"),
                eq("application/json"),
                isA(TypeReference.class));
    }

    @Test
    @SneakyThrows
    void testOverloadingInvoker()
    {
        final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        final UnaryOperator<ApiClientInvoker> invokerOverload = invoker -> new ApiClientInvoker()
        {
            @Nullable
            @Override
            public <T> T invokeAPI(
                @Nonnull String path,
                @Nonnull String method,
                @Nullable final List<Pair> queryParams,
                @Nullable final List<Pair> collectionQueryParams,
                @Nullable String urlQueryDeepObject,
                @Nullable Object body,
                @Nonnull final Map<String, String> headerParams,
                @Nonnull final Map<String, Object> formParams,
                @Nullable final String accept,
                @Nonnull final String contentType,
                @Nonnull final TypeReference<T> returnType )
            {
                // customizations
                path = "/overloaded";
                method = "PUT";
                queryParams.add(new Pair("overloaded", "true"));
                collectionQueryParams.add(new Pair("overloadedCollection", "true"));
                urlQueryDeepObject = "overloaded=true";
                body = Map.of("overloaded", true);
                headerParams.put("overloaded", "true");

                // call delegate
                return invoker
                    .invokeAPI(
                        path,
                        method,
                        queryParams,
                        collectionQueryParams,
                        urlQueryDeepObject,
                        body,
                        headerParams,
                        formParams,
                        accept,
                        contentType,
                        returnType);
            }
        };

        final var apiClient = ApiClient.fromHttpClient(httpClient).withInvoker(invokerOverload);
        new DefaultApi(apiClient).findPets();

        verify(httpClient).execute(argThat(req -> Try.run(() -> {
            final String requestUri = "/overloaded?overloaded=true&overloadedCollection=true&overloaded=true";
            assertThat(req.getRequestUri()).isEqualTo(requestUri);
            assertThat(req.getMethod()).isEqualTo("PUT");
            assertThat(req.getHeaders()).extracting(Header::getName).containsExactlyInAnyOrder("overloaded", "Accept");
            assertThat(req.getFirstHeader("overloaded").getValue()).isEqualTo("true");
            assertThat(req.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
            assertThat(req.getEntity().getContent()).hasContent("{\"overloaded\":true}");
            assertThat(req.getEntity().getContentType()).isEqualTo(ContentType.APPLICATION_JSON.toString());
            assertThat(req.getEntity().getContentEncoding()).isNull();
        }).isSuccess()), any(HttpContext.class), any(HttpClientResponseHandler.class));
    }
}
