/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

import io.vavr.control.Option;

@WireMockTest
class FluentHelperReadVersionIdentifierTest
{
    private static final String NO_METADATA = null;
    private static final String NO_ETAG = null;
    private static final String ODATA_ENDPOINT_URL = "/endpoint/url";
    private static final String ODATA_QUERY_URL = ODATA_ENDPOINT_URL + "/Products";

    private final SdkGroceryStoreService service =
        new DefaultSdkGroceryStoreService().withServicePath(ODATA_ENDPOINT_URL);

    private HttpDestination destination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Nonnull
    private static String createPayload( @Nullable String metadata )
    {
        metadata = metadata == null ? "" : "\"__metadata\": " + metadata + ",";
        return String.format("{%s\"Id\": %d}", metadata, (int) (Math.random() * 100));
    }

    @Nonnull
    private static String createPayloadEtag( @Nullable String etag )
    {
        etag = etag == null ? "" : ", \"etag\": \"W/\\\"" + etag + "\\\"\"";
        final String id = "https://127.0.0.1/com.sap.cloud.sdk.store.grocery/Products(44444)";
        final String metadata =
            String.format("{\"id\":\"%s\",\"uri\":\"%s\",\"type\":\"SdkGroceryStore.ProductType\"%s}", id, id, etag);
        ;
        return createPayload(metadata);
    }

    @Test
    void testQueryExecuteSetsVersionIdentifierOnAllEntities()
    {
        final String response =
            String
                .format(
                    "{\"d\":{\"results\":[%s,%s,%s,%s,%s]}}",
                    createPayloadEtag("2017-06-12"),
                    createPayloadEtag("2017-07-31"),
                    createPayloadEtag("2017-08-07"),
                    createPayloadEtag("2018-04-25"),
                    createPayloadEtag("2018-07-17"));
        stubFor(get(urlEqualTo(ODATA_QUERY_URL)).willReturn(aResponse().withBody(response)));

        final List<Product> products = service.getAllProduct().executeRequest(destination);

        assertThat(products).hasSize(5);
        assertThat(products)
            .extracting(Product::getVersionIdentifier)
            .extracting(Option::get)
            .containsExactly(
                "W/\"2017-06-12\"",
                "W/\"2017-07-31\"",
                "W/\"2017-08-07\"",
                "W/\"2018-04-25\"",
                "W/\"2018-07-17\"");
    }

    @Test
    void testQueryExecuteSucceedsWithoutVersionIdentifierOnPartlyMissingMetadata()
    {
        final String response =
            String
                .format(
                    "{\"d\":{\"results\":[%s,%s,%s,%s,%s]}}",
                    createPayloadEtag("2017-06-12"),
                    createPayload(NO_METADATA),
                    createPayloadEtag("2017-08-07"),
                    createPayload(NO_METADATA),
                    createPayloadEtag("2018-07-17"));
        stubFor(get(urlEqualTo(ODATA_QUERY_URL)).willReturn(aResponse().withBody(response)));

        final List<Product> products = service.getAllProduct().executeRequest(destination);

        assertThat(products).hasSize(5);
        assertThat(products)
            .extracting(Product::getVersionIdentifier)
            .containsExactly(
                Option.some("W/\"2017-06-12\""),
                Option.none(),
                Option.some("W/\"2017-08-07\""),
                Option.none(),
                Option.some("W/\"2018-07-17\""));
    }

    @Test
    void testQueryExecuteSucceedsWithoutMetadata()
    {
        final String respons =
            String
                .format(
                    "{\"d\":{\"results\":[%s,%s,%s,%s,%s]}}",
                    createPayload(NO_METADATA),
                    createPayload(NO_METADATA),
                    createPayload(NO_METADATA),
                    createPayload(NO_METADATA),
                    createPayload(NO_METADATA));
        stubFor(get(urlEqualTo(ODATA_QUERY_URL)).willReturn(aResponse().withBody(respons)));

        final List<Product> products = service.getAllProduct().executeRequest(destination);

        assertThat(products).hasSize(5);
        assertThat(products)
            .extracting(Product::getVersionIdentifier)
            .containsExactly(Option.none(), Option.none(), Option.none(), Option.none(), Option.none());
    }

    @Test
    void testQueryExecuteSucceedsWithoutVersionIdentifierOnPartlyMissingVersionIdentifier()
    {
        final String response =
            String
                .format(
                    "{\"d\":{\"results\":[%s,%s,%s,%s,%s]}}",
                    createPayloadEtag("2017-06-12"),
                    createPayloadEtag(NO_ETAG),
                    createPayloadEtag("2017-08-07"),
                    createPayloadEtag(NO_ETAG),
                    createPayloadEtag("2018-07-17"));
        stubFor(get(urlEqualTo(ODATA_QUERY_URL)).willReturn(aResponse().withBody(response)));

        final List<Product> products = service.getAllProduct().executeRequest(destination);

        assertThat(products).hasSize(5);
        assertThat(products)
            .extracting(Product::getVersionIdentifier)
            .containsExactly(
                Option.some("W/\"2017-06-12\""),
                Option.none(),
                Option.some("W/\"2017-08-07\""),
                Option.none(),
                Option.some("W/\"2018-07-17\""));
    }

    @Test
    void testQueryExecuteSucceedsWithoutVersionIdentifier()
    {
        final String response =
            String
                .format(
                    "{\"d\":{\"results\":[%s,%s,%s,%s,%s]}}",
                    createPayloadEtag(NO_ETAG),
                    createPayloadEtag(NO_ETAG),
                    createPayloadEtag(NO_ETAG),
                    createPayloadEtag(NO_ETAG),
                    createPayloadEtag(NO_ETAG));
        stubFor(get(urlEqualTo(ODATA_QUERY_URL)).willReturn(aResponse().withBody(response)));

        final List<Product> products = service.getAllProduct().executeRequest(destination);

        assertThat(products).hasSize(5);
        assertThat(products)
            .extracting(Product::getVersionIdentifier)
            .containsExactly(Option.none(), Option.none(), Option.none(), Option.none(), Option.none());
    }
}
