package com.sap.cloud.sdk.cloudplatform.resilience4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationKey;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

/**
 * Default implementation for circuit breaker provider.
 */
public class DefaultCircuitBreakerProvider implements CircuitBreakerProvider, GenericDecorator
{
    private static final CircuitBreakerConfig DEFAULT_CIRCUIT_BREAKER_CONFIG = CircuitBreakerConfig.custom().build();

    private final ConcurrentMap<ResilienceIsolationKey, CircuitBreakerRegistry> circuitBreakerRegistries =
        new ConcurrentHashMap<>();

    private CircuitBreakerRegistry getCircuitBreakerRegistry( @Nonnull final ResilienceIsolationKey isolationKey )
    {
        return circuitBreakerRegistries
            .computeIfAbsent(isolationKey, ( k ) -> CircuitBreakerRegistry.of(DEFAULT_CIRCUIT_BREAKER_CONFIG));
    }

    @Nonnull
    @Override
    public CircuitBreaker getCircuitBreaker( @Nonnull final ResilienceConfiguration configuration )
    {
        final String identifier = configuration.identifier();
        final ResilienceIsolationKey isolationKey = ResilienceIsolationKey.of(configuration.isolationMode());
        final CircuitBreakerRegistry circuitBreakerRegistry = getCircuitBreakerRegistry(isolationKey);

        final CircuitBreakerConfig customCircuitBreakerConfig =
            CircuitBreakerConfig
                .custom()
                .failureRateThreshold(configuration.circuitBreakerConfiguration().failureRateThreshold())
                .waitDurationInOpenState(configuration.circuitBreakerConfiguration().waitDuration())
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(configuration.circuitBreakerConfiguration().closedBufferSize())
                .minimumNumberOfCalls(configuration.circuitBreakerConfiguration().closedBufferSize())
                .permittedNumberOfCallsInHalfOpenState(configuration.circuitBreakerConfiguration().halfOpenBufferSize())
                .build();

        return circuitBreakerRegistry.circuitBreaker(identifier, customCircuitBreakerConfig);
    }

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        if( !configuration.circuitBreakerConfiguration().isEnabled() ) {
            return callable;
        }
        return CircuitBreaker.decorateCallable(getCircuitBreaker(configuration), callable);
    }
}
