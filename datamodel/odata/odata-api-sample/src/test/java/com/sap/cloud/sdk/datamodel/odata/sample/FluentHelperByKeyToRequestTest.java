/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductByKeyFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

class FluentHelperByKeyToRequestTest
{
    private static final String ODATA_ENDPOINT_URL = "/endpoint/url";
    private static final String ENTITY_COLLECTION = "Products";

    private static final ODataEntityKey ENTITY_KEY =
        new ODataEntityKey(ODataProtocol.V2).addKeyProperty("ProductId", 123);

    private final SdkGroceryStoreService service =
        new DefaultSdkGroceryStoreService().withServicePath(ODATA_ENDPOINT_URL);
    private final ProductByKeyFluentHelper fluentHelper = service.getProductByKey(123);

    @Test
    void testGetByKey()
    {
        final ODataRequestReadByKey vdmQuery = fluentHelper.toRequest();

        final ODataRequestReadByKey expectedQuery =
            new ODataRequestReadByKey(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, ENTITY_KEY, "", ODataProtocol.V2);

        assertThat(vdmQuery.getRelativeUri().toString()).isEqualTo(expectedQuery.getRelativeUri().toString());
    }

    @Test
    void testCustomParameter()
    {
        final String customParameterKey = "customKey";
        final String customParameterValue = "customValue";
        final String expectedQueryString = customParameterKey + "=" + customParameterValue;

        final ODataRequestReadByKey vdmQuery =
            fluentHelper.withQueryParameter(customParameterKey, customParameterValue).toRequest();

        final ODataRequestReadByKey expectedQuery =
            new ODataRequestReadByKey(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                ENTITY_KEY,
                expectedQueryString,
                ODataProtocol.V2);

        assertThat(vdmQuery.getRelativeUri().toString()).isEqualTo(expectedQuery.getRelativeUri().toString());
    }

    @Test
    @Disabled( "Cannot yet override system query parameters." )
    void testCustomParameterOverloadingParameters()
    {
        final ODataRequestReadByKey vdmQuery =
            fluentHelper
                .select(Product.NAME, Product.NAME)
                .select(Product.NAME, Product.NAME)
                // Overriding with custom query params is currently not possible
                .withQueryParameter("$select", "Name")
                .toRequest();

        final String expectedQueryString = "$select=Name";
        final ODataRequestReadByKey expectedQuery =
            new ODataRequestReadByKey(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                ENTITY_KEY,
                expectedQueryString,
                ODataProtocol.V2);

        assertThat(vdmQuery.getRelativeUri().toString()).isEqualTo(expectedQuery.getRelativeUri().toString());
    }

    @Test
    void testCustomHeader()
    {
        final String customHeaderKey = "customKey";
        final String customHeaderValue = "customValue";

        final WireMockServer mockServer = new WireMockServer(wireMockConfig().dynamicPort());
        mockServer.start();

        mockServer
            .addStubMapping(
                get(urlPathEqualTo(ODATA_ENDPOINT_URL + "/" + ENTITY_COLLECTION + ENTITY_KEY))
                    .withHeader(customHeaderKey, WireMock.equalTo(customHeaderValue))
                    .willReturn(WireMock.ok())
                    .build());

        final HttpClient httpClient =
            HttpClientAccessor.getHttpClient(DefaultHttpDestination.builder(mockServer.baseUrl()).build());

        final ODataRequestReadByKey vdmQuery = fluentHelper.withHeader(customHeaderKey, customHeaderValue).toRequest();

        vdmQuery.execute(httpClient);
        mockServer.stop();
    }

    @Test
    void testGetByKeyWithSelect()
    {
        final ODataRequestReadByKey vdmQuery = fluentHelper.select(Product.NAME).toRequest();

        final String expectedQueryString = "$select=Name";
        final ODataRequestReadByKey expectedQuery =
            new ODataRequestReadByKey(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                ENTITY_KEY,
                expectedQueryString,
                ODataProtocol.V2);

        assertThat(vdmQuery.getRelativeUri().toString()).isEqualTo(expectedQuery.getRelativeUri().toString());
    }
}
