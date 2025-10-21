package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import lombok.val;

public class AbstractHttpClientCacheTest
{
    @SneakyThrows
    @Test
    void testClosedPool()
    {
        val d = DefaultHttpDestination.builder("https://sap.com").build();

        val httpClient1 = HttpClientAccessor.getHttpClient(d);
        httpClient1.execute(new HttpHead());
        httpClient1.execute(new HttpHead());

        ((CloseableHttpClient) httpClient1).close();
        assertThatThrownBy(() -> httpClient1.execute(new HttpHead()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Connection pool shut down");
        assertThatThrownBy(() -> httpClient1.execute(new HttpHead()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Connection pool shut down");

        val httpClient2 = HttpClientAccessor.getHttpClient(d);
        assertThat(httpClient1).isNotSameAs(httpClient2);
        httpClient2.execute(new HttpHead());
        httpClient2.execute(new HttpHead());
    }
}
