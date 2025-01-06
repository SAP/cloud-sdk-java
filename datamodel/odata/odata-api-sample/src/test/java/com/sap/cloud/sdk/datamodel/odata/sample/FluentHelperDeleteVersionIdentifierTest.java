/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

@WireMockTest
class FluentHelperDeleteVersionIdentifierTest
{
    private static final String ODATA_ENDPOINT_URL = "/endpoint/url";
    private static final int ADDRESS_ID = 123456;
    private static final String ODATA_QUERY_URL = ODATA_ENDPOINT_URL + "/Addresses(" + ADDRESS_ID + ")";
    private static final SdkGroceryStoreService service =
        new DefaultSdkGroceryStoreService().withServicePath(ODATA_ENDPOINT_URL);

    private static final String versionIdentifier = "W/\"datetimeoffset'2018-04-25T09%3A51%3A49.1719080Z'\"";

    private HttpDestination destination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        stubFor(head(urlEqualTo(ODATA_ENDPOINT_URL)).willReturn(ok().withHeader("x-csrf-token", "abc")));
        stubFor(delete(urlEqualTo(ODATA_QUERY_URL)).willReturn(noContent()));
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Test
    void testVersionIdentifierIsSentByDefault()
    {
        final Address addressToDelete = Address.builder().id(ADDRESS_ID).build();
        addressToDelete.setVersionIdentifier(versionIdentifier);

        service.deleteAddress(addressToDelete).executeRequest(destination);

        verify(
            deleteRequestedFor(urlEqualTo(ODATA_QUERY_URL))
                .withHeader(HttpHeaders.IF_MATCH, equalTo(versionIdentifier)));
    }

    @Test
    void testNoHeaderIsSentByDefault()
    {
        final Address addressToDelete = Address.builder().id(ADDRESS_ID).build();
        service.deleteAddress(addressToDelete).executeRequest(destination);

        verify(deleteRequestedFor(urlEqualTo(ODATA_QUERY_URL)).withoutHeader(HttpHeaders.IF_MATCH));
    }

    @Test
    void testMatchAnyVersionIdentifier()
    {
        final Address addressToDelete = Address.builder().id(ADDRESS_ID).build();
        addressToDelete.setVersionIdentifier("some-identifier");
        service.deleteAddress(addressToDelete).matchAnyVersionIdentifier().executeRequest(destination);

        verify(deleteRequestedFor(urlEqualTo(ODATA_QUERY_URL)).withHeader(HttpHeaders.IF_MATCH, equalTo("*")));
    }

    @Test
    void testDisableVersionIdentifier()
    {
        final Address addressToDelete = Address.builder().id(ADDRESS_ID).build();
        addressToDelete.setVersionIdentifier("some-identifier");
        service.deleteAddress(addressToDelete).disableVersionIdentifier().executeRequest(destination);

        verify(deleteRequestedFor(urlEqualTo(ODATA_QUERY_URL)).withoutHeader(HttpHeaders.IF_MATCH));
    }
}
