/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.sdkgrocerystore.Receipt;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt}. This interface is used by {@link testcomparison.namespaces.sdkgrocerystore.field.ReceiptField ReceiptField} and {@link testcomparison.namespaces.sdkgrocerystore.link.ReceiptLink ReceiptLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Receipt#ID ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Receipt#CUSTOMER_ID CUSTOMER_ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Receipt#TOTAL_AMOUNT TOTAL_AMOUNT}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.Receipt#TO_CUSTOMER TO_CUSTOMER}</li>
 * </ul>
 * 
 */
public interface ReceiptSelectable
    extends EntitySelectable<Receipt>
{


}
