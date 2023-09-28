package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;

import io.vavr.control.Option;

public class FluentHelperReadTest
{
    @Rule
    public final WireMockRule erpServer = new WireMockRule(wireMockConfig().dynamicPort());

    private static final String ODATA_ENDPOINT_URL = "/endpoint/url";
    private static final String ODATA_QUERY_URL = ODATA_ENDPOINT_URL + "/Products";
    private static final String PRODUCT_RESPONSE_BODY =
        "{"
            + "  \"d\": {"
            + "    \"results\": [{"
            + "    \"__metadata\": {"
            + "      \"id\": \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)\","
            + "      \"uri\": \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)\","
            + "      \"type\": \"SdkGroceryStore.ProductType\","
            + "      \"etag\": \"W/\\\"datetimeoffset'2018-04-25T09%3A51%3A49.1719080Z'\\\"\""
            + "    },"
            + "    \"Id\": \"44444\","
            + "    \"Name\": \"Product\","
            + "    \"Price\": \"19.99\","
            + "    \"VendorId\": 42,"
            + "    \"Image\": \"AQID\","
            + "    \"ShelfId\": null,"
            + "    \"Vendor\": {\"__deferred\": {\"uri\": \"https://127.0.0.1//com.sap.cloud.sdk.store.grocery/Products(44444)/Vendor\"}},"
            + "    \"Shelf\": {\"__deferred\": {\"uri\": \"https://127.0.0.1//com.sap.cloud.sdk.store.grocery/Products(44444)/Shelf\"}}"
            + "  }]}"
            + "}";

    private DefaultHttpDestination destination;

    @Before
    public void before()
    {
        destination = DefaultHttpDestination.builder(erpServer.baseUrl()).build();
    }

    @Test
    public void testExecuteReturnsModifiableListWithETags()
    {
        stubFor(get(urlEqualTo(ODATA_QUERY_URL)).willReturn(aResponse().withBody(PRODUCT_RESPONSE_BODY)));

        final List<Product> returnedList =
            new DefaultSdkGroceryStoreService()
                .withServicePath(ODATA_ENDPOINT_URL)
                .getAllProduct()
                .executeRequest(destination);

        assertThat(returnedList)
            .extracting(Product::getVersionIdentifier)
            .extracting(Option::getOrNull)
            .containsOnly("W/\"datetimeoffset'2018-04-25T09%3A51%3A49.1719080Z'\"");

        assertListIsModifiable(returnedList);
    }

    private static void assertListIsModifiable( final List<Product> listToCheck )
    {
        final int returnedSize = listToCheck.size();
        final Product someProduct = new Product();

        listToCheck.add(someProduct);

        assertThat(listToCheck).hasSize(returnedSize + 1).contains(someProduct, atIndex(returnedSize));
    }
}
