/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.sample.services;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf;

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
public interface SdkGroceryStoreService
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
     * Creates a batch request builder object.
     *
     * @return A request builder to handle batch operation on this service. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder#execute(Destination) execute} method
     *         on the request builder object.
     */
    @Nonnull
    BatchRequestBuilder batch();

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Customer> getAllCustomers();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Customer> countCustomers();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer}
     * entity using key fields.
     *
     * @param id
     *            ID of the customer.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     *         using key fields. This request builder allows methods which modify the underlying query to be called
     *         before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Customer> getCustomersByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer}
     * entity and save it to the S/4HANA system.
     *
     * @param customer
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Customer> createCustomers( @Nonnull final Customer customer );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer
     * Customer} entity and save it to the S/4HANA system.
     *
     * @param customer
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Customer> updateCustomers( @Nonnull final Customer customer );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer
     * Customer} entity in the S/4HANA system.
     *
     * @param customer
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer Customer} entity.
     *         To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Customer> deleteCustomers( @Nonnull final Customer customer );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Product> getAllProducts();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Product> countProducts();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product}
     * entity using key fields.
     *
     * @param id
     *            ID of the product.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity
     *         using key fields. This request builder allows methods which modify the underlying query to be called
     *         before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Product> getProductsByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity
     * and save it to the S/4HANA system.
     *
     * @param product
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Product> createProducts( @Nonnull final Product product );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product}
     * entity and save it to the S/4HANA system.
     *
     * @param product
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Product> updateProducts( @Nonnull final Product product );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product}
     * entity in the S/4HANA system.
     *
     * @param product
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product Product} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Product> deleteProducts( @Nonnull final Product product );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Receipt> getAllReceipts();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Receipt> countReceipts();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt}
     * entity using key fields.
     *
     * @param id
     *            ID of the receipt.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     *         using key fields. This request builder allows methods which modify the underlying query to be called
     *         before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Receipt> getReceiptsByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     * and save it to the S/4HANA system.
     *
     * @param receipt
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Receipt> createReceipts( @Nonnull final Receipt receipt );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt}
     * entity and save it to the S/4HANA system.
     *
     * @param receipt
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Receipt> updateReceipts( @Nonnull final Receipt receipt );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt}
     * entity in the S/4HANA system.
     *
     * @param receipt
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt Receipt} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Receipt> deleteReceipts( @Nonnull final Receipt receipt );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Address> getAllAddresses();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entities.
     *         This request builder allows methods which modify the underlying query to be called before executing the
     *         query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Address> countAddresses();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address}
     * entity using key fields.
     *
     * @param id
     *            ID of the address.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity
     *         using key fields. This request builder allows methods which modify the underlying query to be called
     *         before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Address> getAddressesByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity
     * and save it to the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Address> createAddresses( @Nonnull final Address address );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address}
     * entity and save it to the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Address> updateAddresses( @Nonnull final Address address );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address}
     * entity in the S/4HANA system.
     *
     * @param address
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address Address} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Address>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Address> deleteAddresses( @Nonnull final Address address );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entities. This
     *         request builder allows methods which modify the underlying query to be called before executing the query
     *         itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Shelf> getAllShelves();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entities. This
     *         request builder allows methods which modify the underlying query to be called before executing the query
     *         itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Shelf> countShelves();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     * using key fields.
     *
     * @param id
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity using
     *         key fields. This request builder allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Shelf> getShelvesByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity and
     * save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Shelf> createShelves( @Nonnull final Shelf shelf );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entity and save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Shelf> updateShelves( @Nonnull final Shelf shelf );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entity in the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Shelf> deleteShelves( @Nonnull final Shelf shelf );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entities. This
     *         request builder allows methods which modify the underlying query to be called before executing the query
     *         itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Shelf> getAllShopFloorShelves();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entities. This
     *         request builder allows methods which modify the underlying query to be called before executing the query
     *         itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Shelf> countShopFloorShelves();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     * using key fields.
     *
     * @param id
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity using
     *         key fields. This request builder allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Shelf> getShopFloorShelvesByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity and
     * save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Shelf> createShopFloorShelves( @Nonnull final Shelf shelf );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entity and save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Shelf> updateShopFloorShelves( @Nonnull final Shelf shelf );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entity in the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Shelf> deleteShopFloorShelves( @Nonnull final Shelf shelf );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entities. This
     *         request builder allows methods which modify the underlying query to be called before executing the query
     *         itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<Shelf> getAllStorageShelves();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity collection
     * matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entities. This
     *         request builder allows methods which modify the underlying query to be called before executing the query
     *         itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<Shelf> countStorageShelves();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     * using key fields.
     *
     * @param id
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity using
     *         key fields. This request builder allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<Shelf> getStorageShelvesByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity and
     * save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<Shelf> createStorageShelves( @Nonnull final Shelf shelf );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entity and save it to the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<Shelf> updateStorageShelves( @Nonnull final Shelf shelf );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf}
     * entity in the S/4HANA system.
     *
     * @param shelf
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity
     *            object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf Shelf} entity. To
     *         perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<Shelf> deleteStorageShelves( @Nonnull final Shelf shelf );

    /**
     * Fetch multiple {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entities.
     *
     * @return A request builder to fetch multiple
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entities. This request builder allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<OpeningHours> getAllOpeningHours();

    /**
     * Fetch the number of entries from the
     * {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entity
     * collection matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entities. This request builder allows methods which modify the underlying query to be called before
     *         executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<OpeningHours> countOpeningHours();

    /**
     * Fetch a single {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entity using key fields.
     *
     * @param id
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to fetch a single
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entity using key fields. This request builder allows methods which modify the underlying query to be
     *         called before executing the query itself. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<OpeningHours> getOpeningHoursByKey( final Integer id );

    /**
     * Create a new {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entity and save it to the S/4HANA system.
     *
     * @param openingHours
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours
     *            OpeningHours} entity object that will be created in the S/4HANA system.
     * @return A request builder to create a new
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entity. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<OpeningHours> createOpeningHours( @Nonnull final OpeningHours openingHours );

    /**
     * Update an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entity and save it to the S/4HANA system.
     *
     * @param openingHours
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours
     *            OpeningHours} entity object that will be updated in the S/4HANA system.
     * @return A request builder to update an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entity. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<OpeningHours> updateOpeningHours( @Nonnull final OpeningHours openingHours );

    /**
     * Deletes an existing {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours
     * OpeningHours} entity in the S/4HANA system.
     *
     * @param openingHours
     *            {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours
     *            OpeningHours} entity object that will be deleted in the S/4HANA system.
     * @return A request builder to delete an existing
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}
     *         entity. To perform execution, call the
     *         {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.OpeningHours>#execute
     *         execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<OpeningHours> deleteOpeningHours( @Nonnull final OpeningHours openingHours );

}
