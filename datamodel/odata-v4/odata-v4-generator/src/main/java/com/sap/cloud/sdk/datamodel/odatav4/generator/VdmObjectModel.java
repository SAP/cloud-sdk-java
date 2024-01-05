/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.util.Map;

import javax.annotation.Nullable;

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
     * Full qualified name of the entity or complex type as defined in the OData EDM.
     */
    private String edmNameFullyQualified;

    /**
     * If the entity or complex type in the OData EDM has a {@code sap:label} attribute, then its value is stored here.
     */
    private String edmLabel;

    /**
     * Name of the entity set in the OData EDM that contains this entity. For complex types, the value is always
     * {@code null}.
     */
    @Nullable
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
