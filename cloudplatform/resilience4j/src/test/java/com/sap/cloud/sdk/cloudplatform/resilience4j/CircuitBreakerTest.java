/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CircuitBreakerConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

class CircuitBreakerTest
{

    @Test
    void testCircuitBreakerStaysClosed()
        throws Exception
    {
        final List<Boolean> attempts = Arrays.asList(true, true, false, true);
        final TestCallable callable = spy(new TestCallable(attempts::get));

        final CircuitBreakerConfiguration circuitBreakerConfiguration =
            CircuitBreakerConfiguration
                .of()
                .waitDuration(Duration.ofHours(1))
                .closedBufferSize(3)
                .failureRateThreshold(50.0f);
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("circuitbreaker.test.1")
                .circuitBreakerConfiguration(circuitBreakerConfiguration);
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, configuration);

        executeCallable(wrappedCallable, attempts.size());

        verify(callable, times(attempts.size())).call();
    }

    @Test
    void testCircuitBreakerOpens()
        throws Exception
    {
        final List<Boolean> attempts = Arrays.asList(false, true, false);
        final TestCallable callable = spy(new TestCallable(attempts::get));

        final CircuitBreakerConfiguration circuitBreakerConfiguration =
            CircuitBreakerConfiguration
                .of()
                .waitDuration(Duration.ofHours(1))
                .closedBufferSize(3)
                .failureRateThreshold(50.0f);
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("circuitbreaker.test.2")
                .circuitBreakerConfiguration(circuitBreakerConfiguration);
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, configuration);

        final int attemptedInvocations = attempts.size() * 2;
        final List<Boolean> callResults = executeCallable(wrappedCallable, attemptedInvocations);

        assertThat(callResults).hasSize(attemptedInvocations);
        assertThat(callResults).startsWith(attempts.toArray(new Boolean[0]));

        assertThatThrownBy(wrappedCallable::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasCauseExactlyInstanceOf(CallNotPermittedException.class);

        verify(callable, times(circuitBreakerConfiguration.closedBufferSize())).call();
    }

    @Test
    void testCircuitBreakerHalfOpens()
        throws Exception
    {
        // with closed buffer = 1 the CB must open after the first failure
        final List<Boolean> attempts = Arrays.asList(false, false);
        final TestCallable callable = spy(new TestCallable(attempts::get));

        final CircuitBreakerConfiguration circuitBreakerConfiguration =
            CircuitBreakerConfiguration
                .of()
                .closedBufferSize(1)
                .failureRateThreshold(50.0f)
                .waitDuration(Duration.ofMillis(100));
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("circuitbreaker.test.3")
                .circuitBreakerConfiguration(circuitBreakerConfiguration);
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, configuration);

        assertThatThrownBy(wrappedCallable::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasRootCauseExactlyInstanceOf(Exception.class)
            .hasMessageContaining("Simulated failure");

        // this guarantees that the wait duration in the open state is exceeded
        // and the circuit breaker transitions to the half-open state
        Thread.sleep(circuitBreakerConfiguration.waitDuration().toMillis() + 100);

        assertThatThrownBy(wrappedCallable::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasRootCauseExactlyInstanceOf(Exception.class)
            .hasMessageContaining("Simulated failure");

        assertThat(callable.getAttemptCounter()).isEqualTo(attempts.size());
    }

    @Test
    void testCircuitBreakerClosesAgain()
        throws Exception
    {
        final List<Boolean> attempts = Arrays.asList(false, false, true, true, false);
        final TestCallable callable = spy(new TestCallable(attempts::get));

        final CircuitBreakerConfiguration circuitBreakerConfiguration =
            CircuitBreakerConfiguration
                .of()
                .closedBufferSize(2)
                .halfOpenBufferSize(2)
                .failureRateThreshold(50.0f)
                .waitDuration(Duration.ofMillis(1));
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("circuitbreaker.test.4")
                .circuitBreakerConfiguration(circuitBreakerConfiguration);
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, configuration);

        executeCallable(wrappedCallable, 2);

        // this guarantees that the wait duration is exceeded
        // and the circuit breaker transitions to the half-open state
        Thread.sleep(circuitBreakerConfiguration.waitDuration().toMillis() + 100);

        executeCallable(wrappedCallable, 3);

        verify(callable, times(attempts.size())).call();
    }

    private static List<Boolean> executeCallable( final Callable<?> callable, final int times )
    {
        final List<Boolean> result = new ArrayList<>();
        for( int i = 0; i < times; i++ ) {
            try {
                callable.call();
                result.add(i, true);
            }
            catch( final Exception ignored ) {
                result.add(i, false);
            }
        }
        return result;
    }

    @RequiredArgsConstructor
    private static class TestCallable implements Callable<Void>
    {
        private final Function<Integer, Boolean> succeedOnAttempt;
        @Getter
        private int attemptCounter = 0;

        @Override
        public Void call()
            throws Exception
        {
            attemptCounter++;

            if( !succeedOnAttempt.apply(attemptCounter - 1) ) {
                throw new Exception("Simulated failure, attempt nr: " + attemptCounter);
            } else {
                return null;
            }
        }
    }
}
