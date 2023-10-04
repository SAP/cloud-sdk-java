/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.frameworks.resilience4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationKey;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;

/**
 * Default implementation for bulkhead provider.
 */
public class DefaultBulkheadProvider implements BulkheadProvider, GenericDecorator
{
    private static final BulkheadConfig DEFAULT_BULK_HEAD_CONFIG = BulkheadConfig.custom().build();

    private final ConcurrentMap<ResilienceIsolationKey, BulkheadRegistry> bulkheadRegistries =
        new ConcurrentHashMap<>();

    private BulkheadRegistry getBulkheadRegistry( @Nonnull final ResilienceIsolationKey isolationKey )
    {
        return bulkheadRegistries.computeIfAbsent(isolationKey, ( k ) -> BulkheadRegistry.of(DEFAULT_BULK_HEAD_CONFIG));
    }

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        if( !configuration.bulkheadConfiguration().isEnabled() ) {
            return callable;
        }
        return Bulkhead.decorateCallable(getBulkhead(configuration), callable);
    }

    @Nonnull
    @Override
    public Bulkhead getBulkhead( @Nonnull final ResilienceConfiguration configuration )
    {
        final String identifier = configuration.identifier();
        final ResilienceIsolationKey isolationKey = ResilienceIsolationKey.of(configuration.isolationMode());
        final BulkheadRegistry bulkheadRegistry = getBulkheadRegistry(isolationKey);

        final BulkheadConfig customBulkheadConfig =
            BulkheadConfig
                .custom()
                .maxConcurrentCalls(configuration.bulkheadConfiguration().maxConcurrentCalls())
                .maxWaitDuration(configuration.bulkheadConfiguration().maxWaitDuration())
                .build();

        return bulkheadRegistry.bulkhead(identifier, customBulkheadConfig);
    }
}
