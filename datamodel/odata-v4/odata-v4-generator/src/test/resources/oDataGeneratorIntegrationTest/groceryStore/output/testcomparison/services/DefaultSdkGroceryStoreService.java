/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

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
import lombok.Getter;
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
public class DefaultSdkGroceryStoreService
    implements ServiceWithNavigableEntities, SdkGroceryStoreService
{

    @Nonnull
    @Getter
    private final String servicePath;

    /**
     * Creates a service using {@link SdkGroceryStoreService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultSdkGroceryStoreService() {
        servicePath = SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultSdkGroceryStoreService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultSdkGroceryStoreService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultSdkGroceryStoreService(servicePath);
    }

    @Override
    @Nonnull
    public BatchRequestBuilder batch() {
        return new BatchRequestBuilder(servicePath);
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Customer> getAllCustomer() {
        return new GetAllRequestBuilder<Customer>(servicePath, Customer.class, "Customers");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Customer> countCustomer() {
        return new CountRequestBuilder<Customer>(servicePath, Customer.class, "Customers");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Customer> getCustomerByKey(final Integer id) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Customer>(servicePath, Customer.class, key, "Customers");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Customer> createCustomer(
        @Nonnull
        final Customer customer) {
        return new CreateRequestBuilder<Customer>(servicePath, customer, "Customers");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Customer> updateCustomer(
        @Nonnull
        final Customer customer) {
        return new UpdateRequestBuilder<Customer>(servicePath, customer, "Customers");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Customer> deleteCustomer(
        @Nonnull
        final Customer customer) {
        return new DeleteRequestBuilder<Customer>(servicePath, customer, "Customers");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Product> getAllProduct() {
        return new GetAllRequestBuilder<Product>(servicePath, Product.class, "Products");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Product> countProduct() {
        return new CountRequestBuilder<Product>(servicePath, Product.class, "Products");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Product> getProductByKey(final Integer id) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Product>(servicePath, Product.class, key, "Products");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Product> createProduct(
        @Nonnull
        final Product product) {
        return new CreateRequestBuilder<Product>(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Product> updateProduct(
        @Nonnull
        final Product product) {
        return new UpdateRequestBuilder<Product>(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Product> deleteProduct(
        @Nonnull
        final Product product) {
        return new DeleteRequestBuilder<Product>(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Receipt> getAllReceipt() {
        return new GetAllRequestBuilder<Receipt>(servicePath, Receipt.class, "Receipts");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Receipt> countReceipt() {
        return new CountRequestBuilder<Receipt>(servicePath, Receipt.class, "Receipts");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Receipt> getReceiptByKey(final Integer id) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Receipt>(servicePath, Receipt.class, key, "Receipts");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Receipt> createReceipt(
        @Nonnull
        final Receipt receipt) {
        return new CreateRequestBuilder<Receipt>(servicePath, receipt, "Receipts");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Receipt> updateReceipt(
        @Nonnull
        final Receipt receipt) {
        return new UpdateRequestBuilder<Receipt>(servicePath, receipt, "Receipts");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Receipt> deleteReceipt(
        @Nonnull
        final Receipt receipt) {
        return new DeleteRequestBuilder<Receipt>(servicePath, receipt, "Receipts");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Address> getAllAddress() {
        return new GetAllRequestBuilder<Address>(servicePath, Address.class, "Addresses");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Address> countAddress() {
        return new CountRequestBuilder<Address>(servicePath, Address.class, "Addresses");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Address> getAddressByKey(final Integer id) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Address>(servicePath, Address.class, key, "Addresses");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Address> createAddress(
        @Nonnull
        final Address address) {
        return new CreateRequestBuilder<Address>(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Address> updateAddress(
        @Nonnull
        final Address address) {
        return new UpdateRequestBuilder<Address>(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Address> deleteAddress(
        @Nonnull
        final Address address) {
        return new DeleteRequestBuilder<Address>(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<Shelf> getAllShelf() {
        return new GetAllRequestBuilder<Shelf>(servicePath, Shelf.class, "Shelves");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<Shelf> countShelf() {
        return new CountRequestBuilder<Shelf>(servicePath, Shelf.class, "Shelves");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<Shelf> getShelfByKey(final Integer id) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<Shelf>(servicePath, Shelf.class, key, "Shelves");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<Shelf> createShelf(
        @Nonnull
        final Shelf shelf) {
        return new CreateRequestBuilder<Shelf>(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<Shelf> updateShelf(
        @Nonnull
        final Shelf shelf) {
        return new UpdateRequestBuilder<Shelf>(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<Shelf> deleteShelf(
        @Nonnull
        final Shelf shelf) {
        return new DeleteRequestBuilder<Shelf>(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<OpeningHours> getAllOpeningHours() {
        return new GetAllRequestBuilder<OpeningHours>(servicePath, OpeningHours.class, "OpeningHours");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<OpeningHours> countOpeningHours() {
        return new CountRequestBuilder<OpeningHours>(servicePath, OpeningHours.class, "OpeningHours");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<OpeningHours> getOpeningHoursByKey(final Integer id) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Id", id);
        return new GetByKeyRequestBuilder<OpeningHours>(servicePath, OpeningHours.class, key, "OpeningHours");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<OpeningHours> createOpeningHours(
        @Nonnull
        final OpeningHours openingHours) {
        return new CreateRequestBuilder<OpeningHours>(servicePath, openingHours, "OpeningHours");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<OpeningHours> updateOpeningHours(
        @Nonnull
        final OpeningHours openingHours) {
        return new UpdateRequestBuilder<OpeningHours>(servicePath, openingHours, "OpeningHours");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<OpeningHours> deleteOpeningHours(
        @Nonnull
        final OpeningHours openingHours) {
        return new DeleteRequestBuilder<OpeningHours>(servicePath, openingHours, "OpeningHours");
    }

}
