/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;

/**
 * Used to build S/4HANA service URI, considering parameters such as base URI and relative path.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class ServiceUriBuilder
{
    /**
     * Adds a slash ('/') as a prefix if it is not yet present.
     *
     * @param path
     *            The String to prefix with a slash.
     * @return A String guaranteed to start with a slash.
     */
    protected static String prependSlashIfMissing( final String path )
    {
        return path.startsWith("/") ? path : "/" + path;
    }

    /**
     * Adds a slash ('/') as a suffix if it is not yet present.
     *
     * @param path
     *            The String to suffix with a slash.
     * @return A String guaranteed to end with a slash.
     */
    protected static String appendSlashIfMissing( final String path )
    {
        return path.endsWith("/") ? path : path + "/";
    }

    /**
     * Encloses the given String with a slash ('/') if they are not yet present.
     *
     * @param path
     *            The String to pre- and suffix with a slash.
     * @return A String guaranteed to start and end with a slash.
     */
    protected static String encloseSlashesIfMissing( final String path )
    {
        return prependSlashIfMissing(appendSlashIfMissing(path));
    }

    /**
     * Builds a ERP service URI for a given relative path.
     *
     * @param baseUri
     *            The URI to replace/adjust the path property.
     * @param relativePath
     *            The relative path to be used.
     *
     * @return A new URI object based on the given {@code baseUri}, modified by adjusting the contained path with the
     *         given {@code relativePath}.
     * @throws IllegalArgumentException
     *             If the new URI object could not be created, most likely due to a wrongly formatted relativePath.
     */
    @Nonnull
    public URI build( @Nonnull final URI baseUri, @Nonnull final String relativePath )
    {
        try {
            final String path = encloseSlashesIfMissing(baseUri.getPath()) + relativePath.replaceFirst("^/", "");

            return new URI(
                baseUri.getScheme(),
                baseUri.getUserInfo(),
                baseUri.getHost(),
                baseUri.getPort(),
                path,
                baseUri.getQuery(),
                baseUri.getFragment());
        }
        catch( final URISyntaxException e ) {
            throw new IllegalArgumentException("Failed to build ERP service URI.", e);
        }
    }
}
