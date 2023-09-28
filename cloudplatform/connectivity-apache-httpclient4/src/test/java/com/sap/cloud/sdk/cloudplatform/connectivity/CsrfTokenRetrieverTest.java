package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.CsrfTokenRetrievalException;

public class CsrfTokenRetrieverTest
{
    @Rule
    public final WireMockRule server = new WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort());

    @Test
    public void testSuccessfulCsrfTokenRetrievalWireMock()
    {
        final String CSRF_TOKEN = "awesome-csrf-token";
        final String servicePath = "/target/service";
        final String extendedPath = "/path/to";
        final String hostUrl = "http://localhost:" + server.port() + extendedPath;
        final String expectedPath = extendedPath + servicePath;

        server
            .stubFor(
                WireMock
                    .head(WireMock.urlEqualTo(expectedPath))
                    .willReturn(WireMock.ok().withHeader("x-csrf-token", CSRF_TOKEN)));
        final Destination destination = DefaultHttpDestination.builder(hostUrl).build();
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);

        final CsrfToken csrfToken = new DefaultCsrfTokenRetriever().retrieveCsrfToken(client, servicePath);

        assertThat(csrfToken.getToken()).isEqualTo(CSRF_TOKEN);
    }

    @Test
    public void testHttpClientThrowsIoException()
        throws IOException
    {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);

        Mockito.when(httpClient.execute(ArgumentMatchers.any(HttpHead.class))).thenThrow(IOException.class);

        assertThatCode(() -> new DefaultCsrfTokenRetriever().retrieveCsrfToken(httpClient, "target"))
            .isInstanceOf(CsrfTokenRetrievalException.class)
            .hasRootCauseInstanceOf(IOException.class);
    }

    @Test
    public void testTargetSystemRespondsWithoutCsrfTokenHeader()
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
    public void testDefaultCsrfTokenRetrieverIsEnabled()
    {
        final CsrfTokenRetriever retriever = new DefaultCsrfTokenRetriever();

        assertThat(retriever.isEnabled()).isTrue();
    }

    @Test
    public void testDisabledCsrfTokenRetrieverConstant()
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
