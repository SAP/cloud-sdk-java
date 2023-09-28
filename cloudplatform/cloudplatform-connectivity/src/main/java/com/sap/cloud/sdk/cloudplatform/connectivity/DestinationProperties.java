package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.function.Function;

import javax.annotation.Nonnull;

import io.vavr.control.Option;

/**
 * Interface representing the minimal information an arbitrary destination needs to implement.
 * <p>
 * As the interface suggests, a generic destination is just a store of key-value-pairs. The provided default methods
 * allow type-safe to the values if the format of content is known.
 */
public interface DestinationProperties
{
    /**
     * Gets the value associated with the given key (if any).
     *
     * @param key
     *            The key to get the value for.
     * @return An {@code Option} object containing the value, if any.
     */
    @Nonnull
    Option<Object> get( @Nonnull final String key );

    /**
     * Retrieves the set of property keys of the destination.
     *
     * @return An iterable of type String which is the set of property keys for a destination
     */
    @Nonnull
    Iterable<String> getPropertyNames();

    /**
     * Convenience method to cast the {@code Object} return type of {@link #get(String)} into the expected type.
     *
     * @param key
     *            The key to get the value for.
     * @param expectedType
     *            The expected type to cast the value to.
     * @param <ValueT>
     *            The expected type of the value.
     * @return An {@code Option} object containing the converted value, if any.
     * @throws ClassCastException
     *             if the contained type cannot be cast to the expected type.
     */
    @Nonnull
    default <ValueT> Option<ValueT> get( @Nonnull final String key, @Nonnull final Class<ValueT> expectedType )
    {
        return get(key, expectedType::cast);
    }

    /**
     * Convenience method to convert the {@code Object} return type of {@link #get(String)} into any expected type.
     *
     * @param key
     *            The key to get the value for.
     * @param conversion
     *            A function converting the object given by {@link #get(String)} into the expected type. Will never be
     *            called if no value (or {@code null}) is present for the given key.
     * @param <ValueT>
     *            The expected type of the value.
     * @return An {@code Option} object containing the converted value, if any.
     */
    @Nonnull
    default <ValueT> Option<ValueT> get( @Nonnull final String key, @Nonnull final Function<Object, ValueT> conversion )
    {
        return get(key).map(conversion);
    }

    /**
     * Convenience method to convert the {@code Object} return type of {@link #get(String)} into any expected type.
     *
     * @param propertyKey
     *            The {@link DestinationPropertyKey} for the value to retrieve.
     * @param <ValueT>
     *            The expected type of the value.
     * @return An {@code Option} object containing the converted value, if any.
     */
    @Nonnull
    default <ValueT> Option<ValueT> get( @Nonnull final DestinationPropertyKey<ValueT> propertyKey )
    {
        return get(propertyKey.getKeyName(), propertyKey.getFromMapConversion());
    }
}
