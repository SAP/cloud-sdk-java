/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours;

/**
 * Interface to enable OData entity selectors for
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours OpeningHours}. This interface
 * is used by {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.field.OpeningHoursField
 * OpeningHoursField} and
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.link.OpeningHoursLink OpeningHoursLink}.
 *
 * <p>
 * Available instances:
 * <ul>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours#ID ID}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours#DAY_OF_WEEK
 * DAY_OF_WEEK}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours#OPEN_TIME OPEN_TIME}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.OpeningHours#CLOSE_TIME
 * CLOSE_TIME}</li>
 * </ul>
 *
 */
public interface OpeningHoursSelectable extends EntitySelectable<OpeningHours>
{

}
