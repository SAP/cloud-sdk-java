package com.sap.cloud.sdk.datamodel.odatav4.core;

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
final class VdmEntityUtil<EntityT extends VdmEntity<?>>
{
    private final Class<EntityT> entityClass;

    @Nonnull
    EntityT newInstance()
    {
        try {
            return entityClass.newInstance();
        }
        catch( final InstantiationException | IllegalAccessException e ) {
            throw new ShouldNotHappenException(
                "Failed to instantiate object of type " + entityClass.getSimpleName(),
                e);
        }
    }
}
