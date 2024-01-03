/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.sdkgrocerystore.Product;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.sdkgrocerystore.Product Product}. This interface is used by {@link testcomparison.namespaces.sdkgrocerystore.field.ProductField ProductField} and {@link testcomparison.namespaces.sdkgrocerystore.link.ProductLink ProductLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Product#ID ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Product#NAME NAME}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Product#SHELF_ID SHELF_ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Product#VENDOR_ID VENDOR_ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Product#PRICE PRICE}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Product#IMAGE IMAGE}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Product#TO_VENDOR TO_VENDOR}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Product#TO_SHELF TO_SHELF}</li>
 * </ul>
 * 
 */
public interface ProductSelectable
    extends EntitySelectable<Product>
{


}
