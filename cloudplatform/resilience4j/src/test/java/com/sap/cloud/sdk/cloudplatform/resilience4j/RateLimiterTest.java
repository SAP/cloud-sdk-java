/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.testutil.MockUtil;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;

@Isolated
class RateLimiterTest
{
    private final MockUtil mockUtil = new MockUtil();

    @BeforeEach
    void setup()
    {
        mockUtil.mockDefaults();
    }

    @AfterEach
    void afterEach()
    {
        TenantAccessor.setTenantFacade(null);
        PrincipalAccessor.setPrincipalFacade(null);
    }

    @Test
    @Disabled( "Test is unreliable on Jenkins. Use this to verify the timeout behavior locally." )
    void testRateLimiterWithRepeatingCalls()
    {
        final Duration timeoutDuration = Duration.ofSeconds(5);
        final Duration limitRefreshPeriod = Duration.ofSeconds(2);
        final int limit = 10;

        final ResilienceConfiguration.RateLimiterConfiguration rateLimiterConfig =
            ResilienceConfiguration.RateLimiterConfiguration.of(timeoutDuration, limitRefreshPeriod, limit);
        final ResilienceConfiguration resilienceConfiguration =
            ResilienceConfiguration.of("RateLimiterTest.static").rateLimiterConfiguration(rateLimiterConfig);
        final Supplier<Integer> supplier = ResilienceDecorator.decorateSupplier(() -> 42, resilienceConfiguration);

        // First call is always slow, don't check time to succeed
        supplier.get();
        // Make limit-1 calls to reach the maximum number of request allowed during one window
        for( int i = 0; i < limit - 1; i++ ) {
            assertThat(timeToSucceed(supplier)).isLessThan(Duration.ofMillis(100));
        }
        // An additional call over the limit amount has to wait the next window
        assertThat(timeToSucceed(supplier)).isCloseTo(limitRefreshPeriod, limitRefreshPeriod.dividedBy(2));

        // After waiting the next window, the following calls should be immediately accepted again
        assertThat(timeToSucceed(supplier)).isLessThan(Duration.ofMillis(100));
    }

    @Test
    void testProvokeTimeout()
    {
        final Duration timeoutDuration = Duration.ofDays(1);
        final Duration limitRefreshPeriod = Duration.ofDays(1);
        final int limit = 10;
        final ResilienceConfiguration.RateLimiterConfiguration rateLimiterConfig =
            ResilienceConfiguration.RateLimiterConfiguration.of(timeoutDuration, limitRefreshPeriod, limit);

        final ResilienceConfiguration.TimeLimiterConfiguration timeLimiter =
            ResilienceConfiguration.TimeLimiterConfiguration.of(Duration.ofSeconds(10));

        final ResilienceConfiguration resilienceConfiguration =
            ResilienceConfiguration
                .of("RateLimiterTest.provoke")
                .rateLimiterConfiguration(rateLimiterConfig)
                .timeLimiterConfiguration(timeLimiter);

        final Supplier<Integer> supplier = ResilienceDecorator.decorateSupplier(() -> 42, resilienceConfiguration);

        // Make limit calls to reach the maximum number of request allowed during one window
        for( int i = 0; i < limit; i++ ) {
            supplier.get();
        }

        // Make another call to provoke RateLimiter
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(supplier);
        assertThat(future)
            .failsWithin(15, TimeUnit.SECONDS)
            .withThrowableOfType(TimeoutException.class)
            .withMessage(null);
    }

    @Test
    void testDisabledRateLimiter()
        throws Exception
    {
        // Test that the callable runs in the main thread
        final long id = Thread.currentThread().getId();

        final Callable<Integer> callable = () -> {
            assertThat(id).isEqualTo(Thread.currentThread().getId());
            return 42;
        };

        final ResilienceConfiguration confWithoutTimeout = ResilienceConfiguration.empty("RateLimiterTest.static");
        final Integer called = ResilienceDecorator.executeCallable(callable, confWithoutTimeout);

        assertThat(called).isEqualTo(42);
    }

    @Test
    void testRateLimiterWorksWithExecuteSupplier()
    {
        final Duration timeoutDuration = Duration.ofSeconds(5);
        final Duration limitRefreshDuration = Duration.ofMinutes(5);
        final int limit = 10;

        final ResilienceConfiguration.RateLimiterConfiguration rateLimiterConfig =
            ResilienceConfiguration.RateLimiterConfiguration.of(timeoutDuration, limitRefreshDuration, limit);
        final ResilienceConfiguration resilienceConfiguration =
            ResilienceConfiguration.empty("RateLimiterTest.static").rateLimiterConfiguration(rateLimiterConfig);
        final Supplier<Integer> supplier = () -> 42;
        final Supplier<Integer> resilientSupplier =
            () -> ResilienceDecorator.executeSupplier(supplier, resilienceConfiguration);

        // make limit calls to reach the allowed limit.
        for( int i = 0; i < limit; ++i ) {
            assertThatNoException().isThrownBy(resilientSupplier::get);
        }

        // make one more call, which exceeds the configured limit
        assertThatThrownBy(resilientSupplier::get)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasCauseExactlyInstanceOf(RequestNotPermitted.class);
    }

