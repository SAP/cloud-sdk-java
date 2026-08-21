package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;

/**
 * Interface to resolve the request URI for a given request. Used to determine the destination-contributed query
 * parameters so that next-link pagination can strip duplicate parameters.
 */
@FunctionalInterface
public interface UriQueryMerger
{
    /**
     * Returns the fully-merged request URI for the given relative request URI. Merges the destination base URL,
     * destination URL query parameters, and destination property query parameters into the URI.
     *
     * @param requestUri
     *            The relative request URI to merge.
     * @return The merged request URI.
     */
    @Nonnull
    URI mergeRequestUri( @Nonnull URI requestUri );
}
