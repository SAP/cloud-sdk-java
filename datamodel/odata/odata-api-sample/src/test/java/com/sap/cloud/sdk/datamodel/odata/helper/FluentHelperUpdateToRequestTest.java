/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.sap.cloud.sdk.datamodel.odata.helper.ModifyPatchStrategy.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestUpdate;
import com.sap.cloud.sdk.datamodel.odata.client.request.UpdateStrategy;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductCount;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.ProductUpdateFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

import lombok.SneakyThrows;

class FluentHelperUpdateToRequestTest
{
    private static final String ODATA_ENDPOINT_URL = "/endpoint/url";
    private static final String ENTITY_COLLECTION = "Products";
    private static final String versionIdentifier = "TestEtag";

    private static final ODataEntityKey ENTITY_KEY =
        new ODataEntityKey(ODataProtocol.V2).addKeyProperty("ProductId", 123);

    private final SdkGroceryStoreService service =
        new DefaultSdkGroceryStoreService().withServicePath(ODATA_ENDPOINT_URL);
    private final Product product =
        Product
            .builder()
            .id(123)
            .name("Mate")
            .price(new BigDecimal("9.99"))
            .image(new byte[] { 0x01, 0x02, 0x03 })
            .build();
    private final ProductUpdateFluentHelper fluentHelper = service.updateProduct(product);

    @SneakyThrows
    @Test
    void testDefaultUpdateMethod()
    {
        final ProductUpdateFluentHelper updateDefault = service.updateProduct(product);
        final ProductUpdateFluentHelper updatePatch = service.updateProduct(product).modifyingEntity();
        final ProductUpdateFluentHelper updatePut = service.updateProduct(product).replacingEntity();

        // check query builders
        final UpdateStrategy strategyDefault = updateDefault.toRequest().getUpdateStrategy();
        final UpdateStrategy strategyPatch = updatePatch.modifyingEntity().toRequest().getUpdateStrategy();
        final UpdateStrategy stratgeyPut = updatePut.replacingEntity().toRequest().getUpdateStrategy();
        assertThat(strategyDefault).isEqualTo(strategyPatch).isNotEqualTo(stratgeyPut);

        // check actual HTTP method
        final HttpDestination destination = DefaultHttpDestination.builder("").build();
        final HttpClient client = mock(HttpClient.class);
        HttpClientAccessor.setHttpClientFactory(dest -> client);
        when(client.execute(any())).thenReturn(new BasicHttpResponse(HttpVersion.HTTP_1_1, 204, "No content."));

        updateDefault.executeRequest(destination);
        updatePatch.executeRequest(destination);
        verify(client, times(2)).execute(argThat(request -> request.getMethod().equals("PATCH")));

        updatePut.executeRequest(destination);
        verify(client, times(1)).execute(argThat(request -> request.getMethod().equals("PUT")));
    }

    @Test
    void testUpdatePut()
    {
        product.setVersionIdentifier(versionIdentifier);
        final ODataRequestUpdate updateQuery = fluentHelper.replacingEntity().toRequest();
        final String serializedEntity = ODataEntitySerializer.serializeEntityForUpdatePut(product, null);

        final ODataRequestUpdate expectedQuery =
            new ODataRequestUpdate(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                ENTITY_KEY,
                serializedEntity,
                UpdateStrategy.REPLACE_WITH_PUT,
                versionIdentifier,
                ODataProtocol.V2);

        assertThat(updateQuery.getRelativeUri()).hasToString(expectedQuery.getRelativeUri().toString());

        //assert basic properties
        assertThat(updateQuery.getSerializedEntity()).isEqualTo(serializedEntity);
        assertThat(updateQuery.getUpdateStrategy()).isEqualTo(UpdateStrategy.REPLACE_WITH_PUT);
        assertThat(updateQuery.getVersionIdentifier()).isEqualTo(versionIdentifier);
    }

    @Test
    void testUpdatePutWithExcludedFields()
    {
        product.setVersionIdentifier(versionIdentifier);
        final ODataRequestUpdate updateQuery =
            fluentHelper.replacingEntity().excludingFields(Product.NAME, Product.TO_VENDOR).toRequest();
        final List<FieldReference> fieldsToExclude =
            Arrays
                .asList(
                    FieldReference.of(Product.NAME.getFieldName()),
                    FieldReference.of(Product.TO_VENDOR.getFieldName()));
        final String serializedEntity = ODataEntitySerializer.serializeEntityForUpdatePut(product, fieldsToExclude);

        final ODataRequestUpdate expectedQuery =
            new ODataRequestUpdate(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                ENTITY_KEY,
                serializedEntity,
                UpdateStrategy.REPLACE_WITH_PUT,
                versionIdentifier,
                ODataProtocol.V2);

        assertThat(updateQuery.getRelativeUri()).hasToString(expectedQuery.getRelativeUri().toString());

        //assert basic properties
        assertThat(updateQuery.getSerializedEntity()).isEqualTo(serializedEntity);
        assertThat(updateQuery.getUpdateStrategy()).isEqualTo(UpdateStrategy.REPLACE_WITH_PUT);
        assertThat(updateQuery.getVersionIdentifier()).isEqualTo(versionIdentifier);
    }

