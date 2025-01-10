/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.sdkgrocerystore.Shelf;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.sdkgrocerystore.Shelf Shelf}. This interface is used by {@link testcomparison.namespaces.sdkgrocerystore.field.ShelfField ShelfField} and {@link testcomparison.namespaces.sdkgrocerystore.link.ShelfLink ShelfLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Shelf#ID ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Shelf#FLOOR_PLAN_ID FLOOR_PLAN_ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Shelf#TO_FLOOR_PLAN TO_FLOOR_PLAN}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Shelf#TO_PRODUCTS TO_PRODUCTS}</li>
 * </ul>
 * 
 */
public interface ShelfSelectable
    extends EntitySelectable<Shelf>
{


}
