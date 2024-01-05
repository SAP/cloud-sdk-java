/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.typeconverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Type converter for converting types to and from their domain-specific counterparts.
 *
 * @param <T>
 *            The type to convert from/to the domain-specific counterpart.
 * @param <DomainT>
 *            The domain-specific type.
 */
public interface TypeConverter<T, DomainT>
{
    /**
     * Getter for an class object of the type to convert from/to the domain-specific counterpart.
     *
     * @return The class object of {@code T}.
     */
    @Nonnull
    Class<T> getType();

    /**
     * Getter for an class object of the domain-specific type.
     *
     * @return The class object of {@code DomainT}.
     */
    @Nonnull
    Class<DomainT> getDomainType();

    /**
     * Transforms the given object to its domain-specific counterpart.
     *
     * @param object
     *            The object to transform.
     * @return A {@code ConvertedObject} wrapping the domain-specific object.
     */
    @Nonnull
    ConvertedObject<DomainT> toDomain( @Nullable final T object );

    /**
     * Transforms the given domain-specific object to the general object.
     *
     * @param domainObject
     *            The domain-specific object to transform.
     * @return A {@code ConvertedObject} wrapping the general object.
     */
    @Nonnull
    ConvertedObject<T> fromDomain( @Nullable final DomainT domainObject );
}
