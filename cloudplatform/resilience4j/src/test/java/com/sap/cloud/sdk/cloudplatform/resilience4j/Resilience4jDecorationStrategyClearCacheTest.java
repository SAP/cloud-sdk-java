/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.cache.Cache;
import javax.cache.Caching;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Isolated( "Test setting facades. TestContext not advised with Dynamic tests." )
class Resilience4jDecorationStrategyClearCacheTest
{
    @RequiredArgsConstructor
    private enum TestCaseConfiguration
    {
        NO_ISOLATION(
            ResilienceIsolationMode.NO_ISOLATION,
            Resilience4jDecorationStrategyClearCacheTest::expectationInNoIsolationMode),
        TENANT_REQUIRED(
            ResilienceIsolationMode.TENANT_REQUIRED,
            Resilience4jDecorationStrategyClearCacheTest::expectationInTenantRequiredMode),
        TENANT_OPTIONAL(
            ResilienceIsolationMode.TENANT_OPTIONAL,
            Resilience4jDecorationStrategyClearCacheTest::expectationInTenantOptionalMode),
        PRINCIPAL_REQUIRED(
            ResilienceIsolationMode.PRINCIPAL_REQUIRED,
            Resilience4jDecorationStrategyClearCacheTest::expectationInPrincipalRequiredMode),
        PRINCIPAL_OPTIONAL(
            ResilienceIsolationMode.PRINCIPAL_OPTIONAL,
            Resilience4jDecorationStrategyClearCacheTest::expectationInPrincipalOptionalMode),
        TENANT_AND_PRINCIPAL_REQUIRED(
            ResilienceIsolationMode.TENANT_AND_USER_REQUIRED,
            Resilience4jDecorationStrategyClearCacheTest::expectationInTenantAndPrincipalRequiredMode),
        TENANT_AND_PRINCIPAL_OPTIONAL(
            ResilienceIsolationMode.TENANT_AND_USER_OPTIONAL,
            Resilience4jDecorationStrategyClearCacheTest::expectationInTenantAndPrincipalOptionalMode);

        private final ResilienceIsolationMode isolationMode;
        private final ExpectationSupplier expectationSupplier;
    }

    @TestFactory
    @Execution( ExecutionMode.SAME_THREAD ) // ensure tests are executed sequentially to avoid unfixed race condition when creating caches
    Collection<DynamicContainer> createClearCacheTests()
    {
        final Collection<TenantPrincipalCombination> allIdCombinations = new ArrayList<>(9);
        for( final String tenantId : Arrays.asList(null, "FirstTenant", "SecondTenant") ) {
            for( final String principalId : Arrays.asList(null, "FirstPrincipal", "SecondPrincipal") ) {
                allIdCombinations.add(new TenantPrincipalCombination(tenantId, principalId));
            }
        }

        final Collection<DynamicContainer> testCategories = new ArrayList<>();
        for( final TestCaseConfiguration configuration : TestCaseConfiguration.values() ) {
            final Collection<DynamicTest> tests = new ArrayList<>();

            for( final TenantPrincipalCombination clearer : allIdCombinations ) {
                for( final TenantPrincipalCombination accessor : allIdCombinations ) {
                    tests.add(createClearCacheTest(configuration, clearer, accessor));
                }
            }

            testCategories.add(DynamicContainer.dynamicContainer(configuration.isolationMode.toString(), tests));
        }

        return testCategories;
    }

    private static DynamicTest createClearCacheTest(
        @Nonnull final TestCaseConfiguration configuration,
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        final Expectation expectation = configuration.expectationSupplier.expectation(clearer, accessor);
        return DynamicTest
            .dynamicTest(
                String.format("Clearer = %s; Accessor = %s --> %s", clearer, accessor, expectation),
                () -> runCacheClearanceTest(configuration, expectation, clearer, accessor));
    }

    /**
     * Validates {@link ResilienceDecorator#clearCache(ResilienceConfiguration)} by inspecting the interaction between
     * two (potentially different) {@link TenantPrincipalCombination}:<br>
     * 1. The {@code clearer} that calls {@link ResilienceDecorator#clearCache(ResilienceConfiguration)} and<br>
     * 2. the {@code accessor} that uses a cached supplier method to access the cache.<br>
     * The general idea is to first get an initial value for the {@code accessor}, then let the {@code clearer} clear
     * the cache and, finally, get the follow up value for the {@code accessor}.<br>
     * Depending on the used {@link ResilienceIsolationMode} and the relationship between the {@code clearer} and the
     * {@code accessor}, we can make sure that the cache clearance respects tenant/principal isolation correctly.
     *
     * @param testCaseConfiguration
     *            The {@link TestCaseConfiguration} to use for this specific unit test instance.
     * @param expectation
     *            The {@link Expectation} that represents the expected outcome of the cache clearance.
     * @param clearer
     *            The {@link TenantPrincipalCombination} that calls
     *            {@link ResilienceDecorator#clearCache(ResilienceConfiguration)}.
     * @param accessor
     *            The {@link TenantPrincipalCombination} that accesses the cache for validation.
     */
    private static void runCacheClearanceTest(
        @Nonnull final TestCaseConfiguration testCaseConfiguration,
        @Nonnull final Expectation expectation,
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        final ResilienceConfiguration resilienceConfiguration =
            ResilienceConfiguration
                .empty(UUID.randomUUID().toString())
                .isolationMode(testCaseConfiguration.isolationMode)
                .cacheConfiguration(
                    ResilienceConfiguration.CacheConfiguration.of(Duration.ofDays(1)).withoutParameters());

        final Callable<Integer> callable = new TestCallable();

        final Try<Integer> maybeInitialValue = tryGetInitialValue(resilienceConfiguration, callable, accessor);

        if( maybeInitialValue.isFailure() ) {
            // getting an initial value failed due to an illegal combination in `accessor` (e.g. no tenant in TENANT_REQUIRED isolation mode)
            // thus, there were no keys added to the cache and, in consequence, clearing the cache did not do anything
            // therefore, we do not need further assertions
            return;
        }

        try {
            clearCache(resilienceConfiguration, clearer);
            assertThat(expectation).isNotEqualTo(Expectation.THROWS_EXCEPTION);
        }
        catch( final ResilienceRuntimeException e ) {
            assertThat(expectation).isEqualTo(Expectation.THROWS_EXCEPTION);
            return;
        }

        final int followUpValue = getFollowUpValue(resilienceConfiguration, callable, accessor);

        switch( expectation ) {
            case CACHE_CLEARED: {
                assertThat(followUpValue).isGreaterThan(maybeInitialValue.get());
                break;
            }
            case CACHE_NOT_CLEARED: {
                assertThat(followUpValue).isEqualTo(maybeInitialValue.get());
                break;
            }
            default: {
                fail(String.format("Unsupported %s \"%s\".", Expectation.class.getSimpleName(), expectation));
                break;
            }
        }
    }

