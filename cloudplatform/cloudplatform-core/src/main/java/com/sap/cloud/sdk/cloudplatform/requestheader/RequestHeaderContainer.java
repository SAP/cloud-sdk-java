package com.sap.cloud.sdk.cloudplatform.requestheader;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Represents an <b>immutable</b> container for multiple HTTP headers. These headers consist of a {@code name} and
 * multiple {@code value}s.
 */
public interface RequestHeaderContainer
{
    /**
     * An empty {@link RequestHeaderContainer}.
     */
    RequestHeaderContainer EMPTY = new RequestHeaderContainer()
    {
        @Nonnull
        @Override
        public List<String> getHeaderNames()
        {
            return Collections.emptyList();
        }

        @Override
        public boolean containsHeader( @Nonnull final String headerName )
        {
            return false;
        }

        @Nonnull
        @Override
        public List<String> getHeaderValues( @Nonnull final String headerName )
        {
            return Collections.emptyList();
        }

        @Nonnull
        @Override
        public Builder toBuilder()
        {
            return DefaultRequestHeaderContainer.builder();
        }
    };

    /**
     * Returns all <b>unique</b> names of the contained HTTP headers.
     * <p>
     * <b>Important:</b> Casing of the individual names might be changed by the implementation. Duplicated names that
     * differ only in their casing will be treated as a single name.
     * <p>
     * <b>Example:</b> The header names {@code HeaderA}, {@code HEADERA}, and {@code headera} will result in only a
     * single value being returned from this method.
     * <p>
     * <b>Note:</b> Do <b>not</b> assume any specific casing of the returned header names. Also, do <b>not</b> use this
     * method for determining whether a specific header is contained in this collection. Use
     * {@link #containsHeader(String)} instead.
     *
     * @return An immutable {@link List} of contained HTTP header names.
     */
    @Nonnull
    List<String> getHeaderNames();

    /**
     * Determines whether an HTTP header with the given {@code headerName} exists in this container. The
     * {@code headerName} is treated <b>case insensitively</b>.
     *
     * @param headerName
     *            The name of the HTTP header to look for.
     * @return {@code true} if an HTTP header with the given name is contained, {@code false} otherwise.
     */
    boolean containsHeader( @Nonnull final String headerName );

    /**
     * Returns a <b>non-unique</b> collection of individual values for an HTTP header with the given {@code headerName}.
     * The {@code headerName} is treated <b>case insensitively</b>.
     *
     * @param headerName
     *            The name of the HTTP header to retrieve the values for.
     * @return An immutable {@link List} of individual HTTP header values.
     */
    @Nonnull
    List<String> getHeaderValues( @Nonnull final String headerName );

    /**
     * Initializes a new instance of {@link Builder} by <b>copying</b> all HTTP headers that are contained in this
     * {@link RequestHeaderContainer}.
     *
     * @return A new instance of {@link Builder}.
     */
    @Nonnull
    Builder toBuilder();

    /**
     * Convenience class for constructing and manipulating {@link RequestHeaderContainer}s.
     */
    interface Builder
    {
        /**
         * <b>Copies</b> all existing headers from the given {@link RequestHeaderContainer}.
         *
         * @param headerContainer
         *            The {@link RequestHeaderContainer} to copy the headers from.
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder withHeaders( @Nonnull final RequestHeaderContainer headerContainer );

        /**
         * Adds a new HTTP header with the given values to this {@link Builder}.
         * <p>
         * If the header does already exist, all given values will be added to it.
         *
         * @param name
         *            The name of the HTTP header. This is treated <b>case insensitively</b>.
         * @param firstValue
         *            The first value of the HTTP header.
         * @param furtherValues
         *            Further values of the HTTP header.
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder withHeader(
            @Nonnull final String name,
            @Nonnull final String firstValue,
            @Nonnull final String... furtherValues );

        /**
         * Adds a new HTTP header with the given values to this {@link Builder}.
         * <p>
         * If the header does already exist, all given values will be added to it.
         *
         * @param name
         *            The name of the HTTP header. This is treated <b>case insensitively</b>.
         * @param values
         *            The values of the HTTP header.
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder withHeader( @Nonnull final String name, @Nonnull final Iterable<String> values );

        /**
         * <b>Copies</b> the HTTP header with the given {@code name} - including <b>all</b> it's values - from the
         * {@code headerContainer}.
         *
         *
         * @param name
         *            The name of the HTTP header.
         * @param headerContainer
         *            The {@link RequestHeaderContainer} to copy the HTTP header from.
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder withHeader( @Nonnull final String name, @Nonnull RequestHeaderContainer headerContainer );

        /**
         * Removes an HTTP header from this {@link Builder}.
         *
         * @param name
         *            The <b>case insensitive</b> name of the HTTP header.
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder withoutHeader( @Nonnull final String name );

        /**
         * Replaces an HTTP header in this {@link Builder}.
         * <p>
         * This method is semantically equivalent to
         * {@code withoutHeader(name).withHeader(name, firstValue, furtherValues)}.
         *
         * @param name
         *            The <b>case insensitive</b> name of the HTTP header.
         * @param firstValue
         *            The first new value of the HTTP header.
         * @param furtherValues
         *            Further new values of the HTTP header.
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder replaceHeader(
            @Nonnull final String name,
            @Nonnull final String firstValue,
            @Nonnull final String... furtherValues );

        /**
         * Replaces an HTTP header in this {@link Builder}.
         * <p>
         * This method is semantically equivalent to {@code withoutHeader(name).withHeader(name, values)}.
         *
         * @param name
         *            The <b>case insensitive</b> name of the HTTP header.
         * @param values
         *            The new values of the HTTP header.
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder replaceHeader( @Nonnull final String name, @Nonnull final Iterable<String> values );

        /**
         * Replaces an HTTP header in this {@link Builder} by <b>copying</b> the HTTP header of the given
         * {@code headerContainer}.
         * <p>
         * This method is equivalent to {@code withoutHeader(name).withHeader(name, headerContainer)}.
         *
         * @param name
         *            The <b>case insensitive</b> name of the HTTP header.
         * @param headerContainer
         *            The {@link RequestHeaderContainer} to copy the HTTP header from.
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder replaceHeader( @Nonnull final String name, @Nonnull final RequestHeaderContainer headerContainer );

        /**
         * Removes <b>all</b> HTTP headers <b>and</b> cookies from this {@link Builder}.
         *
         * @return This {@link Builder} instance.
         */
        @Nonnull
        Builder clear();

        /**
         * Initializes a new instance of {@link RequestHeaderContainer} from the HTTP headers and cookies contained in
         * this {@link Builder}.
         *
         * @return A new instance of {@link RequestHeaderContainer}.
         */
        @Nonnull
        RequestHeaderContainer build();
    }
}
