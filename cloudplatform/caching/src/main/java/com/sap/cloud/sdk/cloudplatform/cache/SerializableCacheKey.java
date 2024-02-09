/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * SerializableCacheKey with either global visibility, tenant isolation, or tenant and principal isolation. The cache
 * key components are guaranteed to be serializable.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class SerializableCacheKey implements GenericCacheKey<SerializableCacheKey, Serializable>, Serializable
{
    private static final long serialVersionUID = 4042868809324769786L;

    @Nullable
    private final String tenantId;

    @Nullable
    private final String principalId;

    @Getter
    private final List<Serializable> components = new ArrayList<>();

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
    public SerializableCacheKey append( @Nonnull final Iterable<Serializable> objects )
        throws IllegalArgumentException
    {
        for( final Serializable object : objects ) {
            if( object == null ) {
                throw new IllegalArgumentException("Object must not be null.");
            }
            components.add(object);
        }
        return this;
    }

    /**
     * Constructs a {@link SerializableCacheKey} for the given tenant and principal identifier, independent of whether
     * they are {@code null} or not. This provides the highest flexibility for defining different levels of isolation.
     *
     * @param tenantId
     *            The identifier of the tenant. If {@code null}, there is no tenant isolation.
     * @param principalId
     *            The identifier of the principal. If {@code null}, there is no principal isolation.
     *
     * @return A new {@link CacheKey} constructed from the given tenant and principal identifier.
     */
    @Nonnull
    public static SerializableCacheKey of( @Nullable final String tenantId, @Nullable final String principalId )
    {
        return new SerializableCacheKey(tenantId, principalId);
    }

    /**
     * Constructs a {@link CacheKey} for the given tenant and principal identifier, independent of whether they are
     * {@code null} or not. This provides the highest flexibility for defining different levels of isolation.
     *
     * @param tenant
     *            The tenant. If {@code null}, there is not tenant isolation.
     * @param principal
     *            The principal. If {@code null}, there is no principal isolation.
     *
     * @return A new {@link CacheKey} constructed from the given tenant and principal identifier.
     */
    @Nonnull
    public static SerializableCacheKey of( @Nullable final Tenant tenant, @Nullable final Principal principal )
    {
        return new SerializableCacheKey(
            tenant != null ? tenant.getTenantId() : null,
            principal != null ? principal.getPrincipalId() : null);
    }
}
