/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
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
}
