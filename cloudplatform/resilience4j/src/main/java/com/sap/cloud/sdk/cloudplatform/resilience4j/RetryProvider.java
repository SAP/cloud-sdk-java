/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import io.github.resilience4j.retry.Retry;

/**
 * Provider for retries.
 */
public interface RetryProvider
{
    /**
     * Provides a retry.
     *
     * @param configuration
     *            The configuration for constructing the retry.
     *
     * @return A retry.
     */
    @Nonnull
    Retry getRetry( @Nonnull final ResilienceConfiguration configuration );
}
