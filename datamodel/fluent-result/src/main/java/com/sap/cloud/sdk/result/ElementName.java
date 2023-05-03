package com.sap.cloud.sdk.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating the name of an element.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ElementName {

    /**
     * The name by which the annotated field can be identified.
     *
     * @return The identifiable name of the field.
     */
    String value();
}
