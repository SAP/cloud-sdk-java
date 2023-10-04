/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Data structure representing an OData entity or complex type.
 */
@Data
@AllArgsConstructor
final class VdmObjectModel
{
    /**
     * Name of the entity or complex type as defined in the OData EDM.
     */
    private String edmName;

    /**
     * If the entity or complex type in the OData EDM has a {@code sap:label} attribute, then its value is stored here.
     */
    private String edmLabel;

    /**
     * Relative URL path to access the OData service where this entity is defined. For complex types, the value is
     * always {@code null}.
     */
    private String edmEndpointPath;

    /**
     * Name of the entity set in the OData EDM that contains this entity. For complex types, the value is always
     * {@code null}.
     */
    private String edmEntityCollectionName;

    /**
     * Name of the Java class that will represent this entity or complex type.
     */
    private String javaClassName;

    /**
     * Map representing the properties of an entity or complex type.
     */
    private Map<String, EntityPropertyModel> properties;

    /**
     * Text that goes into the javadoc of the entity or complex type class (class level).
     */
    private String description;

    /**
     * Whether this entity has a media stream (file) that is accessible through /$value
     */
    @Getter
    private boolean mediaStreamExposed;
}
