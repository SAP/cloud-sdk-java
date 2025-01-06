/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import javax.annotation.Nonnull;

/**
 * Generic interface to provide the original entity class reference.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface EntityReference<EntityT>
{
    /**
     * Get the type of the entity which references the value.
     *
     * @return The entity type,
     */
    @Nonnull
    Class<EntityT> getEntityType();
}
