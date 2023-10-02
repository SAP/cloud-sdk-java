/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCount;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Representation of an OData query for count, as a fluent interface for further configuring the request and
 * {@link #executeRequest(Destination) executing} it.
 */
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
public class FluentHelperCount
{
    private final ODataRequestCount request;

    /**
     * Executes the underlying query for count, using the stored values, plus any query modifiers that were previously
     * called.
     *
     * @param destination
     *            The target system this request should be issued against.
     * @return The number of tuples that match the criteria specified in the query.
     *
     * @throws DestinationAccessException
     *             If there is an issue accessing the
     *             {@link com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination}.
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     * @throws com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException
     *             If the OData request execution failed. Please find the documentation for {@link ODataException}
     *             possible sub-types and error scenarios they can occur in.
     */
    public long executeRequest( @Nonnull final Destination destination )
    {
        final ODataRequestCount requestCount = toRequest();

        final ODataRequestResultGeneric result = requestCount.execute(HttpClientAccessor.getHttpClient(destination));

        return result.as(Long.class);
    }

    /**
     * Creates an instance of {@link ODataRequestCount}.
     * <p>
     * The following settings are used:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the entity collection name</li>
     * <li>the OData query</li>
     * </ul>
     *
     * @return A new count request with the given configuraiton.
     */
    @Nonnull
    public ODataRequestCount toRequest()
    {
        return request;
    }

    /**
     * Activates the CSRF token retrieval for this OData request. This is useful if the server does require CSRF tokens
     * as part of the request.
     *
     * @return The same builder
     */
    @Nonnull
    public FluentHelperCount withCsrfToken()
    {
        request.setCsrfTokenRetriever(new DefaultCsrfTokenRetriever());
        return this;
    }
}
