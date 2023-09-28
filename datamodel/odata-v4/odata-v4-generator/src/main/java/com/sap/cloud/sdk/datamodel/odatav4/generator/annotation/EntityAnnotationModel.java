package com.sap.cloud.sdk.datamodel.odatav4.generator.annotation;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

/**
 * Data structure representing an OData entity or complex type.
 */
@Beta
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
