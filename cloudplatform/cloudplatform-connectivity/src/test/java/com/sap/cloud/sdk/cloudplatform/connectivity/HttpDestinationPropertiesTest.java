/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.exception.RequestHeadersAccessException;
import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;

import io.vavr.control.Try;

class HttpDestinationPropertiesTest
{
    @AfterEach
    void resetRequestContext()
    {
        RequestHeaderAccessor.setHeaderFacade(null);
    }

    @Test
    void tokenForwardingShouldTakeTokenFromCurrentRequestHeaders()
    {
        final String authHeaderValue = "BeArEr someToken";
        final RequestHeaderContainer headers =
            DefaultRequestHeaderContainer.builder().withHeader(HttpHeaders.AUTHORIZATION, authHeaderValue).build();

        RequestHeaderAccessor.setHeaderFacade(() -> Try.success(headers));

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        assertThat(destination.getHeaders()).containsExactly(new Header(HttpHeaders.AUTHORIZATION, authHeaderValue));
    }

    @Test
    void tokenForwardingShouldNotForwardOnNonGivenRequestHeader()
    {
        // no headers present
        RequestHeaderAccessor.setHeaderFacade(() -> Try.success(RequestHeaderContainer.EMPTY));

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        assertThat(destination.getHeaders()).isEmpty();
    }

    @Test
    void tokenForwardingShouldNotForwardOnNonExistingRequestHeaders()
    {
        RequestHeaderAccessor.setHeaderFacade(() -> Try.failure(new RequestHeadersAccessException()));

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

        RequestHeaderAccessor.setHeaderFacade(() -> Try.success(headers));

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        final Collection<Header> createdHeaders = destination.getHeaders();
        assertThat(createdHeaders).map(Header::getName).hasSize(2).containsOnly(HttpHeaders.AUTHORIZATION);
        assertThat(createdHeaders).map(Header::getValue).containsExactly(headerValues);
    }

    @Test
    void tokenForwardingShouldNotForwardOnNullHeaderValues()
    {
        final RequestHeaderContainer headers = mock(RequestHeaderContainer.class);
        when(headers.getHeaderValues(eq(HttpHeaders.AUTHORIZATION))).thenReturn(Collections.singletonList(null));

        RequestHeaderAccessor.setHeaderFacade(() -> Try.success(headers));

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
        RequestHeaderAccessor.setHeaderFacade(() -> Try.success(headers));

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(URI.create("foo"))
                .authenticationType(AuthenticationType.TOKEN_FORWARDING)
                .build();

        assertThat(destination.getHeaders()).isEmpty();
    }
}
