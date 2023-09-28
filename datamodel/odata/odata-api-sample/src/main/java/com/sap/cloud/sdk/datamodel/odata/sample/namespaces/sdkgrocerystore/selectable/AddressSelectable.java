package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address;

/**
 * Interface to enable OData entity selectors for
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address}. This interface is used
 * by {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.field.AddressField AddressField} and
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.link.AddressLink AddressLink}.
 *
 * <p>
 * Available instances:
 * <ul>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address#ID ID}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address#STREET STREET}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address#CITY CITY}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address#STATE STATE}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address#COUNTRY COUNTRY}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address#POSTAL_CODE POSTAL_CODE}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address#LATITUDE LATITUDE}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address#LONGITUDE LONGITUDE}</li>
 * </ul>
 *
 */
public interface AddressSelectable extends EntitySelectable<Address>
{

}
