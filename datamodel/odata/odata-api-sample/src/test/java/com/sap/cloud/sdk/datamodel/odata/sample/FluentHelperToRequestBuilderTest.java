/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataConnectionException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestDelete;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.Order;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Vendor;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

class FluentHelperToRequestBuilderTest
{
    private static final String ODATA_ENDPOINT_URL = SdkGroceryStoreService.DEFAULT_SERVICE_PATH;
    private static final String ENTITY_COLLECTION = "Products";
    private static final String ENTITY_COLLECTION_ADDRESSES = "Addresses";

    public static class ODataRequestComparator
    {
        public static void assertEqualReadRequest( final ODataRequestGeneric req1, final ODataRequestGeneric req2 )
        {
            final URI uri1 = req1.getRelativeUri();
            final URI uri2 = req2.getRelativeUri();
            assertThat(uri1.getPath()).isEqualTo(uri2.getPath());
            assertThat(uri1.getHost()).isEqualTo(uri2.getHost());

            if( uri1.getQuery() != null || uri2.getQuery() != null ) {
                final List<String> qry1 = Arrays.asList(uri1.getQuery().split("&"));
                final List<String> qry2 = Arrays.asList(uri2.getQuery().split("&"));
                assertThat(qry1).containsExactlyInAnyOrderElementsOf(qry2);
            }
        }
    }

