/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;

import io.vavr.control.Try;

/**
 * Representation of a generic executable OData request builder as a fluent interface.
 *
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
public interface RequestBuilderExecutable<ResultT>
{
    /**
     * Execute the OData request.
     *
     * @param destination
     *            The destination to be used as request target.
     * @return The generic OData response result.
     *
     * @throws DestinationAccessException
     *             If there is an issue accessing the {@link Destination}.
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     * @throws ODataException
     *             If the OData request execution failed. Please find the documentation for {@link ODataException}
     *             possible sub-types and error scenarios they can occur in.
     */
    @Nonnull
    ResultT execute( @Nonnull final Destination destination );

    /**
     * Safely execute the OData request.
     *
     * @param destination
     *            The destination to be used as request target.
     * @return The generic OData response result wrapped in a {@code Try} block.
     */
    @Nonnull
    default Try<ResultT> tryExecute( @Nonnull final Destination destination )
    {
        return Try.of(() -> execute(destination));
    }
}
