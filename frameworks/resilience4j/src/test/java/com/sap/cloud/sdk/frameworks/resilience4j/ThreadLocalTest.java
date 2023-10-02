/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.frameworks.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorationStrategy;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContextExecutorService;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

public class ThreadLocalTest
{
    @After
    public void resetExecutor()
    {
        ThreadContextExecutors.setExecutor(null);
    }

    @Ignore( "Testing the JRE." )
    @Test
    public void testThreadLocalIsNotInherited()
        throws InterruptedException
    {
        final ThreadLocal<String> local = new ThreadLocal<>();
        local.set("foo");
        final SoftAssertions softly = new SoftAssertions();

        final Thread thread = new Thread(() -> softly.assertThat(local.get()).isNull());
        thread.start();
        thread.join();

        softly.assertAll();
    }

    @Ignore( "Testing the JRE." )
    @Test
    public void testInheritableThreadLocal()
        throws InterruptedException
    {
        final ThreadLocal<String> local = new InheritableThreadLocal<>();
        local.set("foo");
        final SoftAssertions softly = new SoftAssertions();

        final Thread thread = new Thread(() -> softly.assertThat(local.get()).isEqualTo("foo"));
        thread.start();
        thread.join();

        softly.assertAll();
    }

    @Test
    public void testThreadLocalWithThreadedResilienceDecorator()
    {
        final ResilienceConfiguration resilienceConfiguration = ResilienceConfiguration.of("ThreadLocalTest-0");
        final ThreadLocal<String> storage = new ThreadLocal<>();
        storage.set("foo");

        final String foo = ResilienceDecorator.executeSupplier(storage::get, resilienceConfiguration);
        assertThat(foo).isEqualTo(null);
    }

    @Test
    public void testThreadLocalWithEmptyResilienceDecorator()
    {
        final ResilienceConfiguration resilienceConfiguration = ResilienceConfiguration.empty("ThreadLocalTest-1");
        final ThreadLocal<String> storage = new ThreadLocal<>();
        storage.set("foo");

        final String foo = ResilienceDecorator.executeSupplier(storage::get, resilienceConfiguration);
        assertThat(foo).isEqualTo("foo");
    }

    @Ignore( "Does not work in a repeated test runs or on Jenkins due to default ExecutorService. " )
    @Test
    public void testInheritableThreadLocalWithDefaultExecutor()
        throws Exception
    {
        final InheritableThreadLocal<String> local = new InheritableThreadLocal<>();
        local.set("foo");
        final SoftAssertions softly = new SoftAssertions();

        final ResilienceConfiguration conf = ResilienceConfiguration.of("InheritableThreadLocal-1");
        ResilienceDecorator.executeCallable(() -> softly.assertThat(local.get()).isEqualTo("foo"), conf);

        softly.assertAll();
    }

    @Test
    public void testInheritableThreadLocalWithFixedThreadPoolExecutor()
        throws ExecutionException,
            InterruptedException
    {
        final int NUM_BLOCKING_THREADS_PER_RESILIENT_CALL = 2;
        final InheritableThreadLocal<String> local = new InheritableThreadLocal<>();
        local.set("foo");

        final ResilienceDecorationStrategy decorationStrategy = ResilienceDecorator.getDecorationStrategy();
        final ResilienceConfiguration conf = ResilienceConfiguration.of("InheritableThreadLocal-2");
        final Function<Throwable, String> fallback = null;
        final ExecutorService executor = Executors.newFixedThreadPool(NUM_BLOCKING_THREADS_PER_RESILIENT_CALL);
        ThreadContextExecutors.setExecutor(DefaultThreadContextExecutorService.of(executor));

        final CompletableFuture<String> future = decorationStrategy.queueSupplier(local::get, conf, fallback);
        assertThat(future.get()).isEqualTo("foo");
    }
}
