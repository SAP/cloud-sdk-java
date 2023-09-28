package com.sap.cloud.sdk.datamodel.odatav4.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestUpdate;
import com.sap.cloud.sdk.datamodel.odata.client.request.UpdateStrategy;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.ProductCategory;
import com.sap.cloud.sdk.datamodel.odatav4.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odatav4.sample.services.SdkGroceryStoreService;

public class UpdateEntityTest
{
    private static final String NEW_NAME = "new name";
    private static final BigDecimal NEW_PRICE = BigDecimal.valueOf(42.42d);

    private static final SdkGroceryStoreService service = new DefaultSdkGroceryStoreService();

    @Rule
    public final WireMockRule server = new WireMockRule(wireMockConfig().dynamicPort());

    private HttpDestination destination;
    private HttpClient client;

    @Before
    public void setup()
    {
        destination = DefaultHttpDestination.builder(server.baseUrl()).build();
        client = new DefaultHttpClientFactory().createHttpClient(destination);

        server.stubFor(head(anyUrl()).willReturn(ok().withHeader("x-csrf-token", "required")));
        server.stubFor(put(anyUrl()).willReturn(ok()));
        server.stubFor(patch(anyUrl()).willReturn(ok()));
    }

    @Test
    public void testPatchEntity()
    {
        final Product product = createModifiedProduct();

        final ODataRequestUpdate request = service.updateProducts(product).toRequest();
        assertThat(request.getUpdateStrategy()).isEqualTo(UpdateStrategy.MODIFY_WITH_PATCH);

        final String serializedEntity = request.getSerializedEntity();
        assertContainsNewName(serializedEntity);
        assertContainsNewPrice(serializedEntity);

        request.execute(client);

        final String expected =
            "{\"@odata.type\":\"#com.sap.cloud.sdk.store.grocery.Product\",\"Name\":\"new name\",\"Price\":42.42}";
        verify(
            patchRequestedFor(urlEqualTo("/com.sap.cloud.sdk.store.grocery/Products(42)"))
                .withRequestBody(equalToJson(expected)));
    }

    @Test
    public void testExcludedFieldsInPatchRequestAreStillSent()
    {
        final Product product = createModifiedProduct();

        final ODataRequestUpdate request = service.updateProducts(product).excludingFields(Product.PRICE).toRequest();
        assertThat(request.getUpdateStrategy()).isEqualTo(UpdateStrategy.MODIFY_WITH_PATCH);

        final String serializedEntity = request.getSerializedEntity();
        assertContainsNewName(serializedEntity);
        assertContainsNewPrice(serializedEntity);

        request.execute(client);

        final String expected =
            "{\"@odata.type\":\"#com.sap.cloud.sdk.store.grocery.Product\",\"Name\":\"new name\",\"Price\":42.42}";
        verify(
            patchRequestedFor(urlEqualTo("/com.sap.cloud.sdk.store.grocery/Products(42)"))
                .withRequestBody(equalToJson(expected)));
    }

    @Test
    public void testPatchEntityWithIncludedFields()
    {
        final Product product = createModifiedProduct();

        final ODataRequestUpdate request =
            service.updateProducts(product).includingFields(Product.CATEGORIES).toRequest();
        assertThat(request.getUpdateStrategy()).isEqualTo(UpdateStrategy.MODIFY_WITH_PATCH);

        final String serializedEntity = request.getSerializedEntity();
        assertContainsNewName(serializedEntity);
        assertContainsNewPrice(serializedEntity);
        assertContainsCategories(serializedEntity);

        request.execute(client);

        final String expected =
            "{\"@odata.type\":\"#com.sap.cloud.sdk.store.grocery.Product\",\"Name\":\"new name\",\"Price\":42.42,\"Categories\":[\"Dairy\",\"Beverages\"]}";
        verify(
            patchRequestedFor(urlEqualTo("/com.sap.cloud.sdk.store.grocery/Products(42)"))
                .withRequestBody(equalToJson(expected)));
    }

