package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

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
}
