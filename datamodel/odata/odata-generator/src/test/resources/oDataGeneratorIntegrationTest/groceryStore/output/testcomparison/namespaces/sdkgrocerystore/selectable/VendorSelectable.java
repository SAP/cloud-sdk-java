/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.sdkgrocerystore.Vendor;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.sdkgrocerystore.Vendor Vendor}. This interface is used by {@link testcomparison.namespaces.sdkgrocerystore.field.VendorField VendorField} and {@link testcomparison.namespaces.sdkgrocerystore.link.VendorLink VendorLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Vendor#ID ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Vendor#NAME NAME}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Vendor#ADDRESS_ID ADDRESS_ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Vendor#TO_ADDRESS TO_ADDRESS}</li>
 * </ul>
 * 
 */
public interface VendorSelectable
    extends EntitySelectable<Vendor>
{


}
