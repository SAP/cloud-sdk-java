package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CacheConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.resilience4j.Resilience4jCachingDefaultProviderTest.TestCallable;
import com.sap.cloud.sdk.testutil.TestContext;

class Resilience4jDecorationStrategyTest
{
    @RegisterExtension
    static TestContext context = TestContext.withThreadContext();

    static {
        context.setTenant("tenant_1");
    }

    @Test
    void testConfiguration()
    {
        final String identifierClassName = Resilience4jDecorationStrategyTest.class.getName();

        final ResilienceConfiguration c1 = ResilienceConfiguration.of(Resilience4jDecorationStrategyTest.class);
        final ResilienceConfiguration c2 = ResilienceConfiguration.of(identifierClassName);
        assertThat(c1).isEqualTo(c2);
    }

    @Test
    void testResilience4j()
    {
        final Callable<String> callable = ResilienceDecorator.decorateCallable(() -> {
            Thread.sleep(10000);
            return "Should not happen.";
        },
            ResilienceConfiguration
                .of("testResilience4j")
                .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                .timeLimiterConfiguration(TimeLimiterConfiguration.of().timeoutDuration(Duration.ofSeconds(1))));

        assertThatThrownBy(callable::call)
            .isExactlyInstanceOf(ResilienceRuntimeException.class)
            .hasCauseExactlyInstanceOf(TimeoutException.class);
    }

    @Test
    void testCallableFallback()
        throws Exception
    {
        final Callable<String> callable =
            ResilienceDecorator
                .decorateCallable(
                    () -> "Should return me.",
                    ResilienceConfiguration
                        .of("testCallableFallback")
                        .isolationMode(ResilienceIsolationMode.NO_ISOLATION),
                    ( exception ) -> "Fallback function.");

        assertThat(callable.call()).isEqualTo("Should return me.");
    }

    @Test
    void testCallableFallbackNegative()
        throws Exception
    {
        final Callable<String> callable = ResilienceDecorator.decorateCallable(() -> {
            throw new ResilienceRuntimeException("Simulated exception.");
        },
            ResilienceConfiguration.of("testCallableFallback").isolationMode(ResilienceIsolationMode.NO_ISOLATION),
            ( exception ) -> "Fallback function.");

        assertThat(callable.call()).isEqualTo("Fallback function.");
    }

    @Test
    void testSupplierFallback()
    {
        final Supplier<String> callable =
            ResilienceDecorator
                .decorateSupplier(
                    () -> "Should return me.",
                    ResilienceConfiguration
                        .of("testSupplierFallback")
                        .isolationMode(ResilienceIsolationMode.NO_ISOLATION),
                    ( exception ) -> "Fallback function.");

        assertThat(callable.get()).isEqualTo("Should return me.");
    }

    @Test
    void testSupplierFallbackNegative()
    {
        final Supplier<String> callable = ResilienceDecorator.decorateSupplier(() -> {
            throw new ResilienceRuntimeException("Simulated exception.");
        },
            ResilienceConfiguration.of("testSupplierFallback").isolationMode(ResilienceIsolationMode.NO_ISOLATION),
            ( exception ) -> "Fallback function.");

        assertThat(callable.get()).isEqualTo("Fallback function.");
    }

    @Test
    void testClearAllCacheEntries()
        throws Exception
    {
        final String tenant_id_1 = "tenant_1";
        final String tenant_id_2 = "tenant_2";
        final TestCallable testCallable = new TestCallable();

        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("testCacheIsNotRecreated")
                .isolationMode(ResilienceIsolationMode.TENANT_REQUIRED)
                .cacheConfiguration(CacheConfiguration.of(Duration.ofMinutes(5)).withoutParameters());

        context.setTenant(tenant_id_1);

        final Callable<Integer> callable = ResilienceDecorator.decorateCallable(testCallable, configuration);

        assertThat(callable.call()).isEqualTo(1);
        assertThat(callable.call()).isEqualTo(1);

        context.setTenant(tenant_id_2);
        assertThat(callable.call()).isEqualTo(2);
        assertThat(callable.call()).isEqualTo(2);

        ResilienceDecorator.clearAllCacheEntries(configuration);
        assertThat(callable.call()).isEqualTo(3);
        assertThat(callable.call()).isEqualTo(3);

        context.setTenant(tenant_id_1);
        assertThat(callable.call()).isEqualTo(4);
        assertThat(callable.call()).isEqualTo(4);
    }

    @Test
    void testNamedThreadTimeLimiter()
        throws Exception
    {
        final Callable<String> threadNameCallable =
            ResilienceDecorator
                .decorateCallable(
                    () -> Thread.currentThread().getName(),
                    ResilienceConfiguration
                        .empty("identifier")
                        .timeLimiterConfiguration(
                            ResilienceConfiguration.TimeLimiterConfiguration.of(Duration.ofSeconds(1))),
                    null);
        final String threadName = threadNameCallable.call();
        assertThat(threadName.matches(String.valueOf(Pattern.compile("cloudsdk-resilience-\\d+"))));
    }
}
