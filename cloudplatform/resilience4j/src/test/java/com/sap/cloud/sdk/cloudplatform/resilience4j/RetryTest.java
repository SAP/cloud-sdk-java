/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CircuitBreakerConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.RetryConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

class RetryTest
{
    @Test
    void testDisabledRetries()
        throws Exception
    {
        final Callable<?> callableFailure = mock(Callable.class);
        final Callable<?> callableSuccess = mock(Callable.class);
        when(callableFailure.call()).thenThrow(new TestException("Simulated failure"));
        when(callableSuccess.call()).thenAnswer((Answer<?>) invocation -> 0);

        final ResilienceConfiguration configuration = ResilienceConfiguration.of("test.retries_disabled");
        final Callable<?> wrappedCallableFailure = ResilienceDecorator.decorateCallable(callableFailure, configuration);
        final Callable<?> wrappedCallableSuccess = ResilienceDecorator.decorateCallable(callableSuccess, configuration);

        assertThatThrownBy(wrappedCallableFailure::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasRootCauseExactlyInstanceOf(TestException.class);
        assertThat(wrappedCallableSuccess.call()).isEqualTo(0);

        // assert the callable is executed exactly once
        verify(callableFailure).call();
        verify(callableSuccess).call();
    }

    @Test
    void testRetriesWithoutWaitDurationFailure()
        throws Exception
    {
        final int attempts = 5;
        final Callable<?> callable = mock(Callable.class);
        when(callable.call()).thenThrow(new TestException("Simulated failure"));

        final RetryConfiguration retryConfiguration = RetryConfiguration.of(attempts, Duration.ZERO);
        final ResilienceConfiguration configuration =
            ResilienceConfiguration.of("test.retries.2").retryConfiguration(retryConfiguration);
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, configuration);

        assertThatThrownBy(wrappedCallable::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasRootCauseExactlyInstanceOf(TestException.class);
        verify(callable, times(attempts)).call();
    }

    @Test
    void testRetriesWithoutWaitDurationSuccess()
        throws Exception
    {
        final int attempts = TestCallable.maxExpectedAttempts;
        final Callable<Integer> callable = spy(new TestCallable());

        final RetryConfiguration retryConfiguration = RetryConfiguration.of(attempts, Duration.ZERO);
        final ResilienceConfiguration configuration =
            ResilienceConfiguration.of("test.retries.3").retryConfiguration(retryConfiguration);
        final Callable<Integer> wrappedCallable = ResilienceDecorator.decorateCallable(callable, configuration);

        assertThat(wrappedCallable.call()).isEqualTo(attempts);
        verify(callable, times(attempts)).call();
    }

    @Test
    void testExceptionPredicate()
        throws Exception
    {
        final Callable<?> callable = mock(Callable.class);
        when(callable.call()).thenThrow(new TestException("Simulated failure"));

        final Predicate<Throwable> exceptionPredicate = throwable -> {
            // assert the predicate applies before exceptions are wrapped
            assertThat(throwable)
                .isExactlyInstanceOf(ThreadContextExecutionException.class)
                .hasCauseExactlyInstanceOf(TestException.class);
            // don't retry the TestException
            return !(throwable.getCause() instanceof TestException);
        };

        final RetryConfiguration retryConfiguration =
            RetryConfiguration.of(3, Duration.ZERO).retryOnExceptionPredicate(exceptionPredicate);
        final ResilienceConfiguration configuration =
            ResilienceConfiguration.of("test.retries.exception_predicates").retryConfiguration(retryConfiguration);
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, configuration);

        // Root cause of an exception thrown should be the throwable that caused the last attempt to fail
        assertThatThrownBy(wrappedCallable::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasRootCauseExactlyInstanceOf(TestException.class);
        verify(callable).call();
    }

    @Test
    @Disabled( "Test is unreliable on jenkins. Use this to verify the timeout behaviour locally." )
    void testTimeLimiterWithRetries()
        throws Exception
    {
        // Test that timeouts are applied to retries individually
        final int attempts = 10;
        final Callable<?> callable = mock(Callable.class);
        when(callable.call()).thenAnswer((Answer<Integer>) invocation -> {
            Thread.sleep(10000);
            fail("Thread wasn't canceled");
            return -1;
        });

        final RetryConfiguration retryConfiguration = RetryConfiguration.of(attempts, Duration.ZERO);
        final TimeLimiterConfiguration timeLimiter =
            TimeLimiterConfiguration.of().timeoutDuration(Duration.ofMillis(100));
        final ResilienceConfiguration resilienceConfiguration =
            ResilienceConfiguration
                .of("test.retries.timelimited")
                .retryConfiguration(retryConfiguration)
                .timeLimiterConfiguration(timeLimiter);
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, resilienceConfiguration);

        assertThatThrownBy(wrappedCallable::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasRootCauseExactlyInstanceOf(TimeoutException.class);

        verify(callable, times(attempts)).call();
    }

    @Test
    void testRetriesTriggerCircuitBreaker()
        throws Exception
    {
        final int circuitBreakerClosedBuffer = CircuitBreakerConfiguration.DEFAULT_CLOSED_BUFFER_SIZE;
        final int attempts = circuitBreakerClosedBuffer + 5;

        final Callable<?> callable = mock(Callable.class);
        when(callable.call()).thenThrow(new TestException("Simulated failure"));

        final RetryConfiguration retryConfiguration = RetryConfiguration.of(attempts, Duration.ZERO);
        final CircuitBreakerConfiguration circuitBreakerConfiguration = CircuitBreakerConfiguration.of();
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test.retries.circuit_breaker")
                .retryConfiguration(retryConfiguration)
                .circuitBreakerConfiguration(circuitBreakerConfiguration);
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, configuration);

        assertThatThrownBy(wrappedCallable::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasCauseExactlyInstanceOf(CallNotPermittedException.class);
        verify(callable, times(circuitBreakerClosedBuffer)).call();
    }

    private static class TestCallable implements Callable<Integer>
    {
        private static final int maxExpectedAttempts = 5;
        private int attemptCounter = 0;

        @Override
        public Integer call()
            throws Exception
        {
            attemptCounter++;
            if( attemptCounter < maxExpectedAttempts ) {
                throw new TestException("Simulated failure. Attempt number: " + attemptCounter);
            } else {
                return attemptCounter;
            }
        }
    }

    private static final class TestException extends Exception
    {
        private static final long serialVersionUID = -674277033652783396L;

        TestException( final String message )
        {
            super(message);
        }
    }
}
