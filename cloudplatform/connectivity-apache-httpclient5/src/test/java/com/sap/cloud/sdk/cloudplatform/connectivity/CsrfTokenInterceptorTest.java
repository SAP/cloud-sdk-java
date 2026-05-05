package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import lombok.SneakyThrows;

@WireMockTest
@SuppressWarnings( "unchecked" )
class CsrfTokenInterceptorTest
{
    private static final String CSRF_TOKEN = "test-csrf-token";
    private static final String SERVICE_PATH = "/service/path";

    private HttpClient mockHttpClient;
    private CsrfTokenInterceptor sut;

    @BeforeEach
    void setup()
    {
        mockHttpClient = mock(HttpClient.class);
        sut = new CsrfTokenInterceptor(mockHttpClient);
    }

    @ParameterizedTest
    @MethodSource( "mutatingMethods" )
    @SneakyThrows
    void tokenIsFetchedAndAddedForMutatingMethods( final HttpRequest request )
    {
        final ClassicHttpResponse headResponse = new BasicClassicHttpResponse(200);
        headResponse.addHeader(new BasicHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, CSRF_TOKEN));

        when(mockHttpClient.execute(any(HttpHead.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(inv -> ((HttpClientResponseHandler<?>) inv.getArgument(1)).handleResponse(headResponse));

        sut.process(request, null, null);

        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY).getValue())
            .isEqualTo(CSRF_TOKEN);
    }

    static HttpRequest[] mutatingMethods()
    {
        return new HttpRequest[] {
            new HttpPost(SERVICE_PATH),
            new HttpPut(SERVICE_PATH),
            new HttpPatch(SERVICE_PATH),
            new HttpDelete(SERVICE_PATH) };
    }

    @Test
    @SneakyThrows
    void tokenIsNotFetchedForGetRequest()
    {
        final HttpGet request = new HttpGet(SERVICE_PATH);

        sut.process(request, null, null);

        verify(mockHttpClient, never()).execute(any(), any(HttpClientResponseHandler.class));
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY)).isNull();
    }

    @Test
    @SneakyThrows
    void tokenIsNotFetchedForHeadRequest()
    {
        final HttpHead request = new HttpHead(SERVICE_PATH);

        sut.process(request, null, null);

        verify(mockHttpClient, never()).execute(any(), any(HttpClientResponseHandler.class));
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY)).isNull();
    }

    @Test
    @SneakyThrows
    void tokenIsNotFetchedWhenAlreadyPresent()
    {
        final HttpPost request = new HttpPost(SERVICE_PATH);
        request.addHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, "existing-token");

        sut.process(request, null, null);

        verify(mockHttpClient, never()).execute(any(), any(HttpClientResponseHandler.class));
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY).getValue())
            .isEqualTo("existing-token");
    }

    @Test
    @SneakyThrows
    void requestProceedsWithoutTokenWhenServerReturnsNoHeader( final WireMockRuntimeInfo wm )
    {
        wm.getWireMock().register(head(urlEqualTo(SERVICE_PATH)).willReturn(ok()));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final HttpClient realClient = new ApacheHttpClient5FactoryBuilder().build().createHttpClient(destination);
        final CsrfTokenInterceptor interceptor = new CsrfTokenInterceptor(realClient);

        final HttpPost request = new HttpPost(SERVICE_PATH);

        assertThatCode(() -> interceptor.process(request, null, null)).doesNotThrowAnyException();
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY)).isNull();

        wm.getWireMock().verifyThat(headRequestedFor(urlEqualTo(SERVICE_PATH)));
    }

    @Test
    @SneakyThrows
    void requestProceedsWithoutTokenWhenHeadThrowsIOException()
    {
        when(mockHttpClient.execute(any(HttpHead.class), any(HttpClientResponseHandler.class)))
            .thenThrow(new IOException("Connection refused"));

        final HttpPost request = new HttpPost(SERVICE_PATH);

        assertThatCode(() -> sut.process(request, null, null)).doesNotThrowAnyException();
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY)).isNull();
    }

    @Test
    @SneakyThrows
    void tokenIsFetchedViaRealHttpClientWithWireMock( final WireMockRuntimeInfo wm )
    {
        wm
            .getWireMock()
            .register(
                head(urlEqualTo(SERVICE_PATH))
                    .willReturn(ok().withHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, CSRF_TOKEN)));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final HttpClient realClient = new ApacheHttpClient5FactoryBuilder().build().createHttpClient(destination);
        final CsrfTokenInterceptor interceptor = new CsrfTokenInterceptor(realClient);

        final HttpPost request = new HttpPost(SERVICE_PATH);
        interceptor.process(request, null, null);

        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY).getValue())
            .isEqualTo(CSRF_TOKEN);
    }
}
