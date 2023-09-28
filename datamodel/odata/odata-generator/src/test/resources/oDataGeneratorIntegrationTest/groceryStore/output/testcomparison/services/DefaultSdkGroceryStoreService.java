package testcomparison.services;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import testcomparison.namespaces.sdkgrocerystore.batch.DefaultSdkGroceryStoreServiceBatch;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>SDK_Grocery_Store</td></tr></table>
 *
 */
public class DefaultSdkGroceryStoreService
    implements SdkGroceryStoreService
{

    @Nonnull
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
    public DefaultSdkGroceryStoreServiceBatch batch() {
        return new DefaultSdkGroceryStoreServiceBatch(this, servicePath);
    }

    @Override
    @Nonnull
    public CustomerFluentHelper getAllCustomer() {
        return new CustomerFluentHelper(servicePath, "Customers");
    }

    @Override
    @Nonnull
    public CustomerByKeyFluentHelper getCustomerByKey(final Integer id) {
        return new CustomerByKeyFluentHelper(servicePath, "Customers", id);
    }

    @Override
    @Nonnull
    public CustomerCreateFluentHelper createCustomer(
        @Nonnull
        final Customer customer) {
        return new CustomerCreateFluentHelper(servicePath, customer, "Customers");
    }

    @Override
    @Nonnull
    public ProductFluentHelper getAllProduct() {
        return new ProductFluentHelper(servicePath, "Products");
    }

    @Override
    @Nonnull
    public ProductByKeyFluentHelper getProductByKey(final Integer id) {
        return new ProductByKeyFluentHelper(servicePath, "Products", id);
    }

    @Override
    @Nonnull
    public ProductCreateFluentHelper createProduct(
        @Nonnull
        final Product product) {
        return new ProductCreateFluentHelper(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public ProductUpdateFluentHelper updateProduct(
        @Nonnull
        final Product product) {
        return new ProductUpdateFluentHelper(servicePath, product, "Products");
    }

    @Override
    @Nonnull
    public ReceiptFluentHelper getAllReceipt() {
        return new ReceiptFluentHelper(servicePath, "Receipts");
    }

    @Override
    @Nonnull
    public ReceiptByKeyFluentHelper getReceiptByKey(final Integer id) {
        return new ReceiptByKeyFluentHelper(servicePath, "Receipts", id);
    }

    @Override
    @Nonnull
    public ReceiptCreateFluentHelper createReceipt(
        @Nonnull
        final Receipt receipt) {
        return new ReceiptCreateFluentHelper(servicePath, receipt, "Receipts");
    }

    @Override
    @Nonnull
    public AddressFluentHelper getAllAddress() {
        return new AddressFluentHelper(servicePath, "Addresses");
    }

    @Override
    @Nonnull
    public AddressByKeyFluentHelper getAddressByKey(final Integer id) {
        return new AddressByKeyFluentHelper(servicePath, "Addresses", id);
    }

    @Override
    @Nonnull
    public AddressCreateFluentHelper createAddress(
        @Nonnull
        final Address address) {
        return new AddressCreateFluentHelper(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public AddressUpdateFluentHelper updateAddress(
        @Nonnull
        final Address address) {
        return new AddressUpdateFluentHelper(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public AddressDeleteFluentHelper deleteAddress(
        @Nonnull
        final Address address) {
        return new AddressDeleteFluentHelper(servicePath, address, "Addresses");
    }

    @Override
    @Nonnull
    public ShelfFluentHelper getAllShelf() {
        return new ShelfFluentHelper(servicePath, "Shelves");
    }

    @Override
    @Nonnull
    public ShelfByKeyFluentHelper getShelfByKey(final Integer id) {
        return new ShelfByKeyFluentHelper(servicePath, "Shelves", id);
    }

    @Override
    @Nonnull
    public ShelfCreateFluentHelper createShelf(
        @Nonnull
        final Shelf shelf) {
        return new ShelfCreateFluentHelper(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public ShelfUpdateFluentHelper updateShelf(
        @Nonnull
        final Shelf shelf) {
        return new ShelfUpdateFluentHelper(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public ShelfDeleteFluentHelper deleteShelf(
        @Nonnull
        final Shelf shelf) {
        return new ShelfDeleteFluentHelper(servicePath, shelf, "Shelves");
    }

    @Override
    @Nonnull
    public OpeningHoursFluentHelper getAllOpeningHours() {
        return new OpeningHoursFluentHelper(servicePath, "OpeningHours");
    }

    @Override
    @Nonnull
    public OpeningHoursByKeyFluentHelper getOpeningHoursByKey(final Integer id) {
        return new OpeningHoursByKeyFluentHelper(servicePath, "OpeningHours", id);
    }

    @Override
    @Nonnull
    public OpeningHoursUpdateFluentHelper updateOpeningHours(
        @Nonnull
        final OpeningHours openingHours) {
        return new OpeningHoursUpdateFluentHelper(servicePath, openingHours, "OpeningHours");
    }

    @Override
    @Nonnull
    public VendorFluentHelper getAllVendor() {
        return new VendorFluentHelper(servicePath, "Vendors");
    }

    @Override
    @Nonnull
    public VendorByKeyFluentHelper getVendorByKey(final Integer id) {
        return new VendorByKeyFluentHelper(servicePath, "Vendors", id);
    }

    @Override
    @Nonnull
    public FloorPlanFluentHelper getAllFloorPlan() {
        return new FloorPlanFluentHelper(servicePath, "Floors");
    }

    @Override
    @Nonnull
    public FloorPlanByKeyFluentHelper getFloorPlanByKey(final Integer id) {
        return new FloorPlanByKeyFluentHelper(servicePath, "Floors", id);
    }

    @Override
    @Nonnull
    public PrintReceiptFluentHelper printReceipt(
        @Nonnull
        final Integer receiptId) {
        return new PrintReceiptFluentHelper(servicePath, receiptId);
    }

    @Override
    @Nonnull
    public RevokeReceiptFluentHelper revokeReceipt(
        @Nonnull
        final Integer receiptId) {
        return new RevokeReceiptFluentHelper(servicePath, receiptId);
    }

    @Override
    @Nonnull
    public IsStoreOpenFluentHelper isStoreOpen(
        @Nonnull
        final LocalDateTime dateTime) {
        return new IsStoreOpenFluentHelper(servicePath, dateTime);
    }

    @Override
    @Nonnull
    public OrderProductFluentHelper orderProduct(
        @Nonnull
        final Integer customerId,
        @Nonnull
        final Integer productId,
        @Nonnull
        final Integer quantity) {
        return new OrderProductFluentHelper(servicePath, customerId, productId, quantity);
    }

    @Override
    @Nonnull
    public GetProductQuantitiesFluentHelper getProductQuantities(
        @Nullable
        final Integer shelfId,
        @Nonnull
        final Integer productId) {
        return new GetProductQuantitiesFluentHelper(servicePath, shelfId, productId);
    }

}
