/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.frameworks.resilience4j;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofNanos;

import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.BulkheadConfiguration;
import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CircuitBreakerConfiguration;
import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContextExecutorService;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

import io.github.resilience4j.bulkhead.BulkheadFullException;

public class Resilience4jDecorationAsyncTest
{
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    @After
    public void resetExecutor()
    {
        ThreadContextExecutors.setExecutor(null);
    }

    @Test
    public void testAsyncExecution()
        throws ExecutionException,
            InterruptedException
    {
        final Semaphore taskStart = new Semaphore(0);
        final Semaphore taskEnd = new Semaphore(0);

        final Supplier<String> heavyFunction = createBusyFunction(taskStart, taskEnd);

        // schedule and start lambda asynchronously
        final CompletableFuture<String> futureSupplier =
            ResilienceDecorator
                .queueSupplier(heavyFunction, ResilienceConfiguration.of(Resilience4jDecorationAsyncTest.class));

        // wait for asynchronous task to start
        taskStart.acquire();

        // assert the future is non-blocking
        assertThat(futureSupplier).isNotDone();
        taskEnd.release();

        // evaluate and assert future result
        assertThat(futureSupplier.get()).isEqualTo(SUCCESS);
        assertThat(futureSupplier).isDone();
    }

    @Test
    public void testTimeoutSyncExecution()
    {
        final Semaphore threadsHavingStarted = new Semaphore(0);
        final Semaphore threadsAllowedToEnd = new Semaphore(0);
        final Supplier<String> busyFunction = createBusyFunction(threadsHavingStarted, threadsAllowedToEnd);

        final Supplier<String> supplier =
            ResilienceDecorator
                .decorateSupplier(
                    busyFunction,
                    ResilienceConfiguration
                        .of("test-timeout")
                        .timeLimiterConfiguration(TimeLimiterConfiguration.of().timeoutDuration(ofMillis(1))));

        assertThatThrownBy(supplier::get)
            .isInstanceOf(ResilienceRuntimeException.class) // wrapper exception
            .hasRootCauseInstanceOf(TimeoutException.class); // timeout exception
    }

    @Test
    public void testTimeoutAsyncExecution()
    {
        final Semaphore threadsHavingStarted = new Semaphore(0);
        final Semaphore threadsAllowedToEnd = new Semaphore(0);
        final Supplier<String> busyFunction = createBusyFunction(threadsHavingStarted, threadsAllowedToEnd);

        final CompletableFuture<String> future =
            ResilienceDecorator
                .queueSupplier(
                    busyFunction,
                    ResilienceConfiguration
                        .of("test-async-execution")
                        .timeLimiterConfiguration(TimeLimiterConfiguration.of().timeoutDuration(ofMillis(10))));

        // timeout call
        assertThatThrownBy(future::get)
            .isInstanceOf(ExecutionException.class)
            .hasCauseInstanceOf(ResilienceRuntimeException.class)
            .hasRootCauseInstanceOf(TimeoutException.class);
    }

    @Test
    public void testManyConcurrentRequests()
        throws ExecutionException,
            InterruptedException
    {
        final int numThreads = 3; // this number may depend on the CPU, for concurrent threads
        final int numCalls = 50;

        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test-concurrent-requests")
                .timeLimiterConfiguration(TimeLimiterConfiguration.disabled())
                .bulkheadConfiguration(
                    BulkheadConfiguration.of().maxConcurrentCalls(numCalls).maxWaitDuration(ofNanos(Long.MAX_VALUE)))
                .circuitBreakerConfiguration(
                    CircuitBreakerConfiguration.of().waitDuration(ofNanos(Long.MAX_VALUE)).failureRateThreshold(50));

        final Semaphore threadsHavingStarted = new Semaphore(0);
        final Semaphore threadsAllowedToEnd = new Semaphore(0); // do not allow threads to end
        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ThreadContextExecutors.setExecutor(DefaultThreadContextExecutorService.of(executor));

        final CompletableFuture<?>[] futures = new CompletableFuture<?>[numCalls];
        for( int i = 0; i < numCalls; i++ ) {

            // prepare heavy operation
            final Supplier<String> heavyFunction = createBusyFunction(threadsHavingStarted, threadsAllowedToEnd);

            // schedule and execute operation
            futures[i] = ResilienceDecorator.getDecorationStrategy().queueSupplier(heavyFunction, configuration, null);
        }

        // wait until the required number of concurrent calls is active
        threadsHavingStarted.acquire(numThreads);

        // test complete, allow all threads to finish
        threadsAllowedToEnd.release(numCalls);

        // assert joined result of threads
        final CompletableFuture<List<String>> combined = joinFutures(CompletableFuture::allOf, String.class, futures);
        assertThat(combined.get()).containsOnly(SUCCESS);

        executor.shutdownNow();
    }

