/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CacheConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import com.sap.cloud.sdk.cloudplatform.cache.SerializableCacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationKey;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;

public class Resilience4jCachingCustomProviderTest
{
    private static Cache<SerializableCacheKey, Object> cache;

    public static class CustomProvider extends NoopCacheProvider
    {
        private static final CacheManager cacheManager = mock(CacheManager.class);

        @Override
        public CacheManager getCacheManager()
        {
            return cacheManager;
        }
    }

    @BeforeClass
    public static void setupResilienceStrategy()
    {
        ResilienceDecorator.setDecorationStrategy(new Resilience4jDecorationStrategy());
    }

    @BeforeClass
    public static void setupSystemProperty()
    {
        System.setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER, CustomProvider.class.getName());
    }

    @AfterClass
    public static void cleanSystemProperty()
    {
        System.clearProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER);
    }

    @SuppressWarnings( "unchecked" )
    @BeforeClass
    public static void setupCacheManager()
    {
        cache = (Cache<SerializableCacheKey, Object>) mock(Cache.class);
        doReturn(cache).when(CustomProvider.cacheManager).getCache(ArgumentMatchers.eq("test.caching.provider.custom"));
    }

    @Test
    public void testCachingByCountingInvocations()
        throws Exception
    {
        final ResilienceIsolationKey key = ResilienceIsolationKey.of(ResilienceIsolationMode.NO_ISOLATION);
        final SerializableCacheKey cacheKey = SerializableCacheKey.of(key.getTenant(), key.getPrincipal());

        // First cache call misses [null], second time it hits ["1"]
        doReturn(null, 1).when(cache).get(cacheKey);

        // Monitoring object to count the number of cache misses
        final AtomicInteger cacheMisses = new AtomicInteger(0);

        ClassLoaderUtil.runWithSeparateClassLoader(() -> {
            final ResilienceConfiguration configuration =
                ResilienceConfiguration
                    .of("test.caching.provider.custom")
                    .isolationMode(ResilienceIsolationMode.NO_ISOLATION)
                    .cacheConfiguration(CacheConfiguration.of(Duration.ofHours(1)).withoutParameters());

            // Return a JCache configuration so the decorator doesn't detect changes to the given configuration and resets the cache
            final DefaultCachingDecorator cacheDecorator = new DefaultCachingDecorator();
            final Object jCacheConfiguration =
                cacheDecorator.createCacheConfiguration(configuration.cacheConfiguration());
            doReturn(jCacheConfiguration).when(cache).getConfiguration(any());

            final Supplier<Integer> cachedCall =
                ResilienceDecorator.decorateSupplier(cacheMisses::incrementAndGet, configuration);

            assertThat(cachedCall.get()).isEqualTo(1);
            assertThat(cacheMisses).hasValue(1);

            assertThat(cachedCall.get()).isEqualTo(1);
            assertThat(cacheMisses).hasValue(1);
        });

        // Verify cache was queried twice
        verify(cache, times(2)).get(cacheKey);

        // Verify cache was written once, with "1"
        verify(cache, times(1)).put(cacheKey, 1);
    }
}
