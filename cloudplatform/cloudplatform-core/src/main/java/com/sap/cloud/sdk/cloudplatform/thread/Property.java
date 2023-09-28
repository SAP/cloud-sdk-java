package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.Objects;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyException;

import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a {@link ThreadContext} property with a given value (or exception, if the value could not be determined).
 *
 * @param <T>
 *            The generic value type.
 */
@EqualsAndHashCode
public class Property<T>
{
    @Nonnull
    @Getter
    private final Try<T> value;

    @Getter
    private final boolean isConfidential;

    /**
     * Creates a property.
     *
     * @param value
     *            A {@link Try} that might contain the value of the property.
     *
     * @param isConfidential
     *            Whether the property must be handled confidentially, e.g., it should not be written to log files and
     *            {@link #toString()} should not reveal the value or exception.
     */
    protected Property( @Nonnull final Try<T> value, final boolean isConfidential )
    {
        this.value =
            value.filter(Objects::nonNull, () -> new ThreadContextPropertyException("Property value cannot be null."));
        this.isConfidential = isConfidential;
    }

    /**
     * Only {@link LazyProperty} can use this constructor to bypass the filter nonNull because {@link #getValue()} is
     * overridden.
     *
     * @param isConfidential
     *            Whether the property must be handled confidentially, e.g., it should not be written to log files and
     *            {@link #toString()} should not reveal the value or exception.
     */
    Property( final boolean isConfidential )
    {
        value = Try.success(null);
        this.isConfidential = isConfidential;
    }

    /**
     * Creates a non-confidential property from the given value.
     *
     * @param <T>
     *            The generic value type.
     * @param value
     *            The value to create a property instance for.
     * @return A new property instance.
     */
    @Nonnull
    public static <T> Property<T> of( @Nonnull final T value )
    {
        return new Property<>(Try.success(value), false);
    }

    /**
     * Creates a confidential property from the given value.
     *
     * @param <T>
     *            The generic value type.
     * @param confidentialValue
     *            The confidential value to create a confidential property instance for.
     * @return A new property instance.
     */
    @Nonnull
    public static <T> Property<T> ofConfidential( @Nonnull final T confidentialValue )
    {
        return new Property<>(Try.success(confidentialValue), true);
    }

    /**
     * Creates a non-confidential property from a given {@link Try} of a value.
     *
     * @param valueTry
     *            A {@link Try} value.
     * @return A new property value wrapped in {@link Try}.
     */
    @Nonnull
    public static <T> Property<T> ofTry( @Nonnull final Try<T> valueTry )
    {
        return new Property<>(valueTry, false);
    }

    /**
     * Creates a confidential property from a given {@link Try} of a value.
     *
     * @param confidentialValueTry
     *            A confidential {@link Try} value.
     * @return A new confidential property value wrapped in {@link Try}.
     */
    @Nonnull
    public static <T> Property<T> ofConfidentialTry( @Nonnull final Try<T> confidentialValueTry )
    {
        return new Property<>(confidentialValueTry, true);
    }

    /**
     * Decorate a callable, wrapping its return value into a property for convenience.
     *
     * @param valueGenerator
     *            A producer of a property value.
     * @return The wrapped callable.
     */
    @Nonnull
    public static Callable<Property<?>> decorateCallable( @Nonnull final Callable<?> valueGenerator )
    {
        return () -> new Property<>(Try.ofCallable(valueGenerator), false);
    }

    /**
     * Decorate a callable, wrapping its return value into a confidential property for convenience.
     *
     * @param valueGenerator
     *            A producer of a confidential property value.
     * @return The wrapped callable.
     */
    @Nonnull
    public static Callable<Property<?>> decorateConfidentialCallable( @Nonnull final Callable<?> valueGenerator )
    {
        return () -> new Property<>(Try.ofCallable(valueGenerator), true);
    }

    @Nonnull
    @Override
    public String toString()
    {
        return isConfidential ? "Property(value=(hidden))" : "Property(value=" + getValue() + ")";
    }

    @Nonnull
    Property<T> copy()
    {
        return new Property<>(value, isConfidential);
    }
}