    @Test
    public void testPutEntity()
    {
        final Product product = createModifiedProduct();

        final ODataRequestUpdate request = service.updateProducts(product).replacingEntity().toRequest();
        assertThat(request.getUpdateStrategy()).isEqualTo(UpdateStrategy.REPLACE_WITH_PUT);

        final String serializedEntity = request.getSerializedEntity();
        assertContainsId(serializedEntity);
        assertContainsNewName(serializedEntity);
        assertContainsNewPrice(serializedEntity);
        assertContainsCategories(serializedEntity);

        request.execute(client);

        final String expected =
            "{\"@odata.type\":\"#com.sap.cloud.sdk.store.grocery.Product\",\"Id\":42,\"Name\":\"new name\",\"Price\":42.42,\"Categories\":[\"Dairy\",\"Beverages\"]}";
        verify(
            putRequestedFor(urlEqualTo("/com.sap.cloud.sdk.store.grocery/Products(42)"))
                .withRequestBody(equalToJson(expected)));
    }

    @Test
    public void testPutEntityWithExcludedFields()
    {
        final Product product = createModifiedProduct();

        final ODataRequestUpdate request =
            service
                .updateProducts(product)
                .replacingEntity()
                .excludingFields(Product.PRICE, Product.CATEGORIES)
                .toRequest();
        assertThat(request.getUpdateStrategy()).isEqualTo(UpdateStrategy.REPLACE_WITH_PUT);

        final String serializedEntity = request.getSerializedEntity();
        assertContainsId(serializedEntity);
        assertContainsNewName(serializedEntity);

        request.execute(client);

        final String expected =
            "{\"@odata.type\":\"#com.sap.cloud.sdk.store.grocery.Product\",\"Id\":42,\"Name\":\"new name\"}";
        verify(
            putRequestedFor(urlEqualTo("/com.sap.cloud.sdk.store.grocery/Products(42)"))
                .withRequestBody(equalToJson(expected)));
    }

    @Test
    public void testIncludedFieldsAreStillExcludedInPutRequest()
    {
        final Product product = createModifiedProduct();

        final ODataRequestUpdate request =
            service
                .updateProducts(product)
                .replacingEntity()
                .excludingFields(Product.PRICE, Product.CATEGORIES)
                .includingFields(Product.PRICE)
                .toRequest();
        assertThat(request.getUpdateStrategy()).isEqualTo(UpdateStrategy.REPLACE_WITH_PUT);

        final String serializedEntity = request.getSerializedEntity();
        assertContainsId(serializedEntity);
        assertContainsNewName(serializedEntity);

        request.execute(client);

        final String expected =
            "{\"@odata.type\":\"#com.sap.cloud.sdk.store.grocery.Product\",\"Id\":42,\"Name\":\"new name\"}";
        verify(
            putRequestedFor(urlEqualTo("/com.sap.cloud.sdk.store.grocery/Products(42)"))
                .withRequestBody(equalToJson(expected)));
    }

    private static Product createModifiedProduct()
    {
        final Product product =
            Product
                .builder()
                .id(42)
                .name("old name")
                .price(BigDecimal.valueOf(13.37d))
                .categories(Arrays.asList(ProductCategory.DAIRY, ProductCategory.BEVERAGES))
                .build();

        assertThat(product.getChangedFields()).isEmpty();

        product.setName(NEW_NAME);
        product.setPrice(NEW_PRICE);
        assertThat(product.getChangedFields()).containsOnlyKeys("Name", "Price");

        return product;
    }

    private static void assertContainsId( @Nonnull final String serializedEntity )
    {
        assertThat(serializedEntity).contains("\"Id\":42");
    }

    private static void assertContainsNewName( @Nonnull final String serializedEntity )
    {
        assertThat(serializedEntity).contains("\"Name\":\"" + NEW_NAME + "\"");
    }

    private static void assertContainsNewPrice( @Nonnull final String serializedEntity )
    {
        assertThat(serializedEntity).contains("\"Price\":" + NEW_PRICE);
    }

    private static void assertContainsCategories( @Nonnull final String serializedEntity )
    {
        assertThat(serializedEntity)
            .contains(
                "\"Categories\":[\""
                    + ProductCategory.DAIRY.getName()
                    + "\",\""
                    + ProductCategory.BEVERAGES.getName()
                    + "\"]");
    }
}
