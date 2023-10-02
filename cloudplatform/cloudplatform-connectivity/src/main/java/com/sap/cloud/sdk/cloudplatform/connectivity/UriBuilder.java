/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A builder for the {@link URI} class, taking several optional parameter, constructing a valid URI out of them.
 */
public class UriBuilder
{
    /**
     * Builds an {@link URI} based on the given parameter.
     *
     * @param scheme
     *            The connection scheme of the URI.
     * @param userInfo
     *            The user information for the URI.
     * @param host
     *            The connection host of the URI.
     * @param port
     *            The port to connect on. Use -1 to ignore the port.
     * @param path
     *            The path to the resource on the host (if the host is given).
     * @param query
     *            The query to append to the URI.
     * @param fragment
     *            Any remaining fragments that should be appended to the URI.
     *
     * @return A valid URI bases on the components given.
     *
     * @throws URISyntaxException
     *             If the given parameter cannot be combined into a valid {@link URI}, see {@link URI#URI(String)}
     */
    @Nonnull
    public URI build(
        @Nullable final String scheme,
        @Nullable final String userInfo,
        @Nullable final String host,
        final int port,
        @Nullable final String path,
        @Nullable final String query,
        @Nullable final String fragment )
        throws URISyntaxException
    {
        final StringBuilder sb = new StringBuilder();

        if( scheme != null ) {
            sb.append(scheme);
            sb.append(':');
        }

        if( host != null ) {
            sb.append("//");
            if( userInfo != null ) {
                sb.append(userInfo);
                sb.append('@');
            }
            final boolean needBrackets = host.indexOf(':') >= 0 && !host.startsWith("[") && !host.endsWith("]");
            if( needBrackets ) {
                sb.append('[');
            }
            sb.append(host);
            if( needBrackets ) {
                sb.append(']');
            }
            if( port != -1 ) {
                sb.append(':');
                sb.append(port);
            }
        }

        if( path != null ) {
            sb.append(path);
        }

        if( query != null ) {
            sb.append('?');
            sb.append(query);
        }

        if( fragment != null ) {
            sb.append('#');
            sb.append(fragment);
        }

        return new URI(sb.toString());
    }
}
