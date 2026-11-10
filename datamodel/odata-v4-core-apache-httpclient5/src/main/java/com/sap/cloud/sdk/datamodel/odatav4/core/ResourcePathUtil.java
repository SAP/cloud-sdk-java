package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

class ResourcePathUtil
{
    @Nonnull
    static <EntityT extends VdmEntity<EntityT>, ResultT> ODataResourcePath ofBoundOperation(
        @Nonnull final BoundOperation<EntityT, ResultT> operation )
    {
        final String entityCollection =
            new VdmEntityUtil<>(operation.getBindingType()).newInstance().getEntityCollection();

        return new ODataResourcePath().addSegment(entityCollection);
    }

    @Nonnull
    static <EntityT extends VdmEntity<EntityT>> ODataResourcePath ofEntity( @Nonnull final EntityT entity )
    {
        return new ODataResourcePath().addSegment(entity.getEntityCollection(), entity.getKey());
    }
}
