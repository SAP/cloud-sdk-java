/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.sample.services;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.ServiceWithNavigableEntities;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf;

import lombok.Getter;

/**
 * <h3>Details:</h3>
 * <table summary='Details'>
 * <tr>
 * <td align='right'>OData Service:</td>
 * <td>SDK_Grocery_Store</td>
 * </tr>
 * </table>
 *
 */
public class DefaultSdkGroceryStoreService implements ServiceWithNavigableEntities, SdkGroceryStoreService
{

    @Nonnull
    @Getter
    private final String servicePath;

    /**
     * Creates a service using {@link SdkGroceryStoreService#DEFAULT_SERVICE_PATH} to send the requests.
     *
     */
    public DefaultSdkGroceryStoreService()
    {
        servicePath = SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     *
     */
    private DefaultSdkGroceryStoreService( @Nonnull final String servicePath )
    {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultSdkGroceryStoreService withServicePath( @Nonnull final String servicePath )
    {
        return new DefaultSdkGroceryStoreService(servicePath);
    }

    @Override
    @Nonnull
    public BatchRequestBuilder batch()
    {
        return new BatchRequestBuilder(servicePath);
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Customer> getAllCustomers()
    {
        return new GetAllRequestBuilder<Customer>(servicePath, Customer.class, "Customers");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Customer> countCustomers()
    {
        return new CountRequestBuilder<Customer>(servicePath, Customer.class, "Customers");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Customer> getCustomersByKey( final Integer id )
    {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Customer>(servicePath, Customer.class, key, "Customers");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Customer> createCustomers( @Nonnull final Customer customer )
    {
        return new CreateRequestBuilder<Customer>(servicePath, customer, "Customers");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Customer> updateCustomers( @Nonnull final Customer customer )
    {
        return new UpdateRequestBuilder<Customer>(servicePath, customer, "Customers");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Customer> deleteCustomers( @Nonnull final Customer customer )
    {
        return new DeleteRequestBuilder<Customer>(servicePath, customer, "Customers");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Product> getAllProducts()
    {
        return new GetAllRequestBuilder<Product>(servicePath, Product.class, "Products");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Product> countProducts()
    {
        return new CountRequestBuilder<Product>(servicePath, Product.class, "Products");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Product> getProductsByKey( final Integer id )
    {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Product>(servicePath, Product.class, key, "Products");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Product> createProducts( @Nonnull final Product product )
    {
        return new CreateRequestBuilder<Product>(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Product> updateProducts( @Nonnull final Product product )
    {
        return new UpdateRequestBuilder<Product>(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Product> deleteProducts( @Nonnull final Product product )
    {
        return new DeleteRequestBuilder<Product>(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Receipt> getAllReceipts()
    {
        return new GetAllRequestBuilder<Receipt>(servicePath, Receipt.class, "Receipts");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Receipt> countReceipts()
    {
        return new CountRequestBuilder<Receipt>(servicePath, Receipt.class, "Receipts");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Receipt> getReceiptsByKey( final Integer id )
    {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Receipt>(servicePath, Receipt.class, key, "Receipts");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Receipt> createReceipts( @Nonnull final Receipt receipt )
    {
        return new CreateRequestBuilder<Receipt>(servicePath, receipt, "Receipts");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Receipt> updateReceipts( @Nonnull final Receipt receipt )
    {
        return new UpdateRequestBuilder<Receipt>(servicePath, receipt, "Receipts");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Receipt> deleteReceipts( @Nonnull final Receipt receipt )
    {
        return new DeleteRequestBuilder<Receipt>(servicePath, receipt, "Receipts");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Address> getAllAddresses()
    {
        return new GetAllRequestBuilder<Address>(servicePath, Address.class, "Addresses");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Address> countAddresses()
    {
        return new CountRequestBuilder<Address>(servicePath, Address.class, "Addresses");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Address> getAddressesByKey( final Integer id )
    {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Address>(servicePath, Address.class, key, "Addresses");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Address> createAddresses( @Nonnull final Address address )
    {
        return new CreateRequestBuilder<Address>(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Address> updateAddresses( @Nonnull final Address address )
    {
        return new UpdateRequestBuilder<Address>(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Address> deleteAddresses( @Nonnull final Address address )
    {
        return new DeleteRequestBuilder<Address>(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Shelf> getAllShelves()
    {
        return new GetAllRequestBuilder<Shelf>(servicePath, Shelf.class, "Shelves");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Shelf> countShelves()
    {
        return new CountRequestBuilder<Shelf>(servicePath, Shelf.class, "Shelves");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Shelf> getShelvesByKey( final Integer id )
    {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Shelf>(servicePath, Shelf.class, key, "Shelves");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Shelf> createShelves( @Nonnull final Shelf shelf )
    {
        return new CreateRequestBuilder<Shelf>(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Shelf> updateShelves( @Nonnull final Shelf shelf )
    {
        return new UpdateRequestBuilder<Shelf>(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Shelf> deleteShelves( @Nonnull final Shelf shelf )
    {
        return new DeleteRequestBuilder<Shelf>(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Shelf> getAllShopFloorShelves()
    {
        return new GetAllRequestBuilder<Shelf>(servicePath, Shelf.class, "ShopFloorShelves");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Shelf> countShopFloorShelves()
    {
        return new CountRequestBuilder<Shelf>(servicePath, Shelf.class, "ShopFloorShelves");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Shelf> getShopFloorShelvesByKey( final Integer id )
    {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Shelf>(servicePath, Shelf.class, key, "ShopFloorShelves");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Shelf> createShopFloorShelves( @Nonnull final Shelf shelf )
    {
        return new CreateRequestBuilder<Shelf>(servicePath, shelf, "ShopFloorShelves");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Shelf> updateShopFloorShelves( @Nonnull final Shelf shelf )
    {
        return new UpdateRequestBuilder<Shelf>(servicePath, shelf, "ShopFloorShelves");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Shelf> deleteShopFloorShelves( @Nonnull final Shelf shelf )
    {
        return new DeleteRequestBuilder<Shelf>(servicePath, shelf, "ShopFloorShelves");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Shelf> getAllStorageShelves()
    {
        return new GetAllRequestBuilder<Shelf>(servicePath, Shelf.class, "StorageShelves");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Shelf> countStorageShelves()
    {
        return new CountRequestBuilder<Shelf>(servicePath, Shelf.class, "StorageShelves");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Shelf> getStorageShelvesByKey( final Integer id )
    {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Shelf>(servicePath, Shelf.class, key, "StorageShelves");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Shelf> createStorageShelves( @Nonnull final Shelf shelf )
    {
        return new CreateRequestBuilder<Shelf>(servicePath, shelf, "StorageShelves");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Shelf> updateStorageShelves( @Nonnull final Shelf shelf )
    {
        return new UpdateRequestBuilder<Shelf>(servicePath, shelf, "StorageShelves");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Shelf> deleteStorageShelves( @Nonnull final Shelf shelf )
    {
        return new DeleteRequestBuilder<Shelf>(servicePath, shelf, "StorageShelves");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<OpeningHours> getAllOpeningHours()
    {
        return new GetAllRequestBuilder<OpeningHours>(servicePath, OpeningHours.class, "OpeningHours");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<OpeningHours> countOpeningHours()
    {
        return new CountRequestBuilder<OpeningHours>(servicePath, OpeningHours.class, "OpeningHours");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<OpeningHours> getOpeningHoursByKey( final Integer id )
    {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<OpeningHours>(servicePath, OpeningHours.class, key, "OpeningHours");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<OpeningHours> createOpeningHours( @Nonnull final OpeningHours openingHours )
    {
        return new CreateRequestBuilder<OpeningHours>(servicePath, openingHours, "OpeningHours");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<OpeningHours> updateOpeningHours( @Nonnull final OpeningHours openingHours )
    {
        return new UpdateRequestBuilder<OpeningHours>(servicePath, openingHours, "OpeningHours");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<OpeningHours> deleteOpeningHours( @Nonnull final OpeningHours openingHours )
    {
        return new DeleteRequestBuilder<OpeningHours>(servicePath, openingHours, "OpeningHours");
    }

}
