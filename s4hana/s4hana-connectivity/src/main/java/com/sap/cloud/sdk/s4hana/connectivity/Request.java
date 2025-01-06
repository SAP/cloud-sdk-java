/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import lombok.Getter;
import lombok.Setter;

/**
 * Common interface for ERP queries.
 *
 * @param <RequestT>
 *            The generic request type.
 * @param <RequestResultT>
 *            The generic request result type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public abstract class Request<RequestT extends Request<RequestT, RequestResultT>, RequestResultT extends RequestResult<RequestT, RequestResultT>>
{
    private static final AtomicLong requestIdCounter = new AtomicLong();

    /**
     * Represents an identifier that allows to correlate queries between the Cloud cloudplatform and the ERP.
     * <p>
     * <strong>Important:</strong> The identifier does not provide any guarantees with respect to persistence across the
     * application lifecycle. In particular, the identifier will be reset to 1 when restarting the application.
     */
    @Getter
    private final long requestId;

    /**
     * Custom execution duration threshold indicating that the request is long running.
     */
    @Nullable
    @Getter
    @Setter
    private Duration longRunningRequestThreshold = null;

    /**
     * Set of HTTP headers to send to the underlying request executor. Note that these headers will only be added to
     * HTTP-based queries.
     */
    @Nonnull
    @Getter
    private final List<Header> customHttpHeaders = new LinkedList<>();

    /**
     * Default constructor.
     */
    protected Request()
    {
        requestId = requestIdCounter.incrementAndGet();
    }

    /**
     * Returns the method name that originally constructed this ERP request.
     *
     * @return The method name that originally constructed this ERP request. This information is used for debugging.
     */
    @Nonnull
    public abstract String getConstructedByMethod();

    /**
     * Returns a String representation of the data being accessed by this request. If present, an audit log entry is
     * written using the given data.
     *
     * @return A optional String representation of the data being access by this request. If present, an audit log entry
     *         is written using the given data.
     */
    @Nullable
    public String getReadAccessData()
    {
        return null;
    }

    /**
     * Get the current instance reference.
     *
     * @return The current object.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    protected RequestT getThis()
    {
        return (RequestT) this;
    }

    /**
     * Fluent method which adds a custom HTTP header to this request. Note that these headers will only be added to
     * HTTP-based queries. If the header with the provided name already exists, the value is overwritten.
     *
     * @param name
     *            Header parameter name.
     * @param value
     *            Header parameter value.
     *
     * @return The same {@link Request} instance, so that this method can be used again in a fluent API style.
     */
    @Nonnull
    public RequestT withHeader( @Nonnull final String name, @Nullable final String value )
    {
        return withHeader(new Header(name, value));
    }

    /**
     * Fluent method which adds a custom header to this request. Note that these headers will only be added to
     * HTTP-based queries. If the header with the provided name already exists, the value is overwritten.
     *
     * @param header
     *            The custom header.
     *
     * @return The same {@link Request} instance, so that this method can be used again in a fluent API style.
     */
    @Nonnull
    public RequestT withHeader( @Nonnull final Header header )
    {
        customHttpHeaders.add(header);
        return getThis();
    }

    /**
     * Fluent method which adds the custom headers from another request into this request. Note that these headers will
     * only be added to HTTP-based queries. If any headers in the other request already exist in this request, the
     * values will be overwritten.
     *
     * @param otherRequest
     *            Other request to get header parameters from.
     *
     * @return The same {@link Request} instance, so that this method can be used again in a fluent API style.
     */
    @Nonnull
    public RequestT withSameCustomHttpHeadersAs( @Nonnull final Request<?, ?> otherRequest )
    {
        customHttpHeaders.addAll(otherRequest.getCustomHttpHeaders());
        return getThis();
    }

    /**
     * Executes a given request using the given {@link Destination}.
     *
     * @param destination
     *            The {@link Destination} to be used for request execution.
     *
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException
     *             If there is an issue while serializing the request.
     *
     * @throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
     *             If there is an issue while executing the request.
     *
     * @throws DestinationNotFoundException
     *             If no destination with the name specified in the {@link Destination} can be found.
     *
     * @throws DestinationAccessException
     *             If there is an issue while accessing destination information.
     *
     * @return The executed request result.
     */
    @Nonnull
    public abstract RequestResultT execute( @Nonnull final Destination destination )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException,
            DestinationNotFoundException,
            DestinationAccessException;
}
