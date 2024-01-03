/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface representing one structured object (e.g one complex business object) resulting from a call to an external
 * service (e.g. after invoking a BAPI or a remote-enabled function module).
 * <p>
 * Use the method {@link #get(String)} to access one particular element inside this object as {@link ResultElement}.
 * <p>
 * Use the method {@link #as(Class)} to cast this object into a given class type.
 */
public interface ResultObject extends ResultElement
{
    /**
     * Returns one particular element inside this result object identified by its name.
     *
     * @param elementName
     *            Name of the requested element.
     *
     * @return The found element as {@link ResultElement}.
     *
     * @throws UnsupportedOperationException
     *             If requested element cannot be found within this object.
     */
    @Nullable
    ResultElement get( @Nonnull final String elementName )
        throws UnsupportedOperationException;

    /**
     * Returns this {@link ResultObject} casted into the given type {@code T}.
     *
     * @param objectType
     *            The {@link Class} type of the type {@code T}
     * @param <T>
     *            The type the result object shall be casted to.
     *
     * @return An instance of {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If the cast into the given object type failed.
     */
    @Nonnull
    <T> T as( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException;

    /**
     * Returns this {@link ResultObject} casted into the given type {@code T}.
     *
     * @param objectType
     *            The {@link Type} of the type {@code T}
     * @param <T>
     *            The type the result object shall be casted to.
     *
     * @return An instance of {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If the cast into the given object type failed.
     */
    @Nonnull
    <T> T as( @Nonnull final Type objectType )
        throws UnsupportedOperationException;
}
