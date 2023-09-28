package com.sap.cloud.sdk.cloudplatform.resilience;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sap.cloud.sdk.cloudplatform.cache.GenericCacheKey;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;

/**
 * Represents a filter function to select specific parts of a cache depending on a {@link ResilienceConfiguration}, a
 * {@link GenericCacheKey}, and a cache entry.
 */
@FunctionalInterface
public interface CacheFilter
{
    /**
     * Determines whether a specific cache entry matches the expectations of this cache filter.
     *
     * @param configuration
     *            The {@link ResilienceConfiguration} that is used for the current cache access operation.
     * @param cacheKey
     *            The {@link GenericCacheKey} that is associated with the current cache entry.
     * @param cacheEntry
     *            The actual cache entry.
     * @return {@code true} if the cache entry should be included, {@code false} otherwise.
     */
    boolean matches(
        @Nonnull final ResilienceConfiguration configuration,
        @Nonnull final GenericCacheKey<?, ?> cacheKey,
        @Nullable Object cacheEntry );

    /**
     * Creates a {@link CacheFilter}, which includes all cache entries associated with {@link GenericCacheKey}s that are
     * accessible with respect to the passed {@link Tenant}.
     *
     * @param tenant
     *            The tenant
     * @return A {@link CacheFilter} that respects the provided tenant.
     */
    @Nonnull
    static CacheFilter keyMatchesTenant( @Nonnull final Tenant tenant )
    {
        return ( configuration, cacheKey, cacheEntry ) -> Objects
            .equals(cacheKey.getTenantId().getOrNull(), tenant.getTenantId());
    }

    /**
     * Creates a {@link CacheFilter}, which matches all cache entries associated with {@link GenericCacheKey}s that are
     * accessible with respect to the {@link Principal} used at the time when the {@link CacheFilter} is applied.<br>
     *
     * Whether the {@link CacheFilter} considers the {@link Principal}, depends on the {@link ResilienceIsolationMode}
     * stored in the corresponding {@link ResilienceConfiguration}.
     *
     * @return A {@link CacheFilter} that respects principal isolation.
     */
    @Nonnull
    static CacheFilter keyMatchesPrincipal()
    {
        return ( configuration, cacheKey, cacheEntry ) -> {
            final ResilienceIsolationKey isolationKey = ResilienceIsolationKey.of(configuration.isolationMode());
            final String principalId =
                isolationKey.getPrincipal() == null ? null : isolationKey.getPrincipal().getPrincipalId();

            return Objects.equals(cacheKey.getPrincipalId().getOrNull(), principalId);
        };
    }

    /**
     * Creates a {@link CacheFilter}, which includes all cache entries associated with {@link GenericCacheKey}s that are
     * accessible with respect to the passed {@link Principal}.
     *
     * @param principal
     *            The principal
     * @return A {@link CacheFilter} that respects the provided principal id
     */
    @Nonnull
    static CacheFilter keyMatchesPrincipal( @Nonnull final Principal principal )
    {
        return ( configuration, cacheKey, cacheEntry ) -> Objects
            .equals(cacheKey.getPrincipalId().getOrNull(), principal.getPrincipalId());
    }

    /**
     * Creates a {@link CacheFilter}, which matches all cache entries associated with {@link GenericCacheKey}s that are
     * accessible with respect to the {@link Tenant} used at the time when the {@link CacheFilter} is applied.<br>
     *
     * Whether the {@link CacheFilter} considers the {@link Tenant}, depends on the {@link ResilienceIsolationMode}
     * stored in the corresponding {@link ResilienceConfiguration}.
     *
     * @return A {@link CacheFilter} that respects tenant isolation.
     */
    @Nonnull
    static CacheFilter keyMatchesTenant()
    {
        return ( configuration, cacheKey, cacheEntry ) -> {
            final ResilienceIsolationKey isolationKey = ResilienceIsolationKey.of(configuration.isolationMode());
            final String tenantId = isolationKey.getTenant() == null ? null : isolationKey.getTenant().getTenantId();

            return Objects.equals(cacheKey.getTenantId().getOrNull(), tenantId);
        };
    }

    /**
     * Creates a {@link CacheFilter}, which matches all cache entries associated with {@link GenericCacheKey}s that are
     * accessible with respect to the parameters in the {@link ResilienceConfiguration.CacheConfiguration} in the
     * corresponding {@link ResilienceConfiguration}.
     *
     * @return A {@link CacheFilter} that respects the cache parameters
     */
    @Nonnull
    static CacheFilter keyMatchesParameters()
    {
        return ( configuration, cacheKey, cacheEntry ) -> Iterables
            .elementsEqual(cacheKey.getComponents(), configuration.cacheConfiguration().parameters());
    }

    /**
     * Creates a {@link CacheFilter}, which includes all cache entries associated with {@link GenericCacheKey}s that are
     * accessible with respect to the passed parameters.
     *
     * @param parameters
     *            The parameters
     * @return A {@link CacheFilter} that respects the provided parameters
     */
    @Nonnull
    static CacheFilter keyMatchesParameters( @Nonnull final Iterable<Object> parameters )
    {
        return ( configuration, cacheKey, cacheEntry ) -> Iterables.elementsEqual(cacheKey.getComponents(), parameters);
    }

    /**
     * Creates a new {@link CacheFilter} instance which represents the conjunction of all passed cache filters.
     *
     * @param firstFilter
     *            The first cache filter for the conjunction
     * @param additionalFilters
     *            All additional cache filters which shall be applied as one conjunction
     * @return The conjunction of all cache filters
     */
    @Nonnull
    static CacheFilter and( @Nonnull final CacheFilter firstFilter, @Nonnull final CacheFilter... additionalFilters )
    {
        return ( configuration, cacheKey, cacheEntry ) -> {
            final List<CacheFilter> allFilters = Lists.newArrayList(additionalFilters);
            allFilters.add(0, firstFilter);

            return allFilters
                .stream()
                .allMatch(cacheFilter -> cacheFilter.matches(configuration, cacheKey, cacheEntry));
        };
    }

    /**
     * Creates a new {@link CacheFilter} instance which represents the disjunction of all passed cache filters.
     *
     * @param firstFilter
     *            The first cache filter for the disjunction
     * @param additionalFilters
     *            All additional cache filters which shall be applied as one disjunction
     * @return The disjunction of all cache filters
     */
    @Nonnull
    static CacheFilter or( @Nonnull final CacheFilter firstFilter, @Nonnull final CacheFilter... additionalFilters )
    {
        return ( configuration, cacheKey, cacheEntry ) -> {
            final List<CacheFilter> allFilters = Lists.newArrayList(additionalFilters);
            allFilters.add(0, firstFilter);

            return allFilters
                .stream()
                .anyMatch(cacheFilter -> cacheFilter.matches(configuration, cacheKey, cacheEntry));
        };
    }
}
