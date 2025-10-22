package com.sap.cloud.sdk.cloudplatform.resilience4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationKey;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Default implementation for circuit breaker provider.
 */
@Slf4j
public class DefaultCircuitBreakerProvider implements CircuitBreakerProvider, GenericDecorator
{
    private static final CircuitBreakerConfig DEFAULT_CIRCUIT_BREAKER_CONFIG = CircuitBreakerConfig.custom().build();

    private final ConcurrentMap<ResilienceIsolationKey, CircuitBreakerRegistry> circuitBreakerRegistries =
        new ConcurrentHashMap<>();

    private final Map<String, Throwable> lastExceptions = new HashMap<>();

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

        val circuitBreaker = circuitBreakerRegistry.circuitBreaker(identifier, customCircuitBreakerConfig);

        circuitBreaker
            .getEventPublisher()
            .onError(event -> lastExceptions.put(circuitBreaker.getName(), event.getThrowable()));

        return circuitBreaker;
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

        val circuitBreaker = getCircuitBreaker(configuration);
        return () -> {
            try {
                return CircuitBreaker.decorateCallable(circuitBreaker, callable).call();
            }
            catch( CallNotPermittedException e ) {
                val message =
                    "CircuitBreaker '" + circuitBreaker.getName() + "' is OPEN and does not permit further calls";
                log.debug(message);
                val lastException = lastExceptions.get(circuitBreaker.getName());
                if( lastException == null ) {
                    throw new ResilienceRuntimeException(message, e);
                }
                val resilienceRuntimeException =
                    new ResilienceRuntimeException(
                        message + ". Triggered by " + lastException.getMessage(),
                        lastException);
                resilienceRuntimeException.addSuppressed(e);
                throw resilienceRuntimeException;
            }
        };
    }
}
