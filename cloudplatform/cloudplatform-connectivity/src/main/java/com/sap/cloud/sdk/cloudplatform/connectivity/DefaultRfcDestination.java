package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Immutable default implementation of the {@link RfcDestination} interface.
 *
 * @deprecated Please use {@link DefaultDestination} instead.
 */
@Deprecated
@EqualsAndHashCode
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class DefaultRfcDestination implements RfcDestination
{
    @Delegate
    private final DestinationProperties properties;

    /**
     * Creates an immutable destination with the given properties.
     *
     * @param properties
     *            The properties of this destination.
     * @return An immutable destination with the given properties.
     */
    @Nonnull
    public static RfcDestination fromProperties( @Nonnull final DestinationProperties properties )
    {
        if( !canBeConstructedFrom(properties) ) {
            throw new IllegalArgumentException("Properties do not contain a destination name.");
        }
        return new DefaultRfcDestination(properties);
    }

    static boolean canBeConstructedFrom( @Nonnull final DestinationProperties properties )
    {
        return properties.get(DestinationProperty.NAME).isDefined();
    }
}
