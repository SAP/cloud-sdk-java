/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.CsrfTokenRetrievalException;

@WireMockTest
class CsrfTokenRetrieverTest
{
    @Test
    void testSuccessfulCsrfTokenRetrievalWireMock( @Nonnull final WireMockRuntimeInfo wm )
    {
        final String CSRF_TOKEN = "awesome-csrf-token";
        final String servicePath = "/target/service";
        final String extendedPath = "/path/to";
        final String hostUrl = "http://localhost:" + wm.getHttpPort() + extendedPath;
        final String expectedPath = extendedPath + servicePath;

        stubFor(
            WireMock
                .head(WireMock.urlEqualTo(expectedPath))
                .willReturn(WireMock.ok().withHeader("x-csrf-token", CSRF_TOKEN)));
        final Destination destination = DefaultHttpDestination.builder(hostUrl).build();
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);

        final CsrfToken csrfToken = new DefaultCsrfTokenRetriever().retrieveCsrfToken(client, servicePath);

        assertThat(csrfToken.getToken()).isEqualTo(CSRF_TOKEN);
    }

    @Test
    void testHttpClientThrowsIoException()
        throws IOException
    {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);

        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpHead.class))).thenThrow(IOException.class);

        assertThatCode(() -> new DefaultCsrfTokenRetriever().retrieveCsrfToken(httpClient, "target"))
            .isInstanceOf(CsrfTokenRetrievalException.class)
            .hasRootCauseInstanceOf(IOException.class);
    }

    @Test
    void testTargetSystemRespondsWithoutCsrfTokenHeader()
        throws IOException
    {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        final HttpResponse httpResponse = Mockito.mock(HttpResponse.class);

        Mockito.when(httpResponse.getFirstHeader(ArgumentMatchers.any())).thenReturn(null);
        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpHead.class))).thenReturn(httpResponse);

        assertThatCode(() -> new DefaultCsrfTokenRetriever().retrieveCsrfToken(httpClient, "target"))
            .isInstanceOf(CsrfTokenRetrievalException.class)
            .hasRootCauseInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testDefaultCsrfTokenRetrieverIsEnabled()
    {
        final CsrfTokenRetriever retriever = new DefaultCsrfTokenRetriever();

        assertThat(retriever.isEnabled()).isTrue();
    }

    @Test
    void testDisabledCsrfTokenRetrieverConstant()
    {
        final CsrfTokenRetriever disabledRetriever = CsrfTokenRetriever.DISABLED_CSRF_TOKEN_RETRIEVER;

        assertThat(disabledRetriever.isEnabled()).isFalse();
        assertThatIllegalStateException()
            .isThrownBy(() -> disabledRetriever.retrieveCsrfToken(Mockito.mock(HttpClient.class), "foo"));
        assertThatIllegalStateException()
            .isThrownBy(
                () -> disabledRetriever
                    .retrieveCsrfToken(Mockito.mock(HttpClient.class), "foo", Collections.emptyMap()));
    }
}
