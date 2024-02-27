package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
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
        final ApacheHttpClient5Wrapper sut =
            new ApacheHttpClient5Wrapper(mock(CloseableHttpClient.class), firstDestination);

        assertThat(sut.withDestination(firstDestination)).isSameAs(sut);
        assertThat(sut.withDestination(firstDestination)).isNotSameAs(sut.withDestination(secondDestination));

        assertThatThrownBy(() -> sut.withDestination(thirdDestination)).isInstanceOf(ShouldNotHappenException.class);
    }
}
