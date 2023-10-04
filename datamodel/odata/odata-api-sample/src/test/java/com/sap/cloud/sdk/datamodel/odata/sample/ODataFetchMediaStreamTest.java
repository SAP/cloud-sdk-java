/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RunWith( Parameterized.class )
@RequiredArgsConstructor
public class ODataFetchMediaStreamTest
{
    private static final String SRV_PATH = SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    private static final SdkGroceryStoreService SRV = new DefaultSdkGroceryStoreService().withServicePath(SRV_PATH);

    private static final String JSON_RESPONSE =
        "{"
            + "\"d\" : {"
            + "  \"results\" : ["
            + "    {"
            + "      \"__metadata\" : {"
            + "        \"id\": \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)\"," // ignored by the SDK
            + "        \"uri\": \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)\"," // ignored by the SDK
            + "        \"type\": \"SdkGroceryStore.ProductType\","
            + "        \"etag\": \"W/\\\"datetimeoffset'2023-04-18T10%3A55%3A00.5351290Z'\\\"\","
            + "        \"content_type\" : \"text/plain\"," // ignored by the SDK
            + "        \"media_src\" : \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)/$value\"" // ignored by the SDK
            + "      },"
            + "      \"Id\" : 44444,"
            + "      \"Name\" : \"Cloud SDK\","
            + "      \"ShelfId\" : 300,"
            + "      \"VendorId\" : 500,"
            + "      \"Price\" : 10200,"
            + "      \"Image\" : \"REDACTED\","
            + "      \"Vendor\": {\"__deferred\": {\"uri\": \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)/Vendor\"}},"
            + "      \"Shelf\": {\"__deferred\": {\"uri\": \"https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)/Shelf\"}}"
            + "    }"
            + "  ]"
            + "}"
            + "}";

    @Rule
    public final WireMockRule erpServer = new WireMockRule(wireMockConfig().dynamicPort());

    private DefaultHttpDestination destination;

    @Before
    public void before()
    {
        destination = DefaultHttpDestination.builder(erpServer.baseUrl()).build();

        stubFor(get(urlPathMatching(SRV_PATH + "/Products")).willReturn(okJson(JSON_RESPONSE)));
    }

    @Parameterized.Parameters( name = "File: {0}" )
    public static List<String> data()
    {
        return Arrays.asList("ODataFetchMediaStreamTest/test.txt", "ODataFetchMediaStreamTest/SAP_logo.png");
    }

    private final String file;

    @Test
    @SneakyThrows
    public void testWithFile()
    {
        stubFor(get(urlPathMatching(SRV_PATH + "/Products\\((.*)\\)/\\$value")).willReturn(ok().withBodyFile(file)));

        final List<Product> products = SRV.getAllProduct().executeRequest(destination);

        for( final Product product : products ) {
            try( final InputStream productMedia = product.fetchMediaStream() ) {
                assertInputStreamMatchesFile(productMedia);
            }
        }
        for( final Product product : products ) {
            try( final InputStream productMedia = product.fetchMediaStream() ) {
                assertInputStreamMatchesFile(productMedia);
            }
        }

        // assuring no cache
        verify(2 * products.size(), getRequestedFor(urlPathMatching(".*/Products\\((.*)\\)/\\$value")));
    }

    @SneakyThrows
    private void assertInputStreamMatchesFile( final InputStream productMedia )
    {
        assertThat(productMedia).isNotNull();
        assertThat(productMedia.available()).isGreaterThan(0);

        final InputStream expectedFile = getClass().getClassLoader().getResourceAsStream("__files/" + file);
        assertThat(productMedia).hasSameContentAs(expectedFile);
    }
}
