/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.requestheader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation of the {@link RequestHeaderContainer} interface.
 */
@Beta
@EqualsAndHashCode
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class DefaultRequestHeaderContainer implements RequestHeaderContainer
{
    @Nonnull
    private final ImmutableListMultimap<String, String> headers;

    /**
     * Initializes a new instance of the {@link DefaultRequestHeaderContainer} class from the given {@code headers}.
     *
     * @param headers
     *            A {@link Map} of {@link String} (HTTP header name) to {@link String} (HTTP header value). Please note
     *            that headers with empty or null values are skipped.
     * @return A new instance of {@link RequestHeaderContainer}.
     */
    @Nonnull
    public static RequestHeaderContainer fromSingleValueMap( @Nonnull final Map<String, String> headers )
    {
        final Map<String, List<String>> multiValueMap = Maps.transformValues(headers, Collections::singletonList);
        return fromMultiValueMap(multiValueMap);
    }

    /**
     * Initializes a new instance of the {@link DefaultRequestHeaderContainer} class from the given {@code headers}.
     *
     * @param headers
     *            A {@link Map} of {@link String} (HTTP header name) to {@link Iterable} (HTTP header values). Please
     *            note that headers with empty or null values are skipped.
     * @return A new instance of {@link RequestHeaderContainer}.
     */
    @Nonnull
    public static RequestHeaderContainer fromMultiValueMap(
        @Nonnull final Map<String, ? extends Iterable<String>> headers )
    {
        final ImmutableListMultimap.Builder<String, String> normalizedResult = ImmutableListMultimap.builder();

        for( final Map.Entry<String, ? extends Iterable<String>> header : headers.entrySet() ) {
            final String headerName = header.getKey();
            final Iterable<String> headerValues = header.getValue();

            if( headerName == null || headerValues == null ) {
                continue;
            }

            normalizedResult.putAll(normalize(headerName), Iterables.filter(headerValues, Objects::nonNull));
        }

        return new DefaultRequestHeaderContainer(normalizedResult.build());
    }

    @Nonnull
    private static String normalize( @Nonnull final String headerName )
    {
        return headerName.trim().toLowerCase();
    }

    @Nonnull
    @Override
    public List<String> getHeaderNames()
    {
        return headers.keySet().asList();
    }

    @Override
    public boolean containsHeader( @Nonnull final String headerName )
    {
        return headers.containsKey(normalize(headerName));
    }

    @Nonnull
    @Override
    public List<String> getHeaderValues( @Nonnull final String headerName )
    {
        return headers.get(normalize(headerName));
    }

    @Nonnull
    @Override
    public RequestHeaderContainer.Builder toBuilder()
    {
        final Builder builder = new Builder();
        headers.forEach(builder::withHeader);
        return builder;
    }

    /**
     * Initializes a new instance of {@link Builder} to construct a new {@link DefaultRequestHeaderContainer} from
     * scratch.
     *
     * @return A new instance of {@link Builder}.
     */
    @Nonnull
    public static Builder builder()
    {
        return new Builder();
    }

    @Nonnull
    @Override
    public String toString()
    {
        return "DefaultRequestHeaderContainer(headerNames=" + this.getHeaderNames() + ")";
    }

    /**
     * Implementation of the {@link RequestHeaderContainer.Builder} interface that is able to construct and manipulate
     * {@link DefaultRequestHeaderContainer}s.
     */
    @Beta
    @NoArgsConstructor( access = AccessLevel.PRIVATE )
    public static class Builder implements RequestHeaderContainer.Builder
    {
        @Nonnull
        private final Map<String, Collection<String>> headers = new HashMap<>();

        @Nonnull
        @Override
        public RequestHeaderContainer.Builder withHeaders( @Nonnull final RequestHeaderContainer headerContainer )
        {
            headerContainer.getHeaderNames().forEach(name -> withHeader(name, headerContainer.getHeaderValues(name)));
            return this;
        }

        @Nonnull
        @Override
        public RequestHeaderContainer.Builder withHeader(
            @Nonnull final String name,
            @Nonnull final String firstValue,
            @Nonnull final String... furtherValues )
        {
            return withHeader(name, Lists.asList(firstValue, furtherValues));
        }

        @Nonnull
        @Override
        public
            RequestHeaderContainer.Builder
            withHeader( @Nonnull final String name, @Nonnull final Iterable<String> values )
        {
            final Collection<String> existingValues =
                headers.computeIfAbsent(normalize(name), key -> new ArrayList<>());
            values.forEach(existingValues::add);
            return this;
        }

        @Nonnull
        @Override
        public
            RequestHeaderContainer.Builder
            withHeader( @Nonnull final String name, @Nonnull final RequestHeaderContainer headerContainer )
        {
            return withHeader(name, headerContainer.getHeaderValues(name));
        }

        @Nonnull
        @Override
        public RequestHeaderContainer.Builder withoutHeader( @Nonnull final String name )
        {
            headers.remove(normalize(name));
            return this;
        }

        @Nonnull
        @Override
        public RequestHeaderContainer.Builder replaceHeader(
            @Nonnull final String name,
            @Nonnull final String firstValue,
            @Nonnull final String... furtherValues )
        {
            return withoutHeader(name).withHeader(name, firstValue, furtherValues);
        }

        @Nonnull
        @Override
        public
            RequestHeaderContainer.Builder
            replaceHeader( @Nonnull final String name, @Nonnull final Iterable<String> values )
        {
            return withoutHeader(name).withHeader(name, values);
        }

        @Nonnull
        @Override
        public
            RequestHeaderContainer.Builder
            replaceHeader( @Nonnull final String name, @Nonnull final RequestHeaderContainer headerContainer )
        {
            return withoutHeader(name).withHeader(name, headerContainer);
        }

        @Nonnull
        @Override
        public RequestHeaderContainer.Builder clear()
        {
            headers.clear();
            return this;
        }

        @Nonnull
        @Override
        public RequestHeaderContainer build()
        {
            return fromMultiValueMap(headers);
        }
    }
}
