/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import io.github.resilience4j.bulkhead.Bulkhead;

/**
 * Provider for bulkheads.
 */
public interface BulkheadProvider
{
    /**
     * Provides a bulkhead.
     *
     * @param configuration
     *            The configuration for constructing the bulkhead.
     *
     * @return A bulkhead.
     */
    @Nonnull
    Bulkhead getBulkhead( @Nonnull final ResilienceConfiguration configuration );
}
