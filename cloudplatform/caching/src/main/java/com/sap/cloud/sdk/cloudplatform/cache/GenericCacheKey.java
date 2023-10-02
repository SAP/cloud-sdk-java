/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.cache;

import java.util.List;

import javax.annotation.Nonnull;

import io.vavr.control.Option;

/**
 * GenericCacheKey interface for providing access to tenant and principal data, as well as a list of appended
 * components.
 *
 * @param <ClassT>
 *            The type of the actual implementation.
 * @param <ComponentT>
 *            The type of the appended component data.
 */
public interface GenericCacheKey<ClassT, ComponentT>
{
    /**
     * Getter for the list of additional cache key components.
     *
     * @return The list of additional cache key components.
     */
    @Nonnull
    List<ComponentT> getComponents();

    /**
     * Getter for the Id of the tenant or zone for which the key is used.
     *
     * @return The tenant or zone identifier.
     */
    @Nonnull
    Option<String> getTenantId();

    /**
     * Getter for the name of the principal for which the key is used.
     *
     * @return The principal identifier.
     */
    @Nonnull
    Option<String> getPrincipalId();

    /**
     * Appends the given Objects to this instance. In order to compare cache keys, {@link Object#equals(Object)} and
     * {@link Object#hashCode()} are used. The given objects must not be {@code null}.
     *
     * @param objects
     *            Additional objects that should be used to identify a cache key.
     *
     * @throws IllegalArgumentException
     *             If any of the given objects is {@code null}.
     *
     * @return This instance with the objects added.
     */
    @Nonnull
    ClassT append( @Nonnull final Iterable<ComponentT> objects )
        throws IllegalArgumentException;
}
