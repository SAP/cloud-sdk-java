/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;

/**
 * Fluent helper to delete an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf
 * Shelf} entity in the S/4HANA system.
 * <p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class ShelfDeleteFluentHelper extends FluentHelperDelete<ShelfDeleteFluentHelper, Shelf>
{

    /**
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity object that will
     * be deleted in the S/4HANA system.
     *
     */
    private final Shelf entity;

    /**
     * Creates a fluent helper object that will delete a
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity on the OData
     * endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper
     * object.
     *
     * @param entityCollection
     *            The entity collection to direct the update requests to.
     * @param servicePath
     *            The service path to direct the update requests to.
     * @param entity
     *            The Shelf to delete from the endpoint.
     */
    public ShelfDeleteFluentHelper(
        @Nonnull final String servicePath,
        @Nonnull final Shelf entity,
        @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected Shelf getEntity()
    {
        return entity;
    }

}
