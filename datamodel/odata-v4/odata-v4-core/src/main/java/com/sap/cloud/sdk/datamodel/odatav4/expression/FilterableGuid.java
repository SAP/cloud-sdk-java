/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import java.util.UUID;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Guid.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableGuid<EntityT> extends FilterableValue<EntityT, UUID>
{

}
