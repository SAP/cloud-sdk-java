/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Immutable implementation of the {@link DestinationLoader} interface providing a way to define a chain
 * {@code DestinationLoader}s to go through until a destination was found.
 */
@EqualsAndHashCode
@ToString
@Slf4j
public class DestinationLoaderChain implements DestinationLoader
{
    private final List<DestinationLoader> loaders;

    /**
     * Instantiates a {@link DestinationLoaderChain} based on the given loaders in the order returned by
     * {@link Collection#iterator()}.
     *
     * @param loaders
     *            The loaders to go though to find a destination.
     *
     * @throws IllegalArgumentException
     *             If the given collection of loaders is empty.
     */
    public DestinationLoaderChain( @Nonnull final Collection<DestinationLoader> loaders )
        throws IllegalArgumentException
    {
        if( loaders.isEmpty() ) {
            throw new IllegalArgumentException("The given set of loaders must not be empty.");
        }
        this.loaders = new ArrayList<>(loaders);
    }

    /**
     * Entry point to build a custom {@code DestinationLoaderChain}.
     *
     * @param firstLoader
     *            The first loader to be used by the chain.
     *
     * @return A {@code DestinationLoaderChainBuilder} without any registered {@code DestinationLoaderChain}.
     */
    @Nonnull
    public static DestinationLoaderChainBuilder builder( @Nonnull final DestinationLoader firstLoader )
    {
        return new DestinationLoaderChainBuilder().append(firstLoader);
    }

    @Nonnull
    @Override
    public
        Try<Destination>
        tryGetDestination( @Nonnull final String destinationName, @Nonnull final DestinationOptions options )
    {
        if( loaders.isEmpty() ) {
            return Try
                .failure(
                    new DestinationAccessException(
                        "No destination loaders were registered. "
                            + "Make sure at least one loader is available on the classpath or is programmatically registered, e.g. by having the 'scp-cf'  dependency on the classpath."));
        }

        final ArrayList<Throwable> suppressedList = new ArrayList<>();

        for( final DestinationLoader loader : loaders ) {
            log
                .debug(
                    "Delegating destination lookup for destination {} to the destination loader {}.",
                    destinationName,
                    loader.getClass().getSimpleName());

            final Try<Destination> result = loader.tryGetDestination(destinationName, options);

            if( result.isSuccess() ) {
                log
                    .debug(
                        "Destination loader {} successfully loaded destination {}.",
                        loader.getClass().getSimpleName(),
                        destinationName);

                return result;
            }
            final Throwable cause = result.getCause();

            if( !hasCauseAssignableFrom(cause, DestinationNotFoundException.class) ) {
                log
                    .debug(
                        "Destination loader {} returned an exception when loading destination {}:",
                        loader.getClass().getSimpleName(),
                        destinationName,
                        cause);
                return result;
            }
            log
                .debug(
                    "No destination with name '{}' was found in destination loader {}.",
                    destinationName,
                    loader.getClass().getSimpleName());

            suppressedList.add(cause);
        }

        final DestinationNotFoundException preparedException =
            new DestinationNotFoundException(
                destinationName,
                "Destination could not be found in any of the registered loaders. Check the suppressed exceptions and the logs above to see which loaders where queried.");
        suppressedList.forEach(preparedException::addSuppressed);
        return Try.failure(preparedException);
    }

    void appendLoader( @Nonnull final DestinationLoader loader )
    {
        loaders.add(loader);
    }

    void prependLoader( @Nonnull final DestinationLoader loader )
    {
        loaders.add(0, loader);
    }

    /**
     * Helper method that checks if a given exception is contained in any of the throwable's causes
     *
     * @param t
     *            The throwable to check.
     * @param cls
     *            The class of the exception to check for.
     * @return True, {@code t} contains the exception, otherwise false.
     */
    private static boolean hasCauseAssignableFrom( @Nonnull final Throwable t, @Nonnull final Class<?> cls )
    {
        return ExceptionUtils.getThrowableList(t).stream().map(Throwable::getClass).anyMatch(cls::isAssignableFrom);
    }

    /**
     * Builder class to construct a {@code DestinationLoaderChain}.
     */
    public static final class DestinationLoaderChainBuilder
    {
        private final List<DestinationLoader> loaders = new ArrayList<>();

        private DestinationLoaderChainBuilder()
        {
            // Should only be instantiated via the static builder method in the main class
        }

        /**
         * The next {@code DestinationLoader} to query if the previous loaders did not return any {@code Destination}.
         *
         * @param nextLoader
         *            The next loader to register
         * @return This builder.
         */
        @Nonnull
        public DestinationLoaderChainBuilder append( @Nonnull final DestinationLoader nextLoader )
        {
            loaders.add(nextLoader);
            return this;
        }

        /**
         * Finally create the immutable {@code DestinationLoaderChain} based on the given order of
         * {@code DestinationLoader}s.
         *
         * @return A fully functional {@code DestinationLoaderChain}.
         */
        @Nonnull
        public DestinationLoaderChain build()
        {
            return new DestinationLoaderChain(loaders);
        }
    }
}
