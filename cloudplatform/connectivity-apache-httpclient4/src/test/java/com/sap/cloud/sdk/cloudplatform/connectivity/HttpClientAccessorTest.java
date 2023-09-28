package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HttpClientAccessorTest
{
    @Test
    public void testDefaultHttpClientCache()
    {
        assertThat(HttpClientAccessor.getHttpClientCache()).isExactlyInstanceOf(DefaultHttpClientCache.class);
    }
}
