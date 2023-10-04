/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.cache;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * Util class containing helper methods to work with caches.
 */
public final class CacheUtil
{
    private CacheUtil()
    {
        /* Utils class that should not be instantiated/subclassed */
    }

    /**
     * Wraps the given {@code Callable} into a function that ignores the given parameter.
     * <p>
     * Every {@code Exception} thrown by the {@code Callable} will be wrapped in a {@link CacheRuntimeException} so that
     * the returned {@code Function} does not throw any checked {@code Exceptions}.
     *
     * @param callable
     *            The {@code Callable} to wrap.
     * @param <K>
     *            The type of the ignored key.
     * @param <V>
     *            The type of the value returned by the {@code Callable} and {@code Function}.
     * @return A {@code Function} executing the given {@code Callable} without looking at the parameter.
     */
    @Nonnull
    public static <K, V> Function<K, V> wrapCallableAsFunction( @Nonnull final Callable<V> callable )
    {
        return ( final K ignoreKey ) -> {
            try {
                return callable.call();
            }
            catch( final Exception e ) {
                throw new CacheRuntimeException(e);
            }
        };
    }

    /**
     * Wraps the given {@code Supplier} into a function that ignores the given parameter.
     *
     * @param supplier
     *            The {@code Supplier} to wrap.
     * @param <K>
     *            The type of the ignored key.
     * @param <V>
     *            The type of the value returned by the {@code Supplier} and {@code Function}.
     * @return A {@code Function} executing the given {@code Supplier} without looking at the parameter.
     */
    @Nonnull
    public static <K, V> Function<K, V> wrapSupplierAsFunction( @Nonnull final Supplier<V> supplier )
    {
        return ( final K ignoreKey ) -> supplier.get();
    }
}
