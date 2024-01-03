/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationKey;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

/**
 * Default implementation for a provider of retries.
 */
public class DefaultRetryProvider implements RetryProvider, GenericDecorator
{

    private static final RetryConfig DEFAULT_RETRY_CONFIG = RetryConfig.custom().build();

    private final ConcurrentMap<ResilienceIsolationKey, RetryRegistry> retryRegistries = new ConcurrentHashMap<>();

    private RetryRegistry getRetryRegistry( @Nonnull final ResilienceIsolationKey isolationKey )
    {
        return retryRegistries.computeIfAbsent(isolationKey, ( k ) -> RetryRegistry.of(DEFAULT_RETRY_CONFIG));
    }

    @Nonnull
    @Override
    public Retry getRetry( @Nonnull final ResilienceConfiguration configuration )
    {
        final String identifier = configuration.identifier();
        final ResilienceIsolationKey isolationKey = ResilienceIsolationKey.of(configuration.isolationMode());
        final RetryRegistry retryRegistry = getRetryRegistry(isolationKey);

        final RetryConfig customRetryConfig =
            RetryConfig
                .custom()
                .maxAttempts(configuration.retryConfiguration().maxAttempts())
                .waitDuration(configuration.retryConfiguration().waitDuration())
                .retryOnException(configuration.retryConfiguration().retryOnExceptionPredicate())
                .build();

        return retryRegistry.retry(identifier, customRetryConfig);
    }

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        if( !configuration.retryConfiguration().isEnabled() ) {
            return callable;
        }
        final Retry retry = getRetry(configuration);
        return Retry.decorateCallable(retry, callable);
    }
}
