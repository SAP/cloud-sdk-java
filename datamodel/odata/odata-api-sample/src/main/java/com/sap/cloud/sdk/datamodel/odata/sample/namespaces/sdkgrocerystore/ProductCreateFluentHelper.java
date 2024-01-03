/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;

/**
 * Fluent helper to create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product
 * Product} entity and save it to the S/4HANA system.
 * <p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class ProductCreateFluentHelper extends FluentHelperCreate<ProductCreateFluentHelper, Product>
{

    /**
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity object that
     * will be created in the S/4HANA system.
     *
     */
    private final Product entity;

    /**
     * Creates a fluent helper object that will create a
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity on the OData
     * endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper
     * object.
     *
     * @param entityCollection
     *            Entity Collection to direct the create requests to.
     * @param servicePath
     *            The service path to direct the create requests to.
     * @param entity
     *            The Product to create.
     */
    public ProductCreateFluentHelper(
        @Nonnull final String servicePath,
        @Nonnull final Product entity,
        @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected Product getEntity()
    {
        return entity;
    }

}
