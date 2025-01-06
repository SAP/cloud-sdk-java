/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCount;
import com.sap.cloud.sdk.datamodel.odata.helper.Order;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

/**
 * Tests the method {@link FluentHelperCount#executeRequest(Destination)}
 */
@WireMockTest
class FluentHelperCountExecuteRequestTest
{
    private final ExpressionFluentHelper<Product> FIRST_NAME_FILTER_EXPRESSION = Product.NAME.eq("Cloud SDK");

    private static final String FIRST_NAME_FILTER = "$filter=Name eq 'Cloud SDK'";
    private static final String FIRST_NAME_FILTER_ESCAPED = FIRST_NAME_FILTER.replace(" ", "%20");

    private static final String ODATA_ENDPOINT_URL = "/endpoint/url";
    private static final String ODATA_ENTITY = ODATA_ENDPOINT_URL + "/Products";

    private static final String COUNT = "/$count";
    private static final String COUNT_WITH_FIRST_NAME_FILTER = COUNT + "?" + FIRST_NAME_FILTER_ESCAPED;

    private static final HttpHeader contentTypeHeader = new HttpHeader("content-type", "text/plain; charset=utf-8");
    private static final HttpHeaders responseHeaders = new HttpHeaders(contentTypeHeader);

    private static final SdkGroceryStoreService service =
        new DefaultSdkGroceryStoreService().withServicePath(ODATA_ENDPOINT_URL);
    private DefaultHttpDestination destination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Test
    void testCountRemovesEverythingExceptCustomParameters()
    {
        final String odataRequestUrl = ODATA_ENTITY + COUNT + "?custom=param";

        stubFor(get(urlEqualTo(odataRequestUrl)).willReturn(aResponse().withBody("10").withHeaders(responseHeaders)));

        final long countResult =
            service
                .getAllProduct()
                .withHeader("key", "val")
                .select(Product.NAME)
                .select(Product.TO_VENDOR)
                .top(10)
                .skip(10)
                .orderBy(Product.NAME, Order.ASC)
                .withQueryParameter("custom", "param")
                .count()
                .executeRequest(destination);

        assertThat(countResult).isEqualTo(10);

        verify(getRequestedFor(anyUrl()).withHeader("key", equalTo("val")));
    }

    @Test
    void testCountWithWorkingFilter()
    {
        final String odataRequestUrl = ODATA_ENTITY + COUNT_WITH_FIRST_NAME_FILTER;

        stubFor(get(urlEqualTo(odataRequestUrl)).willReturn(aResponse().withBody("2").withHeaders(responseHeaders)));

        final long countOfProducts =
            service.getAllProduct().filter(FIRST_NAME_FILTER_EXPRESSION).count().executeRequest(destination);

        assertThat(countOfProducts).isEqualTo(2);
    }

    @Test
    void testCountWithEmptyFilter()
    {
        final String odataRequestUrl = ODATA_ENTITY + COUNT_WITH_FIRST_NAME_FILTER;

        stubFor(get(urlEqualTo(odataRequestUrl)).willReturn(aResponse().withBody("0").withHeaders(responseHeaders)));

        final long countOfProducts =
            service.getAllProduct().filter(FIRST_NAME_FILTER_EXPRESSION).count().executeRequest(destination);

        assertThat(countOfProducts).isEqualTo(0);
    }
}
