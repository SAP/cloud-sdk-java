/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data structure representing an OData navigation property.
 */
@Data
@AllArgsConstructor
final class NavigationPropertyModel
{
    /**
     * Name of the navigation property as defined in the OData EDM.
     */
    private String edmName;

    /**
     * Name of the return type of the navigation property as defined in the OData EDM.
     */
    private Service.EntityType returnEntityType;

    /**
     * Whether the relation is 0..1, 1..1, or 1..n
     */
    private Multiplicity multiplicity;

    /**
     * Name of the member variable that will represent this navigation property and store the associated entities.
     */
    private String javaMemberName;

    /**
     * Name of the fluent helper constant that represents this navigation property when using fluent helper query
     * operations.
     */
    private String javaConstantName;

    /**
     * Name that will be used for the fetch...() method. This is one of a set of helper methods for querying associated
     * entities.
     */
    private String javaMethodNameFetch;

    /**
     * Name that will be used for the get...OrNull() method. This is one of a set of helper methods for querying
     * associated entities.
     */
    private String javaMethodNameGetIfPresent;

    /**
     * Name that will be used for the get...OrFetch() method. This is one of a set of helper methods for querying
     * associated entities.
     */
    private String javaMethodNameGetOrFetch;

    /**
     * Name that will be used for the add...() method. This is a helper method to add associated entities.
     */
    private String javaMethodNameAdd;

    /**
     * Name that will be used for the set...() method. This is a helper method to set associated entities.
     */
    private String javaMethodNameSet;

    /**
     * Name that will be used for the builder method, for seamless integration with the Lombok {@link lombok.Builder}
     * pattern.
     */
    private String javaMethodNameSetBuilder;
}
