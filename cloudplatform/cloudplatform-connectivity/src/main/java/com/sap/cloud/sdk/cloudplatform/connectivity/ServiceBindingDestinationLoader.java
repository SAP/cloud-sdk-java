/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;

/**
 * Represents a class that is capable of transforming a {@link ServiceBinding} and
 * {@link ServiceBindingDestinationOptions} into an {@link HttpDestination}.
 *
 * @since 4.16.0
 */
@FunctionalInterface
@Beta
public interface ServiceBindingDestinationLoader
{
    /**
     * Returns the default <i>loader chain</i> instance. This instance will locate all available
     * {@link ServiceBindingDestinationLoader} implementations (using the <i>service loader pattern</i>) on the
     * classpath. Those instances will be queried <b>in no particular order</b> to try to transform the provided
     * {@link ServiceBindingDestinationOptions} into an {@link HttpDestination}.
     *
     * @return The default <i>loader chain</i> instance.
     */
    @Nonnull
    static ServiceBindingDestinationLoader defaultLoaderChain()
    {
        return DefaultServiceBindingDestinationLoaderChain.DEFAULT_INSTANCE;
    }

    /**
     * Returns a new <i>loader chain</i> instance that will query the provided {@code delegateLoaders} <b>in the
     * provided order</b> to try to transform the provided {@link ServiceBindingDestinationOptions} into an
     * {@link HttpDestination}.
     *
     * @param delegateLoaders
     *            The {@link ServiceBindingDestinationLoader} instances that should be used.
     * @return A new <i>loader chain</i> instance.
     */
    @Nonnull
    static ServiceBindingDestinationLoader newLoaderChain(
        @Nonnull final List<ServiceBindingDestinationLoader> delegateLoaders )
    {
        return new DefaultServiceBindingDestinationLoaderChain(delegateLoaders);
    }

    /**
     * Tries to transform the given {@code serviceBinding} and {@code options} into an {@link HttpDestination}.
     *
     * @param options
     *            The {@link ServiceBindingDestinationOptions} that contain further context information about how the
     *            transformation process should be done.
     * @return Either a {@link Try#success(Object)} if the transformation succeeded, {@link Try#failure(Throwable)}
     *         otherwise.
     *         <p>
     *         In case of a {@link Try#failure(Throwable)}, the cause will either be a
     *         {@link com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException} or a
     *         {@link com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException}.
     *         </p>
     */
    @Nonnull
    Try<HttpDestination> tryGetDestination( @Nonnull final ServiceBindingDestinationOptions options );

    /**
     * Tries to transform the given {@code options} into an {@link HttpDestination}.
     * <p>
     * This method is equivalent to {@code tryGetDestination(options).get();}.
     * </p>
     *
     * @param options
     *            The {@link ServiceBindingDestinationOptions} that contain further context information (such as the
     *            {@link ServiceBinding} that should be transformed) for the transformation process.
     * @return An {@link HttpDestination} that can be used to connect to be bound service.
     * @throws DestinationAccessException
     *             Thrown if the provided {@code options} couldn't be transformed into an {@link HttpDestination}.
     * @throws DestinationNotFoundException
     *             Thrown if no {@link HttpDestination} could be found for the provided {@code options}.
     */
    @Nonnull
    default HttpDestination getDestination( @Nonnull final ServiceBindingDestinationOptions options )
        throws DestinationAccessException,
            DestinationNotFoundException
    {
        return tryGetDestination(options).get();
    }
}
