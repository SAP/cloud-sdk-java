/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

/**
 * Provider for circuit breakers.
 */
public interface CircuitBreakerProvider
{
    /**
     * Provides a circuit breaker.
     *
     * @param configuration
     *            The configuration for constructing the circuit breaker.
     *
     * @return A circuit breaker.
     */
    @Nonnull
    CircuitBreaker getCircuitBreaker( @Nonnull final ResilienceConfiguration configuration );
}
