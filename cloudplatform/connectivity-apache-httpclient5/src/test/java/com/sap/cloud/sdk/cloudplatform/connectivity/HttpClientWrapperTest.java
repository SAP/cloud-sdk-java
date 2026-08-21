package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.net.URI;
import java.util.List;

import org.apache.hc.client5.http.config.RequestConfig;
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
            new ApacheHttpClient5Wrapper(mock(CloseableHttpClient.class), firstDestination, mock(RequestConfig.class));

        assertThat(sut.withDestination(firstDestination)).isSameAs(sut);
        assertThat(sut.withDestination(firstDestination)).isNotSameAs(sut.withDestination(secondDestination));

        assertThatThrownBy(() -> sut.withDestination(thirdDestination)).isInstanceOf(ShouldNotHappenException.class);
    }

    @Test
    void mergeRequestUriContainsDestinationQueryParameters()
    {
        final DefaultHttpDestination destination =
            DefaultHttpDestination.builder("http://foo.com/service/").property("URL.queries.sap-client", "100").build();
        final ApacheHttpClient5Wrapper sut =
            new ApacheHttpClient5Wrapper(mock(CloseableHttpClient.class), destination, mock(RequestConfig.class));

        final URI result = sut.mergeRequestUri(URI.create(""));

        assertThat(result.getRawQuery()).contains("sap-client=100");
    }

    @Test
    void mergeRequestUriContainsMultipleDestinationQueryParameters()
    {
        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder("http://foo.com/service/")
                .property("URL.queries.sap-client", "100")
                .property("URL.queries.lang", "en")
                .build();
        final ApacheHttpClient5Wrapper sut =
            new ApacheHttpClient5Wrapper(mock(CloseableHttpClient.class), destination, mock(RequestConfig.class));

        final URI result = sut.mergeRequestUri(URI.create(""));

        assertThat(result.getRawQuery()).contains("sap-client=100").contains("lang=en");
    }

    @Test
    void mergeRequestUriHasNoQueryStringWhenDestinationHasNoQueryProperties()
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder("http://foo.com/service/").build();
        final ApacheHttpClient5Wrapper sut =
            new ApacheHttpClient5Wrapper(mock(CloseableHttpClient.class), destination, mock(RequestConfig.class));

        final URI result = sut.mergeRequestUri(URI.create(""));

        // no destination-injected params — query should be null or empty
        assertThat(result.getRawQuery()).isNullOrEmpty();
    }

    @Test
    void mergeRequestUriIncludesDestinationBaseUrlQueryParameters()
    {
        // Query params baked into the destination URL itself (not URL.queries.* properties)
        // should also appear in the merged URI, as the wrapper merges the full destination URI.
        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder("http://foo.com/service/?sap-client=200")
                .property("URL.queries.sap-client", "100")
                .build();
        final ApacheHttpClient5Wrapper sut =
            new ApacheHttpClient5Wrapper(mock(CloseableHttpClient.class), destination, mock(RequestConfig.class));

        final URI result = sut.mergeRequestUri(URI.create(""));

        assertThat(result.getRawQuery()).contains("sap-client=200").contains("sap-client=100");
    }
}
