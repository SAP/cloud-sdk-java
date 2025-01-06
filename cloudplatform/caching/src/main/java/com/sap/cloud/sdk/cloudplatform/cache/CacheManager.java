/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.ImmutableList;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Grants bulk processing for all (statically) registered caches.
 */
@Slf4j
public final class CacheManager
{
    private static final List<Cache<CacheKey, ?>> cacheList = new ArrayList<>();

    /**
     * Getter for a list of all caches registered in the {@link CacheManager}.
     *
     * @return The list of all caches.
     */
    @Nonnull
    public static ImmutableList<Cache<CacheKey, ?>> getCacheList()
    {
        return ImmutableList.copyOf(cacheList);
    }

    /**
     * Registers a cache at in the {@link CacheManager}.
     *
     * @param cache
     *            The cache to be registered.
     * @param <T>
     *            The type of the values in the cache.
     * @return The given cache.
     */
    @Nonnull
    public static synchronized <T> Cache<CacheKey, T> register( @Nonnull final Cache<CacheKey, T> cache )
    {
        cacheList.add(cache);
        return cache;
    }

    /**
     * Unregisters a cache from the {@link CacheManager}.
     * <p>
     * <strong>Note:</strong> The given {@code cache} <strong>will not</strong> be cleared / manipulated with this
     * method.
     *
     * @param cache
     *            The cache to be unregistered.
     * @param <T>
     *            The type of the values for the cache.
     * @return The given cache.
     * @since 4.3.0
     */
    @Nonnull
    public static synchronized <T> Cache<CacheKey, T> unregister( @Nonnull final Cache<CacheKey, T> cache )
    {
        cacheList.remove(cache);
        return cache;
    }

    /**
     * Cleans up all cache entries that have been invalidated.
     */
    public static void cleanUp()
    {
        for( final Cache<CacheKey, ?> cache : cacheList ) {
            cache.cleanUp();
        }

        log.info("Clean up of invalidated caches finished successfully.");
    }

    /**
     * Invalidates all entries in all caches.
     *
     * @return The number of invalidated cache entries.
     */
    public static long invalidateAll()
    {
        long size = 0;

        for( final Cache<CacheKey, ?> cache : cacheList ) {
            size += cache.estimatedSize();
            cache.invalidateAll();
        }

        log.info("Successfully invalidated roughly {} entries in {} caches.", size, cacheList.size());
        return size;
    }

    /**
     * Invalidates all caches of the current tenant.
     *
     * @return The number of invalidated cache entries.
     */
    public static long invalidateTenantCaches()
    {
        final Try<Tenant> tenantTry = TenantAccessor.tryGetCurrentTenant();

        if( tenantTry.isSuccess() ) {
            return invalidateTenantCaches(tenantTry.get().getTenantId());
        } else {
            log.debug("Cache could not be invalidated for tenant.", tenantTry.getCause());
            return 0;
        }
    }

    /**
     * Invalidates all caches of the given tenant.
     *
     * @param tenantId
     *            The tenant to invalidate all caches for.
     * @return The number of invalidated cache entries.
     */
    public static long invalidateTenantCaches( @Nullable final String tenantId )
    {
        long size = 0;

        for( final Cache<CacheKey, ?> cache : cacheList ) {
            final List<CacheKey> keysToInvalidate = new ArrayList<>();

            for( final CacheKey cacheKey : cache.asMap().keySet() ) {
                log.debug("Checking cache invalidation for tenant '{}': {}.", tenantId, cacheKey);

                if( Objects.equals(tenantId, cacheKey.getTenantId().getOrNull()) ) {
                    keysToInvalidate.add(cacheKey);
                }
            }

            log.debug("Invalidating caches of tenant '{}': {}.", tenantId, keysToInvalidate);

            size += keysToInvalidate.size();
            cache.invalidateAll(keysToInvalidate);
        }

        log.info("Successfully invalidated caches of tenant '{}': {}.", tenantId, size);
        return size;
    }