    @Test
    void testRateLimitIsSharedByConfiguration()
    {
        final Duration timeoutDuration = Duration.ofSeconds(5);
        final Duration limitRefreshDuration = Duration.ofMinutes(5);
        final int limit = 10;

        final ResilienceConfiguration.RateLimiterConfiguration rateLimiterConfig =
            ResilienceConfiguration.RateLimiterConfiguration.of(timeoutDuration, limitRefreshDuration, limit);
        final ResilienceConfiguration resilienceConfiguration1 =
            ResilienceConfiguration.empty("RateLimiterTest.static1").rateLimiterConfiguration(rateLimiterConfig);
        final ResilienceConfiguration resilienceConfiguration2 =
            ResilienceConfiguration.empty("RateLimiterTest.static2").rateLimiterConfiguration(rateLimiterConfig);

        final Supplier<Integer> supplier1 = () -> 42;
        final Supplier<Integer> supplier2 = () -> 1337;
        final Supplier<Integer> resilientSupplier1 =
            ResilienceDecorator.decorateSupplier(supplier1, resilienceConfiguration1);
        final Supplier<Integer> resilientSupplier2 =
            ResilienceDecorator.decorateSupplier(supplier2, resilienceConfiguration1);
        final Supplier<Integer> resilientSupplier3 =
            ResilienceDecorator.decorateSupplier(supplier2, resilienceConfiguration2);

        // make limit calls to reach the allowed limit.
        for( int i = 0; i < limit; ++i ) {
            // execute the two supplier alternatingly
            if( i % 2 == 0 ) {
                assertThatNoException().isThrownBy(resilientSupplier1::get);
            } else {
                assertThatNoException().isThrownBy(resilientSupplier2::get);
            }
        }

        // neither of the two suppliers that share a configuration can be executed further
        // because they share the same rate limit
        assertThatThrownBy(resilientSupplier1::get)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasCauseExactlyInstanceOf(RequestNotPermitted.class);
        assertThatThrownBy(resilientSupplier2::get)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasCauseExactlyInstanceOf(RequestNotPermitted.class);

        // sanity check:
        // the third supplier (although it is actually the same function) is not affected by the rate limit
        assertThatNoException().isThrownBy(resilientSupplier3::get);
    }

    @Test
    void testRateLimiterIsTenantIsolated()
    {
        final Duration timeoutDuration = Duration.ofSeconds(5);
        final Duration limitRefreshDuration = Duration.ofMinutes(5);
        final int limit = 10;

        final ResilienceConfiguration.RateLimiterConfiguration rateLimiterConfig =
            ResilienceConfiguration.RateLimiterConfiguration.of(timeoutDuration, limitRefreshDuration, limit);
        final ResilienceConfiguration resilienceConfiguration =
            ResilienceConfiguration
                .empty("RateLimiterTest.static")
                .isolationMode(ResilienceIsolationMode.TENANT_REQUIRED)
                .rateLimiterConfiguration(rateLimiterConfig);

        {
            // sanity check: Not tenant leads to exception during decoration
            mockUtil.clearTenants();

            assertThatThrownBy(() -> ResilienceDecorator.decorateSupplier(() -> 42, resilienceConfiguration))
                .isExactlyInstanceOf(ResilienceRuntimeException.class)
                .hasCauseExactlyInstanceOf(TenantAccessException.class);
        }

        final long start = System.nanoTime();

        {
            // tenant A
            mockUtil.clearTenants();
            mockUtil.mockCurrentTenant("tenantA");

            final Supplier<Integer> supplier =
                () -> ResilienceDecorator.executeSupplier(() -> 42, resilienceConfiguration);

            for( int i = 0; i < limit; ++i ) {
                assertThatNoException().isThrownBy(supplier::get);
            }

            assertThatThrownBy(supplier::get)
                .isExactlyInstanceOf(ResilienceRuntimeException.class)
                .hasCauseExactlyInstanceOf(RequestNotPermitted.class);
        }

        {
            // tenant B
            mockUtil.clearTenants();
            mockUtil.mockCurrentTenant("tenantB");

            // use the same resilience configuration
            final Supplier<Integer> supplier =
                () -> ResilienceDecorator.executeSupplier(() -> 42, resilienceConfiguration);

            // making new calls is still fine because the Rate Limiter is tenant isolated
            for( int i = 0; i < limit; ++i ) {
                assertThatNoException().isThrownBy(supplier::get);
            }

            assertThatThrownBy(supplier::get)
                .isExactlyInstanceOf(ResilienceRuntimeException.class)
                .hasCauseExactlyInstanceOf(RequestNotPermitted.class);
        }

        final long stop = System.nanoTime();

        // all calls succeeded within the first refresh window
        assertThat(Duration.ofNanos(stop - start)).isCloseTo(Duration.ZERO, limitRefreshDuration);
    }

    @Nonnull
    private Duration timeToSucceed( @Nonnull final Supplier<Integer> supplier )
    {
        final long start = System.nanoTime();
        supplier.get();
        final long stop = System.nanoTime();
        return Duration.ofNanos(stop - start);
    }
}
