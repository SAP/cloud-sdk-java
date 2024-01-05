/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

class HttpClientCacheTest
{
    @Test
    void testDisabledCache()
    {
        final HttpClientFactory factory = spy(new DefaultHttpClientFactory());
        final HttpDestination destination = mock(HttpDestination.class);

        final HttpClientCache sut = HttpClientCache.DISABLED;
        sut.tryGetHttpClient(factory);
        sut.tryGetHttpClient(factory);
        verify(factory, times(2)).createHttpClient();

        sut.tryGetHttpClient(destination, factory);
        sut.tryGetHttpClient(destination, factory);
        verify(factory, times(2)).createHttpClient(destination);
    }

    @Test
    void test() {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder("http://foo")
                .headerProviders(c -> List.of(new Header("h1", "v1")))
                .build();
        final DefaultHttpDestination dest2 = DefaultHttpDestination.builder("http://foo")
                .headerProviders(c -> List.of(new Header("h2", "v2")))
                .build();

        assertThat(dest1).isEqualTo(dest2);
        final HttpClientWrapper client1 = (HttpClientWrapper) HttpClientAccessor.getHttpClient(dest1);
        final HttpClientWrapper client2 = (HttpClientWrapper) HttpClientAccessor.getHttpClient(dest2);
        assertThat(client1).isSameAs(client2);

        final HttpUriRequest request1 = client1.wrapRequest(new HttpGet("/"));
        assertThat(request1.getAllHeaders()).contains(new HttpClientWrapper.ApacheHttpHeader(new Header("h1", "v1")));
        assertThat(request1.getAllHeaders()).doesNotContain(new HttpClientWrapper.ApacheHttpHeader(new Header("h2", "v2")));

        final HttpUriRequest request2 = client2.wrapRequest(new HttpGet("/"));
        assertThat(request2.getAllHeaders()).contains(new HttpClientWrapper.ApacheHttpHeader(new Header("h2", "v2")));
        assertThat(request2.getAllHeaders()).doesNotContain(new HttpClientWrapper.ApacheHttpHeader(new Header("h1", "v1")));
    }
}
