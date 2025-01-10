package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyException;

import io.vavr.control.Try;

/**
 * A {@link Property} that will calculate its value lazily, i.e. once and only when {@link #getValue()} is called.
 * <p>
 * </p>
 * This class is intentionally <b>NOT</b> thread-safe since it isn't expected to be evaluated by more than one Thread.
 * If it was, we might have messed something up about the isolation of {@link ThreadContext} instances.
 *
 * @param <T>
 *            The generic value type.
 */
@Beta
class LazyProperty<T> extends Property<T>
{
    @Nonnull
    private final Supplier<T> lazyValueFactory;
    @Nullable
    private Try<T> cachedLazyValue = null;

    protected LazyProperty( @Nonnull final Supplier<T> lazyValueFactory, final boolean isConfidential )
    {
        super(isConfidential);
        this.lazyValueFactory = lazyValueFactory;
    }

    /**
     * Creates a new non-confidential {@link LazyProperty} from the given {@code valueSupplier}.
     *
     * @param valueSupplier
     *            The {@link Supplier} that should be used when getting the actual property value via
     *            {@link #getValue()}.
     * @return A new instance of {@link LazyProperty}.
     * @param <T>
     *            The generic value type.
     */
    public static <T> LazyProperty<T> of( @Nonnull final Supplier<T> valueSupplier )
    {
        return new LazyProperty<>(valueSupplier, false);
    }

    /**
     * Creates a new confidential {@link LazyProperty} from the given {@code valueSupplier}.
     *
     * @param valueSupplier
     *            The {@link Supplier} that should be used when getting the actual property value via
     *            {@link #getValue()}.
     * @return A new instance of {@link LazyProperty}.
     * @param <T>
     *            The generic value type.
     */
    public static <T> LazyProperty<T> ofConfidential( @Nonnull final Supplier<T> valueSupplier )
    {
        return new LazyProperty<>(valueSupplier, true);
    }

    /**
     * Indicates whether this {@link LazyProperty} has already been evaluated.
     *
     * @return {@code true} if the lazy value has been evaluated and cached, {@code false} otherwise.
     */
    public boolean hasBeenEvaluated()
    {
        return cachedLazyValue != null;
    }

    @Nonnull
    @Override
    public Try<T> getValue()
    {
        if( cachedLazyValue == null ) {
            cachedLazyValue =
                Try
                    .ofSupplier(lazyValueFactory)
                    .filter(
                        Objects::nonNull,
                        () -> new ThreadContextPropertyException("Property value cannot be null."));
        }

        return cachedLazyValue;
    }

    @Nonnull
    @Override
    public String toString()
    {
        if( isConfidential() ) {
            return "LazyProperty(value=(hidden))";
        }

        if( cachedLazyValue == null ) {
            // value has not been evaluated yet, let's not do it just for the string
            return "LazyProperty(value=(not yet evaluated))";
        }

        return "LazyProperty(value=" + cachedLazyValue.getOrNull() + ")";
    }

    @Nonnull
    @Override
    public Property<T> copy()
    {
        return new LazyProperty<>(lazyValueFactory, isConfidential());
    }
}
