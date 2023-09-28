package com.sap.cloud.sdk.frameworks.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CacheConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.frameworks.resilience4j.Resilience4jCachingDefaultProviderTest.TestCallable;
import com.sap.cloud.sdk.testutil.MockUtil;

public class Resilience4jDecorationStrategyTest
{
    @SuppressWarnings( "deprecation" )
    @Nonnull
    private static final MockUtil mockUtil = new MockUtil();

    @BeforeClass
    public static void beforeClass()
    {
        ResilienceDecorator.setDecorationStrategy(new Resilience4jDecorationStrategy());
    }

    @BeforeEach
    public void beforeEach()
    {
        mockUtil.clearTenants();
        mockUtil.clearPrincipals();
    }

    @Test
    public void testConfiguration()
    {
        final String identifierClassName = Resilience4jDecorationStrategyTest.class.getName();

        final ResilienceConfiguration c1 = ResilienceConfiguration.of(Resilience4jDecorationStrategyTest.class);
        final ResilienceConfiguration c2 = ResilienceConfiguration.of(identifierClassName);
        assertThat(c1).isEqualTo(c2);
    }

    @Test
    public void testResilience4j()
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
    public void testCallableFallback()
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
    public void testCallableFallbackNegative()
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
    public void testSupplierFallback()
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
    public void testSupplierFallbackNegative()
    {
        final Supplier<String> callable = ResilienceDecorator.decorateSupplier(() -> {
            throw new ResilienceRuntimeException("Simulated exception.");
        },
            ResilienceConfiguration.of("testSupplierFallback").isolationMode(ResilienceIsolationMode.NO_ISOLATION),
            ( exception ) -> "Fallback function.");

        assertThat(callable.get()).isEqualTo("Fallback function.");
    }

    @Test
    public void testClearAllCacheEntries()
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

        mockTenantAndPrincipal(tenant_id_1, null);

        final Callable<Integer> callable = ResilienceDecorator.decorateCallable(testCallable, configuration);

        assertThat(callable.call()).isEqualTo(1);
        assertThat(callable.call()).isEqualTo(1);

        mockTenantAndPrincipal(tenant_id_2, null);
        assertThat(callable.call()).isEqualTo(2);
        assertThat(callable.call()).isEqualTo(2);

        ResilienceDecorator.clearAllCacheEntries(configuration);
        assertThat(callable.call()).isEqualTo(3);
        assertThat(callable.call()).isEqualTo(3);

        mockTenantAndPrincipal(tenant_id_1, null);
        assertThat(callable.call()).isEqualTo(4);
        assertThat(callable.call()).isEqualTo(4);
    }

    @Test
    public void testNamedThreadTimeLimiter()
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

    private static void mockTenantAndPrincipal( @Nullable String tenantId, @Nullable String principalId )
    {
        mockUtil.setOrMockCurrentTenant(tenantId);
        mockUtil.setOrMockCurrentPrincipal(principalId);
    }
}
