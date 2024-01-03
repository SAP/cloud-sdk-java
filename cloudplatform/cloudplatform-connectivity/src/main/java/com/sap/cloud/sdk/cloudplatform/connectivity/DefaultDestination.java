/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.security.KeyStore;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import io.vavr.control.Option;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Immutable default implementation of the {@link Destination} interface to be used as the "single source of truth". All
 * other destination implementations provided will retrieve the properties out of this class.
 */
@EqualsAndHashCode
@Slf4j
public final class DefaultDestination implements Destination
{
    private final Map<String, Object> properties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Creates a new {@code DefaultDestination} with the given properties.
     *
     * @param map
     *            The properties to be used for the destination builder.
     * @return A new builder instance.
     * @since 5.0.0
     */
    @Nonnull
    public static Builder fromMap( @Nonnull final Map<String, ?> map )
    {
        final Builder builder = new Builder();
        builder.properties.putAll(map);

        return builder;
    }

    @Nonnull
    @Override
    public Option<Object> get( @Nonnull final String someKey )
    {
        return Option.of(properties.get(someKey));
    }

    @Nonnull
    @Override
    public Iterable<String> getPropertyNames()
    {
        return Collections.unmodifiableSet(properties.keySet());
    }

    @Nonnull
    @Override
    public String toString()
    {
        final Map<String, Object> nonSensitiveProperties = Maps.newHashMap(properties);

        for( final Map.Entry<String, Object> entry : nonSensitiveProperties.entrySet() ) {
            if( entry.getKey().toLowerCase(Locale.ENGLISH).contains("password") ) {
                entry.setValue("(hidden)");
            }
        }

        return getClass().getSimpleName() + "(properties=" + nonSensitiveProperties + ")";
    }

    private DefaultDestination( @Nonnull final Map<String, ?> properties )
    {
        this.properties.putAll(properties);
    }

    /**
     * Returns a new {@link Builder} instance that is initialized with this {@link DefaultDestination}.
     * <p>
     * Please note that this operation performs a <b>shallow copy only</b>. As a consequence, complex objects (such as
     * the {@link KeyStore}s) will be copied <b>by reference only</b>, which leads to a shared state between the
     * {@code destination} and the {@link DefaultHttpDestination} to be created.
     * </p>
     *
     * @return A new {@link Builder} instance.
     * @since 5.0.0
     */
    @Nonnull
    public Builder toBuilder()
    {
        final Builder builder = new Builder();
        getPropertyNames().forEach(name -> builder.property(name, get(name).get()));

        return builder;
    }

    /**
     * Starts a builder to be used to create a {@code DefaultDestination} with some properties.
     *
     * @return A new {@code Builder} instance.
     */
    @Nonnull
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder class to allow for easy creation of an immutable {@code DefaultDestination} instance.
     */
    @ToString
    public static class Builder
    {
        final Map<String, Object> properties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        /**
         * Adds the given key-value pair to the destination to be created. This will overwrite any property already
         * assigned to the key.
         *
         * @param key
         *            The key to assign a property for.
         * @param value
         *            The property value to be assigned.
         * @return This builder.
         */
        @Nonnull
        public Builder property( @Nonnull final String key, @Nonnull final Object value )
        {
            properties.put(key, value);
            return this;
        }

        /**
         * Adds the given key-value pair to the destination to be created. This will overwrite any property already
         * assigned to the key.
         *
         * @param key
         *            The key to assign a property for.
         * @param value
         *            The property value to be assigned.
         * @return This builder.
         */
        @Nonnull
        <ValueT> Builder property( @Nonnull final DestinationPropertyKey<ValueT> key, @Nonnull final ValueT value )
        {
            properties.put(key.getKeyName(), value);
            return this;
        }

        @Nonnull
        <ValueT> Option<ValueT> get( @Nonnull final DestinationPropertyKey<ValueT> key )
        {
            return key.get(properties);
        }

        @Nonnull
        <ValueT> Option<ValueT> get( @Nonnull final String key, @Nonnull final Function<Object, ValueT> conversion )
        {
            return Option.of(properties.get(key)).map(conversion);
        }

        @Nonnull
        Builder removeProperty( @Nonnull final DestinationPropertyKey<?> key )
        {
            properties.remove(key.getKeyName());
            return this;
        }

        /**
         * Sets the name of the {@code DefaultDestination}.
         *
         * @param name
         *            The destination name
         * @return This builder.
         * @since 5.0.0
         */
        @Nonnull
        public Builder name( @Nonnull final String name )
        {
            return property(DestinationProperty.NAME, name);
        }

        /**
         * Sets the URI of the to-be-built {@link DefaultDestination}.
         *
         * @param uri
         *            The URI to set.
         * @return This builder.
         * @since 5.0.0
         */
        @Nonnull
        public Builder uri( @Nonnull final String uri )
        {
            return property(DestinationProperty.URI, uri);
        }

        /**
         * Sets the URI of the to-be-built {@link DefaultDestination}.
         *
         * @param uri
         *            The URI to set.
         * @return This builder.
         * @since 5.0.0
         */
        @Nonnull
        public Builder uri( @Nonnull final URI uri )
        {
            return uri(uri.toString());
        }

        /**
         * Finally creates the {@code DefaultDestination} with the properties retrieved via the
         * {@link #property(String, Object)} method.
         *
         * @return A fully instantiated {@code DefaultDestination}.
         */
        @Nonnull
        public DefaultDestination build()
        {
            return new DefaultDestination(properties);
        }
    }
}
