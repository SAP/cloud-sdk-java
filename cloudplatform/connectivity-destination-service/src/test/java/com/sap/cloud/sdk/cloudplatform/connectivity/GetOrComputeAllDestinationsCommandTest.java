package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

import io.vavr.control.Try;
import lombok.SneakyThrows;

class GetOrComputeAllDestinationsCommandTest
{
    private static final int TEST_TIMEOUT = 30_000; // 30 seconds
    private static final String DESTINATION_NAME = "SomeDestinationName";

    private static final DestinationOptions EMPTY_OPTIONS = DestinationOptions.builder().build();
    private static final Tenant t1 = new DefaultTenant("tenant-1");
    private static final Tenant t2 = new DefaultTenant("tenant-2");

    private Cache<CacheKey, List<DestinationProperties>> allDestinationsCache;
    private Cache<CacheKey, ReentrantLock> isolationLocks;

    @BeforeEach
    void setup()
    {
        CacheManager.invalidateAll();
        allDestinationsCache = Caffeine.newBuilder().build();
        isolationLocks = Caffeine.newBuilder().build();
    }

    @AfterEach
    void resetDestinationCache()
    {
        CacheManager.invalidateAll();
    }

    @Test
    @SuppressWarnings( "unchecked" )
    void testCommandIsIdempotent()
    {
        final Destination destination = DefaultDestination.builder().name(DESTINATION_NAME).build();
        final Supplier<Try<List<DestinationProperties>>> tryGetAllDestinations =
            (Supplier<Try<List<DestinationProperties>>>) spy(Supplier.class);
        when(tryGetAllDestinations.get()).thenReturn(Try.success(Collections.singletonList(destination)));

        final Supplier<GetOrComputeAllDestinationsCommand> commandSupplier =
            () -> GetOrComputeAllDestinationsCommand
                .prepareCommand(
                    EMPTY_OPTIONS,
                    allDestinationsCache,
                    isolationLocks,
                    any -> tryGetAllDestinations.get());

        // running multiple times within the cache duration should only yield one execution
        commandSupplier.get().execute();
        commandSupplier.get().execute();
        final GetOrComputeAllDestinationsCommand command = commandSupplier.get();
        command.execute();
        command.execute();
        final Try<List<DestinationProperties>> result = command.execute();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).containsExactly(destination);

        assertThat(allDestinationsCache.estimatedSize()).isEqualTo(1);
        assertThat(allDestinationsCache.getIfPresent(CacheKey.ofNoIsolation().append(EMPTY_OPTIONS)))
            .containsExactly(destination);
        verify(tryGetAllDestinations, times(1)).get();
    }

    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    @SneakyThrows
    @SuppressWarnings( "unchecked" )
    void testIsolationAndAtomicityPerTenant()
    {
        final CountDownLatch mainThreadLatch = new CountDownLatch(1);
        final CountDownLatch getAllLatch = new CountDownLatch(1);
        final AtomicInteger lockInvocations = new AtomicInteger();
        final CacheKey t1Key = CacheKey.of(t1, null).append(EMPTY_OPTIONS);
        final CacheKey t2Key = CacheKey.of(t2, null).append(EMPTY_OPTIONS);
        final ReentrantLock tenantIsolationLock = spy(ReentrantLock.class);

        doAnswer(invocation -> {
            if( lockInvocations.incrementAndGet() > 1 ) {
                // second invocation for t1: unlock the first tryGetAllDestinations call
                getAllLatch.countDown();
            }
            return invocation.callRealMethod();
        }).when(tenantIsolationLock).lock();
        isolationLocks.put(t1Key, tenantIsolationLock);

        final Supplier<Try<List<DestinationProperties>>> tryGetAllDestinationsT1 =
            (Supplier<Try<List<DestinationProperties>>>) mock(Supplier.class);
        when(tryGetAllDestinationsT1.get()).then(invocation -> {
            mainThreadLatch.countDown();
            getAllLatch.await();

            return Try.success(Collections.emptyList());
        });
        final Supplier<Try<List<DestinationProperties>>> tryGetAllDestinationsT2 =
            (Supplier<Try<List<DestinationProperties>>>) mock(Supplier.class);
        when(tryGetAllDestinationsT2.get()).thenReturn(Try.success(Collections.emptyList()));

        final Callable<Try<List<DestinationProperties>>> callableT1 =
            () -> TenantAccessor
                .executeWithTenant(
                    t1,
                    () -> GetOrComputeAllDestinationsCommand
                        .prepareCommand(
                            EMPTY_OPTIONS,
                            allDestinationsCache,
                            isolationLocks,
                            any -> tryGetAllDestinationsT1.get())
                        .execute());
        final Callable<Try<List<DestinationProperties>>> callableT2 =
            () -> TenantAccessor
                .executeWithTenant(
                    t2,
                    () -> GetOrComputeAllDestinationsCommand
                        .prepareCommand(
                            EMPTY_OPTIONS,
                            allDestinationsCache,
                            isolationLocks,
                            any -> tryGetAllDestinationsT2.get())
                        .execute());

        final Future<Try<List<DestinationProperties>>> firstInvocation = ThreadContextExecutors.submit(callableT1);
        mainThreadLatch.await();
        final Future<Try<List<DestinationProperties>>> secondInvocation = ThreadContextExecutors.submit(callableT2);
        final Future<Try<List<DestinationProperties>>> thirdInvocation = ThreadContextExecutors.submit(callableT1);

        assertThat(firstInvocation.get()).isNotNull();
        assertThat(secondInvocation.get()).isNotNull();
        assertThat(thirdInvocation.get()).isNotNull();

        // test successful cache hits afterwards (only for T2, since that has no latches)
        callableT2.call();
        callableT2.call();

        verify(tryGetAllDestinationsT1, times(1)).get();
        verify(tryGetAllDestinationsT2, times(1)).get();
        verify(tenantIsolationLock, times(2)).lock();
        verify(tenantIsolationLock, times(2)).unlock();

        assertThat(allDestinationsCache.estimatedSize()).isEqualTo(2);
        assertThat(allDestinationsCache.getIfPresent(t1Key)).isNotNull();
        assertThat(allDestinationsCache.getIfPresent(t2Key)).isNotNull();
    }
}
