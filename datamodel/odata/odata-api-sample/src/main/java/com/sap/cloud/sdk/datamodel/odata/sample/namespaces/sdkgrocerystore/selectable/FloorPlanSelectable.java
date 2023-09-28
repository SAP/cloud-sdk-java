package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan;

/**
 * Interface to enable OData entity selectors for
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan FloorPlan}. This interface is
 * used by {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.field.FloorPlanField
 * FloorPlanField} and {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.link.FloorPlanLink
 * FloorPlanLink}.
 *
 * <p>
 * Available instances:
 * <ul>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan#ID ID}</li>
 * <li>{@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.FloorPlan#IMAGE_URI IMAGE_URI}</li>
 * </ul>
 *
 */
public interface FloorPlanSelectable extends EntitySelectable<FloorPlan>
{

}
