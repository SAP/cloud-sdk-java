package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductCreateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Vendor;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

@WireMockTest
class FluentHelperCreateODataClientTest
{
    private static final String ODATA_ENDPOINT_URL = "/com.sap.cloud.sdk.store.grocery";
    private static final String ODATA_QUERY_URL = ODATA_ENDPOINT_URL + "/Products";

    private static final Product productToCreate =
        Product
            .builder()
            .name("Product")
            .price(new BigDecimal("19.99"))
            .vendor(Vendor.builder().name("SAP").build())
            .image(new byte[] { 0x01, 0x02, 0x03 })
            .build();

    private final SdkGroceryStoreService service = new DefaultSdkGroceryStoreService();
    private HttpClient httpClient;

    @BeforeEach
    void setupHttpClient( @Nonnull final WireMockRuntimeInfo wm )
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        httpClient = HttpClientAccessor.getHttpClient(destination);
    }

    @Test
    void testBasicProperties()
    {
        final ProductCreateFluentHelper createFluentHelper = service.createProduct(productToCreate);
        final ODataRequestCreate createRequest = createFluentHelper.toRequest();

        assertThat(createRequest.getServicePath()).isEqualTo(ODATA_ENDPOINT_URL);
        assertThat(createRequest.getRelativeUri().toString()).isEqualTo(ODATA_QUERY_URL);
    }

    @Test
    void testHeaders()
    {
        final String httpHeaderKey = "my-header";
        final String httpHeaderValue = "my-value";

        stubFor(head(anyUrl()).willReturn(serverError()));
        stubFor(
            post(urlEqualTo(ODATA_QUERY_URL))
                .withHeader(httpHeaderKey, WireMock.equalTo(httpHeaderValue))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .willReturn(WireMock.noContent()));

        final ProductCreateFluentHelper createFluentHelper = service.createProduct(productToCreate);
        createFluentHelper.withHeader(httpHeaderKey, httpHeaderValue).toRequest().execute(httpClient);

        verify(
            postRequestedFor(urlEqualTo(ODATA_QUERY_URL))
                .withHeader(httpHeaderKey, WireMock.equalTo(httpHeaderValue))
                .withHeader("Content-Type", WireMock.equalTo("application/json")));
    }

    @Test
    void testEntitySerialization()
    {
        final String request =
            "{"
                + "  \"Name\": \"Product\","
                + "  \"Price\": \"19.99\","
                + "  \"Vendor\": { \"Name\": \"SAP\" },"
                + "  \"Image\": \"AQID\""
                + "}";
        final String response =
            "{"
                + "  \"d\": {"
                + "    \"__metadata\": {"
                + "      \"id\": \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)\","
                + "      \"uri\": \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)\","
                + "      \"type\": \"SdkGroceryStore.ProductType\","
                + "      \"etag\": \"W/\\\"datetimeoffset'2023-04-18T10%3A55%3A00.5351290Z'\\\"\""
                + "    },"
                + "    \"Id\": \"44444\","
                + "    \"Name\": \"Product\","
                + "    \"Price\": \"19.99\","
                + "    \"VendorId\": 42,"
                + "    \"Image\": \"AQID\","
                + "    \"ShelfId\": null,"
                + "    \"Vendor\": {\"__deferred\": {\"uri\": \"https://127.0.0.1//com.sap.cloud.sdk.store.grocery/Products(44444)/Vendor\"}},"
                + "    \"Shelf\": {\"__deferred\": {\"uri\": \"https://127.0.0.1//com.sap.cloud.sdk.store.grocery/Products(44444)/Shelf\"}}"
                + "  }"
                + "}";

        stubFor(head(anyUrl()).willReturn(serverError()));
        stubFor(post(urlEqualTo(ODATA_QUERY_URL)).withRequestBody(equalToJson(request)).willReturn(okJson(response)));

        final ProductCreateFluentHelper createFluentHelper = service.createProduct(productToCreate);
        final ODataRequestResultGeneric result = createFluentHelper.toRequest().execute(httpClient);

        // assertions on deserialization
        final Product product = result.as(Product.class);
        assertThat(product).isNotNull();
        assertThat(product.getVersionIdentifier())
            .containsExactly("W/\"datetimeoffset'2023-04-18T10%3A55%3A00.5351290Z'\"");

        verify(postRequestedFor(urlEqualTo(ODATA_QUERY_URL)).withRequestBody(WireMock.equalToJson(request)));
    }
}
