/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.GenericCacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheFilter;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.testutil.MockUtil;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Executing the tests in the same thread to circumvent a race condition when creating caches in parallel.
 */
@Execution( ExecutionMode.SAME_THREAD )
public class CacheFilterTest
{
    @SuppressWarnings( "deprecation" )
    @Nonnull
    private static final MockUtil mockUtil = new MockUtil();

    private static List<CacheFilter> invokedFilters;

    private static TestCallable callable;

    private static final ResilienceConfiguration RESILIENCE_CONFIGURATION =
        ResilienceConfiguration
            .empty(UUID.randomUUID().toString())
            .cacheConfiguration(ResilienceConfiguration.CacheConfiguration.of(Duration.ofDays(1)).withParameters(1));

    @BeforeEach
    public void beforeEach()
    {
        ResilienceDecorator.clearAllCacheEntries(RESILIENCE_CONFIGURATION);
        invokedFilters = new ArrayList<>();
        callable = new TestCallable();
    }

    @Test
    public void testCacheFilterAppliedToTheCache()
    {
        final Try<Integer> initialValue = tryGetInitialValue(RESILIENCE_CONFIGURATION, callable);

        final CacheFilter clearAllFilter = ( configuration, cacheKey, cacheEntry ) -> true;

        ResilienceDecorator.clearCache(RESILIENCE_CONFIGURATION, clearAllFilter);

        final int followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);

