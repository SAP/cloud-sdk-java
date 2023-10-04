/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyException;

import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * This class represents the context of a thread, allowing to access and share information transparently across threads.
 * A typical use case for a thread context is to store information about the current request in a servlet container.
 */
public interface ThreadContext
{
    /**
     * Retrieves a {@link Try} of the property's value for the given name. The {@link Try} is considered to fail in case
     * of exceptions or if the property does not exist. Implementations have to ensure that this method is thread-safe.
     *
     * @param name
     *            The name of the property.
     * @param <T>
     *            The generic value type.
     * @return The value wrapped in a {@link Try}.
     * @throws ClassCastException
     *             If the property cannot be cast to the desired type.
     */
    @Nonnull
    <T> Try<T> getPropertyValue( @Nonnull final String name );

    /**
     * Sets a value for the property for the given name, if it has not been set before. Implementations have to ensure
     * that this method is thread-safe.
     *
     * @param name
     *            The name of the property.
     * @param value
     *            A {@link Property}.
     *
     * @throws ThreadContextPropertyException
     *             If there is an issue while setting the property.
     */
    void setPropertyIfAbsent( @Nonnull final String name, @Nonnull final Property<?> value )
        throws ThreadContextPropertyException;

    /**
     * Sets a value for the property for the given name, if it has not been set before. Implementations have to ensure
     * that this method is thread-safe.
     *
     * @param name
     *            The name of the property.
     * @param valueGenerator
     *            A {@link Callable} that returns the {@link Property} to set
     *
     * @throws ThreadContextPropertyException
     *             If there is an issue while setting the property or executing the valueGenerator.
     */
    default void setPropertyIfAbsent( @Nonnull final String name, @Nonnull final Callable<Property<?>> valueGenerator )
        throws ThreadContextPropertyException
    {
        if( !containsProperty(name) ) {
            try {
                setPropertyIfAbsent(name, valueGenerator.call());
            }
            catch( final Exception e ) {
                throw new ThreadContextPropertyException(e);
            }
        }
    }

    /**
     * Set a value for the property for the given name, independent whether it has been set before. Implementations have
     * to ensure that this method is thread-safe.
     *
     * @param name
     *            The name of the property.
     * @param value
     *            A {@link Property}.
     *
     * @throws ThreadContextPropertyException
     *             If there is an issue while setting the property.
     */
    default void setProperty( @Nonnull final String name, @Nonnull final Property<?> value )
        throws ThreadContextPropertyException
    {
        if( containsProperty(name) ) {
            removeProperty(name);
        }
        setPropertyIfAbsent(name, value);
    }

    /**
     * Removes the property with the given name.
     * <p>
     * <strong>Caution: Implementations may not be thread-safe!</strong>
     *
     * @param name
     *            The name of the property.
     *
     * @return A {@link Option} holding the removed value, or {@link Option#none()} if the property did not exist
     *         before.
     *
     * @throws ClassCastException
     *             If the property cannot be cast to the desired type.
     * @param <T>
     *            The generic value type.
     */
    @Nonnull
    <T> Option<Property<T>> removeProperty( @Nonnull final String name )
        throws ClassCastException;

    /**
     * Check whether a property with the given name is present and readable.
     * <p>
     * <strong>Caution: Implementations may not be thread-safe!</strong>
     *
     * @param name
     *            The name of the property.
     *
     * @return A boolean indicating whether the property does exist and is readable.
     */
    boolean containsProperty( @Nonnull final String name );

    /**
     * Create a new {@link ThreadContext} containing all properties of this context. Both the new and the existing
     * context will point to the same property objects. This is useful for passing on a context to a new thread, without
     * the two contexts interfering with each other.
     * <p>
     * <strong>Caution: Implementations may not be thread-safe!</strong>
     *
     * @return A new instance of {@link ThreadContext}.
     * @throws ThreadContextAccessException
     *             If the current implementation does not support the copy operation.
     */
    @Nonnull
    default ThreadContext duplicate()
    {
        throw new ThreadContextAccessException(getClass().getName() + " does not support duplication.");
    }
}
