/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.cache.Cache;
import javax.cache.Caching;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.GenericCacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheFilter;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Executing the tests in the same thread to circumvent a race condition when creating caches in parallel.
 */
@Isolated
@Execution( ExecutionMode.SAME_THREAD )
class CacheFilterTest
{
    private static final Tenant TENANT_1 = new DefaultTenant("tenant_1");
    private static final Tenant TENANT_2 = new DefaultTenant("tenant_2");
    private static List<CacheFilter> invokedFilters;

    private static TestCallable callable;

    private static final ResilienceConfiguration RESILIENCE_CONFIGURATION =
        ResilienceConfiguration
            .empty(UUID.randomUUID().toString())
            .cacheConfiguration(ResilienceConfiguration.CacheConfiguration.of(Duration.ofDays(1)).withParameters(1));

    @BeforeEach
    void beforeEach()
    {
        ResilienceDecorator.clearAllCacheEntries(RESILIENCE_CONFIGURATION);
        invokedFilters = new ArrayList<>();
        callable = new TestCallable();
    }

    @AfterEach
    void afterEach()
    {
        TenantAccessor.setTenantFacade(null);
        PrincipalAccessor.setPrincipalFacade(null);
    }

    @Test
    void testCacheFilterAppliedToTheCache()
    {
        final Try<Integer> initialValue = tryGetInitialValue(callable);

        final CacheFilter clearAllFilter = ( configuration, cacheKey, cacheEntry ) -> true;

        ResilienceDecorator.clearCache(RESILIENCE_CONFIGURATION, clearAllFilter);

        final int followUpValue = getFollowUpValue(callable);

        assertThat(initialValue.get()).isEqualTo(1);
        assertThat(followUpValue).isEqualTo(2);
    }

    @Test
    void testAllCacheFiltersInvokedUntilOneMatches()
    {
        tryGetInitialValue(callable);

        final NeverMatchingCacheFilter firstFilter = new NeverMatchingCacheFilter();
        final NeverMatchingCacheFilter secondFilter = new NeverMatchingCacheFilter();
        final AlwaysMatchingCacheFilter thirdFilter = new AlwaysMatchingCacheFilter();
        final AlwaysMatchingCacheFilter fourthFilter = new AlwaysMatchingCacheFilter();

        ResilienceDecorator
            .clearCache(RESILIENCE_CONFIGURATION, CacheFilter.or(firstFilter, secondFilter, thirdFilter, fourthFilter));

        assertThat(firstFilter.isInvoked()).isTrue();
        assertThat(secondFilter.isInvoked()).isTrue();
        assertThat(thirdFilter.isInvoked()).isTrue();
        assertThat(fourthFilter.isInvoked()).isFalse();

        assertThat(invokedFilters).containsExactly(firstFilter, secondFilter, thirdFilter);
    }

    @Test
    void testClearCacheMethodAppliesDefaultFiltersAsConjunction()
    {
        //cache miss, hence cache entry added with (tenant1, parameter 1) -> 1
        final Try<Integer> initialValue =
            TenantAccessor.executeWithTenant(TENANT_1, () -> tryGetInitialValue(callable));

        //cache consists of (tenant1, parameter 1) -> 1
        //cache miss, entry added with (tenant2, parameter 2) -> 2
        //cache consists of (tenant1, parameter 1) -> 1 and (tenant2, parameter 1) -> 2
        Integer followUpValue = TenantAccessor.executeWithTenant(TENANT_2, () -> getFollowUpValue(callable));

        assertThat(initialValue).contains(1);
        assertThat(followUpValue).isEqualTo(2);

        //cache consists of (tenant1, parameter 1) -> 1 and (tenant2, parameter 1) -> 2
        //clearing cache with cache filter (tenant 2, parameter 1)
        //cache then consists of (tenant1, parameter 1) -> 1
        TenantAccessor.executeWithTenant(TENANT_2, () -> ResilienceDecorator.clearCache(RESILIENCE_CONFIGURATION));

        //cache consists of (tenant1, parameter 1) -> 1
        //cache hit, no entry added
        followUpValue = TenantAccessor.executeWithTenant(TENANT_1, () -> getFollowUpValue(callable));

        assertThat(followUpValue).isEqualTo(1);

        //cache consists of (tenant1, parameter 1) -> 1
        //cache miss, entry with (tenant 2, parameter 1) -> 3 added
        followUpValue = TenantAccessor.executeWithTenant(TENANT_2, () -> getFollowUpValue(callable));
        assertThat(followUpValue).isEqualTo(3);
    }

