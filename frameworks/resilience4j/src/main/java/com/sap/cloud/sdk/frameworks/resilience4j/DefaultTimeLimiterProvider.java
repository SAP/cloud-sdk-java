/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.frameworks.resilience4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
 * Decorates a callable with a time limiter based on a provided resilience configuration.
 */
public class DefaultTimeLimiterProvider implements TimeLimiterProvider, GenericDecorator
{

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        if( !configuration.timeLimiterConfiguration().isEnabled() ) {
            return callable;
        }
        final ThreadContextExecutor threadContextExecutor = ThreadContextExecutor.fromCurrentOrNewContext();
        final Supplier<Future<T>> futureSupplier =
            () -> ThreadContextExecutors.getExecutor().submit(callable, threadContextExecutor);
        final TimeLimiter timeLimiter = getTimeLimiter(configuration);
        return TimeLimiter.decorateFutureSupplier(timeLimiter, futureSupplier);
    }

    @Nonnull
    @Override
    public TimeLimiter getTimeLimiter( @Nonnull final ResilienceConfiguration configuration )
    {
        if( !configuration.timeLimiterConfiguration().isEnabled() ) {
            throw new IllegalArgumentException("The provided resilience configuration does not set a timeout.");
        }
        return TimeLimiter
            .of(
                configuration.identifier(),
                TimeLimiterConfig
                    .custom()
                    .timeoutDuration(configuration.timeLimiterConfiguration().timeoutDuration())
                    .cancelRunningFuture(configuration.timeLimiterConfiguration().shouldCancelRunningFuture())
                    .build());
    }
}