    @Nonnull
    @SneakyThrows
    private static Try<Integer> tryGetInitialValue(
        @Nonnull final ResilienceConfiguration configuration,
        @Nonnull final Callable<Integer> callable,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        final Cache<?, ?> cache = Caching.getCachingProvider().getCacheManager().getCache(configuration.identifier());
        if( cache != null ) {
            cache.clear();
        }

        return Try.of(() -> {
            accessor.mock();
            return ResilienceDecorator.executeCallable(callable, configuration);
        });
    }

    private static void clearCache(
        @Nonnull final ResilienceConfiguration configuration,
        @Nonnull final TenantPrincipalCombination clearer )
    {
        clearer.mock();
        ResilienceDecorator.clearCache(configuration);
    }

    @SneakyThrows
    private static int getFollowUpValue(
        @Nonnull final ResilienceConfiguration configuration,
        @Nonnull final Callable<Integer> callable,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        accessor.mock();
        return ResilienceDecorator.executeCallable(callable, configuration);
    }

    private static Expectation expectationInNoIsolationMode(
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        return Expectation.CACHE_CLEARED;
    }

    private static Expectation expectationInTenantRequiredMode(
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        if( clearer.tenantId == null ) {
            return Expectation.THROWS_EXCEPTION;
        }

        return expectationInTenantOptionalMode(clearer, accessor);
    }

    private static Expectation expectationInTenantOptionalMode(
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        if( Objects.equals(clearer.tenantId, accessor.tenantId) ) {
            return Expectation.CACHE_CLEARED;
        }

        return Expectation.CACHE_NOT_CLEARED;
    }

    private static Expectation expectationInPrincipalRequiredMode(
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        if( clearer.principalId == null ) {
            return Expectation.THROWS_EXCEPTION;
        }

        return expectationInPrincipalOptionalMode(clearer, accessor);
    }

    private static Expectation expectationInPrincipalOptionalMode(
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        if( Objects.equals(clearer.principalId, accessor.principalId) ) {
            return Expectation.CACHE_CLEARED;
        }

        return Expectation.CACHE_NOT_CLEARED;
    }

    private static Expectation expectationInTenantAndPrincipalRequiredMode(
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        if( clearer.tenantId == null || clearer.principalId == null ) {
            return Expectation.THROWS_EXCEPTION;
        }

        return expectationInTenantAndPrincipalOptionalMode(clearer, accessor);
    }

    private static Expectation expectationInTenantAndPrincipalOptionalMode(
        @Nonnull final TenantPrincipalCombination clearer,
        @Nonnull final TenantPrincipalCombination accessor )
    {
        if( Objects.equals(clearer.tenantId, accessor.tenantId)
            && Objects.equals(clearer.principalId, accessor.principalId) ) {
            return Expectation.CACHE_CLEARED;
        }

        return Expectation.CACHE_NOT_CLEARED;
    }

    @Data
    private static class TenantPrincipalCombination
    {
        private final String tenantId;
        private final String principalId;

        // Workaround due to dynamic testing not able to use lifecycle methods, i.e. @afterEach of TestContext
        void mock()
        {
            TenantAccessor
                .setTenantFacade(() -> Try.of(() -> tenantId).filter(Objects::nonNull).map(DefaultTenant::new));

            PrincipalAccessor
                .setPrincipalFacade(
                    () -> Try.of(() -> principalId).filter(Objects::nonNull).map(DefaultPrincipal::new));
        }

        @Override
        public String toString()
        {
            return String.format("(%s, %s)", tenantId, principalId);
        }
    }

    private enum Expectation
    {
        THROWS_EXCEPTION,
        CACHE_CLEARED,
        CACHE_NOT_CLEARED,
    }

    @FunctionalInterface
    private interface ExpectationSupplier
    {
        Expectation expectation(
            @Nonnull final TenantPrincipalCombination clearer,
            @Nonnull final TenantPrincipalCombination accessor );
    }

    private static class TestCallable implements Callable<Integer>
    {
        private int counter = 0;

        @Override
        public Integer call()
            throws Exception
        {
            return ++counter;
        }
    }
}
