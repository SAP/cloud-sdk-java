package com.sap.cloud.sdk.frameworks.resilience4j;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

/**
 * Interface to decorate a function with resilient properties.
 */
public interface GenericDecorator
{
    /**
     * Decorates the given callable to enable a resilient property.
     *
     * @param configuration
     *            The configuration to be used.
     * @param callable
     *            The callable to decorate.
     * @param <T>
     *            The type of the callable.
     *
     * @return The decorated callable.
     */
    @Nonnull
    <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration );
}
