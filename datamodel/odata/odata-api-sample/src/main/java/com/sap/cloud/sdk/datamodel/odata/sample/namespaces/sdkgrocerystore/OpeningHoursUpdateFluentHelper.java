/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;

/**
 * Fluent helper to update an existing
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity and save
 * it to the S/4HANA system.
 * <p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class OpeningHoursUpdateFluentHelper extends FluentHelperUpdate<OpeningHoursUpdateFluentHelper, OpeningHours>
{

    /**
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity
     * object that will be updated in the S/4HANA system.
     *
     */
    private final OpeningHours entity;

    /**
     * Creates a fluent helper object that will update a
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity on
     * the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent
     * helper object.
     *
     * @param servicePath
     *            The service path to direct the update requests to.
     * @param entity
     *            The OpeningHours to take the updated values from.
     */
    public OpeningHoursUpdateFluentHelper(
        @Nonnull final String servicePath,
        @Nonnull final OpeningHours entity,
        @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected OpeningHours getEntity()
    {
        return entity;
    }

}
