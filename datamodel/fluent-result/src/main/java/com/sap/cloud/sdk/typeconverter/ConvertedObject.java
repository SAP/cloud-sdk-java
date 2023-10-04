/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.typeconverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.typeconverter.exception.ObjectNotConvertibleException;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Wrapper class enclosing the result of a conversion. The conversion may or may not have succeeded, therefore some
 * accessor methods are given to handle those cases graciously.
 *
 * @param <T>
 *            The type of the contained object.
 */
@RequiredArgsConstructor( access = AccessLevel.PROTECTED )
@Data
public class ConvertedObject<T>
{
    /**
     * Checks whether the conversion failed and the value could not be converted.
     *
     * @return {@code true} if the value could not be converted, {@code false} else.
     */
    @Getter
    private final boolean notConvertible;

    @Nullable
    private final T object;

    /**
     * Retrieves the converted object.
     *
     * @return The converted object. May be {@code null}.
     *
     * @throws ObjectNotConvertibleException
     *             If the object is not convertible.
     */
    @Nullable
    public T get()
        throws ObjectNotConvertibleException
    {
        if( notConvertible ) {
            throw new ObjectNotConvertibleException("Object is not convertible.");
        }
        return object;
    }

    /**
     * Returns the contained value, or {@code null} if the object was not convertible.
     *
     * @return The converted object, or {@code null} if the object was not convertible.
     */
    @Nullable
    public T orNull()
    {
        return orElse(null);
    }

    /**
     * Returns the contained value, or the given default value if the object was not convertible.
     *
     * @param defaultValue
     *            The value to return in case this {@code ConvertedObject} was not convertible.
     *
     * @return The converted object, or the given default value if the object was not convertible.
     */
    @Nullable
    public T orElse( @Nullable final T defaultValue )
    {
        if( notConvertible ) {
            return defaultValue;
        }
        return object;
    }

    /**
     * Checks whether the conversion succeeded and the value could be converted.
     *
     * @return {@code true} if the value could be converted, {@code false} else.
     */
    public boolean isConvertible()
    {
        return !notConvertible;
    }

    /**
     * Creates a {@code ConvertedObject} containing the given object as its value.
     *
     * @param convertedObject
     *            The object to wrap in a {@code ConvertedObject}.
     * @param <T>
     *            The type of the object to wrap.
     *
     * @return A {@code ConvertedObject} wrapping {@code convertedObject}.
     */
    @Nonnull
    public static <T> ConvertedObject<T> of( @Nullable final T convertedObject )
    {
        return new ConvertedObject<>(false, convertedObject);
    }

    /**
     * Creates a {@code ConvertedObject} containing {@code null} as its value. The reason to use this method might be
     * that no object was given to convert.
     *
     * @param <T>
     *            The type the contained value should have.
     *
     * @return A {@code ConvertedObject} containing only {@code null}.
     */
    @Nonnull
    public static <T> ConvertedObject<T> ofNull()
    {
        return of(null);
    }

    /**
     * Creates a {@code ConvertedObject} containing {@code null} as its value. The reason to use this method might be
     * that the given object could not be converted.
     *
     * @param <T>
     *            The type the contained value should have.
     *
     * @return A {@code ConvertedObject} containing only {@code null}.
     */
    @Nonnull
    public static <T> ConvertedObject<T> ofNotConvertible()
    {
        return new ConvertedObject<>(true, null);
    }
}
