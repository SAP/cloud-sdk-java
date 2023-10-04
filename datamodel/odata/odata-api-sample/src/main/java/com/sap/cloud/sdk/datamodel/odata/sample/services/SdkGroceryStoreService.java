/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.services;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
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
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.batch.SdkGroceryStoreServiceBatch;

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
public interface SdkGroceryStoreService extends BatchService<SdkGroceryStoreServiceBatch>
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path
     * used to access the endpoint.
     *
     */
    String DEFAULT_SERVICE_PATH = "/com.sap.cloud.sdk.store.grocery";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also
     * adjusts the respective entity URLs.
     *
     * @param servicePath
     *            Service path that will override the default.
     * @return A new service instance with the specified service path.
     */
    @Nonnull
    SdkGroceryStoreService withServicePath( @Nonnull final String servicePath );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer}
     * entities.
     *
     * @return A fluent helper to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer} entities.
     *         This fluent helper allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.CustomerFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    CustomerFluentHelper getAllCustomer();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer}
     * entity using key fields.
     *
     * @param id
     *            Customer identifier used as entity key value.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A fluent helper to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     *         using key fields. This fluent helper allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.CustomerByKeyFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    CustomerByKeyFluentHelper getCustomerByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     * and save it to the S/4HANA system.
     *
     * @param customer
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     *            object that will be created in the S/4HANA system.
     * @return A fluent helper to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.CustomerCreateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    CustomerCreateFluentHelper createCustomer( @Nonnull final Customer customer );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product}
     * entities.
     *
     * @return A fluent helper to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entities.
     *         This fluent helper allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ProductFluentHelper getAllProduct();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity
     * using key fields.
     *
     * @param id
     *
     * @return A fluent helper to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity using
     *         key fields. This fluent helper allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductByKeyFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ProductByKeyFluentHelper getProductByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity
     * and save it to the S/4HANA system.
     *
     * @param product
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity
     *            object that will be created in the S/4HANA system.
     * @return A fluent helper to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductCreateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ProductCreateFluentHelper createProduct( @Nonnull final Product product );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product}
     * entity and save it to the S/4HANA system.
     *
     * @param product
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity
     *            object that will be updated in the S/4HANA system.
     * @return A fluent helper to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductUpdateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ProductUpdateFluentHelper updateProduct( @Nonnull final Product product );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt}
     * entities.
     *
     * @return A fluent helper to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt} entities.
     *         This fluent helper allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ReceiptFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ReceiptFluentHelper getAllReceipt();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     * using key fields.
     *
     * @param id
     *
     * @return A fluent helper to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity using
     *         key fields. This fluent helper allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ReceiptByKeyFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ReceiptByKeyFluentHelper getReceiptByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     * and save it to the S/4HANA system.
     *
     * @param receipt
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     *            object that will be created in the S/4HANA system.
     * @return A fluent helper to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ReceiptCreateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ReceiptCreateFluentHelper createReceipt( @Nonnull final Receipt receipt );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address}
     * entities.
     *
     * @return A fluent helper to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entities.
     *         This fluent helper allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    AddressFluentHelper getAllAddress();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     * using key fields.
     *
     * @param id
     *
     * @return A fluent helper to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity using
     *         key fields. This fluent helper allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressByKeyFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    AddressByKeyFluentHelper getAddressByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     * and save it to the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be created in the S/4HANA system.
     * @return A fluent helper to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressCreateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    AddressCreateFluentHelper createAddress( @Nonnull final Address address );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address}
     * entity and save it to the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be updated in the S/4HANA system.
     * @return A fluent helper to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressUpdateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    AddressUpdateFluentHelper updateAddress( @Nonnull final Address address );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address}
     * entity in the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A fluent helper to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.AddressDeleteFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    AddressDeleteFluentHelper deleteAddress( @Nonnull final Address address );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entities.
     *
     * @return A fluent helper to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entities. This
     *         fluent helper allows methods which modify the underlying query to be called before executing the query
     *         itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ShelfFluentHelper getAllShelf();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     * using key fields.
     *
     * @param id
     *
     * @return A fluent helper to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity using key
     *         fields. This fluent helper allows methods which modify the underlying query to be called before executing
     *         the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfByKeyFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ShelfByKeyFluentHelper getShelfByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity and
     * save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity object
     *            that will be created in the S/4HANA system.
     * @return A fluent helper to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfCreateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ShelfCreateFluentHelper createShelf( @Nonnull final Shelf shelf );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     * and save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity object
     *            that will be updated in the S/4HANA system.
     * @return A fluent helper to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfUpdateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ShelfUpdateFluentHelper updateShelf( @Nonnull final Shelf shelf );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entity in the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity object
     *            that will be deleted in the S/4HANA system.
     * @return A fluent helper to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfDeleteFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    ShelfDeleteFluentHelper deleteShelf( @Nonnull final Shelf shelf );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entities.
     *
     * @return A fluent helper to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entities. This fluent helper allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHoursFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    OpeningHoursFluentHelper getAllOpeningHours();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entity using key fields.
     *
     * @param id
     *
     * @return A fluent helper to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entity using key fields. This fluent helper allows methods which modify the underlying query to be called
     *         before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHoursByKeyFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    OpeningHoursByKeyFluentHelper getOpeningHoursByKey( final Integer id );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entity and save it to the S/4HANA system.
     *
     * @param openingHours
     *            {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *            entity object that will be updated in the S/4HANA system.
     * @return A fluent helper to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entity. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHoursUpdateFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    OpeningHoursUpdateFluentHelper updateOpeningHours( @Nonnull final OpeningHours openingHours );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Vendor Vendor}
     * entities.
     *
     * @return A fluent helper to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Vendor Vendor} entities. This
     *         fluent helper allows methods which modify the underlying query to be called before executing the query
     *         itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.VendorFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    VendorFluentHelper getAllVendor();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Vendor Vendor} entity
     * using key fields.
     *
     * @param id
     *
     * @return A fluent helper to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Vendor Vendor} entity using
     *         key fields. This fluent helper allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.VendorByKeyFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    VendorByKeyFluentHelper getVendorByKey( final Integer id );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan FloorPlan}
     * entities.
     *
     * @return A fluent helper to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan FloorPlan} entities.
     *         This fluent helper allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlanFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    FloorPlanFluentHelper getAllFloorPlan();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan FloorPlan}
     * entity using key fields.
     *
     * @param id
     *
     * @return A fluent helper to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan FloorPlan} entity
     *         using key fields. This fluent helper allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlanByKeyFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    FloorPlanByKeyFluentHelper getFloorPlanByKey( final Integer id );

    /**
     * Returns the Count of Attachments
     * <p>
     * </p>
     * <p>
     * Creates a fluent helper for the <b>PrintReceipt</b> OData function import.
     * </p>
     *
     * @param receiptId
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>ReceiptId</b>
     *            </p>
     * @return A fluent helper object that will execute the <b>PrintReceipt</b> OData function import with the provided
     *         parameters. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.PrintReceiptFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    PrintReceiptFluentHelper printReceipt( @Nonnull final Integer receiptId );

    /**
     * Returns the Count of Attachments
     * <p>
     * </p>
     * <p>
     * Creates a fluent helper for the <b>RevokeReceipt</b> OData function import.
     * </p>
     *
     * @param receiptId
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>ReceiptId</b>
     *            </p>
     * @return A fluent helper object that will execute the <b>RevokeReceipt</b> OData function import with the provided
     *         parameters. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.RevokeReceiptFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    RevokeReceiptFluentHelper revokeReceipt( @Nonnull final Integer receiptId );

    /**
     * Check whether the store is open.
     * <p>
     * </p>
     * <p>
     * Creates a fluent helper for the <b>IsStoreOpen</b> OData function import.
     * </p>
     *
     * @param dateTime
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>DateTime</b>
     *            </p>
     * @return A fluent helper object that will execute the <b>IsStoreOpen</b> OData function import with the provided
     *         parameters. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.IsStoreOpenFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    IsStoreOpenFluentHelper isStoreOpen( @Nonnull final LocalDateTime dateTime );

    /**
     * Create an order for a given customer.
     * <p>
     * </p>
     * <p>
     * Creates a fluent helper for the <b>OrderProduct</b> OData function import.
     * </p>
     *
     * @param quantity
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>Quantity</b>
     *            </p>
     * @param productId
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>ProductId</b>
     *            </p>
     * @param customerId
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>CustomerId</b>
     *            </p>
     * @return A fluent helper object that will execute the <b>OrderProduct</b> OData function import with the provided
     *         parameters. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OrderProductFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    OrderProductFluentHelper orderProduct(
        @Nonnull final Integer customerId,
        @Nonnull final Integer productId,
        @Nonnull final Integer quantity );

    /**
     * Get inventory of a given shelf.
     * <p>
     * </p>
     * <p>
     * Creates a fluent helper for the <b>GetProductQuantities</b> OData function import.
     * </p>
     *
     * @param shelfId
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>ShelfId</b>
     *            </p>
     * @param productId
     *            Constraints: Not nullable
     *            <p>
     *            Original parameter name from the Odata EDM: <b>ProductId</b>
     *            </p>
     * @return A fluent helper object that will execute the <b>GetProductQuantities</b> OData function import with the
     *         provided parameters. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.GetProductQuantitiesFluentHelper#execute
     *         execute} method on the fluent helper object.
     */
    @Nonnull
    GetProductQuantitiesFluentHelper
        getProductQuantities( @Nonnull final Integer shelfId, @Nonnull final Integer productId );

}
