/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.HttpClient;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;

/**
 * Factory class that creates {@link HttpClient} instances based on the given {@link Destination}.
 * <p>
 * <strong>Caution:</strong> Implementations must be thread-safe.
 * <p>
 * A default implementation of this interface can be instantiated by using the {@link ApacheHttpClient5FactoryBuilder}.
 * </p>
 *
 * @since 4.20.0
 */
@Beta
@FunctionalInterface
public interface ApacheHttpClient5Factory
{
    /**
     * Creates an {@link HttpClient} independent of any destination.
     * <p>
     * <strong>Caution:</strong> Implementations must ensure that this method is thread-safe.
     *
     * @return An {@code HttpClient}, independent of any destination.
     * @throws HttpClientInstantiationException
     *             if there occurred an error during the creation of the client.
     */
    @Nonnull
    default HttpClient createHttpClient()
    {
        return createHttpClient(null);
    }

    /**
     * Creates an {@link HttpClient} based on the given {@link HttpDestinationProperties}.
     * <p>
     * <strong>Caution:</strong> Implementations must ensure that this method is thread-safe.
     *
     * @param destination
     *            The destination to create the {@code HttpClient} for.
     * @return An {@code HttpClient} based on the given Destination.
     * @throws DestinationAccessException
     *             if the type of the destination is not HTTP.
     * @throws HttpClientInstantiationException
     *             if there occurred an error during the creation of the client.
     */
    @Nonnull
    HttpClient createHttpClient( @Nullable final HttpDestinationProperties destination )
        throws DestinationAccessException,
            HttpClientInstantiationException;
}
