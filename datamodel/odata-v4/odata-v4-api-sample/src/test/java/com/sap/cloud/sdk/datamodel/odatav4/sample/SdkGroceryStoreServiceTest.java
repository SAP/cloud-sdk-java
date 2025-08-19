package com.sap.cloud.sdk.datamodel.odatav4.sample;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odatav4.core.ActionResponseCollection;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.ModificationResponse;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Customer;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.DateRange;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.ProductCategory;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.ProductCount;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.PurchaseHistoryItem;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Receipt;
import com.sap.cloud.sdk.datamodel.odatav4.sample.services.DefaultSdkGroceryStoreService;

class SdkGroceryStoreServiceTest
{
    // HTTP status codes
    private static final int HTTP_OK = 200;
    private static final int HTTP_NO_CONTENT = 204;

    @RegisterExtension
    static final WireMockExtension SERVER =
        WireMockExtension
            .newInstance()
            .options(options().usingFilesUnderClasspath(SdkGroceryStoreServiceTest.class.getSimpleName()).dynamicPort())
            .build();

    private final DefaultSdkGroceryStoreService service = new DefaultSdkGroceryStoreService();

    private HttpDestination destination;

    @BeforeEach
    void setup()
    {
        destination = DefaultHttpDestination.builder(SERVER.baseUrl()).build();
    }

    @Test
    void testGetAllCustomers()
    {
        final List<Customer> customerList = service.getAllCustomers().execute(destination);
        assertThat(customerList).isNotNull().hasSize(3);
    }

    @Test
    void testGetCustomerByKey()
    {
        final Customer customer = service.getCustomersByKey(9001).execute(destination);

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(9001);
        assertThat(customer.getName()).isEqualTo("Customer Name 1");
        assertThat(customer.getEmail()).isEqualTo("customer1@mail.sap");
        assertThat(customer.getAddressId()).isEqualTo(100);
    }

    @Test
    void testGetAllReceipts()
    {
        final List<Receipt> receipts = service.getAllReceipts().top(10).execute(destination);
        assertThat(receipts).isNotNull().hasSize(4);
    }

    @Test
    void testGetReceiptByKey()
    {
        final Receipt item = service.getReceiptsByKey(123456).execute(destination);
        assertThat(item).isNotNull();
        assertThat(item.getCustomerId()).isEqualTo(9001);
        assertThat(item.getTotalAmount()).isEqualTo(new BigDecimal("100"));
    }

    @Test
    void testGetReceiptWithQuery()
    {
        final int limit = 1;
        final GetAllRequestBuilder<Receipt> requestBuilder =
            service
                .getAllReceipts()
                .select(Receipt.ID, Receipt.TO_CUSTOMER.select(Customer.NAME, Customer.ID), Receipt.PRODUCT_COUNTS)
                .filter(Receipt.TOTAL_AMOUNT.greaterThanEqual(400))
                .top(limit);

        final List<Receipt> resultList = requestBuilder.execute(destination);

        assertThat(resultList).isNotNull().hasSize(limit);
        assertThat(resultList).allMatch(item -> item.getId() != null);
        assertThat(resultList).allMatch(item -> item.getProductCounts() != null);
        assertThat(resultList).allMatch(item -> item.getCustomerIfPresent().get().getName() != null);
    }

    @Test
    void testFilterPurchaseHistory()
    {
        final DateRange dateRange =
            DateRange
                .builder()
                .start(OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .end(OffsetDateTime.of(2023, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .build();

        final Customer customer = Customer.builder().id(1337).build();
        customer.setVersionIdentifier("123");

        final ActionResponseCollection<PurchaseHistoryItem> historyItems =
            service
                .forEntity(customer)
                .applyAction(
                    Customer
                        .filterPurchaseHistory(
                            Collections.singleton(Receipt.builder().customerId(1337).id(4242).build()),
                            Arrays.asList("milk", "eggs"),
                            Arrays.asList(ProductCategory.DAIRY, ProductCategory.MEAT),
                            dateRange))
                .execute(destination);

        assertThat(historyItems.getResponseStatusCode()).isEqualTo(HTTP_OK);
        assertThat(historyItems.getResponseResult()).isNotEmpty();
        assertThat(historyItems.getResponseResult().get()).satisfiesExactly(item -> {
            assertThat(item.getReceiptId()).isEqualTo(123456);
            assertThat(item.getProductCount()).isNotNull();
            assertThat(item.getProductCount().getProductId()).isEqualTo(1001);
            assertThat(item.getProductCount().getQuantity()).isEqualTo(10);
        });
    }

    @Test
    void testCudOperations()
    {
        final List<ProductCount> products =
            Arrays
                .asList(
                    ProductCount.builder().productId(1).quantity(1).build(),
                    ProductCount.builder().productId(2).quantity(2).build());

        Receipt item = Receipt.builder().customerId(9001).productCounts(products).build();

        // Create a new receipt item
        item = service.createReceipts(item).execute(destination).getModifiedEntity();
        assertThat(item.getCustomerId()).isEqualTo(9001);
        assertThat(item.getTotalAmount()).isEqualTo(new BigDecimal("100"));

        // Modify it with a PATCH update to 90 total amount value
        item.setTotalAmount(new BigDecimal("90.00"));
        ModificationResponse<Receipt> response = service.updateReceipts(item).modifyingEntity().execute(destination);
        assertThat(response.getResponseStatusCode()).isEqualTo(HTTP_NO_CONTENT);

        // Modify it with a PUT update back to original total amount value
        item.setTotalAmount(new BigDecimal("100.00"));
        response = service.updateReceipts(item).replacingEntity().execute(destination);
        assertThat(response).isNotNull();
        assertThat(response.getResponseStatusCode()).isEqualTo(HTTP_NO_CONTENT);

        // Delete the thing again
        response = service.deleteReceipts(item).execute(destination);
        assertThat(response).isNotNull();
        assertThat(response.getResponseStatusCode()).isEqualTo(HTTP_NO_CONTENT);
    }
}
