/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.Set;

import com.sap.cloud.sdk.datamodel.odata.generator.annotation.AnnotationDefinition;
import com.sap.cloud.sdk.datamodel.odata.generator.annotation.AnnotationParameter;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;

class AnnotationHelper
{

    /**
     * Adds the annotation defined by this object to something that is created in CodeModel and accepts annotations. In
     * other words, an object that implements {@link JAnnotatable}
     *
     * NOTE: This method has the side effect of modifying the object that is passed in. First it calls the
     * {@code annotate()} method of {@link JAnnotatable} to add the annotation. Then it will call the {@code param()}
     * methods of {@link JAnnotationUse} that match the type of each annotation parameter value.
     */
    // Due to CodeModel limitations it's only possible to have a method that modifies the object parameter (side effect).
    static void addAnnotationToJavaItem( final AnnotationDefinition annotation, final JAnnotatable javaItem )
    {
        final JAnnotationUse appliedAnnotation = javaItem.annotate(annotation.getAnnotationClass());

        for( final AnnotationParameter parameter : annotation.getAnnotationParameters() ) {
            final Object parameterValue = parameter.getValue();

            if( parameterValue instanceof Class<?> ) {
                appliedAnnotation.param(parameter.getName(), (Class<?>) parameterValue);
            } else if( parameterValue instanceof Enum<?> ) {
                appliedAnnotation.param(parameter.getName(), (Enum<?>) parameterValue);
            } else if( parameterValue instanceof String ) {
                appliedAnnotation.param(parameter.getName(), (String) parameterValue);
            } else if( parameterValue instanceof Character ) {
                appliedAnnotation.param(parameter.getName(), (Character) parameterValue);
            } else if( parameterValue instanceof Byte ) {
                appliedAnnotation.param(parameter.getName(), (Byte) parameterValue);
            } else if( parameterValue instanceof Boolean ) {
                appliedAnnotation.param(parameter.getName(), (Boolean) parameterValue);
            } else if( parameterValue instanceof Double ) {
                appliedAnnotation.param(parameter.getName(), (Double) parameterValue);
            } else if( parameterValue instanceof Float ) {
                appliedAnnotation.param(parameter.getName(), (Float) parameterValue);
            } else if( parameterValue instanceof Long ) {
                appliedAnnotation.param(parameter.getName(), (Long) parameterValue);
            } else if( parameterValue instanceof Integer ) {
                appliedAnnotation.param(parameter.getName(), (Integer) parameterValue);
            } else if( parameterValue instanceof Short ) {
                appliedAnnotation.param(parameter.getName(), (Short) parameterValue);
            } else {
                throw new ODataGeneratorException(String.format("""
                    Annotation parameter value for %s has an unsupported type of %s.\
                    Please make sure to only use the exposed constructors of the AnnotationParameter class.\
                    """, parameter.getName(), parameterValue.getClass().getName()));
            }
        }
    }

    static void addAllAnnotationsToJavaItem( final Set<AnnotationDefinition> annotations, final JAnnotatable javaItem )
    {
        for( final AnnotationDefinition ad : annotations ) {
            addAnnotationToJavaItem(ad, javaItem);
        }
    }
}
