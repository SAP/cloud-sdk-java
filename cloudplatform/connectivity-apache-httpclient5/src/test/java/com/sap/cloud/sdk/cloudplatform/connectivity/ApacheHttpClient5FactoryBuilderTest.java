package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import lombok.SneakyThrows;

@WireMockTest
class ApacheHttpClient5FactoryBuilderTest
{
    private static final String SERVICE_PATH = "/service/";
    private static final String RESOURCE_PATH = SERVICE_PATH + "Entity";
    private static final String CSRF_TOKEN = "test-token";

    @Test
    void testBuilderContainsOptionalParametersOnly()
    {
        // make sure we can build a new factory instance without supplying any parameters
        assertThatNoException().isThrownBy(() -> new ApacheHttpClient5FactoryBuilder().build());
    }

    @Test
    @SneakyThrows
    void csrfInterceptorIsDisabledByDefault( final WireMockRuntimeInfo wm )
    {
        wm.getWireMock().register(post(urlEqualTo(RESOURCE_PATH)).willReturn(noContent()));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final HttpClient client = new ApacheHttpClient5FactoryBuilder().build().createHttpClient(destination);

        client.execute(new HttpPost(RESOURCE_PATH), r -> null);

        wm.getWireMock().verifyThat(1, postRequestedFor(urlEqualTo(RESOURCE_PATH)));
        wm.getWireMock().verifyThat(0, headRequestedFor(anyUrl()));
    }

    @Test
    @SneakyThrows
    void csrfInterceptorIsEnabledWhenOptedIn( final WireMockRuntimeInfo wm )
    {
        wm
            .getWireMock()
            .register(
                head(urlEqualTo(SERVICE_PATH))
                    .willReturn(ok().withHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, CSRF_TOKEN)));
        wm.getWireMock().register(post(urlEqualTo(RESOURCE_PATH)).willReturn(noContent()));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final HttpClient client =
            new ApacheHttpClient5FactoryBuilder().withCsrfTokenInterceptor().build().createHttpClient(destination);

        client.execute(new HttpPost(RESOURCE_PATH), r -> null);

        wm
            .getWireMock()
            .verifyThat(
                1,
                headRequestedFor(urlEqualTo(SERVICE_PATH))
                    .withHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, equalTo("fetch")));
        wm
            .getWireMock()
            .verifyThat(
                1,
                postRequestedFor(urlEqualTo(RESOURCE_PATH))
                    .withHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, equalTo(CSRF_TOKEN)));
    }
}
