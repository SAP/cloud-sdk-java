/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating the precision and scale of a decimal field
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface DecimalDescriptor {

    /**
     * The associated precision of the decimal number
     *
     * @return The associated precision of the decimal number
     */
    int precision();

    /**
     * The associated scale of the decimal number
     *
     * @return The associated scale of the decimal number
     */
    int scale();
}