    @Test
    void testCacheFilterAnd()
    {
        final CacheFilter trueFilter = ( configuration, cacheKey, cacheEntry ) -> true;
        final CacheFilter falseFilter = ( configuration, cacheKey, cacheEntry ) -> false;

        final CacheFilter trueAndFalse = CacheFilter.and(trueFilter, falseFilter);
        final CacheFilter trueAndTrue = CacheFilter.and(trueFilter, trueFilter);
        final CacheFilter onlyTrue = CacheFilter.and(trueFilter);
        final CacheFilter onlyFalse = CacheFilter.and(falseFilter);
        final CacheFilter falseAndFalse = CacheFilter.and(falseFilter, falseFilter);
        final CacheFilter trueAndFalseAndTrue = CacheFilter.and(trueFilter, falseFilter, trueFilter);

        assertThat(cacheFilterMatches(trueAndFalse)).isFalse();
        assertThat(cacheFilterMatches(trueAndTrue)).isTrue();
        assertThat(cacheFilterMatches(onlyTrue)).isTrue();
        assertThat(cacheFilterMatches(onlyFalse)).isFalse();
        assertThat(cacheFilterMatches(falseAndFalse)).isFalse();
        assertThat(cacheFilterMatches(trueAndFalseAndTrue)).isFalse();
    }

    @Test
    void testCacheFilterOr()
    {
        final CacheFilter trueFilter = ( configuration, cacheKey, cacheEntry ) -> true;
        final CacheFilter falseFilter = ( configuration, cacheKey, cacheEntry ) -> false;

        final CacheFilter trueOrFalse = CacheFilter.or(trueFilter, falseFilter);
        final CacheFilter trueOrTrue = CacheFilter.or(trueFilter, trueFilter);
        final CacheFilter onlyTrue = CacheFilter.or(trueFilter);
        final CacheFilter onlyFalse = CacheFilter.or(falseFilter);
        final CacheFilter falseOrFalse = CacheFilter.or(falseFilter, falseFilter);
        final CacheFilter trueOrFalseOrTrue = CacheFilter.or(trueFilter, falseFilter, trueFilter);

        assertThat(cacheFilterMatches(trueOrFalse)).isTrue();
        assertThat(cacheFilterMatches(trueOrTrue)).isTrue();
        assertThat(cacheFilterMatches(onlyTrue)).isTrue();
        assertThat(cacheFilterMatches(onlyFalse)).isFalse();
        assertThat(cacheFilterMatches(falseOrFalse)).isFalse();
        assertThat(cacheFilterMatches(trueOrFalseOrTrue)).isTrue();
    }

    @Test
    void testFilterCacheByTenantAndThenByParameter()
    {
        //cache miss, hence cache entry added with (tenant1, parameter 1) -> 1
        final Try<Integer> initialValue =
            TenantAccessor.executeWithTenant(TENANT_1, () -> tryGetInitialValue(callable));

        //cache consists of (tenant1, parameter 1) -> 1
        //cache miss and (tenant 2, parameter 1) -> 2 added to the cache
        Integer followUpValue = TenantAccessor.executeWithTenant(TENANT_2, () -> getFollowUpValue(callable));

        assertThat(initialValue).contains(1);
        assertThat(followUpValue).isEqualTo(2);

        final CacheFilter tenantFilter =
            (( configuration, cacheKey, cacheEntry ) -> cacheKey.getTenantId().contains(TENANT_1.getTenantId()));

        TenantAccessor
            .executeWithTenant(TENANT_2, () -> ResilienceDecorator.clearCache(RESILIENCE_CONFIGURATION, tenantFilter));

        followUpValue = TenantAccessor.executeWithTenant(TENANT_1, () -> getFollowUpValue(callable));
        assertThat(followUpValue).isEqualTo(3);

        followUpValue = TenantAccessor.executeWithTenant(TENANT_2, () -> getFollowUpValue(callable));
        assertThat(followUpValue).isEqualTo(2);

        //cache consists of (tenant2, parameter 1) -> 2 and (tenant1, parameter 1) -> 3
        final CacheFilter parameterFilter =
            (( configuration, cacheKey, cacheEntry ) -> cacheKey.getComponents().contains(1));

        ResilienceDecorator.clearCache(RESILIENCE_CONFIGURATION, CacheFilter.or(tenantFilter, parameterFilter));

        followUpValue = TenantAccessor.executeWithTenant(TENANT_1, () -> getFollowUpValue(callable));
        assertThat(followUpValue).isEqualTo(4);

        followUpValue = TenantAccessor.executeWithTenant(TENANT_2, () -> getFollowUpValue(callable));
        assertThat(followUpValue).isEqualTo(5);
    }

