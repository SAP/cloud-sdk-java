/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.sdkgrocerystore.Address;
import testcomparison.namespaces.sdkgrocerystore.Customer;
import testcomparison.namespaces.sdkgrocerystore.OpeningHours;
import testcomparison.namespaces.sdkgrocerystore.Product;
import testcomparison.namespaces.sdkgrocerystore.Receipt;
import testcomparison.namespaces.sdkgrocerystore.Shelf;


/**
 * Implementation of the {@link SdkGroceryStoreServiceBatchChangeSet} interface, enabling you to combine multiple operations into one changeset. For further information have a look into the {@link testcomparison.services.SdkGroceryStoreService SdkGroceryStoreService}.
 * 
 */
public class DefaultSdkGroceryStoreServiceBatchChangeSet
    extends BatchChangeSetFluentHelperBasic<SdkGroceryStoreServiceBatch, SdkGroceryStoreServiceBatchChangeSet>
    implements SdkGroceryStoreServiceBatchChangeSet
{

    @Nonnull
    private final testcomparison.services.SdkGroceryStoreService service;

    DefaultSdkGroceryStoreServiceBatchChangeSet(
        @Nonnull
        final DefaultSdkGroceryStoreServiceBatch batchFluentHelper,
        @Nonnull
        final testcomparison.services.SdkGroceryStoreService service) {
        super(batchFluentHelper, batchFluentHelper);
        this.service = service;
    }

    @Nonnull
    @Override
    protected DefaultSdkGroceryStoreServiceBatchChangeSet getThis() {
        return this;
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet createCustomer(
        @Nonnull
        final Customer customer) {
        return addRequestCreate(service::createCustomer, customer);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet createProduct(
        @Nonnull
        final Product product) {
        return addRequestCreate(service::createProduct, product);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet updateProduct(
        @Nonnull
        final Product product) {
        return addRequestUpdate(service::updateProduct, product);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet createReceipt(
        @Nonnull
        final Receipt receipt) {
        return addRequestCreate(service::createReceipt, receipt);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet createAddress(
        @Nonnull
        final Address address) {
        return addRequestCreate(service::createAddress, address);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet updateAddress(
        @Nonnull
        final Address address) {
        return addRequestUpdate(service::updateAddress, address);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet deleteAddress(
        @Nonnull
        final Address address) {
        return addRequestDelete(service::deleteAddress, address);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet createShelf(
        @Nonnull
        final Shelf shelf) {
        return addRequestCreate(service::createShelf, shelf);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet updateShelf(
        @Nonnull
        final Shelf shelf) {
        return addRequestUpdate(service::updateShelf, shelf);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet deleteShelf(
        @Nonnull
        final Shelf shelf) {
        return addRequestDelete(service::deleteShelf, shelf);
    }

    @Nonnull
    @Override
    public SdkGroceryStoreServiceBatchChangeSet updateOpeningHours(
        @Nonnull
        final OpeningHours openingHours) {
        return addRequestUpdate(service::updateOpeningHours, openingHours);
    }

}
