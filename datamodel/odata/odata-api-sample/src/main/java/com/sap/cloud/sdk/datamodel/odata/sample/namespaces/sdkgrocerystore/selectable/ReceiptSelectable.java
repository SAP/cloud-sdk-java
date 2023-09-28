package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt;

/**
 * Interface to enable OData entity selectors for
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt Receipt}. This interface is used
 * by {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.field.ReceiptField ReceiptField} and
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.link.ReceiptLink ReceiptLink}.
 *
 * <p>
 * Available instances:
 * <ul>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt#ID ID}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt#CUSTOMER_ID CUSTOMER_ID}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt#TOTAL_AMOUNT
 * TOTAL_AMOUNT}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt#TO_CUSTOMER TO_CUSTOMER}</li>
 * </ul>
 *
 */
public interface ReceiptSelectable extends EntitySelectable<Receipt>
{

}
