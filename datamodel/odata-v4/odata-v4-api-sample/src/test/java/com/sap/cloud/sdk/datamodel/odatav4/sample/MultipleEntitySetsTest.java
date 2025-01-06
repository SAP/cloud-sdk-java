/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.sample;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore.Shelf;
import com.sap.cloud.sdk.datamodel.odatav4.sample.services.DefaultSdkGroceryStoreService;

class MultipleEntitySetsTest
{

    private static final DefaultSdkGroceryStoreService service = new DefaultSdkGroceryStoreService();

    @Test
    void testFetchNavigationProperty()
    {
        final Shelf shelf = Shelf.builder().id(42).build();
        final ShopFloorShelf shopFloorShelf = new ShopFloorShelf();
        shopFloorShelf.setId(24);

        final ODataRequestReadByKey firstRequest =
            service.forEntity(shelf).navigateTo(Shelf.TO_FLOOR_PLAN).get().toRequest();
        assertThat(firstRequest.getRelativeUri()).hasToString("/com.sap.cloud.sdk.store.grocery/Shelves(42)/FloorPlan");

        final ODataRequestReadByKey secondRequest =
            service.forEntity(shopFloorShelf).navigateTo(Shelf.TO_FLOOR_PLAN).get().toRequest();
        assertThat(secondRequest.getRelativeUri())
            .hasToString("/com.sap.cloud.sdk.store.grocery/ShopFloorShelves(24)/FloorPlan");
    }

    /**
     * We generate an entity class per entity type ({@link Shelf}) and not one entity class per entity set. That is why
     * {@link Shelf} returns the name of the first entity set inside {@link Shelf#getEntityCollection()}.
     *
     * Since we lack an entity class for the second entity set, we have to use this workaround: We subclass the entity
     * class representing the entity type and overwrite the method {@link VdmEntity#getEntityCollection()}.
     */
    private static class ShopFloorShelf extends Shelf
    {
        @Override
        protected String getEntityCollection()
        {
            return "ShopFloorShelves";
        }
    }
}
