/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CacheConfiguration;
import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.RetryConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.cache.Caching;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.sap.cloud.sdk.cloudplatform.cache.SerializableCacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.testutil.TestContext;

import io.vavr.control.Try;
import lombok.SneakyThrows;

class Resilience4jCachingDefaultProviderTest
{
    @RegisterExtension
    static final TestContext context = TestContext.withThreadContext();

    @BeforeAll
    static void beforeClass()
    {
        ResilienceDecorator.setDecorationStrategy(new Resilience4jDecorationStrategy());
    }

    @BeforeEach
    @AfterEach
    void cleanupAroundTests()
    {
        context.clearTenant();
        context.clearPrincipal();
        DefaultCachingDecorator.lockCache.invalidateAll();
    }

    @Test
    void testCachingWithDefaultProvider()
    {
        // resilient configuration with cache
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test.caching.provider.default")
                .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withoutParameters());

        // cached call
        final Queue<String> alphabet = new ArrayDeque<>(Arrays.asList("A", "B", "C", "D", "E"));

        final Supplier<String> todaysLetter = ResilienceDecorator.decorateSupplier(alphabet::poll, configuration);

        assertThat(todaysLetter.get()).isEqualTo("A");
        assertThat(alphabet.peek()).isEqualTo("B");

        assertThat(todaysLetter.get()).isEqualTo("A");
        assertThat(alphabet.peek()).isEqualTo("B");

        // Test Cache invalidation
        ResilienceDecorator.clearCache(configuration);
        assertThat(todaysLetter.get()).isEqualTo("B");
        assertThat(alphabet.peek()).isEqualTo("C");
        // after invalidation the cache is still in use
        assertThat(todaysLetter.get()).isEqualTo("B");
        assertThat(alphabet.peek()).isEqualTo("C");

