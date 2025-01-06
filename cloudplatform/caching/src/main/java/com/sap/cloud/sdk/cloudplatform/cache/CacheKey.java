package com.sap.cloud.sdk.cloudplatform.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * CacheKey with either global visibility, tenant isolation, or tenant and principal isolation.
 */
@EqualsAndHashCode
@ToString
@AllArgsConstructor( access = AccessLevel.PRIVATE )
public final class CacheKey implements GenericCacheKey<CacheKey, Object>
{
    @Nullable
    private final String tenantId;

    @Nullable
    private final String principalId;

    @Getter
    private final List<Object> components = new ArrayList<>();

    @Nonnull
    @Override
    public Option<String> getTenantId()
    {
        return Option.of(tenantId);
    }

    @Nonnull
    @Override
    public Option<String> getPrincipalId()
    {
        return Option.of(principalId);
    }

    @Nonnull
    @Override
    public CacheKey append( @Nonnull final Iterable<Object> objects )
        throws IllegalArgumentException
    {
        for( final Object object : objects ) {
            if( object == null ) {
                throw new IllegalArgumentException("Object must not be null.");
            }
            components.add(object);
        }
        return this;
    }

    /**
     * Appends the given Objects to this instance. In order to compare cache keys, {@link Object#equals(Object)} and
     * {@link Object#hashCode()} are used. The given objects must not be {@code null}.
     *
     * @param objects
     *            Additional objects that should be used to identify a cache key.
     * @return This instance with the objects added.
     * @throws IllegalArgumentException
     *             If any of the given objects is {@code null}.
     */
    @Nonnull
    public CacheKey append( @Nonnull final Object... objects )
    {
        return append(Arrays.asList(objects));
    }

    /**
     * Constructs a {@link CacheKey} for the given tenant and principal identifier, independent of whether they are
     * {@code null} or not. This provides the highest flexibility for defining different levels of isolation.
     *
     * @param tenant
     *            The tenant. If {@code null}, there is no tenant isolation.
     * @param principal
     *            The principal. If {@code null}, there is no principal isolation.
     * @return A new {@link CacheKey} constructed from the given tenant and principal identifier.
     */
    @Nonnull
    public static CacheKey of( @Nullable final Tenant tenant, @Nullable final Principal principal )
    {
        return fromIds(
            tenant != null ? tenant.getTenantId() : null,
            principal != null ? principal.getPrincipalId() : null);
    }

    /**
     * Constructs a new {@link CacheKey} instance for the given {@code tenantId} and {@code principalId}, independent of
     * whether they are {@code null} or not. This provides the highest flexibility for defining different levels of
     * isolation.
     *
     * @param tenantId
     *            The tenant identifier. If {@code null}, there is no tenant isolation.
     * @param principalId
     *            The principal identifier. If {@code null}, there is no principal isolation.
     * @return A new {@link CacheKey} constructed from the given tenant and principal identifier.
     * @since 4.6.0
     */
    @Beta
    @Nonnull
    public static CacheKey fromIds( @Nullable final String tenantId, @Nullable final String principalId )
    {
        return new CacheKey(tenantId, principalId);
    }

    /**
     * Constructs an instance of {@link CacheKey} without tenant or principal isolation. This can be used to share a
     * cache globally within the application.
     *
     * @return A new {@link CacheKey} without isolation.
     */
    @Nonnull
    public static CacheKey ofNoIsolation()
    {
        return new CacheKey(null, null);
    }

    /**
     * Constructs a tenant-isolated instance of {@link CacheKey}. This can be used to share a cache among the principals
     * of a tenant.
     * <p>
     * When using this method, the tenant isolation is strictly enforced. This means that if the tenant is not
     * available, an exception is thrown.
     *
     * @return A new {@link CacheKey} with tenant isolation based on the current tenant.
     * @throws TenantAccessException
     *             If there is an issue while accessing the tenant.
     */
    @Nonnull
    public static CacheKey ofTenantIsolation()
        throws TenantAccessException
    {
        return of(TenantAccessor.getCurrentTenant(), null);
    }

    /**
     * Constructs a tenant-optional-isolated instance of {@link CacheKey}. This can be used to share a cache among the
     * principals of a tenant.
     * <p>
     * When using this method, the tenant isolation is not enforced. This means that if the tenant is not available, an
     * no isolation will be applied.
     *
     * @return A new {@link CacheKey} with tenant-optional isolation based on the current tenant.
     */
    @Nonnull
    public static CacheKey ofTenantOptionalIsolation()
    {
        return of(TenantAccessor.tryGetCurrentTenant().getOrNull(), null);
    }

    /**
     * Constructs a tenant- and principal-isolated instance of {@link CacheKey}.
     * <p>
     * When using this method, the tenant and principal isolation is strictly enforced. This means that if the tenant is
     * not available or the principal is not authenticated, an exception is thrown.
     *
     * @return A new {@link CacheKey} with tenant and principal isolation based on the current tenant and principal.
     * @throws TenantAccessException
     *             If there is an issue while accessing the tenant.
     * @throws PrincipalAccessException
     *             If there is an issue while accessing the principal.
     */
    @Nonnull
    public static CacheKey ofTenantAndPrincipalIsolation()
        throws TenantAccessException,
            PrincipalAccessException
    {
        return of(TenantAccessor.getCurrentTenant(), PrincipalAccessor.getCurrentPrincipal());
    }

    /**
     * Constructs a tenant- and principal-optional-isolated instance of {@link CacheKey}.
     * <p>
     * When using this method, the tenant and principal isolation is not enforced. This means that if the tenant is not
     * available or the principal is not authenticated, the missing information is not used for isolation.
     *
     * @return A new {@link CacheKey} with tenant- and principal-optional isolation based on the current tenant and
     *         principal.
     */
    @Nonnull
    public static CacheKey ofTenantAndPrincipalOptionalIsolation()
    {
        return of(
            TenantAccessor.tryGetCurrentTenant().getOrNull(),
            PrincipalAccessor.tryGetCurrentPrincipal().getOrNull());
    }
}
