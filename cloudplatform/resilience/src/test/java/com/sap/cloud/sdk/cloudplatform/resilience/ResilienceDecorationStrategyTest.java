/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

import io.vavr.control.Try;

class ResilienceDecorationStrategyTest
{
    public static class TestDecorationStrategy implements ResilienceDecorationStrategy
    {
        @Nonnull
        @Override
        public <T> Supplier<T> decorateSupplier(
            @Nonnull final Supplier<T> supplier,
            @Nonnull final ResilienceConfiguration configuration,
            @Nullable final Function<? super Throwable, T> fallbackFunction )
        {
            if( fallbackFunction != null ) {
                return () -> {
                    final Try<T> callWithFallback = Try.ofSupplier(supplier).recover(fallbackFunction);
                    return callWithFallback.get();
                };
            }
            return supplier;
        }

        @Nonnull
        @Override
        public <T> Callable<T> decorateCallable(
            @Nonnull final Callable<T> callable,
            @Nonnull final ResilienceConfiguration configuration,
            @Nullable final Function<? super Throwable, T> fallbackFunction )
        {
            if( fallbackFunction != null ) {
                return () -> {
                    final Try<T> callWithFallback = Try.ofCallable(callable).recover(fallbackFunction);
                    return callWithFallback.get();
                };
            }
            return callable;
        }

        @Nonnull
        public String getThreadName()
        {
            return Thread.currentThread().getName();
        }

        @Test
        void testNamedThreadQueueCallable()
            throws ExecutionException,
                InterruptedException
        {
            final CompletableFuture<String> threadNameQueueCallable =
                queueCallable(this::getThreadName, ResilienceConfiguration.empty("identifier"), null);
            final String threadName = threadNameQueueCallable.get();
            assertThat(threadName).matches("cloudsdk-executor-\\d+");
        }

        @Test
        void testNamedThreadQueueSupplier()
            throws ExecutionException,
                InterruptedException
        {
            final CompletableFuture<String> threadNameQueueSupplier =
                queueSupplier(this::getThreadName, ResilienceConfiguration.empty("identifier"), null);
            final String threadName = threadNameQueueSupplier.get();
            assertThat(threadName).matches("cloudsdk-executor-\\d+");
        }

        @Test
        void testThreadNameFromExecutor()
            throws ExecutionException,
                InterruptedException
        {
            final Supplier<String> nameSupplier = () -> Thread.currentThread().getName();
            final CompletableFuture<String> nameFuture =
                CompletableFuture.supplyAsync(nameSupplier, ThreadContextExecutors.getExecutor());
            assertThat(nameFuture.get()).matches("cloudsdk-executor-\\d+");
        }

        @Test
        void testExecuteCallableWithFallback()
        {
            final String positiveTest =
                new TestDecorationStrategy()
                    .executeCallable(() -> "Expected result", null, ( exception ) -> "Fallback result");

            assertThat(positiveTest).isEqualTo("Expected result");

            final Callable<String> failingCallable = () -> {
                throw new RuntimeException("Simulated failure");
            };

            final String positiveFallbackTest =
                new TestDecorationStrategy().executeCallable(failingCallable, null, ( exception ) -> "Fallback result");

            assertThat(positiveFallbackTest).isEqualTo("Fallback result");

            final Function<Throwable, String> negativeTest = ( exception ) -> {
                throw new RuntimeException("Bad example", exception);
            };

            assertThatThrownBy(() -> new TestDecorationStrategy().executeCallable(failingCallable, null, negativeTest))
                .isInstanceOf(ResilienceRuntimeException.class);
        }

        @Test
        void testThreadContextPropagated()
        {
            ThreadContextExecutors.execute(() -> {
                final ThreadContext parentThreadContext = ThreadContextAccessor.getCurrentContext();

                try {
                    assertThat(new TestDecorationStrategy().queueCallable(() -> {
                        final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                        assertThat(childContext).isEqualTo(parentThreadContext);
                        assertThat(childContext).isNotSameAs(parentThreadContext);
                        return null;
                    }, null, ( exception ) -> "Fallback result").get()).isNotEqualTo("Fallback result");
                }
                catch( final InterruptedException | ExecutionException e ) {
                    throw new RuntimeException(e);
                }
            });

        }

        @Test
        void testThreadContextPropagatedWithTimeLimiter()
        {
            ThreadContextExecutors.execute(() -> {
                final ThreadContext parentThreadContext = ThreadContextAccessor.getCurrentContext();

                try {
                    assertThat(new TestDecorationStrategy().queueCallable(() -> {
                        final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                        assertThat(childContext).isEqualTo(parentThreadContext);
                        assertThat(childContext).isNotSameAs(parentThreadContext);
                        return null;
                    },
                        ResilienceConfiguration
                            .of("identifier")
                            .timeLimiterConfiguration(ResilienceConfiguration.TimeLimiterConfiguration.of()),
                        ( exception ) -> "Fallback result").get()).isNotEqualTo("Fallback result");
                }
                catch( final InterruptedException | ExecutionException e ) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
