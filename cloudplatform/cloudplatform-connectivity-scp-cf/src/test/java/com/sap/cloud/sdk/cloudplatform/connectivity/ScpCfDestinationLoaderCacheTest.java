/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Duration;

import org.junit.After;
import org.junit.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheExpirationStrategy;

import io.vavr.control.Option;

public class ScpCfDestinationLoaderCacheTest
{
    @After
    public void resetCache()
    {
        ScpCfDestinationLoader.Cache.reset();
    }

    @Test
    public void testDisable()
    {
        assertThat(ScpCfDestinationLoader.Cache.isEnabled()).isTrue();

        // sut
        ScpCfDestinationLoader.Cache.disable();
        assertThat(ScpCfDestinationLoader.Cache.isEnabled()).isFalse();
        assertThatCode(ScpCfDestinationLoader.Cache::enableChangeDetection).isInstanceOf(IllegalStateException.class);
        assertThatCode(ScpCfDestinationLoader.Cache::instanceSingle).isInstanceOf(IllegalStateException.class);
        assertThatCode(ScpCfDestinationLoader.Cache::instanceAll).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testReset()
    {
        final Cache<CacheKey, ?> cacheSingle = ScpCfDestinationLoader.Cache.instanceSingle();
        final Cache<CacheKey, ?> cacheAll = ScpCfDestinationLoader.Cache.instanceAll();

        // sut
        ScpCfDestinationLoader.Cache.reset();
        assertThat(cacheSingle).isNotSameAs(ScpCfDestinationLoader.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(ScpCfDestinationLoader.Cache.instanceSingle());
    }

    @Test
    public void testChangeDetection()
    {
        final Cache<CacheKey, ?> cacheSingle = ScpCfDestinationLoader.Cache.instanceSingle();
        final Cache<CacheKey, ?> cacheAll = ScpCfDestinationLoader.Cache.instanceAll();
        assertThat(ScpCfDestinationLoader.Cache.isChangeDetectionEnabled()).isFalse();

        // sut
        ScpCfDestinationLoader.Cache.enableChangeDetection();
        assertThat(ScpCfDestinationLoader.Cache.isChangeDetectionEnabled()).isTrue();
        assertThat(cacheSingle).isNotSameAs(ScpCfDestinationLoader.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(ScpCfDestinationLoader.Cache.instanceSingle());
    }

    @Test
    public void testExpiration()
    {
        final Option<Duration> oldDuration = ScpCfDestinationLoader.Cache.getExpirationDuration();
        final Duration newDuration = Duration.ofSeconds(1);
        Cache<CacheKey, ?> cacheSingle = ScpCfDestinationLoader.Cache.instanceSingle();
        Cache<CacheKey, ?> cacheAll = ScpCfDestinationLoader.Cache.instanceAll();

        // sut
        ScpCfDestinationLoader.Cache.setExpiration(newDuration, CacheExpirationStrategy.WHEN_CREATED);
        assertThat(ScpCfDestinationLoader.Cache.getExpirationDuration()).isNotEqualTo(oldDuration);
        assertThat(ScpCfDestinationLoader.Cache.getExpirationDuration()).containsExactly(newDuration);
        assertThat(cacheSingle).isNotSameAs(cacheSingle = ScpCfDestinationLoader.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(cacheAll = ScpCfDestinationLoader.Cache.instanceSingle());

        // sut
        ScpCfDestinationLoader.Cache.disableExpiration();
        assertThat(ScpCfDestinationLoader.Cache.getExpirationDuration()).isEmpty();
        assertThat(cacheSingle).isNotSameAs(ScpCfDestinationLoader.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(ScpCfDestinationLoader.Cache.instanceSingle());
    }

    @Test
    public void testSize()
    {
        Cache<CacheKey, ?> cacheSingle = ScpCfDestinationLoader.Cache.instanceSingle();
        Cache<CacheKey, ?> cacheAll = ScpCfDestinationLoader.Cache.instanceAll();

        // sut
        ScpCfDestinationLoader.Cache.setSizeLimit(100);
        assertThat(cacheSingle).isNotSameAs(cacheSingle = ScpCfDestinationLoader.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(cacheAll = ScpCfDestinationLoader.Cache.instanceSingle());

        // sut
        ScpCfDestinationLoader.Cache.disableSizeLimit();
        assertThat(cacheSingle).isNotSameAs(ScpCfDestinationLoader.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(ScpCfDestinationLoader.Cache.instanceSingle());
    }
}
