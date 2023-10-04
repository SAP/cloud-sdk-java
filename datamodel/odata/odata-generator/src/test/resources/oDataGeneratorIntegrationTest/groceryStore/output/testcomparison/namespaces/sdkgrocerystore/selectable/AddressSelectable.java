/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.sdkgrocerystore.Address;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.sdkgrocerystore.Address Address}. This interface is used by {@link testcomparison.namespaces.sdkgrocerystore.field.AddressField AddressField} and {@link testcomparison.namespaces.sdkgrocerystore.link.AddressLink AddressLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Address#ID ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Address#STREET STREET}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Address#CITY CITY}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Address#STATE STATE}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Address#COUNTRY COUNTRY}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Address#POSTAL_CODE POSTAL_CODE}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Address#LATITUDE LATITUDE}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Address#LONGITUDE LONGITUDE}</li>
 * </ul>
 * 
 */
public interface AddressSelectable
    extends EntitySelectable<Address>
{


}
