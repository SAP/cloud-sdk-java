package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;

class HttpClientWrapperTest
{
    @Test
    void testDestinationWrapping()
    {
        final DefaultHttpDestination firstDestination = DefaultHttpDestination.builder("http://foo.com").build();
        final DefaultHttpDestination secondDestination =
            DefaultHttpDestination.builder("http://foo.com").headerProviders(c -> List.of()).build();
        final DefaultHttpDestination thirdDestination = DefaultHttpDestination.builder("http://bar.com").build();
        final HttpClientWrapper sut = new HttpClientWrapper(mock(CloseableHttpClient.class), firstDestination);

        // withDestination returns the same wrapper instance when the destination reference is identical
        assertThat(sut.withDestination(firstDestination)).isSameAs(sut);

        // withDestination throws an exception when destinations are not equal (different header providers)
        assertThatThrownBy(() -> sut.withDestination(secondDestination)).isInstanceOf(ShouldNotHappenException.class);

        // withDestination throws an exception when destinations have different URIs
        assertThatThrownBy(() -> sut.withDestination(thirdDestination)).isInstanceOf(ShouldNotHappenException.class);
    }
}
