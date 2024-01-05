/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field in a VDM entity as being a key field. This means that the annotated field plus any other
 * fields with this annotation uniquely identify an instance of
 * {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity}. The VDM generator will add this annotation to the entity
 * classes it creates based on the OData EDMX ({@code <Key>} tag under {@code <EntityType>})
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface Key {
}
