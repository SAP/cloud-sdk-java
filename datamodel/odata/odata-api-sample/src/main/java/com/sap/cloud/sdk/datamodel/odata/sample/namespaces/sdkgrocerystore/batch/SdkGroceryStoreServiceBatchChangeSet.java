/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.batch;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf;

/**
 * This interface enables you to combine multiple operations into one change set. For further information have a look
 * into the {@link com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService SdkGroceryStoreService}.
 *
 */
public interface SdkGroceryStoreServiceBatchChangeSet
    extends
    FluentHelperBatchChangeSet<SdkGroceryStoreServiceBatchChangeSet>,
    FluentHelperBatchEndChangeSet<SdkGroceryStoreServiceBatch>
{

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     * and save it to the S/4HANA system.
     *
     * @param customer
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     *            object that will be created in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet createCustomer( @Nonnull final Customer customer );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity
     * and save it to the S/4HANA system.
     *
     * @param product
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity
     *            object that will be created in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet createProduct( @Nonnull final Product product );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product}
     * entity and save it to the S/4HANA system.
     *
     * @param product
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity
     *            object that will be updated in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet updateProduct( @Nonnull final Product product );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     * and save it to the S/4HANA system.
     *
     * @param receipt
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     *            object that will be created in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet createReceipt( @Nonnull final Receipt receipt );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     * and save it to the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be created in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet createAddress( @Nonnull final Address address );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address}
     * entity and save it to the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be updated in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet updateAddress( @Nonnull final Address address );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address}
     * entity in the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be deleted in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet deleteAddress( @Nonnull final Address address );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity and
     * save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity object
     *            that will be created in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet createShelf( @Nonnull final Shelf shelf );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     * and save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity object
     *            that will be updated in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet updateShelf( @Nonnull final Shelf shelf );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entity in the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity object
     *            that will be deleted in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet deleteShelf( @Nonnull final Shelf shelf );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entity and save it to the S/4HANA system.
     *
     * @param openingHours
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *            entity object that will be updated in the S/4HANA system.
     * @return This fluent helper to continue adding operations to the change set. To finalize the current change set
     *         call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    SdkGroceryStoreServiceBatchChangeSet updateOpeningHours( @Nonnull final OpeningHours openingHours );

}
