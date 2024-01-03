/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

/**
 * Interface representing a structural property of {@link EntityT} that points towards an object of {@link TargetT}.
 * Structured properties are either complex types or navigational properties.
 *
 * @see com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty
 * @see com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty
 *
 * @param <EntityT>
 *            Entity this property is part of.
 * @param <TargetT>
 *            {@link VdmObject} this property represents.
 */
interface StructuredProperty<EntityT extends VdmObject<?>, TargetT extends VdmObject<?>> extends Property<EntityT>
{
}