        assertThat(initialValue.get()).isEqualTo(1);
        assertThat(followUpValue).isEqualTo(2);
    }

    @Test
    public void testAllCacheFiltersInvokedUntilOneMatches()
    {
        tryGetInitialValue(RESILIENCE_CONFIGURATION, callable);

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
    public void testClearCacheMethodAppliesDefaultFiltersAsConjunction()
    {
        mockUtil.setOrMockCurrentTenant("tenant1");

        //cache miss, hence cache entry added with (tenant1, parameter 1) -> 1
        final Try<Integer> initialValue = tryGetInitialValue(RESILIENCE_CONFIGURATION, callable);

        mockUtil.setOrMockCurrentTenant("tenant2");

        //cache consists of (tenant1, parameter 1) -> 1
        //cache miss, entry added with (tenant2, parameter 2) -> 2
        //cache consists of (tenant1, parameter 1) -> 1 and (tenant2, parameter 1) -> 2
        Integer followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);

        assertThat(initialValue.get()).isEqualTo(1);
        assertThat(followUpValue).isEqualTo(2);

        //cache consists of (tenant1, parameter 1) -> 1 and (tenant2, parameter 1) -> 2
        //clearing cache with cache filter (tenant 2, parameter 1)
        //cache then consists of (tenant1, parameter 1) -> 1
        ResilienceDecorator.clearCache(RESILIENCE_CONFIGURATION);

        mockUtil.setOrMockCurrentTenant("tenant1");

        //cache consists of (tenant1, parameter 1) -> 1
        //cache hit, no entry added
        followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);
        assertThat(followUpValue).isEqualTo(1);

        mockUtil.setOrMockCurrentTenant("tenant2");

        //cache consists of (tenant1, parameter 1) -> 1
        //cache miss, entry with (tenant 2, parameter 1) -> 3 added
        followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);
        assertThat(followUpValue).isEqualTo(3);
    }

    @Test
    public void testCacheFilterAnd()
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
    public void testCacheFilterOr()
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
    public void testFilterCacheByTenantAndThenByParameter()
    {
        mockUtil.setOrMockCurrentTenant("tenant1");

        //cache miss, hence cache entry added with (tenant1, parameter 1) -> 1
        final Try<Integer> initialValue = tryGetInitialValue(RESILIENCE_CONFIGURATION, callable);

        mockUtil.setOrMockCurrentTenant("tenant2");

        //cache consists of (tenant1, parameter 1) -> 1
        //cache miss and (tenant 2, parameter 1) -> 2 added to the cache
        int followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);

        assertThat(initialValue.get()).isEqualTo(1);
        assertThat(followUpValue).isEqualTo(2);

        final CacheFilter tenantFilter =
            (( configuration, cacheKey, cacheEntry ) -> cacheKey.getTenantId().get().equals("tenant1"));

        ResilienceDecorator.clearCache(RESILIENCE_CONFIGURATION, tenantFilter);

        mockUtil.setOrMockCurrentTenant("tenant1");

        followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);
        assertThat(followUpValue).isEqualTo(3);

        mockUtil.setOrMockCurrentTenant("tenant2");

        followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);
        assertThat(followUpValue).isEqualTo(2);

        //cache consists of (tenant2, parameter 1) -> 2 and (tenant1, parameter 1) -> 3
        final CacheFilter parameterFilter =
            (( configuration, cacheKey, cacheEntry ) -> cacheKey.getComponents().contains(1));

        ResilienceDecorator.clearCache(RESILIENCE_CONFIGURATION, CacheFilter.or(tenantFilter, parameterFilter));

        mockUtil.setOrMockCurrentTenant("tenant1");

        followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);
        assertThat(followUpValue).isEqualTo(4);

        mockUtil.setOrMockCurrentTenant("tenant2");

        followUpValue = getFollowUpValue(RESILIENCE_CONFIGURATION, callable);
        assertThat(followUpValue).isEqualTo(5);
    }

    @Test
    public void testCacheFilterFactoryMethodsForTenant()
    {
        final String tenantId = "tenant";
        mockUtil.setOrMockCurrentTenant(tenantId);

        final GenericCacheKey<?, ?> cacheKey = mock(GenericCacheKey.class);
        when(cacheKey.getTenantId()).thenReturn(Option.of(tenantId));

        final CacheFilter currentTenantFilter = CacheFilter.keyMatchesTenant();
        assertThat(currentTenantFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isTrue();

        CacheFilter specificTenantFilter = CacheFilter.keyMatchesTenant(new DefaultTenant(tenantId,""));
        assertThat(specificTenantFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isTrue();

        specificTenantFilter = CacheFilter.keyMatchesTenant(new DefaultTenant("other-tenant", ""));
        assertThat(specificTenantFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isFalse();
    }

    @Test
    public void testCacheFilterFactoryMethodsForPrincipal()
    {
        final String principalId = "principal";
        mockUtil.setOrMockCurrentPrincipal(principalId);

        final GenericCacheKey<?, ?> cacheKey = mock(GenericCacheKey.class);
        when(cacheKey.getPrincipalId()).thenReturn(Option.of(principalId));

        final CacheFilter currentPrincipalFilter = CacheFilter.keyMatchesPrincipal();
        assertThat(currentPrincipalFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isTrue();

        CacheFilter specificPrincipalFilter = CacheFilter.keyMatchesPrincipal(new DefaultPrincipal(principalId));
        assertThat(specificPrincipalFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isTrue();

        specificPrincipalFilter = CacheFilter.keyMatchesPrincipal(new DefaultPrincipal("other-principal"));
        assertThat(specificPrincipalFilter.matches(RESILIENCE_CONFIGURATION, cacheKey, null)).isFalse();
    }

    @Test
    public void testCacheFilterFactoryMethodsForParameters()
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
    private static Try<Integer> tryGetInitialValue(
        @Nonnull final ResilienceConfiguration configuration,
        @Nonnull final Callable<Integer> callable )
    {
        final Cache<?, ?> cache = Caching.getCachingProvider().getCacheManager().getCache(configuration.identifier());
        if( cache != null ) {
            cache.clear();
        }

        return Try.of(() -> {
            return ResilienceDecorator.executeCallable(callable, configuration);
        });
    }

    @SneakyThrows
    private static int getFollowUpValue(
        @Nonnull final ResilienceConfiguration configuration,
        @Nonnull final Callable<Integer> callable )
    {
        return ResilienceDecorator.executeCallable(callable, configuration);
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
