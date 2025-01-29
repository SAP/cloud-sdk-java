/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.sdkgrocerystore.Customer;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer}. This interface is used by {@link testcomparison.namespaces.sdkgrocerystore.field.CustomerField CustomerField} and {@link testcomparison.namespaces.sdkgrocerystore.link.CustomerLink CustomerLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Customer#ID ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Customer#NAME NAME}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Customer#EMAIL EMAIL}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Customer#ADDRESS_ID ADDRESS_ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Customer#TO_ADDRESS TO_ADDRESS}</li>
 * </ul>
 * 
 */
public interface CustomerSelectable
    extends EntitySelectable<Customer>
{


}
