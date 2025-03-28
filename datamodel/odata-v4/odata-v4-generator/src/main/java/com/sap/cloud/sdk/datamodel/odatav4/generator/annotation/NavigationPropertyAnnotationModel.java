package com.sap.cloud.sdk.datamodel.odatav4.generator.annotation;

import javax.annotation.Nonnull;

/**
 * Data structure representing an OData navigation property.
 */
public interface NavigationPropertyAnnotationModel
{
    /**
     * Name of the navigation property as defined in the OData EDM.
     *
     * @return the OData EDM name.
     */
    @Nonnull
    String getEdmName();

    /**
     * Checks whether the relation is 1..n (one-to-many), or not.
     *
     * @return {@code true} if this navigation is a one-to-many relation, {@code false} else.
     */
    boolean isManyMultiplicity();
}
