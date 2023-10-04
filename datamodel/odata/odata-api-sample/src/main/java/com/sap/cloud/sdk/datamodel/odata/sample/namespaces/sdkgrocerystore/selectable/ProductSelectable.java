/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product;

/**
 * Interface to enable OData entity selectors for
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product Product}. This interface is used
 * by {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.field.ProductField ProductField} and
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.link.ProductLink ProductLink}.
 *
 * <p>
 * Available instances:
 * <ul>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product#ID ID}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product#NAME NAME}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product#SHELF_ID SHELF_ID}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product#VENDOR_ID VENDOR_ID}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product#PRICE PRICE}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product#IMAGE IMAGE}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product#TO_VENDOR TO_VENDOR}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Product#TO_SHELF TO_SHELF}</li>
 * </ul>
 *
 */
public interface ProductSelectable extends EntitySelectable<Product>
{

}
