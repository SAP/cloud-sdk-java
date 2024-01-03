/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.services;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressCreateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressDeleteFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressUpdateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.CustomerByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.CustomerCreateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.CustomerFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlanByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlanFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.GetProductQuantitiesFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.IsStoreOpenFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHoursByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHoursFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHoursUpdateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OrderProductFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.PrintReceiptFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductCreateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductUpdateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ReceiptByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ReceiptCreateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ReceiptFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.RevokeReceiptFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfCreateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfDeleteFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfUpdateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.VendorByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.VendorFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.batch.DefaultSdkGroceryStoreServiceBatch;

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
public class DefaultSdkGroceryStoreService implements SdkGroceryStoreService
{

    @Nonnull
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
    public DefaultSdkGroceryStoreServiceBatch batch()
    {
        return new DefaultSdkGroceryStoreServiceBatch(this, servicePath);
    }

    @Override
    @Nonnull
    public CustomerFluentHelper getAllCustomer()
    {
        return new CustomerFluentHelper(servicePath, "Customers");
    }

    @Override
    @Nonnull
    public CustomerByKeyFluentHelper getCustomerByKey( final Integer id )
    {
        return new CustomerByKeyFluentHelper(servicePath, "Customers", id);
    }

    @Override
    @Nonnull
    public CustomerCreateFluentHelper createCustomer( @Nonnull final Customer customer )
    {
        return new CustomerCreateFluentHelper(servicePath, customer, "Customers");
    }

    @Override
    @Nonnull
    public ProductFluentHelper getAllProduct()
    {
        return new ProductFluentHelper(servicePath, "Products");
    }

    @Override
    @Nonnull
    public ProductByKeyFluentHelper getProductByKey( final Integer id )
    {
        return new ProductByKeyFluentHelper(servicePath, "Products", id);
    }

    @Override
    @Nonnull
    public ProductCreateFluentHelper createProduct( @Nonnull final Product product )
    {
        return new ProductCreateFluentHelper(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public ProductUpdateFluentHelper updateProduct( @Nonnull final Product product )
    {
        return new ProductUpdateFluentHelper(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public ReceiptFluentHelper getAllReceipt()
    {
        return new ReceiptFluentHelper(servicePath, "Receipts");
    }

    @Override
    @Nonnull
    public ReceiptByKeyFluentHelper getReceiptByKey( final Integer id )
    {
        return new ReceiptByKeyFluentHelper(servicePath, "Receipts", id);
    }

    @Override
    @Nonnull
    public ReceiptCreateFluentHelper createReceipt( @Nonnull final Receipt receipt )
    {
        return new ReceiptCreateFluentHelper(servicePath, receipt, "Receipts");
    }

    @Override
    @Nonnull
    public AddressFluentHelper getAllAddress()
    {
        return new AddressFluentHelper(servicePath, "Addresses");
    }

    @Override
    @Nonnull
    public AddressByKeyFluentHelper getAddressByKey( final Integer id )
    {
        return new AddressByKeyFluentHelper(servicePath, "Addresses", id);
    }

    @Override
    @Nonnull
    public AddressCreateFluentHelper createAddress( @Nonnull final Address address )
    {
        return new AddressCreateFluentHelper(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public AddressUpdateFluentHelper updateAddress( @Nonnull final Address address )
    {
        return new AddressUpdateFluentHelper(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public AddressDeleteFluentHelper deleteAddress( @Nonnull final Address address )
    {
        return new AddressDeleteFluentHelper(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public ShelfFluentHelper getAllShelf()
    {
        return new ShelfFluentHelper(servicePath, "Shelves");
    }

    @Override
    @Nonnull
    public ShelfByKeyFluentHelper getShelfByKey( final Integer id )
    {
        return new ShelfByKeyFluentHelper(servicePath, "Shelves", id);
    }

    @Override
    @Nonnull
    public ShelfCreateFluentHelper createShelf( @Nonnull final Shelf shelf )
    {
        return new ShelfCreateFluentHelper(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public ShelfUpdateFluentHelper updateShelf( @Nonnull final Shelf shelf )
    {
        return new ShelfUpdateFluentHelper(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public ShelfDeleteFluentHelper deleteShelf( @Nonnull final Shelf shelf )
    {
        return new ShelfDeleteFluentHelper(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public OpeningHoursFluentHelper getAllOpeningHours()
    {
        return new OpeningHoursFluentHelper(servicePath, "OpeningHours");
    }

    @Override
    @Nonnull
    public OpeningHoursByKeyFluentHelper getOpeningHoursByKey( final Integer id )
    {
        return new OpeningHoursByKeyFluentHelper(servicePath, "OpeningHours", id);
    }

    @Override
    @Nonnull
    public OpeningHoursUpdateFluentHelper updateOpeningHours( @Nonnull final OpeningHours openingHours )
    {
        return new OpeningHoursUpdateFluentHelper(servicePath, openingHours, "OpeningHours");
    }

    @Override
    @Nonnull
    public VendorFluentHelper getAllVendor()
    {
        return new VendorFluentHelper(servicePath, "Vendors");
    }

    @Override
    @Nonnull
    public VendorByKeyFluentHelper getVendorByKey( final Integer id )
    {
        return new VendorByKeyFluentHelper(servicePath, "Vendors", id);
    }

    @Override
    @Nonnull
    public FloorPlanFluentHelper getAllFloorPlan()
    {
        return new FloorPlanFluentHelper(servicePath, "Floors");
    }

    @Override
    @Nonnull
    public FloorPlanByKeyFluentHelper getFloorPlanByKey( final Integer id )
    {
        return new FloorPlanByKeyFluentHelper(servicePath, "Floors", id);
    }

    @Override
    @Nonnull
    public PrintReceiptFluentHelper printReceipt( @Nonnull final Integer receiptId )
    {
        return new PrintReceiptFluentHelper(servicePath, receiptId);
    }

    @Override
    @Nonnull
    public RevokeReceiptFluentHelper revokeReceipt( @Nonnull final Integer receiptId )
    {
        return new RevokeReceiptFluentHelper(servicePath, receiptId);
    }

    @Override
    @Nonnull
    public IsStoreOpenFluentHelper isStoreOpen( @Nonnull final LocalDateTime dateTime )
    {
        return new IsStoreOpenFluentHelper(servicePath, dateTime);
    }

    @Override
    @Nonnull
    public OrderProductFluentHelper orderProduct(
        @Nonnull final Integer customerId,
        @Nonnull final Integer productId,
        @Nonnull final Integer quantity )
    {
        return new OrderProductFluentHelper(servicePath, customerId, productId, quantity);
    }

    @Override
    @Nonnull
    public
        GetProductQuantitiesFluentHelper
        getProductQuantities( @Nonnull final Integer shelfId, @Nonnull final Integer productId )
    {
        return new GetProductQuantitiesFluentHelper(servicePath, shelfId, productId);
    }

}
