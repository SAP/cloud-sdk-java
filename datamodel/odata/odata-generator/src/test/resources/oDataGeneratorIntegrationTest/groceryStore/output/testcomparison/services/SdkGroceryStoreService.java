/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
import testcomparison.namespaces.sdkgrocerystore.Address;
import testcomparison.namespaces.sdkgrocerystore.AddressByKeyFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.AddressCreateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.AddressDeleteFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.AddressFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.AddressUpdateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.Customer;
import testcomparison.namespaces.sdkgrocerystore.CustomerByKeyFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.CustomerCreateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.CustomerFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.FloorPlanByKeyFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.FloorPlanFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.GetProductQuantitiesFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.IsStoreOpenFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.OpeningHours;
import testcomparison.namespaces.sdkgrocerystore.OpeningHoursByKeyFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.OpeningHoursFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.OpeningHoursUpdateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.OrderProductFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.PrintReceiptFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.Product;
import testcomparison.namespaces.sdkgrocerystore.ProductByKeyFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ProductCreateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ProductFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ProductUpdateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.Receipt;
import testcomparison.namespaces.sdkgrocerystore.ReceiptByKeyFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ReceiptCreateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ReceiptFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.RevokeReceiptFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.Shelf;
import testcomparison.namespaces.sdkgrocerystore.ShelfByKeyFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ShelfCreateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ShelfDeleteFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ShelfFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.ShelfUpdateFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.VendorByKeyFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.VendorFluentHelper;
import testcomparison.namespaces.sdkgrocerystore.batch.SdkGroceryStoreServiceBatch;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>SDK_Grocery_Store</td></tr></table>
 * 
 */