    @Test
    public void testBulkheadFullForConcurrentlyRunningRequests()
    {
        final int numCalls = 3;

        final int numThreads = numCalls + numCalls; // 1 for each time limiter and 1 for each async execution per call

        final int numThreshold = 2; // bulkhead threshold for concurrent calls

        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test-bulkhead-full")
                .timeLimiterConfiguration(
                    TimeLimiterConfiguration.of().timeoutDuration(ofMillis(5000)).shouldCancelRunningFuture(true))
                .bulkheadConfiguration(
                    BulkheadConfiguration.of().maxConcurrentCalls(numThreshold).maxWaitDuration(ofMillis(10)))
                .circuitBreakerConfiguration(
                    CircuitBreakerConfiguration.of().waitDuration(ofNanos(Long.MAX_VALUE)).failureRateThreshold(50));

        final Semaphore threadsHavingStarted = new Semaphore(0);
        final Semaphore threadsAllowedToEnd = new Semaphore(0); // do not allow threads to end
        final ExecutorService executor =
            new ThreadPoolExecutor(numThreads, numThreads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ThreadContextExecutors.setExecutor(DefaultThreadContextExecutorService.of(executor));

        final CompletableFuture<?>[] futures = new CompletableFuture<?>[numCalls];
        for( int i = 0; i < numCalls; i++ ) {

            // prepare heavy operation
            final Supplier<String> heavyFunction = createBusyFunction(threadsHavingStarted, threadsAllowedToEnd);

            // schedule and execute operation
            futures[i] = ResilienceDecorator.getDecorationStrategy().queueSupplier(heavyFunction, configuration, null);
        }

        // only future to finish is due to the bulkhead exception
        final CompletableFuture<?> concurrencyResult = joinFutures(CompletableFuture::anyOf, String.class, futures);
        assertThatThrownBy(concurrencyResult::get)
            .isInstanceOf(ExecutionException.class) // wrapper exception
            .hasRootCauseInstanceOf(BulkheadFullException.class); // bulkhead exception

        assertThat(futures)
            .haveAtLeastOne(new Condition<>(CompletableFuture::isCompletedExceptionally, "Exception in Future."))
            .haveAtLeastOne(new Condition<>(CompletableFuture::isDone, "Future is completed with unknown outcome."))
            .haveAtLeastOne(new Condition<>(future -> !future.isDone(), "Future is not yet completed."));

        executor.shutdownNow();
    }

    @Test
    @Ignore( "Test is unreliable on Jenkins. Use this to verify the behavior locally." )
    public void testTimeoutExceptionWhenBulkheadIsNotFullForConcurrentlyRunningRequests()
    {
        final int numCalls = 3;

        final int numThreads = numCalls + numCalls; // 1 for each time limiter and 1 for each async execution per call

        final int numThreshold = 3;

        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test-bulkhead-with-timeout")
                .timeLimiterConfiguration(
                    TimeLimiterConfiguration.of().timeoutDuration(ofMillis(400)).shouldCancelRunningFuture(true))
                .bulkheadConfiguration(
                    BulkheadConfiguration.of().maxConcurrentCalls(numThreshold).maxWaitDuration(ofMillis(10)));

        final Semaphore threadsHavingStarted = new Semaphore(0);
        final Semaphore threadsAllowedToEnd = new Semaphore(0); // do not allow threads to end
        final ExecutorService executor =
            new ThreadPoolExecutor(numThreads, numThreads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ThreadContextExecutors.setExecutor(DefaultThreadContextExecutorService.of(executor));

        final CompletableFuture<?>[] futures = new CompletableFuture<?>[numCalls];
        for( int i = 0; i < numCalls; i++ ) {

            // prepare heavy operation
            final Supplier<String> heavyFunction = createBusyFunction(threadsHavingStarted, threadsAllowedToEnd);

            // schedule and execute operation
            futures[i] = ResilienceDecorator.getDecorationStrategy().queueSupplier(heavyFunction, configuration, null);
        }

        // All futures finish exceptionally with timeout
        final CompletableFuture<?> concurrencyResult = joinFutures(CompletableFuture::anyOf, String.class, futures);
        assertThatThrownBy(concurrencyResult::get)
            .isInstanceOf(ExecutionException.class) // wrapper exception
            .hasRootCauseInstanceOf(TimeoutException.class);

        assertThat(futures)
            .satisfiesExactly(
                f -> assertThat(f).isCompletedExceptionally().isDone(),
                f -> assertThat(f).isCompletedExceptionally().isDone(),
                f -> assertThat(f).isCompletedExceptionally().isDone());

        assertThat(threadsHavingStarted.availablePermits()).isEqualTo(3); //Assert that all 3 tasks started execution before timeout
        executor.shutdownNow();
    }

    @Nonnull
    private <T> CompletableFuture<List<T>> joinFutures(
        @Nonnull final Function<CompletableFuture<?>[], CompletableFuture<?>> futureCombiner,
        @Nonnull final Class<T> clazz,
        final CompletableFuture<?>... futures )
    {
        return futureCombiner
            .apply(futures)
            .thenApply(
                commonFuture -> Arrays
                    .stream(futures)
                    .map(CompletableFuture::join) // wait for each future to finish
                    .map(clazz::cast) // extract result to string list
                    .collect(Collectors.toList()));
    }

    private
        Supplier<String>
        createBusyFunction( @Nonnull final Semaphore semaphoreStart, @Nonnull final Semaphore semaphoreEnd )
    {
        return () -> {
            try {
                semaphoreStart.release();
                while( !semaphoreEnd.tryAcquire(500, TimeUnit.MILLISECONDS) ) {
                    // do nothing
                    System.out.print(".");
                }
            }
            catch( final InterruptedException e ) {
                // semaphore polling was interrupted
                System.out.println("err");
                return ERROR;
            }
            return SUCCESS;
        };
    }
}
