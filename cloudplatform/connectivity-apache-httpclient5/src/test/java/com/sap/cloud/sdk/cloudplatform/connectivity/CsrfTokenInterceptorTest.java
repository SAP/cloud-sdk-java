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
import java.net.URI;

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
    private static final String SERVICE_ROOT = "/service/";
    private static final String REQUEST_PATH = "/service/entity";

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
            new HttpPost(REQUEST_PATH),
            new HttpPut(REQUEST_PATH),
            new HttpPatch(REQUEST_PATH),
            new HttpDelete(REQUEST_PATH) };
    }

    @Test
    @SneakyThrows
    void tokenIsNotFetchedForGetRequest()
    {
        final HttpGet request = new HttpGet(REQUEST_PATH);

        sut.process(request, null, null);

        verify(mockHttpClient, never()).execute(any(), any(HttpClientResponseHandler.class));
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY)).isNull();
    }

    @Test
    @SneakyThrows
    void tokenIsNotFetchedForHeadRequest()
    {
        final HttpHead request = new HttpHead(REQUEST_PATH);

        sut.process(request, null, null);

        verify(mockHttpClient, never()).execute(any(), any(HttpClientResponseHandler.class));
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY)).isNull();
    }

    @Test
    @SneakyThrows
    void tokenIsNotFetchedWhenAlreadyPresent()
    {
        final HttpPost request = new HttpPost(REQUEST_PATH);
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
        wm.getWireMock().register(head(urlEqualTo(SERVICE_ROOT)).willReturn(ok()));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final HttpClient realClient = new ApacheHttpClient5FactoryBuilder().build().createHttpClient(destination);
        final CsrfTokenInterceptor interceptor = new CsrfTokenInterceptor(realClient);

        final HttpPost request = new HttpPost(REQUEST_PATH);

        assertThatCode(() -> interceptor.process(request, null, null)).doesNotThrowAnyException();
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY)).isNull();

        wm.getWireMock().verifyThat(headRequestedFor(urlEqualTo(SERVICE_ROOT)));
    }

    @Test
    @SneakyThrows
    void requestProceedsWithoutTokenWhenHeadThrowsIOException()
    {
        when(mockHttpClient.execute(any(HttpHead.class), any(HttpClientResponseHandler.class)))
            .thenThrow(new IOException("Connection refused"));

        final HttpPost request = new HttpPost(REQUEST_PATH);

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
                head(urlEqualTo(SERVICE_ROOT))
                    .willReturn(ok().withHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, CSRF_TOKEN)));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final HttpClient realClient = new ApacheHttpClient5FactoryBuilder().build().createHttpClient(destination);
        final CsrfTokenInterceptor interceptor = new CsrfTokenInterceptor(realClient);

        final HttpPost request = new HttpPost(REQUEST_PATH);
        interceptor.process(request, null, null);

        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY).getValue())
            .isEqualTo(CSRF_TOKEN);
    }

    @Test
    @SneakyThrows
    void nonPrintableCharactersAreStrippedFromToken()
    {
        final String tokenWithNonPrintable = "valid\u0001token\u007f";
        final ClassicHttpResponse headResponse = new BasicClassicHttpResponse(200);
        headResponse.addHeader(new BasicHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, tokenWithNonPrintable));

        when(mockHttpClient.execute(any(HttpHead.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(inv -> ((HttpClientResponseHandler<?>) inv.getArgument(1)).handleResponse(headResponse));

        final HttpPost request = new HttpPost(REQUEST_PATH);
        sut.process(request, null, null);

        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY).getValue())
            .isEqualTo("validtoken");
    }

    @Test
    @SneakyThrows
    void onlyFirstTokenHeaderIsUsedWhenMultipleReturned()
    {
        final ClassicHttpResponse headResponse = new BasicClassicHttpResponse(200);
        headResponse.addHeader(new BasicHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, "first-token"));
        headResponse.addHeader(new BasicHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, "second-token"));

        when(mockHttpClient.execute(any(HttpHead.class), any(HttpClientResponseHandler.class)))
            .thenAnswer(inv -> ((HttpClientResponseHandler<?>) inv.getArgument(1)).handleResponse(headResponse));

        final HttpPost request = new HttpPost(REQUEST_PATH);
        sut.process(request, null, null);

        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY).getValue())
            .isEqualTo("first-token");
    }

    @Test
    @SneakyThrows
    void headRequestIsSentToServiceRootNotResourcePath( final WireMockRuntimeInfo wm )
    {
        wm
            .getWireMock()
            .register(
                head(urlEqualTo(SERVICE_ROOT))
                    .willReturn(ok().withHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY, CSRF_TOKEN)));

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final HttpClient realClient = new ApacheHttpClient5FactoryBuilder().build().createHttpClient(destination);
        final CsrfTokenInterceptor interceptor = new CsrfTokenInterceptor(realClient);

        // Request targets a specific resource, but HEAD for CSRF must go to the service root
        final HttpPost request = new HttpPost("/service/Entity(1)");
        interceptor.process(request, null, null);

        wm.getWireMock().verifyThat(headRequestedFor(urlEqualTo(SERVICE_ROOT)));
        assertThat(request.getFirstHeader(CsrfTokenInterceptor.X_CSRF_TOKEN_HEADER_KEY).getValue())
            .isEqualTo(CSRF_TOKEN);
    }

    @Test
    void deriveServiceRootUri_stripsLastSegment()
    {
        assertThat(CsrfTokenInterceptor.deriveServiceRootUri(URI.create("http://host/service/Entity")))
            .isEqualTo(URI.create("http://host/service/"));
    }

    @Test
    void deriveServiceRootUri_preservesTrailingSlash()
    {
        assertThat(CsrfTokenInterceptor.deriveServiceRootUri(URI.create("http://host/service/")))
            .isEqualTo(URI.create("http://host/service/"));
    }

    @Test
    void deriveServiceRootUri_stripsDollarBatchSegment()
    {
        assertThat(CsrfTokenInterceptor.deriveServiceRootUri(URI.create("http://host/service/$batch")))
            .isEqualTo(URI.create("http://host/service/"));
    }

    @Test
    void deriveServiceRootUri_handlesRootPath()
    {
        assertThat(CsrfTokenInterceptor.deriveServiceRootUri(URI.create("http://host/")))
            .isEqualTo(URI.create("http://host/"));
    }

    @Test
    void deriveServiceRootUri_handlesDeeplyNestedPath()
    {
        assertThat(CsrfTokenInterceptor.deriveServiceRootUri(URI.create("http://host/a/b/c/Entity")))
            .isEqualTo(URI.create("http://host/a/b/c/"));
    }
}
