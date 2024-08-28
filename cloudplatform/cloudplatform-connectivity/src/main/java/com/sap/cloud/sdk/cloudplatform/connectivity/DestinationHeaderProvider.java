/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Allows to provide additional headers for a specific destination.
 */
public interface DestinationHeaderProvider
{
    /**
     * Provides a list of {@link Header} objects which should be used with the given destination for a request URI.
     *
     * @param requestContext
     *            The destination and request specific context.
     * @return The headers to use with the given destination.
     */
    @Nonnull
    List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext );

    /**
     * Defines the header provider cardinality: how many instances the header provider class can be attached to a single
     * destination.
     * <ul>
     * <li>&lt;1: undefined</li>
     * <li>1: consider only last attached instance</li>
     * <li>n&gt;1: last n attached instances.</li>
     * </ul>
     *
     * @return The header provider cardinality.
     */
    default int getCardinality()
    {
        return Integer.MAX_VALUE;
    }
}
