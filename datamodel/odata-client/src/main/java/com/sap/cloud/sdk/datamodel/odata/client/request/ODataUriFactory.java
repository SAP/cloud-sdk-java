/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

/**
 * Builds up OData URLs and ensures correct encoding.
 */
@Slf4j
public class ODataUriFactory
{
    private static final Pattern PATTERN_DELTA_TOKEN =
        Pattern.compile("\\$deltatoken=([^&]+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_SKIP_TOKEN = Pattern.compile("\\$skiptoken=([^&]+)", Pattern.CASE_INSENSITIVE);

    private static final Predicate<String> VALID_URL_QUERY =
        Pattern.compile("^[a-zA-Z0-9/?:@\\-._~!$&'()*+,;=%]*$").asPredicate();

    /**
     * Constructs a URI out of service path, entity path and query string.
     *
     * @param servicePath
     *            The unencoded service path.
     * @param resourcePath
     *            The {@link ODataResourcePath path} identifying the resource to be accessed.
     * @param encodedQuery
     *            Optional: An encoded string representing the URL query part.
     *
     * @return The correctly encoded URI.
     */
    @Nonnull
    static URI createAndEncodeUri(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath resourcePath,
        @Nullable final String encodedQuery,
        @Nonnull final UriEncodingStrategy strategy )
    {
        return createAndEncodeUri(servicePath, resourcePath.toEncodedPathString(strategy), encodedQuery, strategy);
    }

    /**
     * Constructs a URI out of service path, entity path and query string.
     *
     * @param servicePath
     *            The unencoded service path.
     * @param encodedResourcePath
     *            The encoded resource path identifying the resource to be accessed.
     * @param encodedQuery
     *            Optional: An encoded string representing the URL query part.
     *
     * @return The correctly encoded URI.
     */
    @Nonnull
    static URI createAndEncodeUri(
        @Nonnull final String servicePath,
        @Nonnull final String encodedResourcePath,
        @Nullable final String encodedQuery,
        @Nonnull final UriEncodingStrategy strategy )
    {
        String encodedPath = encodePath(servicePath, strategy);
        encodedPath = sanitizeUrlPath(encodedPath);

        encodedPath += encodedResourcePath.startsWith("/") ? encodedResourcePath : "/" + encodedResourcePath;

        final Option<String> maybeQueryEncoded = Option.of(encodedQuery).filter(s -> !Strings.isNullOrEmpty(s));

        if( maybeQueryEncoded.isDefined() && !maybeQueryEncoded.exists(VALID_URL_QUERY) ) {
            throw new IllegalArgumentException(
                "The query part of OData request is not correctly encoded: \"" + encodedQuery + "\"");
        }

        final String resultUrl = encodedPath + maybeQueryEncoded.map(q -> "?" + q).getOrElse("");

        try {
            return new URI(resultUrl);
        }
        catch( final URISyntaxException e ) {
            log
                .error(
                    "Failed to construct URI for OData request with service path '{}', resource path '{}' and query '{}'.",
                    servicePath,
                    encodedResourcePath,
                    encodedQuery,
                    e);
            throw new IllegalArgumentException("Failed to construct URI.", e);
        }
    }

    /**
     * Encodes the individual path parts of a string. Forward slashes are treated as path segment separators and thus
     * not be encoded. Encoding is done by {@link UrlEscapers#urlPathSegmentEscaper()}.
     *
     * @param path
     *            The unencoded URL path.
     *
     * @return The percentage encoded URL path.
     */
    @Nonnull
    public static String encodePath( @Nonnull final String path )
    {
        return encodePath(path, UriEncodingStrategy.REGULAR);
    }

    /**
     * Encodes the individual path parts of a string. Forward slashes are treated as path segment separators and thus
     * not be encoded. Encoding is done by {@link UrlEscapers#urlPathSegmentEscaper()}.
     *
     * @param path
     *            The unencoded URL path.
     *
     * @param strategy
     *            The URI encoding strategy.
     * @return The percentage encoded URL path.
     */
    @Nonnull
    public static String encodePath( @Nonnull final String path, @Nonnull final UriEncodingStrategy strategy )
    {
        return Arrays
            .stream(path.split("/"))
            .map(strategy.getPathPercentEscaper()::escape)
            .collect(Collectors.joining("/"));
    }

    /**
     * Encodes an individual part of a URL path. Any forward slashes will not be treated as path segment separators but
     * instead be encoded. Encoding is done by the default {@link UrlEscapers#urlPathSegmentEscaper()}.
     *
     * @param path
     *            The unencoded URL path segment.
     *
     * @return The percentage encoded URL path segment.
     */
    @Nonnull
    public static String encodePathSegment( @Nonnull final String path )
    {
        return encodePathSegment(path, UriEncodingStrategy.REGULAR);
    }

    /**
     * Encodes an individual part of a URL path. Any forward slashes will not be treated as path segment separators but
     * instead be encoded. Encoding is done according to the passed {@link UriEncodingStrategy#getPathPercentEscaper()}.
     *
     * @param path
     *            The unencoded URL path segment.
     *
     * @param strategy
     *            The URI encoding strategy.
     * @return The percentage encoded URL path segment.
     */
    @Nonnull
    public static String encodePathSegment( @Nonnull final String path, @Nonnull final UriEncodingStrategy strategy )
    {
        return strategy.getPathPercentEscaper().escape(path);
    }

    /**
     * Encodes all characters according to the default encoding strategy {@link UriEncodingStrategy#REGULAR}.
     *
     * @param input
     *
     *            The query string of the request
     * @return The encoded query
     */
    @Nonnull
    public static String encodeQuery( @Nonnull final String input )
    {
        return encodeQuery(input, UriEncodingStrategy.REGULAR);
    }

    /**
     * Encodes all characters according to the provided {@link UriEncodingStrategy}.
     *
     * @param input
     *            The query string of the request
     * @param strategy
     *            The URI encoding strategy.
     * @return The encoded query
     */
    @Nonnull
    public static String encodeQuery( @Nonnull final String input, @Nonnull final UriEncodingStrategy strategy )
    {
        return strategy.getQueryPercentEscaper().escape(input);
    }

    /**
     * Get the delta-token from a URL.
     *
     * @param url
     *            The url or {@code null}.
     * @return Either {@code Option.some(String)} with the delta-token or {@code Option.empty()}.
     */
    @Nonnull
    public static Option<String> extractDeltaToken( @Nullable final String url )
    {
        return Option.of(url).map(PATTERN_DELTA_TOKEN::matcher).filter(Matcher::find).map(m -> m.group(1));
    }

    /**
     * Get the skip-token from a URL.
     *
     * @param url
     *            The url or {@code null}.
     * @return Either {@code Option.some(String)} with the skip-token or {@code Option.empty()}.
     */
    @Nonnull
    public static Option<String> extractSkipToken( @Nullable final String url )
    {
        return Option.of(url).map(PATTERN_SKIP_TOKEN::matcher).filter(Matcher::find).map(m -> m.group(1));
    }

    /**
     * Brings any string into the form "/A/B/C". The path will contain no double slashes, always start with a slash and
     * never end with a slash. An empty path will stay empty.
     *
     * @param path
     *            The path to be sanitized.
     *
     * @return The sanitized path according to the rules above.
     */
    @Nonnull
    private static String sanitizeUrlPath( @Nonnull final String path )
    {
        final String pathWithPrefixingSlash = "/" + path;
        final String pathWithoutDoubleSlashes = pathWithPrefixingSlash.replaceAll("//+", "/");
        return pathWithoutDoubleSlashes.replaceAll("/$", "");
    }
}
