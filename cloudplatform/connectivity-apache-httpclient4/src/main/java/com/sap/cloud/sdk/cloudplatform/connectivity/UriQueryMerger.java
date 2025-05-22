package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;

/**
 * Interface to resolve the request URI for a given request. This is used to determine the full request URI.
 */
@FunctionalInterface
public interface UriQueryMerger
{

    /**
     * Returns the request URI for the given request. This method is used to resolve the request URI
     *
     * @param requestUri
     *            the request uri to merge.
     * @return the request URI.
     */
    @Nonnull
    URI mergeRequestUri( @Nonnull final URI requestUri );
}