    @Test
    void testGetAllToRequest()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService()
                .getAllProduct()
                .select(Product.NAME)
                .filter(Product.PRICE.lt(BigDecimal.valueOf(100)))
                .orderBy(Product.NAME, Order.ASC)
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$filter=Price%20lt%20100M&$orderby=Name%20asc&$select=Name",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetByKeyToRequest()
    {
        final ODataRequestReadByKey vdmRequest =
            new DefaultSdkGroceryStoreService().getProductByKey(1000001).select(Product.PRICE).toRequest();

        final Map<String, Object> keyProperties = new LinkedHashMap<>();
        keyProperties.put("ProductId", 1000001);

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ODataResourcePath.of(ENTITY_COLLECTION, ODataEntityKey.of(keyProperties, ODataProtocol.V2)),
                "$select=Price",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductWithTop()
    {
        final ODataRequestRead vdmRequest = new DefaultSdkGroceryStoreService().getAllProduct().top(10).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$top=10", ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductWithSkip()
    {
        final ODataRequestRead vdmRequest = new DefaultSdkGroceryStoreService().getAllProduct().skip(10).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$skip=10", ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductsWithFilter()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService().getAllProduct().filter(Product.NAME.eq("")).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$filter=Name%20eq%20%27%27", ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductWithThreeFilters()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService()
                .getAllProduct()
                .filter(Product.NAME.startsWith("SAP"))
                .filter(Product.PRICE.lt(BigDecimal.valueOf(100)))
                .filter(Product.VENDOR_ID.eq(500))
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$filter=startswith(Name,%27SAP%27)%20and%20Price%20lt%20100M%20and%20VendorId%20eq%20500",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductWithThreeFiltersAlternative()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService()
                .getAllProduct()
                .filter(
                    Product.NAME
                        .startsWith("SAP")
                        .and(Product.PRICE.lt(BigDecimal.valueOf(100)).and(Product.VENDOR_ID.eq(500))))
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$filter=startswith(Name,%27SAP%27)%20and%20((Price%20lt%20100M)%20and%20(VendorId%20eq%20500))",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductWithNestedSelect()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService()
                .getAllProduct()
                .select(Product.TO_VENDOR.select(Vendor.TO_ADDRESS))
                .select(Product.TO_SHELF.select(Shelf.TO_FLOOR_PLAN.select(FloorPlan.ALL_FIELDS)))
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$select=Vendor/Address/*,Shelf/FloorPlan/*&$expand=Vendor/Address,Shelf/FloorPlan",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetProductByKey()
    {
        final ODataRequestReadByKey vdmRequest =
            new DefaultSdkGroceryStoreService().getProductByKey(1000001).toRequest();

        final Map<String, Object> keyProperties = new LinkedHashMap<>();
        keyProperties.put("ProductId", 1000001);

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ODataResourcePath.of(ENTITY_COLLECTION, ODataEntityKey.of(keyProperties, ODataProtocol.V2)),
                "",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetProductByKeyWithSelect()
    {
        final ODataRequestReadByKey vdmRequest =
            new DefaultSdkGroceryStoreService().getProductByKey(1000001).select(Product.PRICE).toRequest();

        final Map<String, Object> keyProperties = new LinkedHashMap<>();
        keyProperties.put("ProductId", 1000001);

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ODataResourcePath.of(ENTITY_COLLECTION, ODataEntityKey.of(keyProperties, ODataProtocol.V2)),
                "$select=Price",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductWithOrderByAsc()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService().getAllProduct().orderBy(Product.NAME, Order.ASC).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$orderby=Name%20asc", ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductWithOrderByDesc()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService().getAllProduct().orderBy(Product.NAME, Order.DESC).toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(ODATA_ENDPOINT_URL, ENTITY_COLLECTION, "$orderby=Name%20desc", ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testGetAllProductWithSelect()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService()
                .getAllProduct()
                .select(Product.ID, Product.NAME, Product.IMAGE, Product.field("Size", String.class), Product.PRICE)
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$select=Id,Name,Image,Size,Price",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testFunctionImport()
    {
        final ODataRequestGeneric vdmRequest =
            new DefaultSdkGroceryStoreService().getProductQuantities(36227, 1).toRequest();

        final ODataRequestFunction expectedRequest =
            new ODataRequestFunction(
                ODATA_ENDPOINT_URL,
                ODataResourcePath.of("GetProductQuantities"),
                "ShelfId=36227&ProductId=1",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testDeleteProductAddressToRequest()
    {
        final Address productAddress = Address.builder().id(1234567).build();

        final ODataRequestDelete vdmDeleteRequest =
            new DefaultSdkGroceryStoreService().deleteAddress(productAddress).toRequest();

        final Map<String, Object> keys = new HashMap<>();
        keys.put("Id", productAddress.getId());

        final ODataRequestDelete expectedRequest =
            new ODataRequestDelete(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION_ADDRESSES,
                ODataEntityKey.of(keys, ODataProtocol.V2),
                "",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmDeleteRequest, expectedRequest);
    }

    @Test
    void testCustomRequestParameter()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService()
                .getAllProduct()
                .select(Product.NAME)
                .filter(Product.PRICE.lt(BigDecimal.valueOf(100)))
                .orderBy(Product.NAME, Order.ASC)
                .withQueryParameter("$search", "some text")
                .withQueryParameter("sap-client", "123")
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$filter=Price%20lt%20100M&$orderby=Name%20asc&$search=some%20text&sap-client=123&$select=Name",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);
    }

    @Test
    void testCustomRequestParameterOverloadingParameters()
    {
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService()
                .getAllProduct()
                .select(Product.NAME)
                .filter(Product.PRICE.lt(BigDecimal.valueOf(100)))
                .orderBy(Product.NAME, Order.ASC)
                .withQueryParameter("$search", "some text")
                .withQueryParameter("$select", "foo")
                .withQueryParameter("$filter", "bar")
                .withQueryParameter("sap-client", "123")
                .toRequest();

        final ODataRequestRead expectedRequest =
            new ODataRequestRead(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                "$filter=Price%20lt%20100M&$filter=bar&$orderby=Name%20asc&$search=some%20text&sap-client=123&$select=Name&$select=foo",
                ODataProtocol.V2);

        ODataRequestComparator.assertEqualReadRequest(vdmRequest, expectedRequest);

        // duplicate select definition
        assertThat(vdmRequest.getRequestQuery()).containsOnlyOnce("$select=Name").containsOnlyOnce("$select=foo");

        // duplicate filter definition
        assertThat(vdmRequest.getRequestQuery()).containsOnlyOnce("$filter=Price").containsOnlyOnce("$filter=bar");
    }

    @Test
    void testTemporalFilterExpression()
    {
        final LocalTime time = LocalTime.of(18, 59, 59);
        final ODataRequestRead vdmRequest =
            new DefaultSdkGroceryStoreService()
                .getAllOpeningHours()
                .filter(OpeningHours.OPEN_TIME.lt(time))
                .toRequest();

        assertThat(vdmRequest.getRequestQuery())
            .containsOnlyOnce("$filter=OpenTime lt time'PT18H59M59S'".replace(" ", "%20"));
    }

    @Test
    void testFetchOnUnmanagedEntityWithException()
    {
        final Product product = Product.builder().price(BigDecimal.valueOf(100)).build();

        Assertions.assertThat(product.getShelfOrFetch()).isEmpty();
        assertThatThrownBy(product::fetchShelf).isInstanceOf(ODataRequestException.class);
    }

    @Test
    void testAddOnUnmanagedEntityWithoutException()
    {
        final Product product = Product.builder().price(BigDecimal.valueOf(100)).build();

        product.addShelf(new Shelf());
    }

    @Test
    void testSetOnUnmanagedEntityWithoutException()
    {
        final Product product = Product.builder().price(BigDecimal.valueOf(100)).build();

        product.setShelf(Lists.newArrayList(new Shelf()));
    }

    @Test
    void testBatchWithConflictingServicePath()
    {
        final String customPath1 = "/path1";
        final String customPath2 = "/path2";

        final SdkGroceryStoreService service1 = new DefaultSdkGroceryStoreService().withServicePath(customPath1);
        final SdkGroceryStoreService service2 = new DefaultSdkGroceryStoreService().withServicePath(customPath2);

        final HttpDestination destination = DefaultHttpDestination.builder("http://127.0.0.1:1/").build();

        assertThatCode(() -> service1.batch().addReadOperations(service1.getAllProduct()).executeRequest(destination))
            .isInstanceOf(ODataConnectionException.class); // connection was attempted

        assertThatCode(() -> service1.batch().addReadOperations(service2.getAllProduct()).executeRequest(destination))
            .isInstanceOf(ODataRequestException.class); // connection was not attempted
    }
}
