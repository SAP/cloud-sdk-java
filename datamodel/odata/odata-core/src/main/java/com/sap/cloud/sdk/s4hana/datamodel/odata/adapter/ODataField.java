/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sap.cloud.sdk.typeconverter.TypeConverter;

/**
 * Annotation to be used to link fields to their OData property as well as converting between the domain type of a field
 * and the actually exposed type.
 */
@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface ODataField {
    /**
     * The name of the OData property this field gets mapped to.
     *
     * @return The name of the corresponding OData property.
     */
    String odataName();

    /**
     * The converter to be used to convert between the domain and the exposed type of the annotated field.
     *
     * @return The type of the converter to use for the annotated field.
     */
    Class<? extends TypeConverter<?, ?>> converter() default IdentityConverter.class;
}
