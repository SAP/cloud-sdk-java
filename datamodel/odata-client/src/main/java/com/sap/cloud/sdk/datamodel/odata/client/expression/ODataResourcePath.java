/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.request.AbstractODataParameters;
import com.sap.cloud.sdk.datamodel.odata.client.request.UriEncodingStrategy;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A class that assembles resource references into a URL path. References can be entity sets, entities by key,
 * properties, functions, actions and special endpoints like the metadata endpoint.
 *
 * E.g. the following resource path identifies a function invocation on the a navigational property of the entity
 * identified by {@code 'val'}:
 * <p>
 * {@code Entity(key='val')/NavigationProperty/Model.Function(1)/ResultProperty}.
 * <p>
 * Of the result only the property {@code ResultProperty} is accessed.
 */
@EqualsAndHashCode
public final class ODataResourcePath
{
    /**
     * The current path as a list of its individual path segments.
     */
    @Getter( AccessLevel.PUBLIC )
    @Nonnull
    private final List<Tuple2<String, AbstractODataParameters>> segments = new ArrayList<>();

    /**
     * Convenience method for {@code new ODataResourcePath().addSegment(segment)}. It creates a new resource path for
     * the given path string.
     *
     * @param segment
     *            The string identifying the resource e.g. {@code EntityName}
     * @return A new {@link ODataResourcePath}.
     */
    @Nonnull
    public static ODataResourcePath of( @Nonnull final String segment )
    {
        return new ODataResourcePath().addSegment(segment);
    }

    /**
     * Convenience method for {@code new ODataResourcePath().addSegment(segment, segmentParameter)}. It creates a new
     * resource path for the given path string and parameters.
     *
     * @param segment
     *            The string identifying the resource e.g. {@code EntityName}
     * @param segmentParameter
     *            The parameters to be included in this segment e.g. {@code (key1='val',key2=123)}
     * @return A new {@link ODataResourcePath}.
     */
    @Nonnull
    public static
        ODataResourcePath
        of( @Nonnull final String segment, @Nonnull final AbstractODataParameters segmentParameter )
    {
        return new ODataResourcePath().addSegment(segment, segmentParameter);
    }

    /**
     * Add a navigation to the path.
     *
     * @param segment
     *            The navigation to add. Any slashes will be encoded and not treated as segment separators.
     * @return This builder instance.
     */
    @Nonnull
    public ODataResourcePath addSegment( @Nonnull final String segment )
    {
        return addSegment(segment, null);
    }

    /**
     * Add a navigation with a parameters or function or action call to the path.
     *
     * @param segment
     *            The unencoded navigation or function or action reference to add. Any slashes will be encoded and not
     *            treated as segment separators.
     * @param parameters
     *            The unencoded parameters or parameters to add.
     * @return This builder instance.
     */
    @Nonnull
    public
        ODataResourcePath
        addSegment( @Nonnull final String segment, @Nullable final AbstractODataParameters parameters )
    {
        segments.add(Tuple.of(segment, parameters));
        return this;
    }

    /**
     * Add a path parameter to the last segment. Can only be applied if the last segment added to this builder did not
     * contain any parameters.
     *
     * @param parameters
     *            The unencoded parameters to add.
     * @return This builder instance.
     *
     * @throws IllegalStateException
     *             When the current path is empty or the last segment already contains a parameter.
     */
    @Nonnull
    public ODataResourcePath addParameterToLastSegment( @Nonnull final AbstractODataParameters parameters )
    {
        if( segments.isEmpty() ) {
            throw new IllegalStateException(
                "Cannot add parameter to the last segment because the current path is empty.");
        }
        final Tuple2<String, AbstractODataParameters> lastSegment = segments.get(segments.size() - 1);
        if( lastSegment._2() != null ) {
            final String msg =
                String
                    .format(
                        "Cannot add parameter for path segment \"%s\". The segment already contains a parameter expression.",
                        lastSegment._2());
            throw new IllegalStateException(msg);
        }
        segments.set(segments.size() - 1, lastSegment.update2(parameters));
        return this;
    }

    /**
     * Encodes the path segments and assembles them together. The returned path will always start with a leading forward
     * slash and ends without any trailing slashes.
     *
     * @return The encoded path.
     */
    @Nonnull
    public String toEncodedPathString()
    {
        return toEncodedPathString(UriEncodingStrategy.REGULAR);
    }

    /**
     * Encodes the path segments and assembles them together. The returned path will always start with a leading forward
     * slash and ends without any trailing slashes.
     *
     * @param strategy
     *            The URI encoding strategy.
     * @return The encoded path.
     */
    @Nonnull
    public String toEncodedPathString( @Nonnull final UriEncodingStrategy strategy )
    {
        return "/"
            + segments
                .stream()
                .map(t -> t.map1(strategy.getPathPercentEscaper()::escape))
                .map(t -> t.map2(key -> key != null ? key.toEncodedString(strategy) : ""))
                .map(t -> t._1() + t._2())
                .collect(Collectors.joining("/"));
    }

    /**
     * Builds the path segments to a full URL path.
     *
     * @return The unencoded URL path.
     */
    @Override
    @Nonnull
    public String toString()
    {
        return "/"
            + segments
                .stream()
                .map(t -> t.map2(key -> key != null ? key.toString() : ""))
                .map(t -> t._1() + t._2())
                .collect(Collectors.joining("/"));
    }
}
