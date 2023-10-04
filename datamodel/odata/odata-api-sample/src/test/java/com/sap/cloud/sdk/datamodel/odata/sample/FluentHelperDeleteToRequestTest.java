/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.client.HttpClient;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestDelete;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ShelfDeleteFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

public class FluentHelperDeleteToRequestTest
{
    private static final String ODATA_ENDPOINT_URL = "/endpoint/url/";
    private static final String ENTITY_COLLECTION = "Shelves";

    private static final ODataEntityKey ENTITY_KEY = new ODataEntityKey(ODataProtocol.V2).addKeyProperty("Id", 101);

    private static final String ODATA_QUERY_URL = ODATA_ENDPOINT_URL + ENTITY_COLLECTION + "(101)";
    private static final Shelf ShelfToDelete = Shelf.builder().id(101).build();
    private final SdkGroceryStoreService service =
        new DefaultSdkGroceryStoreService().withServicePath(ODATA_ENDPOINT_URL);
    private final ShelfDeleteFluentHelper fluentHelper = service.deleteShelf(ShelfToDelete);

    @Rule
    public WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Test
    public void testDeleteToRequest()
    {
        final ODataRequestDelete deleteQuery = fluentHelper.toRequest();
        final ODataRequestDelete expectedQuery =
            new ODataRequestDelete(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, ENTITY_KEY, "", ODataProtocol.V2);

        assertThat(deleteQuery.getRelativeUri().toString()).isEqualTo(expectedQuery.getRelativeUri().toString());
    }

    @Test
    public void testHeaders()
    {
        final String httpHeaderKey = "my-header";
        final String httpHeaderValue = "my-value";

        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

        stubFor(head(anyUrl()).willReturn(serverError()));
        stubFor(
            delete(urlEqualTo(ODATA_QUERY_URL))
                .withHeader(httpHeaderKey, equalTo(httpHeaderValue))
                .willReturn(noContent()));

        fluentHelper.withHeader(httpHeaderKey, httpHeaderValue).toRequest().execute(httpClient);

        verify(deleteRequestedFor(urlEqualTo(ODATA_QUERY_URL)).withHeader(httpHeaderKey, equalTo(httpHeaderValue)));
    }
}
