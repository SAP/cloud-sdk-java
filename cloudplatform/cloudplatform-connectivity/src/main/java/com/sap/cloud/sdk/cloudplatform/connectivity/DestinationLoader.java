package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * This describes the interface used to retrieve destinations from some source via the {@link DestinationAccessor}.
 */
@FunctionalInterface
public interface DestinationLoader
{
    /**
     * Retrieves a destination for the given name.
     * <p>
     * The returned {@link Try} object will contain a
     * {@link com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException} in case the
     * destination could not be found or a
     * {@link com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException} with more details in
     * case something else went wrong.
     * <p>
     * If the returned {@code Try} object contains no Exception it will always contain a non-null {@code Destination}
     * value.
     *
     * @param destinationName
     *            The name of the destination to obtain.
     *
     * @return A {@code Try} object containing either the non-null {@code Destination} value or an exception.
     */
    @Nonnull
    default Try<Destination> tryGetDestination( @Nonnull final String destinationName )
    {
        return tryGetDestination(destinationName, DestinationOptions.builder().build());
    }

    /**
     * Retrieves a destination for the given name and configuration options.
     * <p>
     * The returned {@link Try} object will contain a
     * {@link com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException} in case the
     * destination could not be found or a
     * {@link com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException} with more details in
     * case something else went wrong.
     * <p>
     * If the returned {@code Try} object contains no Exception it will always contain a non-null {@code Destination}
     * value.
     *
     * @param destinationName
     *            The name of the destination to obtain.
     * @param options
     *            Additional settings to modify the behaviour of the destination loader.
     *
     * @return A {@code Try} object containing either the non-null {@code Destination} value or an exception.
     */
    @Nonnull
    Try<Destination>
        tryGetDestination( @Nonnull final String destinationName, @Nonnull final DestinationOptions options );
}
