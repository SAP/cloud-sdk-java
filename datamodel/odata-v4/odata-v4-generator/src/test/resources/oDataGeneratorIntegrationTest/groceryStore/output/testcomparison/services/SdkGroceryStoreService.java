/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import testcomparison.namespaces.sdkgrocerystore.Address;
import testcomparison.namespaces.sdkgrocerystore.Customer;
import testcomparison.namespaces.sdkgrocerystore.OpeningHours;
import testcomparison.namespaces.sdkgrocerystore.Product;
import testcomparison.namespaces.sdkgrocerystore.Receipt;
import testcomparison.namespaces.sdkgrocerystore.Shelf;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/SDK_Grocery_Store?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>SDK_Grocery_Store</td></tr></table>
 * 
 */
public interface SdkGroceryStoreService {

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/sdk/sample/com.sap.cloud.sdk.store.grocery";

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
     * Creates a batch request builder object.
     * 
     * @return
     *     A request builder to handle batch operation on this service. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder#execute(Destination) execute} method on the request builder object.
     */
    @Nonnull
    BatchRequestBuilder batch();

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Customer>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<Customer> getAllCustomer();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Customer>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<Customer> countCustomer();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity using key fields.
     * 
     * @param id
     *     ID of the customer.<p>Constraints: Not nullable</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Customer>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<Customer> getCustomerByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity and save it to the S/4HANA system.
     * 
     * @param customer
     *     {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Customer>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<Customer> createCustomer(
        @Nonnull
        final Customer customer);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity and save it to the S/4HANA system.
     * 
     * @param customer
     *     {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Customer>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<Customer> updateCustomer(
        @Nonnull
        final Customer customer);

    /**
     * Deletes an existing {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity in the S/4HANA system.
     * 
     * @param customer
     *     {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Customer>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<Customer> deleteCustomer(
        @Nonnull
        final Customer customer);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Product>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<Product> getAllProduct();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Product>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<Product> countProduct();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity using key fields.
     * 
     * @param id
     *     ID of the product.<p>Constraints: Not nullable</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Product>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<Product> getProductByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity and save it to the S/4HANA system.
     * 
     * @param product
     *     {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Product>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<Product> createProduct(
        @Nonnull
        final Product product);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity and save it to the S/4HANA system.
     * 
     * @param product
     *     {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Product>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<Product> updateProduct(
        @Nonnull
        final Product product);

    /**
     * Deletes an existing {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity in the S/4HANA system.
     * 
     * @param product
     *     {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.Product Product} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Product>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<Product> deleteProduct(
        @Nonnull
        final Product product);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Receipt>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<Receipt> getAllReceipt();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Receipt>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<Receipt> countReceipt();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity using key fields.
     * 
     * @param id
     *     ID of the receipt.<p>Constraints: Not nullable</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Receipt>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<Receipt> getReceiptByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity and save it to the S/4HANA system.
     * 
     * @param receipt
     *     {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Receipt>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<Receipt> createReceipt(
        @Nonnull
        final Receipt receipt);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity and save it to the S/4HANA system.
     * 
     * @param receipt
     *     {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Receipt>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<Receipt> updateReceipt(
        @Nonnull
        final Receipt receipt);

    /**
     * Deletes an existing {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity in the S/4HANA system.
     * 
     * @param receipt
     *     {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Receipt>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<Receipt> deleteReceipt(
        @Nonnull
        final Receipt receipt);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Address>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<Address> getAllAddress();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Address>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<Address> countAddress();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity using key fields.
     * 
     * @param id
     *     ID of the address.<p>Constraints: Not nullable</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Address>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<Address> getAddressByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity and save it to the S/4HANA system.
     * 
     * @param address
     *     {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Address>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<Address> createAddress(
        @Nonnull
        final Address address);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity and save it to the S/4HANA system.
     * 
     * @param address
     *     {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Address>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<Address> updateAddress(
        @Nonnull
        final Address address);

    /**
     * Deletes an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity in the S/4HANA system.
     * 
     * @param address
     *     {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Address>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<Address> deleteAddress(
        @Nonnull
        final Address address);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Shelf>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<Shelf> getAllShelf();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Shelf>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<Shelf> countShelf();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity using key fields.
     * 
     * @param id
     *     <p>Constraints: Not nullable</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Shelf>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<Shelf> getShelfByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity and save it to the S/4HANA system.
     * 
     * @param shelf
     *     {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Shelf>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<Shelf> createShelf(
        @Nonnull
        final Shelf shelf);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity and save it to the S/4HANA system.
     * 
     * @param shelf
     *     {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Shelf>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<Shelf> updateShelf(
        @Nonnull
        final Shelf shelf);

    /**
     * Deletes an existing {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity in the S/4HANA system.
     * 
     * @param shelf
     *     {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.sdkgrocerystore.Shelf>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<Shelf> deleteShelf(
        @Nonnull
        final Shelf shelf);

    /**
     * Fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.sdkgrocerystore.OpeningHours>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<OpeningHours> getAllOpeningHours();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.sdkgrocerystore.OpeningHours>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<OpeningHours> countOpeningHours();

    /**
     * Fetch a single {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity using key fields.
     * 
     * @param id
     *     <p>Constraints: Not nullable</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.sdkgrocerystore.OpeningHours>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<OpeningHours> getOpeningHoursByKey(final Integer id);

    /**
     * Create a new {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity and save it to the S/4HANA system.
     * 
     * @param openingHours
     *     {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.OpeningHours>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<OpeningHours> createOpeningHours(
        @Nonnull
        final OpeningHours openingHours);

    /**
     * Update an existing {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity and save it to the S/4HANA system.
     * 
     * @param openingHours
     *     {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.sdkgrocerystore.OpeningHours>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<OpeningHours> updateOpeningHours(
        @Nonnull
        final OpeningHours openingHours);

    /**
     * Deletes an existing {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity in the S/4HANA system.
     * 
     * @param openingHours
     *     {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.sdkgrocerystore.OpeningHours>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<OpeningHours> deleteOpeningHours(
        @Nonnull
        final OpeningHours openingHours);

}
