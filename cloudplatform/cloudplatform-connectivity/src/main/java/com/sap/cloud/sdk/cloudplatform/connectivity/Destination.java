/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

/**
 * Platform independent representation of a destination as a collection of key-value pairs, can be converted to a
 * {@link HttpDestination} or a {@link RfcDestination}.
 */
public interface Destination extends DestinationProperties
{
    /**
     * Converts this object as a {@code HttpDestination},
     *
     * @return The converted destination.
     *
     * @throws IllegalArgumentException
     *             if this object cannot be converted as a {@code HttpDestination}.
     */
    @Nonnull
    default HttpDestination asHttp()
        throws IllegalArgumentException
    {
        if( this instanceof HttpDestination ) {
            return (HttpDestination) this;
        }
        return DefaultHttpDestination.fromDestination(this).build();
    }

    /**
     * Verifies that this object can be converted to a {@code HttpDestination}.
     *
     * @return {@code true}, if a call to {@link #asHttp()} will succeed; {@code false} otherwise.
     */
    default boolean isHttp()
    {
        return this instanceof HttpDestination || DefaultHttpDestination.canBeConstructedFrom(this);
    }

    /**
     * Converts this object as a {@code RfcDestination},
     *
     * @return The converted destination.
     *
     * @throws IllegalArgumentException
     *             if this object cannot be converted as a {@code RfcDestination}.
     */
    @Nonnull
    default RfcDestination asRfc()
        throws IllegalArgumentException
    {
        if( this instanceof RfcDestination ) {
            return (RfcDestination) this;
        }
        return DefaultRfcDestination.fromProperties(this);
    }

    /**
     * Verifies that this object can be converted to a {@code RfcDestination}.
     *
     * @return {@code true}, if a call to {@link #asRfc()} will succeed; {@code false} otherwise.
     */
    default boolean isRfc()
    {
        return this instanceof RfcDestination || DefaultRfcDestination.canBeConstructedFrom(this);
    }
}
