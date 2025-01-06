/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.resilience;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContextExecutorService;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

class ResilienceDecoratorQueuedOperationsTest
{
    private static final ResilienceConfiguration CONFIG =
        ResilienceConfiguration.of(ResilienceDecoratorQueuedOperationsTest.class);

    @BeforeEach
    void prepareDecorator()
    {
        ResilienceDecorator.setDecorationStrategy(new ResilienceDecorationStrategyTest.TestDecorationStrategy());
    }

    @AfterEach
    void resetDecorator()
    {
        ResilienceDecorator.resetDecorationStrategy();
    }

    @AfterEach
    void resetExecutor()
    {
        ThreadContextExecutors.setExecutor(null);
    }

    @Test
    void testQueuedCallable()
        throws ExecutionException,
            InterruptedException
    {
        final CompletableFuture<String> s = ResilienceDecorator.queueCallable(() -> "foobar", CONFIG);

        assertThat(s.get()).isEqualTo("foobar");
        assertThat(s).isDone();
    }

    @Test
    void testQueuedCallableFallback()
        throws ExecutionException,
            InterruptedException
    {
        final Callable<String> failingCallable = () -> {
            throw new IllegalArgumentException();
        };

        final Function<Throwable, String> fallbackHandler = e -> {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
            return "fallback";
        };

        final CompletableFuture<String> s = ResilienceDecorator.queueCallable(failingCallable, CONFIG, fallbackHandler);

        assertThat(s.get()).isEqualTo("fallback");
        assertThat(s).isDone();
    }

    @Test
    void testQueuedCallableWithExecutor()
        throws ExecutionException,
            InterruptedException
    {
        final int numThreads = 1;
        final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(numThreads, numThreads, 100, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ThreadContextExecutors.setExecutor(DefaultThreadContextExecutorService.of(executor));

        final CompletableFuture<String> s =
            ResilienceDecorator.getDecorationStrategy().queueCallable(() -> "foobar", CONFIG, null);

        assertThat(s.get()).isEqualTo("foobar");
        assertThat(s).isDone();

        // wait some arbitrary time until the task is "guaranteed" to be completed
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(executor.getCompletedTaskCount()).isEqualTo(1);
    }

    @Test
    void testQueuedSupplier()
        throws ExecutionException,
            InterruptedException
    {
        final CompletableFuture<String> s = ResilienceDecorator.queueSupplier(() -> "foobar", CONFIG);

        assertThat(s.get()).isEqualTo("foobar");
        assertThat(s).isDone();
    }

    @Test
    void testQueuedSupplierFallback()
        throws ExecutionException,
            InterruptedException
    {
        final Supplier<String> failingSupplier = () -> {
            throw new IllegalArgumentException();
        };

        final Function<Throwable, String> fallbackHandler = e -> {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
            return "fallback";
        };

        final CompletableFuture<String> s = ResilienceDecorator.queueSupplier(failingSupplier, CONFIG, fallbackHandler);

        assertThat(s.get()).isEqualTo("fallback");
        assertThat(s).isDone();
    }

    @Test
    void testQueuedSupplierWithExecutor()
        throws ExecutionException,
            InterruptedException
    {
        final int numThreads = 2;
        final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(numThreads, numThreads, 100, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ThreadContextExecutors.setExecutor(DefaultThreadContextExecutorService.of(executor));

        final CompletableFuture<String> s =
            ResilienceDecorator.getDecorationStrategy().queueSupplier(() -> "foobar", CONFIG, null);

        assertThat(s.get()).isEqualTo("foobar");
        assertThat(s).isDone();

        // wait some arbitrary time until the task is "guaranteed" to be completed
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(executor.getTaskCount()).isEqualTo(1);
    }
}
