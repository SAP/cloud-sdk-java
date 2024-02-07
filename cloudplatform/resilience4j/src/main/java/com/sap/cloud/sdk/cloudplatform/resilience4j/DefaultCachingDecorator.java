/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.spi.CachingProvider;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.GenericCacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.SerializableCacheKey;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheExpirationStrategy;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationKey;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;

import lombok.extern.slf4j.Slf4j;

/**
 * Default caching decorator.
 */
@Slf4j
public class DefaultCachingDecorator implements GenericDecorator
{
    private static final Map<CacheExpirationStrategy, Function<Duration, Factory<ExpiryPolicy>>> EXPIRY_STRATEGY_FACTORY_MAP =
        ImmutableMap
            .<CacheExpirationStrategy, Function<Duration, Factory<ExpiryPolicy>>> builder()
            .put(CacheExpirationStrategy.WHEN_LAST_ACCESSED, AccessedExpiryPolicy::factoryOf)
            .put(CacheExpirationStrategy.WHEN_LAST_TOUCHED, TouchedExpiryPolicy::factoryOf)
            .put(CacheExpirationStrategy.WHEN_CREATED, CreatedExpiryPolicy::factoryOf)
            .put(CacheExpirationStrategy.WHEN_LAST_MODIFIED, ModifiedExpiryPolicy::factoryOf)
            .build();

    // Cache used to enforce that the cache provider is only executed once to determine a value
    // Visibility set to package-private to allow for testing
    static final Cache<GenericCacheKey<?, ?>, Lock> lockCache =
        Caffeine.newBuilder().expireAfterAccess(java.time.Duration.ofMinutes(30)).build();

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        if( !configuration.cacheConfiguration().isEnabled() ) {
            return callable;
        }

