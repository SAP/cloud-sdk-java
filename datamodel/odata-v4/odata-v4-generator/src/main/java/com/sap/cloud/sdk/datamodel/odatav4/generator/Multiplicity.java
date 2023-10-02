/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

/**
 * Enumeration representing the multiplicity of an OData navigation property.
 */
enum Multiplicity
{
    /**
     * Zero or one associated entity.
     */
    ZERO_TO_ONE,

    /**
     * One to many entities.
     */
    MANY,

    /**
     * One to one entity.
     */
    ONE
}
