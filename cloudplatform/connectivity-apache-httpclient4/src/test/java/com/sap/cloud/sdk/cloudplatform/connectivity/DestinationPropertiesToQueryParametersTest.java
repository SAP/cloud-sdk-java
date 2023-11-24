package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest
class DestinationPropertiesToQueryParametersTest
{
    private static final String RELATIVE_PATH = "/some/path?foo=bar&bar=ba%20z";

    @BeforeEach
    void setupDestination()
    {
        stubFor(any(anyUrl()));
    }

    @Test
    void testDestinationPropertiesToQueryParametersEncoding( @Nonnull final WireMockRuntimeInfo wm )
        throws IOException
    {
        final Destination destination =
            DefaultHttpDestination
                .builder(wm.getHttpBaseUrl())
                .header("foo1", "bar1")
                .property("foo2", "bar2")
                .property("URL.qUeRIes.key1", "value1")
                .property("Url.queries.key2", "foo ?&bar")
                .build();

        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        httpClient.execute(new HttpGet(RELATIVE_PATH));

        verify(getRequestedFor(urlEqualTo(RELATIVE_PATH + "&key1=value1&key2=foo%20%3F%26bar")));
    }

    @Test
    void testUriEncodingWhenUsingDestinationProperties( @Nonnull final WireMockRuntimeInfo wm )
        throws IOException
    {
        final URI destinationUrl =
            URI
                .create(
                    "http://user:pa%20ssd@localhost:" + wm.getHttpPort() + "/loca%20tion/?query=white%20space#fr%20ag");

        final Destination destination =
            DefaultHttpDestination
                .builder(destinationUrl)
                .header("foo1", "bar1")
                .property("foo2", "bar2")
                .property("URL.queries.key1", "value1")
                .property("URL.queries.key2", "val ue2")
                .build();

        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        httpClient.execute(new HttpGet(RELATIVE_PATH));

        verify(
            getRequestedFor(urlPathEqualTo("/loca%20tion/some/path")) // source: destination URI + request
                .withQueryParam("foo", equalTo("bar")) // source: request
                .withQueryParam("bar", equalTo("ba z")) // source: request
                .withQueryParam("query", equalTo("white space")) // source: destination URI
                .withQueryParam("key1", equalTo("value1")) // source: destination properties
                .withQueryParam("key2", equalTo("val ue2")) // source: destination properties
                .withHeader("foo1", equalTo("bar1"))); // source: destination properties

        // HTTP fragment is not sent to server
        // HTTP user-info cannot be asserted with Wiremock
    }
}