        return decorateCallableWithCache(callable, configuration);
    }

    CachingProvider getCachingProvider()
    {
        return Caching.getCachingProvider();
    }

    /**
     * Identifies whether a given value is still valid and should stay part of the cache.
     * <p>
     * Once made protected, this can be overwritten to allow for more fine-grained control over the lifecycle of cache
     * entries. By default, this is just a {@code null} check.
     *
     * @param <T>
     *            The type of the cached value.
     * @param cachedValue
     *            The cached value to check for validity.
     * @return {@code true} if the cached value should still be part of the cache, {@code false} ese.
     */
    private <T> boolean isCachedValueValid( final T cachedValue )
    {
        return cachedValue != null;
    }

    @Nonnull
    @SuppressWarnings( "PMD.CloseResource" ) // closing JCache resource will disable caching feature
    private synchronized <T> Callable<T> decorateCallableWithCache(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        final CacheManager cacheManager = getCachingProvider().getCacheManager();

        final ResilienceConfiguration.CacheConfiguration cacheConfig = configuration.cacheConfiguration();
        final String cacheName = configuration.identifier();

        // The cache (re-) creation must be synchronized to avoid race conditions
        // To avoid complicated double-checked locking we synchronize the overall method
        final javax.cache.Cache<GenericCacheKey<?, ?>, T> cacheInstance = cacheManager.getCache(cacheName);
        final javax.cache.Cache<GenericCacheKey<?, ?>, T> cache;
        // create new Cache instance if none was found for the given name
        if( cacheInstance == null ) {
            cache = cacheManager.createCache(cacheName, createCacheConfiguration(cacheConfig));
        }
        // re-create the cache instance if the configuration has changed
        else {
            cache = recreateCacheOnNewConfiguration(cacheInstance, configuration, cacheManager);
        }

        // note that the "synchronised" declaration of this method does not apply to the execution of this lambda returned here
        // the lambda will not (and should not) be executed in a synchronised manner
        return () -> {
            // in case the cache was destroyed (not to confuse with invalidation) we just directly execute the given callable, without caching
            // this is done to stay behavior compatible with the formerly used resilience4j API that was used here
            if( cache.isClosed() ) {
                log.warn(String.format("""
                    Cache with configuration identifier '%s' was closed. Therefore methods decorated \
                    using that identifier will not be cached anymore, but instead will be executed \
                    directly.\
                    """, configuration.identifier()));
                return callable.call();
            }

            final GenericCacheKey<?, ?> lockCacheKey = determineLockCacheKey(configuration);
            final GenericCacheKey<?, ?> dataCacheKey = determineDataCacheKey(configuration);
            final Lock lock = lockCache.get(lockCacheKey, mapKey -> new ReentrantLock());
            try {
                lock.lock();
                final T value = cache.get(dataCacheKey);
                if( !isCachedValueValid(value) ) {
                    final T actualValue = callable.call();
                    cache.put(dataCacheKey, actualValue);
                    return actualValue;
                }
                return value;
            }
            catch( final Exception e ) {
                throw new ResilienceRuntimeException(e);
            }
            finally {
                lock.unlock();
            }
        };
    }

    private static GenericCacheKey<?, ?> determineLockCacheKey( final ResilienceConfiguration configuration )
    {
        return determineBaseCacheKey(configuration, true);
    }

    private static GenericCacheKey<?, ?> determineDataCacheKey( final ResilienceConfiguration configuration )
    {
        return determineBaseCacheKey(configuration, false);
    }

    private static
        GenericCacheKey<?, ?>
        determineBaseCacheKey( final ResilienceConfiguration configuration, final boolean appendConfigIdentifier )
    {
        final ResilienceConfiguration.CacheConfiguration cacheConfig = configuration.cacheConfiguration();
        final ResilienceIsolationKey isolation = ResilienceIsolationKey.of(configuration.isolationMode());
        final Tenant tenant = isolation.getTenant();
        final Principal principal = isolation.getPrincipal();

        // prepare actual cache key from the isolation key and the provided, additional parameters
        final GenericCacheKey<?, ?> key;
        if( cacheConfig.serializable() ) {

            // translate parameters from List of Object to List of Serializable
            final List<Serializable> parameters =
                StreamSupport
                    .stream(cacheConfig.parameters().spliterator(), false)
                    .map(Serializable.class::cast)
                    .collect(Collectors.toList());

            final SerializableCacheKey cacheKey = SerializableCacheKey.of(tenant, principal).append(parameters);
            if( appendConfigIdentifier ) {
                cacheKey.append(Collections.singleton(configuration.identifier()));
            }
            key = cacheKey;
        } else {
            final CacheKey cacheKey = CacheKey.of(tenant, principal).append(cacheConfig.parameters());
            if( appendConfigIdentifier ) {
                cacheKey.append(configuration.identifier());
            }
            key = cacheKey;
        }
        return key;
    }

    /**
     * Creates a cache configuration to describe the cache behavior.
     *
     * @param <T>
     *            The cache value type.
     * @param configuration
     *            The generic cache configuration.
     *
     * @return A new instance of MutableConfiguration.
     */
    @Nonnull
    protected <T> Configuration<GenericCacheKey<?, ?>, T> createCacheConfiguration(
        @Nonnull final ResilienceConfiguration.CacheConfiguration configuration )
    {
        final Factory<ExpiryPolicy> expiryPolicy = createCacheExpiryPolicyFactory(configuration);

        return new MutableConfiguration<GenericCacheKey<?, ?>, T>()
            .setStatisticsEnabled(false) // default: false
            .setManagementEnabled(false) // default: false
            .setStoreByValue(false) // default: true
            .setExpiryPolicyFactory(expiryPolicy);
    }

    @SuppressWarnings( "unchecked" )
    @Nonnull
    private synchronized <T> javax.cache.Cache<GenericCacheKey<?, ?>, T> recreateCacheOnNewConfiguration(
        @Nonnull final javax.cache.Cache<GenericCacheKey<?, ?>, T> cacheInstance,
        @Nonnull final ResilienceConfiguration resilienceConfiguration,
        @Nonnull final CacheManager cacheManager )
    {
        final Configuration<GenericCacheKey<?, ?>, T> externalCacheConfig =
            cacheInstance.getConfiguration(Configuration.class);

        if( hasExternalCacheConfigurationChanged(resilienceConfiguration, externalCacheConfig) ) {
            log
                .info(
                    "ResilienceConfiguration: {}: Destroying cache since a new cache configuration was detected.",
                    resilienceConfiguration.identifier());
            cacheManager.destroyCache(resilienceConfiguration.identifier());

            return cacheManager
                .createCache(
                    resilienceConfiguration.identifier(),
                    createCacheConfiguration(resilienceConfiguration.cacheConfiguration()));
        }
        return cacheInstance;
    }

    private boolean hasExternalCacheConfigurationChanged(
        @Nonnull final ResilienceConfiguration resilienceConfig,
        @Nullable final Configuration<?, ?> externalCacheConfig )
    {
        if( !(externalCacheConfig instanceof CompleteConfiguration) ) {
            return false;
        }

        final Factory<?> externalPolicyFactory =
            ((CompleteConfiguration<?, ?>) externalCacheConfig).getExpiryPolicyFactory();
        if( externalPolicyFactory == null ) {
            return false;
        }

        final Object externalRawPolicy = externalPolicyFactory.create();
        if( !(externalRawPolicy instanceof ExpiryPolicy) ) {
            return false;
        }

        final ResilienceConfiguration.CacheConfiguration cacheConfiguration = resilienceConfig.cacheConfiguration();
        final ExpiryPolicy expectedPolicy = createCacheExpiryPolicyFactory(cacheConfiguration).create();
        final ExpiryPolicy externalPolicy = (ExpiryPolicy) externalRawPolicy;

        return !Objects.equals(expectedPolicy.getExpiryForUpdate(), externalPolicy.getExpiryForUpdate())
            || !Objects.equals(expectedPolicy.getExpiryForAccess(), externalPolicy.getExpiryForAccess())
            || !Objects.equals(expectedPolicy.getExpiryForCreation(), externalPolicy.getExpiryForCreation());
    }

    @Nonnull
    private Factory<ExpiryPolicy> createCacheExpiryPolicyFactory(
        @Nonnull final ResilienceConfiguration.CacheConfiguration cacheConfig )
    {
        final Duration duration = new Duration(TimeUnit.MILLISECONDS, cacheConfig.expirationDuration().toMillis());
        final CacheExpirationStrategy expiryStrategyType = cacheConfig.expirationStrategy();
        return getExpiryPolicyFactory(expiryStrategyType, duration);
    }

    @Nonnull
    private Factory<ExpiryPolicy> getExpiryPolicyFactory(
        @Nonnull final CacheExpirationStrategy expiryStrategy,
        @Nonnull final Duration cacheDuration )
    {
        final Function<Duration, Factory<ExpiryPolicy>> factoryMethod = EXPIRY_STRATEGY_FACTORY_MAP.get(expiryStrategy);
        if( factoryMethod == null ) {
            throw new ShouldNotHappenException(
                "Provided cache expiry strategy is not supported: Missing mapping between the key and JCache expiry policy.");
        }
        return factoryMethod.apply(cacheDuration);
    }
}
