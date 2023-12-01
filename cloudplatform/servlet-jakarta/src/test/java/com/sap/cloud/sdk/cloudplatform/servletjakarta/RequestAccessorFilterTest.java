/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.servletjakarta;

import static java.util.Arrays.asList;

import static com.google.common.collect.ImmutableMap.of;
import static com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer.fromMultiValueMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

class RequestAccessorFilterTest
{
    @Test
    void testFilterInactive()
        throws ServletException,
            IOException
    {
        final RequestAccessorFilter sut = new RequestAccessorFilter();
        final FilterChain filterChain = mock(FilterChain.class);
        final ServletRequest request = mock(ServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        sut.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testFilterThreadContext()
        throws ServletException,
            IOException
    {
        final RequestAccessorFilter sut = new RequestAccessorFilter();
        final ServletResponse response = mock(ServletResponse.class);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(asList("Foo", "Bar")));
        when(request.getHeaders("Foo")).thenReturn(Collections.enumeration(asList("Foo1", "Foo2")));
        when(request.getHeaders("Bar")).thenReturn(Collections.enumeration(asList("Bar1", "Bar2")));

        final FilterChain filterChain = mock(FilterChain.class);
        doAnswer(( args ) -> {
            final ThreadContext context = ThreadContextAccessor.getCurrentContext();

            assertThat(context.getPropertyValue(RequestAccessorFilter.PROPERTY_SERVLET_REQUEST_SCHEME))
                .containsExactly("http");

            assertThat(context.getPropertyValue(RequestAccessorFilter.PROPERTY_SERVLET_REQUEST_REMOTE_ADDRESS))
                .containsExactly("127.0.0.1");

            assertThat(context.getPropertyValue(RequestHeaderThreadContextListener.PROPERTY_REQUEST_HEADERS))
                .containsExactly(fromMultiValueMap(of("Foo", asList("Foo1", "Foo2"), "Bar", asList("Bar1", "Bar2"))));

            return null;
        }).when(filterChain).doFilter(request, response);

        sut.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
