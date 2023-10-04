/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.servletjakarta;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;

import io.vavr.control.Option;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Servlet filter for storing the current {@link HttpServletRequest} in the current thread context.
 */
@WebFilter( filterName = "RequestAccessorFilter", urlPatterns = "/*" )
@Slf4j
@Beta
public class RequestAccessorFilter implements Filter
{
    @Override
    public void init( @Nonnull final FilterConfig filterConfig )
    {

    }

    @Override
    public void doFilter(
        @Nonnull final ServletRequest request,
        @Nonnull final ServletResponse response,
        @Nonnull final FilterChain filterChain )
    {
        if( request instanceof HttpServletRequest ) {
            try {
                final HttpServletRequest httpRequest = (HttpServletRequest) request;
                final ThreadContext threadContext = new DefaultThreadContext();
                storeServletProperties(httpRequest, threadContext);

                ThreadContextExecutor.using(threadContext).execute(() -> filterChain.doFilter(request, response));
            }
            catch( final Throwable t ) { // ALLOW CATCH THROWABLE
                log.warn("Unexpected servlet filter exception: " + t.getMessage(), t);
                throw new ShouldNotHappenException(t);
            }
        } else {
            if( log.isWarnEnabled() ) {
                log
                    .warn(
                        "Failed to initialize "
                            + ThreadContext.class.getSimpleName()
                            + ": request not of type "
                            + HttpServletRequest.class.getName()
                            + ".");
            }
        }
    }

    private
        void
        storeServletProperties( @Nonnull final HttpServletRequest servlet, @Nonnull final ThreadContext threadContext )
    {
        threadContext
            .setPropertyIfAbsent(
                RequestHeaderThreadContextListener.PROPERTY_REQUEST_HEADERS,
                Property.decorateCallable(() -> extractHeaders(servlet)));
    }

    @Nonnull
    private static RequestHeaderContainer extractHeaders( @Nonnull final HttpServletRequest request )
    {
        final Enumeration<String> headerNames = request.getHeaderNames();
        if( headerNames == null ) {
            return RequestHeaderContainer.EMPTY;
        }

        final Map<String, Collection<String>> headers = new HashMap<>();
        while( headerNames.hasMoreElements() ) {
            final Option<String> headerName = Option.of(headerNames.nextElement());
            headerName
                .map(request::getHeaders)
                .filter(Objects::nonNull)
                .peek(values -> headers.put(headerName.get(), Collections.list(values)));
        }
        return DefaultRequestHeaderContainer.fromMultiValueMap(headers);
    }

    @Override
    public void destroy()
    {

    }
}
