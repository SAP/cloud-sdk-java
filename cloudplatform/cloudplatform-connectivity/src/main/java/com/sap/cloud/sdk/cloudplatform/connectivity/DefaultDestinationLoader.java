/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;
import lombok.NoArgsConstructor;

/**
 * Default implementation of a {@link DestinationLoader} with convenience methods to add {@link HttpDestination HTTP}
 * and {@link RfcDestination RFC} destinations.
 */
@NoArgsConstructor
public final class DefaultDestinationLoader implements DestinationLoader
{
    private final Map<String, Try<Destination>> destinations = new ConcurrentHashMap<>();

    /**
     * Register a {@link Destination} to this destination loader. It must have a non-empty name to be identified by. If
     * a destination with the same name was registered previously it will be replaced by this one.
     *
     * @param destination
     *            A destination this loader should return when queried.
     * @return This loader instance.
     *
     * @throws IllegalArgumentException
     *             if the destination name is null or empty.
     */
    @Nonnull
    public DefaultDestinationLoader registerDestination( @Nonnull final Destination destination )
    {
        destinations.put(getDestinationNameOrThrow(destination), Try.success(destination));
        return this;
    }

    @Nonnull
    @Override
    public
        Try<Destination>
        tryGetDestination( @Nonnull final String destinationName, @Nonnull final DestinationOptions options )
    {
        return destinations
            .computeIfAbsent(destinationName, ignore -> Try.failure(new DestinationNotFoundException(destinationName)));
    }

    private static String getDestinationNameOrThrow( final Destination destination )
    {
        return destination
            .get(DestinationProperty.NAME)
            .filter(name -> !Strings.isNullOrEmpty(name))
            .getOrElseThrow(
                () -> new IllegalArgumentException("The supplied destination is lacking a name to be identified by."));
    }
}
