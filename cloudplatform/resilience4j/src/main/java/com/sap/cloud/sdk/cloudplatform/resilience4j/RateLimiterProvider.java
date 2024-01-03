/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import io.github.resilience4j.ratelimiter.RateLimiter;

/**
 * Provider for rate limiters.
 */
@FunctionalInterface
public interface RateLimiterProvider
{
    /**
     * Provides a rate limiter.
     *
     * @param configuration
     *            The configuration for constructing the rate limiter.
     *
     * @return A rate limiter.
     */
    @Nonnull
    RateLimiter getRateLimiter( @Nonnull final ResilienceConfiguration configuration );
}
