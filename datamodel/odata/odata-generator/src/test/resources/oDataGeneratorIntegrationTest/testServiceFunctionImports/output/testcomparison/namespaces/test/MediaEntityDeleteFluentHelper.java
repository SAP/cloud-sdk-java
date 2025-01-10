/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class MediaEntityDeleteFluentHelper
    extends FluentHelperDelete<MediaEntityDeleteFluentHelper, MediaEntity>
{

    /**
     * {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be deleted in the S/4HANA system.
     * 
     */
    private final MediaEntity entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The MediaEntity to delete from the endpoint.
     */
    public MediaEntityDeleteFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final MediaEntity entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected MediaEntity getEntity() {
        return entity;
    }

}
