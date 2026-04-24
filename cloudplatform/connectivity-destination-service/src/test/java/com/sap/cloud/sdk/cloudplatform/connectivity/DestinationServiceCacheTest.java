package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.augmenter;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ONLY_SUBSCRIBER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.github.benmanes.caffeine.cache.Cache;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheExpirationStrategy;

import io.vavr.control.Option;
import lombok.val;

@Isolated( "Test interacts with global destination cache" )
class DestinationServiceCacheTest
{
    @AfterEach
    void resetCache()
    {
        DestinationService.Cache.reset();
    }

    @SuppressWarnings( "deprecation" )
    @Test
    void testDisable()
    {
        assertThat(DestinationService.Cache.isEnabled()).isTrue();

        // sut
        DestinationService.Cache.disable();
        assertThat(DestinationService.Cache.isEnabled()).isFalse();
        assertThatCode(DestinationService.Cache::enableChangeDetection).isInstanceOf(IllegalStateException.class);
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

    @SuppressWarnings( "deprecation" )
    @Test
    @DisplayName( "Identification of experimental DestinationOptionsAugmenter settings in options" )
    void testExperimentalSettingsIdentificationInOptions()
    {
        val experimentalAugmentersSet =
            new DestinationServiceOptionsAugmenter[][] {
                { augmenter().customHeaders(new Header("experimental-header", "value")) },
                { augmenter().crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.SUBACCOUNT) },
                { augmenter().crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.INSTANCE) },
                { augmenter().fragmentName("a-fragment") } };

        val legacyAugmentersSet =
            new DestinationServiceOptionsAugmenter[][] {
                {},
                { augmenter().refreshToken("a-token") },
                { augmenter().retrievalStrategy(ALWAYS_PROVIDER) },
                { augmenter().tokenExchangeStrategy(FORWARD_USER_TOKEN) },
                { augmenter().tokenExchangeStrategy(EXCHANGE_ONLY), augmenter().retrievalStrategy(CURRENT_TENANT) },
                { augmenter().tokenExchangeStrategy(LOOKUP_ONLY), augmenter().retrievalStrategy(ONLY_SUBSCRIBER) } };

        for( final DestinationServiceOptionsAugmenter[] legacyAugmenters : legacyAugmentersSet ) {
            val b = DestinationOptions.builder();
            Arrays.stream(legacyAugmenters).forEach(b::augmentBuilder);
            assertThat(DestinationService.Cache.isUsingExperimentalFeatures(b.build())).isFalse();
        }

        for( final DestinationServiceOptionsAugmenter[] experimentalAugmenters : experimentalAugmentersSet ) {
            val b = DestinationOptions.builder();
            Arrays.stream(experimentalAugmenters).forEach(b::augmentBuilder);
            assertThat(DestinationService.Cache.isUsingExperimentalFeatures(b.build())).isTrue();
        }
    }
}