    @Test
    void testCacheFilterFactoryMethodsForTenant()
    {
        final GenericCacheKey<?, ?> cacheKey = mock(GenericCacheKey.class);
        when(cacheKey.getTenantId()).thenReturn(Option.of(TENANT_1.getTenantId()));

        TenantAccessor.executeWithTenant(TENANT_1, () -> {
            assertThat(CacheFilter.keyMatchesTenant().matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isTrue();
        });

        CacheFilter specificTenantFilter = CacheFilter.keyMatchesTenant(TENANT_1);
        assertThat(specificTenantFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isTrue();

        specificTenantFilter = CacheFilter.keyMatchesTenant(TENANT_2);
        assertThat(specificTenantFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isFalse();
    }

    @Test
    void testCacheFilterFactoryMethodsForPrincipal()
    {
        final Principal principal = new DefaultPrincipal("principal");

        final GenericCacheKey<?, ?> cacheKey = mock(GenericCacheKey.class);
        when(cacheKey.getPrincipalId()).thenReturn(Option.of(principal.getPrincipalId()));

        PrincipalAccessor.executeWithPrincipal(principal, () -> {
            assertThat(CacheFilter.keyMatchesPrincipal().matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isTrue();
        });

        CacheFilter specificPrincipalFilter = CacheFilter.keyMatchesPrincipal(principal);
        assertThat(specificPrincipalFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isTrue();

        specificPrincipalFilter = CacheFilter.keyMatchesPrincipal(new DefaultPrincipal("other-principal"));
        assertThat(specificPrincipalFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isFalse();
    }

    @Test
    void testCacheFilterFactoryMethodsForParameters()
    {
        final CacheFilter currentParametersFilter = CacheFilter.keyMatchesParameters();

        final CacheKey cacheKeyParameter1 = CacheKey.ofNoIsolation().append(1);
        final CacheKey cacheKeyParameter2 = CacheKey.ofNoIsolation().append(2);

        assertThat(currentParametersFilter.matches(RESILIENCE_CONFIGURATION, cacheKeyParameter1, null)).isTrue();
        assertThat(currentParametersFilter.matches(RESILIENCE_CONFIGURATION, cacheKeyParameter2, null)).isFalse();

        final CacheFilter specificParametersFilter = CacheFilter.keyMatchesParameters(Lists.newArrayList(1));

        assertThat(specificParametersFilter.matches(RESILIENCE_CONFIGURATION, cacheKeyParameter1, null)).isTrue();
        assertThat(specificParametersFilter.matches(RESILIENCE_CONFIGURATION, cacheKeyParameter2, null)).isFalse();
    }

    private boolean cacheFilterMatches( final CacheFilter cacheFilter )
    {
        return cacheFilter.matches(RESILIENCE_CONFIGURATION, mock(GenericCacheKey.class), null);
    }

    @Nonnull
    @SneakyThrows
    private static Try<Integer> tryGetInitialValue( @Nonnull final Callable<Integer> callable )
    {
        final Cache<?, ?> cache =
            Caching
                .getCachingProvider()
                .getCacheManager()
                .getCache(CacheFilterTest.RESILIENCE_CONFIGURATION.identifier());
        if( cache != null ) {
            cache.clear();
        }

        return Try.of(() -> ResilienceDecorator.executeCallable(callable, CacheFilterTest.RESILIENCE_CONFIGURATION));
    }

    @SneakyThrows
    private static Integer getFollowUpValue( @Nonnull final Callable<Integer> callable )
    {
        return ResilienceDecorator.executeCallable(callable, CacheFilterTest.RESILIENCE_CONFIGURATION);
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

    @RequiredArgsConstructor
    private static class AlwaysMatchingCacheFilter implements CacheFilter
    {
        @Getter( AccessLevel.PACKAGE )
        private boolean invoked;

        @Override
        public boolean matches(
            @Nonnull final ResilienceConfiguration configuration,
            @Nonnull final GenericCacheKey<?, ?> cacheKey,
            @Nullable final Object cacheEntry )
        {
            invokedFilters.add(this);
            invoked = true;
            return true;
        }
    }

    @RequiredArgsConstructor
    private static class NeverMatchingCacheFilter implements CacheFilter
    {
        @Getter( AccessLevel.PACKAGE )
        private boolean invoked;

        @Override
        public boolean matches(
            @Nonnull final ResilienceConfiguration configuration,
            @Nonnull final GenericCacheKey<?, ?> cacheKey,
            @Nullable final Object cacheEntry )
        {
            invokedFilters.add(this);
            invoked = true;
            return false;
        }
    }
}
