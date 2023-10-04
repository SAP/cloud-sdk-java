/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;

/**
 * Fluent helper to update an existing
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity and save it to the
 * S/4HANA system.
 * <p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class AddressUpdateFluentHelper extends FluentHelperUpdate<AddressUpdateFluentHelper, Address>
{

    /**
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity object that
     * will be updated in the S/4HANA system.
     *
     */
    private final Address entity;

    /**
     * Creates a fluent helper object that will update a
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity on the OData
     * endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper
     * object.
     *
     * @param servicePath
     *            The service path to direct the update requests to.
     * @param entity
     *            The Address to take the updated values from.
     */
    public AddressUpdateFluentHelper(
        @Nonnull final String servicePath,
        @Nonnull final Address entity,
        @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected Address getEntity()
    {
        return entity;
    }

}
