/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Class used as key to access destination properties, which are stored as key-value-pairs.
 *
 * @param <ValueT>
 *            The data type of the value stored.
 *
 * @since 4.3.0
 */
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
@Getter
public class DestinationPropertyKey<ValueT>
{
    @Nonnull
    private final String keyName;
    @Nonnull
    private final Function<Object, ValueT> fromMapConversion;

    /**
     * Returns the typed value of the {@link DestinationPropertyKey}
     *
     * @param properties
     *            The map of properties of the destination.
     * @return the typed value of the key.
     */
    @Nonnull
    public Option<ValueT> get( @Nonnull final Map<String, Object> properties )
    {
        return Option.of(properties.get(keyName)).map(fromMapConversion);
    }

    /**
     * Creates a new property with {@code keyName}. Values retrieved via
     * {@link DestinationProperties#get(DestinationPropertyKey)} will be retrieved as {@link ValueT}. They will be cast,
     * if possible, or the provided {@code fallback} will be applied, in case the value was stored as String. If both
     * fails, an {@link IllegalArgumentException} will be thrown.
     *
     * @param keyName
     *            The key of the property.
     * @param cls
     *            The class of the values to be stored.
     * @param fallback
     *            A function to retrieve a value from a string representation, in case the value was not stored as
     *            {@link ValueT} object.
     * @param <ValueT>
     *            The type of the values to be stored.
     * @return The newly created {@link DestinationPropertyKey}
     */
    @Nonnull
    static <ValueT> DestinationPropertyKey<ValueT> createProperty(
        @Nonnull final String keyName,
        @Nonnull final Class<ValueT> cls,
        @Nonnull final Function<String, ValueT> fallback )
    {
        return new DestinationPropertyKey<>(keyName, o -> {
            if( cls.isInstance(o) ) {
                return cls.cast(o);
            } else if( o instanceof String ) {
                return fallback.apply((String) o);
            } else {
                throw new IllegalArgumentException(
                    "Unexpected type: "
                        + cls.getSimpleName()
                        + " or String was expected. Type found was: "
                        + o.getClass().getSimpleName());
            }
        });
    }

    /**
     * Convenience method to create properties that will be stored as string. See
     * {@link #createProperty(String, Class, Function)}
     */
    @Nonnull
    static DestinationPropertyKey<String> createStringProperty( @Nonnull final String keyName )
    {
        return createProperty(keyName, String.class, o -> o);
    }

    /**
     * Convenience method to create properties to store {@link URI}s. If an URI is present as string it will be
     * converted by {@link URI#create(String)}. See {@link #createProperty(String, Class, Function)}
     */
    @Nonnull
    static DestinationPropertyKey<URI> createUriProperty( @Nonnull final String keyName )
    {
        return createProperty(keyName, URI.class, URI::create);
    }

    /**
     * Convenience method to create properties that will be stored as {@link List}. Contrary to other properties, it
     * does *not* contain a fallback function to parse the list from string.
     * <p>
     * See {@link #createProperty(String, Class, Function)}
     */
    @Nonnull
    static DestinationPropertyKey<List<?>> createListProperty( @Nonnull final String keyName )
    {
        return new DestinationPropertyKey<>(keyName, o -> {
            if( o instanceof List ) {
                return (List<?>) o;
            } else {
                throw new IllegalArgumentException(
                    "Unexpected type: List was expected. Type found was: " + o.getClass().getSimpleName());
            }
        });
    }

    /**
     * Convenience method to create properties that will be stored as {@link Collection}.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    static <T> DestinationPropertyKey<Collection<T>> createCollectionProperty( @Nonnull final String keyName )
    {
        return new DestinationPropertyKey<>(keyName, o -> {
            if( o instanceof Collection ) {
                return (Collection<T>) o;
            } else {
                throw new IllegalArgumentException(
                    "Unexpected type: Collection was expected. Type found was: " + o.getClass().getSimpleName());
            }
        });
    }

    @Nonnull
    @Override
    public String toString()
    {
        return keyName;
    }
}
