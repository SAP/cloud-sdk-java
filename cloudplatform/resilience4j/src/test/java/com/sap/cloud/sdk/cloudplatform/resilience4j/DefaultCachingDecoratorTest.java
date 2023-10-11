/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CacheConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.sap.cloud.sdk.cloudplatform.cache.GenericCacheKey;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheExpirationStrategy;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@RunWith( Parameterized.class )
public class DefaultCachingDecoratorTest
{
    @Parameterized.Parameters( name = "{index}: {0}" )
    public static Iterable<TestParameter> data()
    {
        return Arrays
            .asList(

                TestParameter
                    .builder()
                    // Given Cloud SDK cache configuration:
                    .cacheConfiguration(
                        CacheConfiguration
                            .of(Duration.ofMinutes(5))
                            .withExpirationStrategy(CacheExpirationStrategy.WHEN_LAST_MODIFIED)
                            .withoutParameters())
                    // Expected JCache expiry settings:
                    .expiryForAccess(null)
                    .expiryForCreation(javax.cache.expiry.Duration.FIVE_MINUTES)
                    .expiryForUpdate(javax.cache.expiry.Duration.FIVE_MINUTES)
                    .build(),

                TestParameter
                    .builder()
                    // Given Cloud SDK cache configuration:
                    .cacheConfiguration(
                        CacheConfiguration
                            .of(Duration.ofMinutes(5))
                            .withExpirationStrategy(CacheExpirationStrategy.WHEN_LAST_ACCESSED)
                            .withoutParameters())
                    // Expected JCache expiry settings:
                    .expiryForAccess(javax.cache.expiry.Duration.FIVE_MINUTES)
                    .expiryForCreation(javax.cache.expiry.Duration.FIVE_MINUTES)
                    .expiryForUpdate(null)
                    .build(),

                TestParameter
                    .builder()
                    // Given Cloud SDK cache configuration:
                    .cacheConfiguration(
                        CacheConfiguration
                            .of(Duration.ofMinutes(5))
                            .withExpirationStrategy(CacheExpirationStrategy.WHEN_LAST_TOUCHED)
                            .withoutParameters())
                    // Expected JCache expiry settings:
                    .expiryForAccess(javax.cache.expiry.Duration.FIVE_MINUTES)
                    .expiryForCreation(javax.cache.expiry.Duration.FIVE_MINUTES)
                    .expiryForUpdate(javax.cache.expiry.Duration.FIVE_MINUTES)
                    .build(),

                TestParameter
                    .builder()
                    // Given Cloud SDK cache configuration:
                    .cacheConfiguration(
                        CacheConfiguration
                            .of(Duration.ofMinutes(5))
                            .withExpirationStrategy(CacheExpirationStrategy.WHEN_CREATED)
                            .withoutParameters())
                    // Expected JCache expiry settings:
                    .expiryForAccess(null)
                    .expiryForCreation(javax.cache.expiry.Duration.FIVE_MINUTES)
                    .expiryForUpdate(null)
                    .build());
    }

    @Value
    @Builder
    public static class TestParameter
    {
        @Nonnull
        final CacheConfiguration cacheConfiguration;
        @Nullable
        final javax.cache.expiry.Duration expiryForAccess;
        @Nullable
        final javax.cache.expiry.Duration expiryForCreation;
        @Nullable
        final javax.cache.expiry.Duration expiryForUpdate;
    }

    final TestParameter param;

    @Test
    public <T> void testCachingWithExpiryStrategy()
    {
        final DefaultCachingDecorator decorator = new DefaultCachingDecorator();

        // check JCache configuration instance type
        final Configuration<GenericCacheKey<?, ?>, T> cacheConfiguration =
            decorator.createCacheConfiguration(param.getCacheConfiguration());
        assertThat(cacheConfiguration).isInstanceOf(CompleteConfiguration.class);

        // check JCache expiry policy factory
        final Factory<ExpiryPolicy> expiryPolicyFactory =
            ((CompleteConfiguration<GenericCacheKey<?, ?>, T>) cacheConfiguration).getExpiryPolicyFactory();
        assertThat(expiryPolicyFactory).isNotNull();

        // check JCache expiry policy settings
        final ExpiryPolicy expiryPolicy = expiryPolicyFactory.create();
        assertThat(expiryPolicy.getExpiryForAccess()).isEqualTo(param.getExpiryForAccess());
        assertThat(expiryPolicy.getExpiryForCreation()).isEqualTo(param.getExpiryForCreation());
        assertThat(expiryPolicy.getExpiryForUpdate()).isEqualTo(param.getExpiryForUpdate());
    }
}
