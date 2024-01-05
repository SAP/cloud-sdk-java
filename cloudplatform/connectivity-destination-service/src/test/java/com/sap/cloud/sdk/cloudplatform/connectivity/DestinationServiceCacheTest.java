/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheExpirationStrategy;

import io.vavr.control.Option;

class DestinationServiceCacheTest
{
    @AfterEach
    void resetCache()
    {
        DestinationService.Cache.reset();
    }

    @Test
    void testDisable()
    {
        assertThat(DestinationService.Cache.isEnabled()).isTrue();

        // sut
        DestinationService.Cache.disable();
        assertThat(DestinationService.Cache.isEnabled()).isFalse();
        assertThatCode(DestinationService.Cache::disableChangeDetection).isInstanceOf(IllegalStateException.class);
        assertThatCode(DestinationService.Cache::instanceSingle).isInstanceOf(IllegalStateException.class);
        assertThatCode(DestinationService.Cache::instanceAll).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testReset()
    {
        final Cache<CacheKey, ?> cacheSingle = DestinationService.Cache.instanceSingle();
        final Cache<CacheKey, ?> cacheAll = DestinationService.Cache.instanceAll();

        // sut
        DestinationService.Cache.reset();
        assertThat(cacheSingle).isNotSameAs(DestinationService.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(DestinationService.Cache.instanceAll());
    }

    @Test
    void testChangeDetection()
    {
        final Cache<CacheKey, ?> cacheSingle = DestinationService.Cache.instanceSingle();
        final Cache<CacheKey, ?> cacheAll = DestinationService.Cache.instanceAll();
        assertThat(DestinationService.Cache.isChangeDetectionEnabled()).isTrue();

        // sut
        DestinationService.Cache.disableChangeDetection();
        assertThat(DestinationService.Cache.isChangeDetectionEnabled()).isFalse();
        assertThat(cacheSingle).isNotSameAs(DestinationService.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(DestinationService.Cache.instanceAll());
    }

    @Test
    void testExpiration()
    {
        final Option<Duration> oldDuration = DestinationService.Cache.getExpirationDuration();
        final Duration newDuration = Duration.ofSeconds(1);
        Cache<CacheKey, ?> cacheSingle = DestinationService.Cache.instanceSingle();
        Cache<CacheKey, ?> cacheAll = DestinationService.Cache.instanceAll();

        // sut
        DestinationService.Cache.setExpiration(newDuration, CacheExpirationStrategy.WHEN_CREATED);
        assertThat(DestinationService.Cache.getExpirationDuration()).containsExactly(newDuration);
        assertThat(cacheSingle).isNotSameAs(cacheSingle = DestinationService.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(cacheAll = DestinationService.Cache.instanceAll());

        // sut
        DestinationService.Cache.disableExpiration();
        assertThat(DestinationService.Cache.getExpirationDuration()).isEmpty();
        assertThat(cacheSingle).isNotSameAs(DestinationService.Cache.instanceSingle());
        assertThat(cacheAll).isNotSameAs(DestinationService.Cache.instanceAll());
    }

    @Test
    void testSize()
    {
        Cache<CacheKey, ?> cacheSingle = DestinationService.Cache.instanceSingle();
        final Cache<CacheKey, ?> cacheAll = DestinationService.Cache.instanceAll();

        // sut
        DestinationService.Cache.setSizeLimit(100);
        assertThat(cacheSingle).isNotSameAs(cacheSingle = DestinationService.Cache.instanceSingle());
        assertThat(cacheAll).isSameAs(DestinationService.Cache.instanceAll());

        // sut
        DestinationService.Cache.disableSizeLimit();
        assertThat(cacheSingle).isNotSameAs(DestinationService.Cache.instanceSingle());
        assertThat(cacheAll).isSameAs(DestinationService.Cache.instanceAll());
    }
}