        // Test Cache destruction
        Caching.getCachingProvider().getCacheManager().destroyCache(configuration.identifier());
        assertThat(todaysLetter.get()).isEqualTo("C");
        assertThat(alphabet.peek()).isEqualTo("D");
        // as the Cache is destroyed it cannot be used anymore
        assertThat(todaysLetter.get()).isEqualTo("D");
        assertThat(alphabet.peek()).isEqualTo("E");
    }

    @Test
    void testCachingWithDifferentIsolationModes()
    {
        mockTenantAndPrincipal("TestTenant", "TestUser");

        // prepare configurations
        final String commonConf = "test.caching.cachkey.isolationMode";
        final List<ResilienceConfiguration> configurations =
            Lists
                .newArrayList(
                    ResilienceConfiguration.of(commonConf).isolationMode(ResilienceIsolationMode.NO_ISOLATION),
                    ResilienceConfiguration.of(commonConf).isolationMode(ResilienceIsolationMode.NO_ISOLATION),
                    ResilienceConfiguration.of(commonConf).isolationMode(ResilienceIsolationMode.TENANT_REQUIRED),
                    ResilienceConfiguration.of(commonConf).isolationMode(ResilienceIsolationMode.TENANT_OPTIONAL),
                    ResilienceConfiguration
                        .of(commonConf)
                        .isolationMode(ResilienceIsolationMode.TENANT_AND_USER_REQUIRED),
                    ResilienceConfiguration
                        .of(commonConf)
                        .isolationMode(ResilienceIsolationMode.TENANT_AND_USER_OPTIONAL));

        // cached call
        final Queue<String> fruits = new ArrayDeque<>(Arrays.asList("Apple", "Banana", "Cherry", "Potato"));

        for( final ResilienceConfiguration configuration : configurations ) {
            // apply caching duration
            configuration.cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withoutParameters());

            final Supplier<String> todaysFruit = ResilienceDecorator.decorateSupplier(fruits::poll, configuration);
            final String fruit1 = todaysFruit.get();
            final String fruit2 = todaysFruit.get();
            assertThat(fruit1).isEqualTo(fruit2);
        }

        assertThat(fruits.peek()).isEqualTo("Potato");
    }

    @Test
    void testCachingWithDifferentUsers()
    {
        final String tenantId = "TestTenant";

        // prepare configurations
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test.caching.cachkey.user")
                .isolationMode(ResilienceIsolationMode.TENANT_AND_USER_REQUIRED)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withoutParameters());

        final Queue<String> fruits = new ArrayDeque<>(Arrays.asList("Apple", "Banana", "Cherry", "Potato"));
        final List<String> userIds = Lists.newArrayList("Alice", "Bob", "Steve");

        for( final String userId : userIds ) {
            mockTenantAndPrincipal(tenantId, userId);

            final Supplier<String> todaysFruit = ResilienceDecorator.decorateSupplier(fruits::poll, configuration);
            final String fruit1 = todaysFruit.get();
            final String fruit2 = todaysFruit.get();
            assertThat(fruit1).isEqualTo(fruit2);
        }
        assertThat(fruits.peek()).isEqualTo("Potato");
    }

    @Test
    void testCachingWithDifferentKeyComponents()
    {
        final List<String> testWords =
            Lists
                .newArrayList(
                    "Nyan",
                    "NyanNyan",
                    "NyanNyanNyan",
                    "Nyan",
                    "NyanNyan",
                    "Nyan",
                    "Nyan",
                    "NyanNyanNyan",
                    "Nyan");

        final Multiset<String> trackedWords = HashMultiset.create();
        final Function<String, Integer> trackedCharacterCounter = ( word ) -> {
            trackedWords.add(word);
            return word.length();
        };

        for( final String word : testWords ) {

            final Supplier<Integer> wordLength =
                ResilienceDecorator
                    .decorateSupplier(
                        () -> trackedCharacterCounter.apply(word),
                        ResilienceConfiguration
                            .of("test.caching.cachkey.word")
                            .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                            .cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withParameters(word)));

            assertThat(wordLength.get()).isEqualTo(word.length());
        }

        assertThat(trackedWords.entrySet()).allMatch(entry -> 1 == entry.getCount());
    }

    @Test
    void testCachingWithDifferentTenants()
    {
        final String userName = "TestUser";

        // prepare configurations
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test.caching.cachkey.user")
                .isolationMode(ResilienceIsolationMode.TENANT_AND_USER_REQUIRED)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withoutParameters());

        final Queue<String> fruits = new ArrayDeque<>(Arrays.asList("Apple", "Banana", "Cherry", "Potato"));
        final List<String> tenantIds = Lists.newArrayList("SAP", "Acme Inc", "Umbrella Corp");

        for( final String tenantId : tenantIds ) {
            mockTenantAndPrincipal(tenantId, userName);

            final Supplier<String> todaysFruit = ResilienceDecorator.decorateSupplier(fruits::poll, configuration);
            final String fruit1 = todaysFruit.get();
            final String fruit2 = todaysFruit.get();
            assertThat(fruit1).isEqualTo(fruit2);
        }
        assertThat(fruits.peek()).isEqualTo("Potato");
    }

    @Test
    void testCachingWithoutProvider()
    {
        // resilient configuration with cache
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test.caching.provider.none")
                .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofHours(1)).withoutParameters());

        // wrap execution of cached call into empty class loader
        final ThrowingCallable failingCall =
            () -> ClassLoaderUtil
                .runWithEmptyClassLoader(() -> ResilienceDecorator.executeSupplier(LocalDate::now, configuration));

        // test assertion for matching "No CachingProviders..." exception
        assertThatCode(failingCall)
            .isInstanceOf(javax.cache.CacheException.class)
            .hasMessageContaining("No CachingProviders have been configured");
    }

    @Test
    void testFallbackResultIsNotCached()
        throws Exception
    {
        mockTenantAndPrincipal("TestTenant", "TestUser");
        final int numberOfRetries = 3;

        // resilient configuration with cache
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("test.caching.fallback")
                .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withoutParameters())
                .retryConfiguration(RetryConfiguration.of(numberOfRetries, Duration.ZERO));

        final Callable<String> testLogic = new Callable<String>()
        {
            private int attemptCounter = 0;

            @Override
            public String call()
            {
                attemptCounter++;
                if( attemptCounter <= numberOfRetries ) {
                    throw new RuntimeException("Simulated failure.");
                } else {
                    return "Recovered function.";
                }
            }
        };

        final Callable<String> testCallable =
            ResilienceDecorator.decorateCallable(testLogic, configuration, ( exception ) -> "Fallback function.");

        // 1st time is failure
        assertThat(testCallable.call()).isEqualTo("Fallback function.");
        // 2nd time is successful
        assertThat(testCallable.call()).isEqualTo("Recovered function.");
        // 3rd time gets cached value
        assertThat(testCallable.call()).isEqualTo("Recovered function.");
    }

    @Test
    void testAutomaticCacheRecreation()
        throws Exception
    {
        final int testParam = 0;
        final ResilienceConfiguration conf = ResilienceConfiguration.of("testAutomaticCacheRecreation");
        final TestCallable testCallable = new TestCallable();
        conf.cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withParameters(testParam));
        Callable<Integer> callable = ResilienceDecorator.decorateCallable(testCallable, conf);
        assertThat(callable.call()).isEqualTo(1);

        conf.cacheConfiguration(CacheConfiguration.of(Duration.ofDays(5)).withoutParameters());
        callable = ResilienceDecorator.decorateCallable(testCallable, conf);
        assertThat(callable.call()).isEqualTo(2);
    }

    @Test
    void testCacheIsNotRecreated()
        throws Exception
    {
        final int testParam = 0;
        final ResilienceConfiguration conf = ResilienceConfiguration.of("testCacheIsNotRecreated");
        final TestCallable testCallable = new TestCallable();
        conf.cacheConfiguration(CacheConfiguration.of(Duration.ofDays(5)).withParameters(testParam));
        Callable<Integer> callable = ResilienceDecorator.decorateCallable(testCallable, conf);
        assertThat(callable.call()).isEqualTo(1);

        callable = ResilienceDecorator.decorateCallable(testCallable, conf);
        assertThat(callable.call()).isEqualTo(1);
    }

    @Test
    void testClearCacheWithParameterIsolation()
        throws Exception
    {
        final ResilienceConfiguration configurationWithoutParameters =
            ResilienceConfiguration
                .empty("testClearCacheWithParameterIsolation")
                .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withoutParameters());

        final ResilienceConfiguration configurationWithParameters =
            ResilienceConfiguration
                .empty("testClearCacheWithParameterIsolation")
                .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofDays(1)).withParameters("Parameter"));

        final Callable<Integer> callable = new TestCallable();

        assertThat(ResilienceDecorator.executeCallable(callable, configurationWithoutParameters)).isEqualTo(1);
        assertThat(ResilienceDecorator.executeCallable(callable, configurationWithParameters)).isEqualTo(2);

        ResilienceDecorator.clearCache(configurationWithParameters);

        assertThat(ResilienceDecorator.executeCallable(callable, configurationWithoutParameters)).isEqualTo(1);
        assertThat(ResilienceDecorator.executeCallable(callable, configurationWithParameters)).isEqualTo(3);
    }

    @Test
    void testClearCacheRespectsParameterOrder()
        throws Exception
    {
        final ResilienceConfiguration firstConfiguration =
            ResilienceConfiguration
                .empty("testClearCacheIgnoresParameterOrder")
                .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .cacheConfiguration(
                    CacheConfiguration.of(Duration.ofDays(1)).withParameters("FirstParameter", "SecondParameter"));

        final ResilienceConfiguration secondConfiguration =
            ResilienceConfiguration
                .empty("testClearCacheIgnoresParameterOrder")
                .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .cacheConfiguration(
                    CacheConfiguration.of(Duration.ofDays(1)).withParameters("SecondParameter", "FirstParameter"));

        final Callable<Integer> callable = new TestCallable();

        assertThat(ResilienceDecorator.executeCallable(callable, firstConfiguration)).isEqualTo(1);
        assertThat(ResilienceDecorator.executeCallable(callable, secondConfiguration)).isEqualTo(2);

        ResilienceDecorator.clearCache(firstConfiguration);

        assertThat(ResilienceDecorator.executeCallable(callable, firstConfiguration)).isEqualTo(3);
        assertThat(ResilienceDecorator.executeCallable(callable, secondConfiguration)).isEqualTo(2);
    }

    @Test
    @SneakyThrows
    @SuppressWarnings( "unchecked" )
    void testCacheLockingOnSameKeyIsLocked()
    {
        final String identifier = "test.check.cache.locked.supplier";
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of(identifier)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofHours(1)).withoutParameters());

        final AtomicInteger counter = new AtomicInteger(0);

        final Callable<Integer> decoratedCallable =
            new DefaultCachingDecorator().decorateCallable(counter::incrementAndGet, configuration);
        final Supplier<Integer> decoratedSupplier = (Supplier<Integer>) mock(Supplier.class);
        when(decoratedSupplier.get()).thenAnswer(invocation -> decoratedCallable.call());

        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final Lock customLock = spy(new ReentrantLock());
        doAnswer(invocation -> {
            // let the calls wait in here for each other, basically getting released to get the lock at the same time
            countDownLatch.countDown();
            countDownLatch.await();
            return invocation.callRealMethod();
        }).when(customLock).lock();

        DefaultCachingDecorator.lockCache
            .put(SerializableCacheKey.of((Tenant) null, null).append(Collections.singleton(identifier)), customLock);

        final CompletableFuture<Integer> firstCall = CompletableFuture.supplyAsync(decoratedSupplier);
        final CompletableFuture<Integer> secondCall = CompletableFuture.supplyAsync(decoratedSupplier);

        assertThat(firstCall.get()).isEqualTo(1);
        assertThat(secondCall.get()).isEqualTo(1);

        assertThat(DefaultCachingDecorator.lockCache.asMap()).hasSize(1);
    }

    @Test
    @SneakyThrows
    @SuppressWarnings( "unchecked" )
    void testCacheLockingOnDifferentCachesIsNotLocked()
    {
        final ResilienceConfiguration configuration1 =
            ResilienceConfiguration
                .of("test.check.cache.locked.supplier.one")
                .cacheConfiguration(CacheConfiguration.of(Duration.ofHours(1)).withoutParameters());
        final ResilienceConfiguration configuration2 =
            ResilienceConfiguration
                .of("test.check.cache.locked.supplier.two")
                .cacheConfiguration(CacheConfiguration.of(Duration.ofHours(1)).withoutParameters());

        final AtomicInteger counter1 = new AtomicInteger(0);
        final AtomicInteger counter2 = new AtomicInteger(41);

        final Callable<Integer> decoratedCallable1 =
            new DefaultCachingDecorator().decorateCallable(counter1::incrementAndGet, configuration1);
        final Supplier<Integer> decoratedSupplier1 = (Supplier<Integer>) mock(Supplier.class);
        when(decoratedSupplier1.get()).thenAnswer(invocation -> decoratedCallable1.call());
        final Callable<Integer> decoratedCallable2 =
            new DefaultCachingDecorator().decorateCallable(counter2::incrementAndGet, configuration2);
        final Supplier<Integer> decoratedSupplier2 = (Supplier<Integer>) mock(Supplier.class);
        when(decoratedSupplier2.get()).thenAnswer(invocation -> decoratedCallable2.call());

        final CompletableFuture<Integer> call1 = CompletableFuture.supplyAsync(decoratedSupplier1);
        final CompletableFuture<Integer> call2 = CompletableFuture.supplyAsync(decoratedSupplier2);

        assertThat(call1.get()).isEqualTo(1);
        assertThat(call2.get()).isEqualTo(42);

        assertThat(DefaultCachingDecorator.lockCache.asMap()).hasSize(2);
    }

    @Test
    @Disabled( "Flaky test to provoke race potential conditions. For manual testing only" )
    void loadTestConcurrentCaching()
    {
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("concurrent-caching")
                .cacheConfiguration(CacheConfiguration.of(Duration.ofMillis(100)).withoutParameters());

        final Semaphore threadWaiting = new Semaphore(0);
        final Supplier<Boolean> testSupplier =
            () -> Try.of(() -> threadWaiting.tryAcquire(100, TimeUnit.MILLISECONDS)).get();

        final Supplier<Boolean> resilientSupplier = ResilienceDecorator.decorateSupplier(testSupplier, configuration);
        final Executor executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        final CompletableFuture<Boolean> future1 = CompletableFuture.supplyAsync(resilientSupplier, executor);
        final CompletableFuture<Boolean> future2 = CompletableFuture.supplyAsync(resilientSupplier, executor);

        // The threads can finish without concurrent cache issues.
        CompletableFuture.allOf(future1, future2).join();
    }

    static final class TestCallable implements Callable<Integer>
    {
        private int counter = 0;

        @Override
        public Integer call()
            throws Exception
        {
            return ++counter;
        }
    }

    private static void mockTenantAndPrincipal( @Nullable final String tenantId, @Nullable final String principalId )
    {
        context.setTenant(tenantId);
        context.setPrincipal(principalId);
    }
}
