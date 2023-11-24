/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;

import lombok.RequiredArgsConstructor;

/**
 * Utility class to manage OData entity deserialization.
 *
 * @param <EntityT>
 *            Entity type to create new instances from.
 */
@RequiredArgsConstructor
public final class VdmEntityUtil<EntityT extends VdmEntity<?>>
{
    private final Class<EntityT> entityClass;

    @Nonnull
    EntityT newInstance()
    {
        try {
            return entityClass.getDeclaredConstructor().newInstance();
        }
        catch( final
            InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e ) {
            throw new ShouldNotHappenException(
                "Failed to instantiate object of type " + entityClass.getSimpleName(),
                e);
        }
    }

    /**
     * Helper method to resolve the expected entity type for the provided fluent helper instance. For internal use.
     *
     * @param fluentHelper
     *            The fluent helper instance to resolve the entity type for.
     * @param <EntityT>
     *            The generic entity type.
     * @return The entity type.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    public static <EntityT> Class<EntityT> getEntityClass( @Nonnull final FluentHelperBasic<?, ?, ?> fluentHelper )
    {
        return (Class<EntityT>) fluentHelper.getEntityClass();
    }
}
