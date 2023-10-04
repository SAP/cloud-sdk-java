/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.apache.http.client.HttpClient;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.helper.Order;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

public class FluentHelperReadToRequestTest
{
    private static final String ODATA_ENDPOINT_URL = "/endpoint/url";
    private static final String ENTITY_COLLECTION = "Products";

    private final SdkGroceryStoreService service =
        new DefaultSdkGroceryStoreService().withServicePath(ODATA_ENDPOINT_URL);
    private final ProductFluentHelper fluentHelper = service.getAllProduct();

    @Test
    public void testOrderBy()
    {
        final ODataRequestRead vdmRequest = fluentHelper.orderBy(Product.NAME, Order.ASC).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$orderby=Name%20asc", ODataProtocol.V2);

        assertThat(vdmRequest.getRequestQuery()).isEqualTo(expectedRequest.getRequestQuery());
    }

    @Test
    public void testSkip()
    {
        final ODataRequestRead vdmRequest = fluentHelper.skip(0).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$skip=0", ODataProtocol.V2);

        assertThat(vdmRequest.getRequestQuery()).isEqualTo(expectedRequest.getRequestQuery());
    }

    @Test
    public void testTop()
    {
        final ODataRequestRead vdmRequest = fluentHelper.top(10).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$top=10", ODataProtocol.V2);

        assertThat(vdmRequest.getRequestQuery()).isEqualTo(expectedRequest.getRequestQuery());
    }

    @Test
    public void testCustomParameter()
    {
        final String customParameterKey = "customKey";
        final String customParameterValue = "customValue";
        final String expectedRequestString = customParameterKey + "=" + customParameterValue;

        final ODataRequestRead vdmRequest =
            fluentHelper.withQueryParameter(customParameterKey, customParameterValue).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, expectedRequestString, ODataProtocol.V2);

        assertThat(vdmRequest.getRelativeUri().toString()).isEqualTo(expectedRequest.getRelativeUri().toString());
    }

    @Test
    public void testCustomParameterOverloadingParameters()
    {
        final ODataRequestRead vdmRequest =
            fluentHelper
                .orderBy(Product.NAME, Order.ASC)
                .orderBy(Product.NAME, Order.DESC)
                .orderBy(Product.NAME, Order.ASC)
                .orderBy(Product.NAME, Order.DESC)
                // Overriding with custom query params is currently not possible
                // .withQueryParameter("$orderby", "Customer desc")
                .toRequest();

        final String expectedRequestString = "$orderby=Name%20desc";
        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, expectedRequestString, ODataProtocol.V2);

        assertThat(vdmRequest.getRequestQuery()).isEqualTo(expectedRequest.getRequestQuery());
    }

    @Test
    public void testCustomHeader()
    {
        final String customHeaderKey = "customKey";
        final String customHeaderValue = "customValue";

        final WireMockServer mockServer = new WireMockServer(wireMockConfig().dynamicPort());
        mockServer.start();

        mockServer
            .addStubMapping(
                WireMock
                    .get(WireMock.urlPathEqualTo(ODATA_ENDPOINT_URL + "/" + ENTITY_COLLECTION))
                    .withHeader(customHeaderKey, WireMock.equalTo(customHeaderValue))
                    .willReturn(WireMock.ok())
                    .build());

        final HttpClient httpClient =
            HttpClientAccessor.getHttpClient(DefaultHttpDestination.builder(mockServer.baseUrl()).build());

        final ODataRequestRead vdmRequest = fluentHelper.withHeader(customHeaderKey, customHeaderValue).toRequest();

        vdmRequest.execute(httpClient);
        mockServer.stop();
    }

    @Test
    public void testSelect()
    {
        final ODataRequestRead vdmRequest = fluentHelper.select(Product.NAME).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$select=Name", ODataProtocol.V2);

        assertThat(vdmRequest.getRequestQuery()).isEqualTo(expectedRequest.getRequestQuery());
    }

    @Test
    public void testFilters()
    {
        final ODataRequestRead vdmRequest =
            fluentHelper
                .filter(Product.NAME.eq("Cloud SDK").and(Product.VENDOR_ID.eq(42)).or(Product.VENDOR_ID.eq(100)))
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$filter=((Name%20eq%20'Cloud%20SDK')%20and%20(VendorId%20eq%2042))%20or%20(VendorId%20eq%20100)",
                ODataProtocol.V2);

        assertThat(vdmRequest.getRequestQuery()).isEqualTo(expectedRequest.getRequestQuery());
    }

    @Test
    public void testFilterEncoding()
    {
        final ODataRequestRead vdmRequest = fluentHelper.filter(Product.NAME.eq("Cl#ou''d' S&*&$*d|\\/K")).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$filter=Name%20eq%20'Cl%23ou''''d''%20S%26*%26%24*d%7C%5C/K'",
                ODataProtocol.V2);

        assertThat(vdmRequest.getRequestQuery()).isEqualTo(expectedRequest.getRequestQuery());
    }

    @Test
    public void testTemporalFilterExpression()
    {
        final LocalTime time = LocalTime.of(18, 59, 59);
        final int day = DayOfWeek.WEDNESDAY.getValue();
        final ODataRequestRead vdmRequest =
            service
                .getAllOpeningHours()
                .filter(OpeningHours.DAY_OF_WEEK.eq(day).and(OpeningHours.OPEN_TIME.lt(time)))
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$filter=(DayOfWeek%20eq%203)%20and%20(OpenTime%20lt%20time'PT18H59M59S')",
                ODataProtocol.V2);

        assertThat(vdmRequest.getRequestQuery()).isEqualTo(expectedRequest.getRequestQuery());
    }
}