public interface SdkGroceryStoreService
    extends BatchService<SdkGroceryStoreServiceBatch>
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/grocerycom.sap.cloud.sdk.store.grocery";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     * 
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    SdkGroceryStoreService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.CustomerFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    CustomerFluentHelper getAllCustomer();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity using key fields.
     * 
     * @param id
     *     Customer identifier used as entity key value.<p>Constraints: Not nullable</p>
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.CustomerByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    CustomerByKeyFluentHelper getCustomerByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity and save it to the S/4HANA system.
     * 
     * @param customer
     *     {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.CustomerCreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    CustomerCreateFluentHelper createCustomer(
        @Nonnull
        final Customer customer);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ProductFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ProductFluentHelper getAllProduct();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity using key fields.
     * 
     * @param id
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ProductByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ProductByKeyFluentHelper getProductByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity and save it to the S/4HANA system.
     * 
     * @param product
     *     {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ProductCreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ProductCreateFluentHelper createProduct(
        @Nonnull
        final Product product);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity and save it to the S/4HANA system.
     * 
     * @param product
     *     {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ProductUpdateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ProductUpdateFluentHelper updateProduct(
        @Nonnull
        final Product product);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ReceiptFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ReceiptFluentHelper getAllReceipt();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity using key fields.
     * 
     * @param id
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ReceiptByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ReceiptByKeyFluentHelper getReceiptByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity and save it to the S/4HANA system.
     * 
     * @param receipt
     *     {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ReceiptCreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ReceiptCreateFluentHelper createReceipt(
        @Nonnull
        final Receipt receipt);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.AddressFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    AddressFluentHelper getAllAddress();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity using key fields.
     * 
     * @param id
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.AddressByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    AddressByKeyFluentHelper getAddressByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity and save it to the S/4HANA system.
     * 
     * @param address
     *     {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.AddressCreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    AddressCreateFluentHelper createAddress(
        @Nonnull
        final Address address);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity and save it to the S/4HANA system.
     * 
     * @param address
     *     {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.AddressUpdateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    AddressUpdateFluentHelper updateAddress(
        @Nonnull
        final Address address);

    /**
     * Deletes an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity in the S/4HANA system.
     * 
     * @param address
     *     {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.AddressDeleteFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    AddressDeleteFluentHelper deleteAddress(
        @Nonnull
        final Address address);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ShelfFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ShelfFluentHelper getAllShelf();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity using key fields.
     * 
     * @param id
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ShelfByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ShelfByKeyFluentHelper getShelfByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity and save it to the S/4HANA system.
     * 
     * @param shelf
     *     {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ShelfCreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ShelfCreateFluentHelper createShelf(
        @Nonnull
        final Shelf shelf);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity and save it to the S/4HANA system.
     * 
     * @param shelf
     *     {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ShelfUpdateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ShelfUpdateFluentHelper updateShelf(
        @Nonnull
        final Shelf shelf);

    /**
     * Deletes an existing {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity in the S/4HANA system.
     * 
     * @param shelf
     *     {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.ShelfDeleteFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    ShelfDeleteFluentHelper deleteShelf(
        @Nonnull
        final Shelf shelf);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.OpeningHoursFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    OpeningHoursFluentHelper getAllOpeningHours();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity using key fields.
     * 
     * @param id
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.OpeningHoursByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    OpeningHoursByKeyFluentHelper getOpeningHoursByKey(final Integer id);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity and save it to the S/4HANA system.
     * 
     * @param openingHours
     *     {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.OpeningHoursUpdateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    OpeningHoursUpdateFluentHelper updateOpeningHours(
        @Nonnull
        final OpeningHours openingHours);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Vendor Vendor} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Vendor Vendor} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.VendorFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    VendorFluentHelper getAllVendor();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Vendor Vendor} entity using key fields.
     * 
     * @param id
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Vendor Vendor} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.VendorByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    VendorByKeyFluentHelper getVendorByKey(final Integer id);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.FloorPlan FloorPlan} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.FloorPlan FloorPlan} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.FloorPlanFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    FloorPlanFluentHelper getAllFloorPlan();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.FloorPlan FloorPlan} entity using key fields.
     * 
     * @param id
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.FloorPlan FloorPlan} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.FloorPlanByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    FloorPlanByKeyFluentHelper getFloorPlanByKey(final Integer id);

    /**
     * Returns the Count of Attachments<p></p><p>Creates a fluent helper for the <b>PrintReceipt</b> OData function import.</p>
     * 
     * @param receiptId
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>ReceiptId</b></p>
     * @return
     *     A fluent helper object that will execute the <b>PrintReceipt</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.PrintReceiptFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    PrintReceiptFluentHelper printReceipt(
        @Nonnull
        final Integer receiptId);

    /**
     * Returns the Count of Attachments<p></p><p>Creates a fluent helper for the <b>RevokeReceipt</b> OData function import.</p>
     * 
     * @param receiptId
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>ReceiptId</b></p>
     * @return
     *     A fluent helper object that will execute the <b>RevokeReceipt</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.RevokeReceiptFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    RevokeReceiptFluentHelper revokeReceipt(
        @Nonnull
        final Integer receiptId);

    /**
     * Check whether the store is open.<p></p><p>Creates a fluent helper for the <b>IsStoreOpen</b> OData function import.</p>
     * 
     * @param dateTime
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>DateTime</b></p>
     * @return
     *     A fluent helper object that will execute the <b>IsStoreOpen</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.IsStoreOpenFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    IsStoreOpenFluentHelper isStoreOpen(
        @Nonnull
        final LocalDateTime dateTime);

    /**
     * Create an order for a given customer.<p></p><p>Creates a fluent helper for the <b>OrderProduct</b> OData function import.</p>
     * 
     * @param quantity
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>Quantity</b></p>
     * @param productId
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>ProductId</b></p>
     * @param customerId
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>CustomerId</b></p>
     * @return
     *     A fluent helper object that will execute the <b>OrderProduct</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.OrderProductFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    OrderProductFluentHelper orderProduct(
        @Nonnull
        final Integer customerId,
        @Nonnull
        final Integer productId,
        @Nonnull
        final Integer quantity);

    /**
     * Get inventory of a given shelf.<p></p><p>Creates a fluent helper for the <b>GetProductQuantities</b> OData function import.</p>
     * 
     * @param shelfId
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>ShelfId</b></p>
     * @param productId
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>ProductId</b></p>
     * @return
     *     A fluent helper object that will execute the <b>GetProductQuantities</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.sdkgrocerystore.GetProductQuantitiesFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    GetProductQuantitiesFluentHelper getProductQuantities(
        @Nullable
        final Integer shelfId,
        @Nonnull
        final Integer productId);

}
