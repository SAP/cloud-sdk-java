/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator.annotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Data structure representing an OData property of an entity or complex type.
 */
public interface EntityPropertyAnnotationModel
{
    /**
     * Name of the property as defined in the OData EDM.
     *
     * @return the OData EDM name.
     */
    @Nonnull
    String getEdmName();

    /**
     * Value is {@code true} if this property is one of the OData simple types, and {@code false} if it is an OData
     * complex type.
     *
     * @return {@code false} if this property is an OData complex type, {@code true} else.
     */
    boolean isSimpleType();

    /**
     * OData EDM type of the property as defined in the OData EDM.
     *
     * @return the OData EDM type.
     */
    @Nonnull
    String getEdmType();

    /**
     * Value is {@code true} for key properties of the OData entity.
     *
     * @return {@code true} iff this property is a key field of the OData entity
     */
    boolean isKeyField();

    /**
     * Precision as defined in the OData EDM. This is only valid for decimal properties.
     *
     * @return the associated precision.
     */
    @Nullable
    Integer getPrecision();

    /**
     * Scale as defined in the OData EDM. This is only valid for decimal properties.
     *
     * @return the associated scale.
     */
    @Nullable
    Integer getScale();
}
