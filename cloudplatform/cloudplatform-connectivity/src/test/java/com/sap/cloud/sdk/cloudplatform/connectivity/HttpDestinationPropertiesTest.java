package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.testutil.TestContext;

class HttpDestinationPropertiesTest
{
    @RegisterExtension
    static TestContext context = TestContext.withThreadContext();

    @Test
    void tokenForwardingShouldTakeTokenFromCurrentRequestHeaders()
    {
        final String authHeaderValue = "BeArEr someToken";
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer.builder().withHeader(HttpHeaders.AUTHORIZATION, authHeaderValue).build();

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        final Collection<Header> result =
            RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> destination.getHeaders());

        assertThat(result).containsExactly(new Header(HttpHeaders.AUTHORIZATION, authHeaderValue));
    }

    @Test
    void tokenForwardingShouldNotForwardOnNonGivenRequestHeader()
    {
        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        final Collection<Header> result =
            RequestHeaderAccessor
                .executeWithHeaderContainer(RequestHeaderContainer.EMPTY, () -> destination.getHeaders());

        assertThat(result).isEmpty();
    }

    @Test
    void tokenForwardingShouldNotForwardOnNonExistingRequestHeaders()
    {
        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        assertThat(destination.getHeaders()).isEmpty();
    }

    @Test
    void tokenForwardingShouldForwardAllHeadersOnMultipleAuthHeaders()
    {
        final String[] headerValues = { "some Value", "some other Value" };
        final DefaultRequestHeaderContainer.Builder headerBuilder = DefaultRequestHeaderContainer.builder();
        for( final String value : headerValues ) {
            headerBuilder.withHeader(HttpHeaders.AUTHORIZATION, value);
        }
        final RequestHeaderContainer headers = headerBuilder.build();

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        final Collection<Header> result =
            RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> destination.getHeaders());

        assertThat(result).map(Header::getName).hasSize(2).containsOnly(HttpHeaders.AUTHORIZATION);
        assertThat(result).map(Header::getValue).containsExactly(headerValues);
    }

    @Test
    void tokenForwardingShouldNotForwardOnNullHeaderValues()
    {
        final RequestHeaderContainer headers = mock(RequestHeaderContainer.class);
        when(headers.getHeaderValues(eq(HttpHeaders.AUTHORIZATION))).thenReturn(Collections.singletonList(null));

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        assertThat(destination.getHeaders()).isEmpty();
    }

    @Test
    void tokenForwardingShouldIgnoreOtherHeadersInCurrentRequest()
    {
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer.builder().withHeader("SomeOtherKey", "Some other value").build();

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        final Collection<Header> result =
            RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> destination.getHeaders());
        assertThat(result).isEmpty();
    }
}
