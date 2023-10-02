/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Shelf;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Vendor;

public class VdmSerializationTest
{
    private static final String PRODUCT_JSON =
        "{"
            + "\"versionIdentifier\":null,"
            + "\"Id\":10401010,"
            + "\"Name\":\"Cloud SDK\","
            + "\"ShelfId\":null,"
            + "\"VendorId\":null,"
            + "\"Price\":null,"
            + "\"Image\":null,"
            + "\"Vendor\":{\"versionIdentifier\":null,\"Id\":null,\"Name\":\"SAP\",\"AddressId\":null,\"Address\":null},"
            + "\"Shelf\":[{\"versionIdentifier\":null,\"Id\":500,\"FloorPlanId\":200,\"FloorPlan\":null,\"Products\":[]}]"
            + "}";

    private static final Product PRODUCT_OBJECT =
        Product
            .builder()
            .id(10401010)
            .name("Cloud SDK")
            .vendor(Vendor.builder().name("SAP").build())
            .shelf(Shelf.builder().id(500).floorPlanId(200).build())
            .build();

    @Test
    public void testGsonSerialization()
    {
        final String actualJson = new GsonBuilder().serializeNulls().create().toJson(PRODUCT_OBJECT);
        assertThat(actualJson).isNotNull().isEqualTo(PRODUCT_JSON);
    }

    @Test
    public void testGsonDeserialization()
    {
        final Product deserializedFromJson = new Gson().fromJson(PRODUCT_JSON, Product.class);
        assertThat(deserializedFromJson).isNotNull();
        assertThat(deserializedFromJson.getName()).isEqualTo("Cloud SDK");
        assertThat(deserializedFromJson.getId()).isEqualTo(10401010);

        final Vendor deserializedCustomer = deserializedFromJson.getVendorOrFetch();
        assertThat(deserializedCustomer).isNotNull();
        assertThat(deserializedCustomer.getName()).isEqualTo("SAP");

        final List<Shelf> deserializedAddresses = deserializedFromJson.getShelfOrFetch();
        assertThat(deserializedAddresses).isNotNull().hasSize(1);

        final Shelf actualBpAddress = deserializedAddresses.get(0);
        assertThat(actualBpAddress.getId()).isEqualTo(500);
        assertThat(actualBpAddress.getFloorPlanId()).isEqualTo(200);
    }

    @Test
    public void testJacksonSerialization()
        throws Exception
    {
        final String actualJson = new ObjectMapper().writeValueAsString(PRODUCT_OBJECT);
        assertThat(actualJson).isNotNull().isEqualTo(PRODUCT_JSON);
    }

    @Test
    public void testJacksonDeserialization()
        throws IOException
    {
        final Product deserializedFromJson = new ObjectMapper().readValue(PRODUCT_JSON, Product.class);
        assertThat(deserializedFromJson).isNotNull();
        assertThat(deserializedFromJson.getName()).isEqualTo("Cloud SDK");
        assertThat(deserializedFromJson.getId()).isEqualTo(10401010);

        final Vendor deserializedCustomer = deserializedFromJson.getVendorOrFetch();
        assertThat(deserializedCustomer).isNotNull();
        assertThat(deserializedCustomer.getName()).isEqualTo("SAP");

        final List<Shelf> deserializedAddresses = deserializedFromJson.getShelfOrFetch();
        assertThat(deserializedAddresses).isNotNull().hasSize(1);

        final Shelf actualBpAddress = deserializedAddresses.get(0);
        assertThat(actualBpAddress.getId()).isEqualTo(500);
        assertThat(actualBpAddress.getFloorPlanId()).isEqualTo(200);
    }
}
