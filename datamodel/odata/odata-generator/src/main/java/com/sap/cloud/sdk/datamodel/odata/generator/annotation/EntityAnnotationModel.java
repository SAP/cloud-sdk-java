/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator.annotation;

import javax.annotation.Nonnull;

/**
 * Data structure representing an OData entity or complex type.
 */
public interface EntityAnnotationModel
{
    /**
     * Number of properties of the represented entity or complex type.
     *
     * @return the number of properties contained in the entity or complex type.
     */
    int getNumberOfProperties();

    /**
     * Name of the Java class that will represent this entity or complex type.
     *
     * @return the Java class name of this entity or complex type.
     */
    @Nonnull
    String getJavaClassName();
}
