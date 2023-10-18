/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import io.github.resilience4j.timelimiter.TimeLimiter;

/**
 * Provider for time limiters.
 */
public interface TimeLimiterProvider
{
    /**
     * Provides a time limiter.
     *
     * @param configuration
     *            The configuration for constructing the time limiter.
     *
     * @return A time limiter.
     */
    @Nonnull
    TimeLimiter getTimeLimiter( @Nonnull final ResilienceConfiguration configuration );
}