    /**
     * Invalidates all caches of the current principal.
     *
     * @return The number of invalidated cache entries.
     */
    public static long invalidatePrincipalCaches()
    {
        final Try<Tenant> tenantTry = TenantAccessor.tryGetCurrentTenant();
        final Try<Principal> principalTry = PrincipalAccessor.tryGetCurrentPrincipal();

        if( tenantTry.isSuccess() && principalTry.isSuccess() ) {
            return invalidatePrincipalCaches(tenantTry.get().getTenantId(), principalTry.get().getPrincipalId());
        } else {
            return 0;
        }
    }

    /**
     * Invalidates all caches of the given principal.
     *
     * @param tenantId
     *            The identifier of the tenant for which all principal caches should be invalidated.
     * @param principalId
     *            The identifier of the principal for which all caches should be invalidated.
     * @return The number of invalidated cache entries.
     */
    public static long invalidatePrincipalCaches( @Nullable final String tenantId, @Nullable final String principalId )
    {
        long size = 0;

        for( final Cache<CacheKey, ?> cache : cacheList ) {
            log.debug("Invalidating principal cache entries.");
            size += invalidatePrincipalEntries(tenantId, principalId, cache);
        }

        final String msg = "Successfully invalidated caches of principal {} within tenant {}: {}";
        log.info(msg, principalId, tenantId, size);
        return size;
    }

    /**
     * Invalidates all cache entries of the current tenant-specific principal.
     *
     * @param cache
     *            The cache in which all caches of the current principal should be invalidated.
     * @return The number of invalidated cache entries.
     */
    public static long invalidatePrincipalEntries( @Nonnull final Cache<CacheKey, ?> cache )
    {
        final Try<Tenant> tenantTry = TenantAccessor.tryGetCurrentTenant();
        tenantTry.onFailure(e -> log.debug("Cache could not be invalidated for tenant.", e));

        final Try<Principal> principalTry = PrincipalAccessor.tryGetCurrentPrincipal();
        principalTry.onFailure(e -> log.debug("Cache could not be invalidated for principal.", e));

        if( tenantTry.isSuccess() && principalTry.isSuccess() ) {
            return invalidatePrincipalEntries(
                tenantTry.get().getTenantId(),
                principalTry.get().getPrincipalId(),
                cache);
        } else {
            return 0;
        }
    }

    /**
     * Invalidates all cache entries of the given tenant-specific principal.
     *
     * @param tenantId
     *            The identifier of the tenant for which all principal cache entries should be invalidated.
     * @param principalId
     *            The identifier of the principal for which all cache entries should be invalidated.
     * @param cache
     *            The cache in which the entries of the principal should be invalidated.
     * @return The number of invalidated cache entries.
     */
    public static long invalidatePrincipalEntries(
        @Nullable final String tenantId,
        @Nullable final String principalId,
        @Nonnull final Cache<CacheKey, ?> cache )
    {
        final List<CacheKey> keysToInvalidate = new ArrayList<>();

        for( final CacheKey cacheKey : cache.asMap().keySet() ) {
            final String msg = "Checking invalidation for principal {} within tenant {}: {}";
            log.debug(msg, principalId, tenantId, cacheKey);

            final boolean isEqualTenantId = Objects.equals(tenantId, cacheKey.getTenantId().getOrNull());
            final boolean isEqualPrincipalId = Objects.equals(principalId, cacheKey.getPrincipalId().getOrNull());

            if( isEqualTenantId && isEqualPrincipalId ) {
                keysToInvalidate.add(cacheKey);
            }
        }

        final String msg = "Invalidating caches of principal '{}' within tenant '{}': {}";
        log.debug(msg, principalId, tenantId, keysToInvalidate);

        final long size = keysToInvalidate.size();
        cache.invalidateAll(keysToInvalidate);
        return size;
    }
}
