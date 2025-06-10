package com.sap.cloud.sdk.cloudplatform.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the priority of a class.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Priority {
    /**
     * The priority value.
     *
     * @return the priority value
     */
    int value();
}
