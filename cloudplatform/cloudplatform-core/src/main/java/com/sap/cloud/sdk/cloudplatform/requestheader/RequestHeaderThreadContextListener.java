/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.requestheader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link ThreadContextListener} that ensures the correct initialization of the
 * {@link RequestHeaderContainer} when working with non-container managed threads on all supported Cloud platforms.
 */
@Slf4j
public class RequestHeaderThreadContextListener implements ThreadContextListener
{
    /**
     * The ThreadContext key.
     */
    public static final String PROPERTY_REQUEST_HEADERS =
        RequestHeaderThreadContextListener.class.getName() + ":request_headers";

    /**
     * The {@link RequestHeaderContainer} to be used by this listener.
     */
    @Nullable
    private final RequestHeaderContainer requestHeaders;

    /**
     * Initializes a new instance of the {@link RequestHeaderThreadContextListener} class without a
     * {@link RequestHeaderContainer}.
     */
    public RequestHeaderThreadContextListener()
    {
        requestHeaders = null;
    }

    /**
     * Initializes a new instance of the {@link RequestHeaderThreadContextListener} class with the given
     * {@code requestHeaders}.
     *
     * @param requestHeaders
     *            The {@link RequestHeaderContainer} to use.
     */
    public RequestHeaderThreadContextListener( @Nonnull final RequestHeaderContainer requestHeaders )
    {
        this.requestHeaders = requestHeaders;
    }

    @Override
    public int getPriority()
    {
        return DefaultPriorities.REQUEST_HEADER_LISTENER;
    }

    @Override
    public void beforeInitialize( @Nonnull final ThreadContext threadContext )
    {
        if( requestHeaders != null ) {
            threadContext.setProperty(PROPERTY_REQUEST_HEADERS, Property.of(requestHeaders));
        } else {
            threadContext
                .setPropertyIfAbsent(
                    PROPERTY_REQUEST_HEADERS,
                    Property.decorateCallable(RequestHeaderAccessor::getHeaderContainer));
        }
    }
}