    @Test
    void testUpdatePatch()
    {
        product.setVersionIdentifier(versionIdentifier);
        final String updatedName = "ChangedName";
        product.setName(updatedName);

        final ODataRequestUpdate updateQuery = fluentHelper.modifyingEntity().toRequest();

        final ODataRequestUpdate expectedQuery =
            new ODataRequestUpdate(
                ODATA_ENDPOINT_URL,
                ENTITY_COLLECTION,
                ENTITY_KEY,
                "{\"Name\":\"" + updatedName + "\"}",
                UpdateStrategy.MODIFY_WITH_PATCH,
                versionIdentifier,
                ODataProtocol.V2);

        assertThat(updateQuery.getRelativeUri()).hasToString(expectedQuery.getRelativeUri().toString());

        //assert basic properties
        assertThat(updateQuery.getSerializedEntity()).isEqualTo(expectedQuery.getSerializedEntity());
        assertThat(updateQuery.getUpdateStrategy()).isEqualTo(UpdateStrategy.MODIFY_WITH_PATCH);
        assertThat(updateQuery.getVersionIdentifier()).isEqualTo(versionIdentifier);
    }

    @Test
    void testUpdateContainsUpdatedAndIncludedFields()
    {
        final String updatedName = "ChangedName";
        product.setName(updatedName);

        final ODataRequestUpdate updateQuery =
            fluentHelper.includingFields(Product.IMAGE, Product.TO_SHELF).toRequest();
        final String expectedSerializedEntity = "{\"Shelf\":[],\"Image\":\"AQID\",\"Name\":\"" + updatedName + "\"}";

        assertThat(updateQuery.getSerializedEntity()).isEqualTo(expectedSerializedEntity);
        assertThat(updateQuery.getUpdateStrategy()).isEqualTo(UpdateStrategy.MODIFY_WITH_PATCH);
    }

    @Test
    void testUpdateIncludeFieldWhichWasAlsoUpdated()
    {
        final String updatedName = "ChangedName";
        product.setName(updatedName);

        final ODataRequestUpdate updateQuery = fluentHelper.includingFields(Product.IMAGE, Product.NAME).toRequest();
        final String expectedSerializedEntity = "{\"Image\":\"AQID\",\"Name\":\"" + updatedName + "\"}";

        assertThat(updateQuery.getSerializedEntity()).isEqualTo(expectedSerializedEntity);
    }

    @Test
    void testUpdateBPatchUpdateNull()
    {
        final String updatedName = "ChangedName";
        product.setPrice(null);
        product.setName(updatedName);

        final ODataRequestUpdate updateQuery = fluentHelper.toRequest();
        final String expectedSerializedEntity = "{\"Price\":null,\"Name\":\"" + updatedName + "\"}";

        assertThat(updateQuery.getSerializedEntity()).isEqualTo(expectedSerializedEntity);
    }

    @Test
    void testUpdatePatchComplexPropertyDelta()
    {
        final ProductCount count1 = ProductCount.builder().productId(123).quantity(10).build();
        final Receipt receipt = Receipt.builder().id(1001).customerId(9001).productCount1(count1).build();

        final String expectedSerializedEntity = "{\"ProductCount1\":{\"Quantity\":20}}";

        count1.setQuantity(20);

        final ODataRequestUpdate receiptUpdate =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .update(ENTITY_COLLECTION, receipt)
                .modifyingEntity(RECURSIVE_DELTA)
                .toRequest();

        assertThat(receiptUpdate).isNotNull();
        assertThat(receiptUpdate.getSerializedEntity()).isEqualTo(expectedSerializedEntity);
    }

    @Test
    void testUpdatePatchComplexPropertyFull()
    {
        final ProductCount count1 = ProductCount.builder().productId(123).quantity(10).build();
        final Receipt receipt = Receipt.builder().id(1001).customerId(9001).productCount1(count1).build();

        final String expectedSerializedEntity = "{\"ProductCount1\":{\"ProductId\":123,\"Quantity\":20}}";

        count1.setQuantity(20);

        final ODataRequestUpdate receiptUpdate =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .update(ENTITY_COLLECTION, receipt)
                .modifyingEntity(RECURSIVE_FULL)
                .toRequest();

        assertThat(receiptUpdate).isNotNull();
        assertThat(receiptUpdate.getSerializedEntity()).isEqualTo(expectedSerializedEntity);
    }

    @Test
    @Disabled( " Test is failing as the getChangedFields() method on Complex Type is not working as expected." )
    void testIgnoreVersionIdentifier()
    {
        product.setVersionIdentifier(versionIdentifier);
        final ODataRequestUpdate updateQuery = fluentHelper.matchAnyVersionIdentifier().toRequest();

        assertThat(updateQuery.getVersionIdentifier()).isEqualTo("*");
        assertThat(updateQuery.getUpdateStrategy()).isEqualTo(UpdateStrategy.MODIFY_WITH_PATCH);
    }
}
