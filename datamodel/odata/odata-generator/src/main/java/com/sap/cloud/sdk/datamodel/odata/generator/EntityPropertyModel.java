/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import com.sun.codemodel.JType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data structure representing an OData property of an entity or complex type.
 */
@Data
@AllArgsConstructor
final class EntityPropertyModel
{
    /**
     * Name of the property as defined in the OData EDM.
     */
    private String edmName;

    /**
     * If the property in the OData EDM has a {@code sap:label} attribute, then its value is stored here.
     */
    private String edmLabel;

    /**
     * OData EDM type of the property as defined in the OData EDM.
     */
    private String edmType;

    /**
     * Name of the member variable that will represent this property.
     */
    private String javaFieldName;

    /**
     * Java type of the member variable that will represent this property.
     */
    private JType javaFieldType;

    /**
     * Name of the fluent helper constant that represents this property when using fluent helper query operations.
     */
    private String javaConstantName;

    /**
     * Value is {@code true} if this property is one of the OData simple types, and {@code false} if it is an OData
     * complex type.
     */
    private boolean isSimpleType;

    /**
     * Value is {@code true} if this property is defined in the OData EDM as being a key property, {@code false}
     * otherwise.
     */
    private boolean isKeyField;

    /**
     * If the property in the OData EDM contains a {@code Documentation} tag with a {@code Summary} sub-tag, then the
     * text inside is read and stored here. If no such text is found, then it falls back to the {@code sap:quickinfo}
     * attribute, and then finally to the {@code sap:label} attribute.
     */
    private String basicDescription;

    /**
     * If the property in the OData EDM contains a {@code Documentation} tag with a {@code LongDescription} tag, then
     * the text is read and stored here.
     */
    private String detailedDescription;

    /**
     * If the property in the OData EDM has information about constraints such as the {@code Nullable} and
     * {@code MaxLength} attributes, then they are read and formatted into a javadoc string here.
     */
    private String constraintsDescription;
}
