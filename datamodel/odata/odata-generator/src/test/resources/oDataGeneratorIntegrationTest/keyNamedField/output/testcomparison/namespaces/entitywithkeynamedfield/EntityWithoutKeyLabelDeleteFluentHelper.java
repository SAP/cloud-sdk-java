/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class EntityWithoutKeyLabelDeleteFluentHelper
    extends FluentHelperDelete<EntityWithoutKeyLabelDeleteFluentHelper, EntityWithoutKeyLabel>
{

    /**
     * {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be deleted in the S/4HANA system.
     * 
     */
    private final EntityWithoutKeyLabel entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The EntityWithoutKeyLabel to delete from the endpoint.
     */
    public EntityWithoutKeyLabelDeleteFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final EntityWithoutKeyLabel entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected EntityWithoutKeyLabel getEntity() {
        return entity;
    }

}
