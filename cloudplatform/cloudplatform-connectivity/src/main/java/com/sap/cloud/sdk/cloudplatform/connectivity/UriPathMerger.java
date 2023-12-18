/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationPathsNotMergeableException;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class offering the ability to merge two URIs.
 */
@Slf4j
public class UriPathMerger
{
    /**
     * Merges two {@code URI} addresses, checking that both {@code URIs} have the same scheme, host and port. If
     * secondary URI is absolute (i.e. it has schema and host definition) then path of primary URI is omitted.
     * Otherwise, the paths are appended. Query parameters are combined. Fragment of secondary URI is preferred.
     *
     * @param primaryUri
     *            The primary {@code URI}, usually pointing to the destination.
     * @param secondaryUri
     *            The secondary {@code URI}, usually identifying the request at runtime.
     *
     * @throws DestinationPathsNotMergeableException
     *             If either of the request paths of the the given URIs are {@code null} or the URIs differ in the used
     *             schema, host, or port.
     *
     * @return The merged {@code URI}, representing the given request executed on the given destination.
     */
    @Nonnull
    public URI merge( @Nonnull final URI primaryUri, @Nullable final URI secondaryUri )
        throws DestinationPathsNotMergeableException
    {
        if( primaryUri.toString().isEmpty() ) {
            if( secondaryUri == null || !secondaryUri.isAbsolute() ) {
                throw new DestinationPathsNotMergeableException(
                    "The destination URI may be empty ONLY if the request URI is absolute.");
            }

            return secondaryUri.getRawPath().isEmpty() ? secondaryUri.resolve("/") : secondaryUri;
        }

        if( !primaryUri.isAbsolute() ) {
            throw new DestinationPathsNotMergeableException("The destination URI must be absolute OR empty.");
        }

        if( secondaryUri == null ) {
            return primaryUri.getRawPath().isEmpty() ? primaryUri.resolve("/") : primaryUri;
        }

        assertSecondaryHostMatchesPrimaryHost(primaryUri, secondaryUri);

        final String mergeRootPath = getMergedPath(primaryUri, secondaryUri);

        final String mergeQuery =
            Stream
                .of(primaryUri.getRawQuery(), secondaryUri.getRawQuery())
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining("&"));

        try {
            final URI mergedUri =
                new UriBuilder()
                    .build(
                        primaryUri.getScheme(),
                        primaryUri.getRawAuthority(),
                        mergeRootPath,
                        StringUtils.trimToNull(mergeQuery),
                        Option.of(secondaryUri.getRawFragment()).getOrElse(primaryUri::getRawFragment));

            log.debug("Merged {} and {} into {}", primaryUri, secondaryUri, mergeRootPath);
            return mergedUri;
        }
        catch( final URISyntaxException e ) {
            throw new ShouldNotHappenException("Failed to merge request URI.", e);
        }
    }

    private void assertSecondaryHostMatchesPrimaryHost( @Nonnull final URI primaryUri, @Nonnull final URI secondaryUri )
        throws DestinationPathsNotMergeableException
    {
        if( secondaryUri.getRawAuthority() != null ) {
            if( !primaryUri.getRawAuthority().equals(secondaryUri.getRawAuthority()) ) {
                throw new DestinationPathsNotMergeableException(
                    String
                        .format(
                            "URIs defined in destination '%s' and supplied by application code '%s' differ.",
                            primaryUri,
                            secondaryUri));
            }
        }
    }

    /**
     * Merge the path segments of two URI instances together.<br>
     *
     * If the request path is relative we need to merge it with the destination path. If, instead, the request path is
     * absolute, then the request path already contains the full path and doesn't need to be merged with the destination
     * path.
     *
     * @param destinationUri
     *            The general URI.
     * @param requestUri
     *            The specific URI.
     * @return The merged path.
     */
    @Nonnull
    private String getMergedPath( @Nonnull final URI destinationUri, @Nonnull final URI requestUri )
    {
        final String requestPath = requestUri.getRawPath();
        final String destinationPath = destinationUri.getRawPath();

        if( requestUri.isAbsolute() ) {
            log.trace("Using specific request path: {}", requestPath);
            return StringUtils.prependIfMissing(requestPath, "/");
        }

        final String mergedPath = mergeUriRawPaths(destinationPath, requestPath);
        log.debug("Merging request path {} into destination path {}: {}", requestPath, destinationPath, mergedPath);
        return mergedPath;
    }

    @Nonnull
    private String mergeUriRawPaths( @Nullable final String firstRawPath, @Nullable final String secondRawPath )
        throws DestinationPathsNotMergeableException
    {
        if( firstRawPath == null || secondRawPath == null ) {
            throw new DestinationPathsNotMergeableException("Cannot merge URI paths that are null.");
        }

        final String first = firstRawPath.trim().replaceFirst("^/", "");
        final String second = secondRawPath.trim().replaceFirst("^/", "");

        final String result;

        if( second.isEmpty() ) {
            result = first;
        } else {
            if( second.startsWith(first) ) {
                result = second;
            } else {
                result = (first.endsWith("/") ? first : first + "/") + second;
            }
        }

        return result.startsWith("/") ? result : "/" + result;
    }
}
