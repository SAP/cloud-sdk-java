/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper for an {@link DestinationLoader}, offering platform agnostic access to destinations.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
@Slf4j
public final class DestinationAccessor
{
    /**
     * The loader containing the platform specific logic to retrieve destinations.
     */
    @Getter
    @Nonnull
    private static DestinationLoader loader = initDestinationLoader();

    /**
     * Sets the loader to be used for all future method invocations on this {@code DestinationAccessor}.
     * <p>
     * If {@code null} is provided this will fallback to the default loader, which has the following priority list:
     * <ol>
     * <li>Read the destination from the <i>destinations</i> environment variable</li>
     * <li>Read the destination with the loader found via the {@link java.util.ServiceLoader} pattern</li>
     * </ol>
     *
     * If the {@code ServiceLoader} finds multiple {@link DestinationLoader} it will behave as if none was given, as no
     * deterministic order of resolution could be identified. Use the {@link DestinationLoaderChain} to create your own
     * priority.
     *
     * @param loader
     *            The new loader to be used. If {@code null} is provided the default loader will be used instead.
     */
    public static void setLoader( @Nullable final DestinationLoader loader )
    {
        if( log.isInfoEnabled() ) {
            if( loader != null ) {
                final String msg = "Setting the current {} used to a custom instance of {}.";
                log.info(msg, DestinationLoader.class, loader.getClass());
            } else {
                final String msg = "Resetting the current {} used to the default {}.";
                log.info(msg, DestinationLoader.class, DestinationLoaderChain.class);
            }
        }
        DestinationAccessor.loader = Option.of(loader).getOrElse(DestinationAccessor::initDestinationLoader);
    }

    private static DestinationLoader initDestinationLoader()
    {
        final String msg = "Creating a new DestinationLoaderChain with {} as the primary {} implementation.";
        log.info(msg, EnvVarDestinationLoader.class, DestinationLoader.class);

        final DestinationLoaderChain.DestinationLoaderChainBuilder loaderChainBuilder =
            DestinationLoaderChain.builder(new EnvVarDestinationLoader());

        final Try<DestinationLoader> loaderFromModule = FacadeLocator.getFacade(DestinationLoader.class);

        loaderFromModule.peek(loader -> {
            final String inf = "Using an instance of {} as the secondary {} implementation.";
            log.info(inf, loader.getClass(), DestinationLoader.class);
            loaderChainBuilder.append(loader);
        });

        return loaderChainBuilder.build();
    }

    /**
     * Loads the destination with the given name.
     *
     * @param destinationName
     *            The name of the destination to search for.
     *
     * @return The loaded destination.
     *
     * @throws DestinationNotFoundException
     *             In case the destination could not be found.
     * @throws DestinationAccessException
     *             More details in case something else went wrong.
     */
    @Nonnull
    public static Destination getDestination( @Nonnull final String destinationName )
        throws DestinationAccessException
    {
        return tryGetDestination(destinationName).getOrElseThrow(failure -> {
            if( failure instanceof DestinationAccessException ) {
                throw (DestinationAccessException) failure;
            } else if( failure instanceof DestinationNotFoundException ) {
                throw (DestinationNotFoundException) failure;
            } else {
                final String msg = "Failed to get destination with name '" + destinationName + "'.";
                throw new DestinationAccessException(msg, failure);
            }
        });
    }

    /**
     * Loads the destination with the given name.
     *
     * @param destinationName
     *            The name of the destination to search for.
     *
     * @return A {@link Try} containing either the destination or an exception describing what went wrong.
     */
    @Nonnull
    public static Try<Destination> tryGetDestination( @Nonnull final String destinationName )
    {
        return getLoader().tryGetDestination(destinationName);
    }

    /**
     * Register an {@link DestinationLoader} created at runtime. If the {@link #getLoader() current destination loader}
     * is already a {@link DestinationLoaderChain} it will be added to the end of this chain. Otherwise a new chain will
     * be created with the current and the new loader.
     *
     * To reset the loader of this accessor use {@link #setLoader(DestinationLoader) setLoader(null)}.
     *
     * @param newLoader
     *            The destination loader to register.
     *
     * @see #prependDestinationLoader(DestinationLoader)
     */
    public static void appendDestinationLoader( @Nonnull final DestinationLoader newLoader )
    {
        final DestinationLoader staticLoader = getLoader();
        if( newLoader == staticLoader ) {
            return;
        }
        if( staticLoader instanceof DestinationLoaderChain ) {
            ((DestinationLoaderChain) staticLoader).appendLoader(newLoader);
        } else {
            setLoader(DestinationLoaderChain.builder(staticLoader).append(newLoader).build());
        }
    }

    /**
     * Register an {@link DestinationLoader} created at runtime. If the {@link #getLoader() current destination loader}
     * is already a {@link DestinationLoaderChain} it will be added at the beginning of this chain. Otherwise a new
     * chain will be created with the new and the current loader.
     *
     * To reset the loader of this accessor use {@link #setLoader(DestinationLoader) setLoader(null)}.
     *
     * @param newLoader
     *            The destination loader to register.
     *
     * @see #appendDestinationLoader(DestinationLoader)
     */
    public static void prependDestinationLoader( @Nonnull final DestinationLoader newLoader )
    {
        final DestinationLoader staticLoader = getLoader();
        if( newLoader == staticLoader ) {
            return;
        }
        if( staticLoader instanceof DestinationLoaderChain ) {
            ((DestinationLoaderChain) staticLoader).prependLoader(newLoader);
        } else {
            setLoader(DestinationLoaderChain.builder(newLoader).append(staticLoader).build());
        }
    }
}
