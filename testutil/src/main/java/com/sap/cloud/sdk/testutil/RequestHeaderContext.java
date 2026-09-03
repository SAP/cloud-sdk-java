package com.sap.cloud.sdk.testutil;

import java.util.Map;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderThreadContextListener;

/**
 * API for setting and clearing the request headers for the current thread.
 *
 * @since 5.35.0
 */
public interface RequestHeaderContext extends TestContextApi
{
    /**
     * Set the given headers for the current thread.
     *
     * @param headers
     *            the headers to use
     * @return the header container
     */
    @Nonnull
    default RequestHeaderContainer setRequestHeaders( @Nonnull final Map<String, String> headers )
    {
        return setRequestHeaders(DefaultRequestHeaderContainer.fromSingleValueMap(headers));
    }

    /**
     * Set the given headers for the current thread.
     *
     * @param headers
     *            the headers to use
     * @return the header container
     */
    @Nonnull
    default RequestHeaderContainer setRequestHeaders( @Nonnull final RequestHeaderContainer headers )
    {
        setProperty(RequestHeaderThreadContextListener.PROPERTY_REQUEST_HEADERS, headers);
        return headers;
    }

    /**
     * Clear the request headers for the current thread.
     */
    default void clearRequestHeaders()
    {
        setProperty(RequestHeaderThreadContextListener.PROPERTY_REQUEST_HEADERS, null);
    }
}
