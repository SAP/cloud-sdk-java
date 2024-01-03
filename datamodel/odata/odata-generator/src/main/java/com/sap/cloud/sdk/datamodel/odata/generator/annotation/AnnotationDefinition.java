/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator.annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data structure to represent a Java annotation and its parameters. When implementing the {@link AnnotationStrategy}
 * interface, you have to construct instances of these classes. The VDM generator uses this to apply annotations to the
 * generated Java code.
 */
@EqualsAndHashCode
public class AnnotationDefinition
{
    @Getter
    private final Class<? extends Annotation> annotationClass;
    @Getter
    private final List<AnnotationParameter> annotationParameters;

    /**
     * Create a plain annotation definition with no annotation parameters.
     *
     * @param annotationClass
     *            Reference to the annotation class itself.
     */
    public AnnotationDefinition( @Nonnull final Class<? extends Annotation> annotationClass )
    {
        this.annotationClass = annotationClass;
        annotationParameters = new LinkedList<>();
    }

    /**
     * Create an annotation definition with the provided annotation parameters.
     *
     * @param annotationClass
     *            Reference to the annotation class itself.
     * @param parameters
     *            A set of parameters that the VDM generator should apply to this annotation.
     */
    public AnnotationDefinition(
        @Nonnull final Class<? extends Annotation> annotationClass,
        @Nonnull final AnnotationParameter... parameters )
    {
        this(annotationClass);
        Collections.addAll(annotationParameters, parameters);
    }

    /**
     * Adds the provided annotation parameter to the set of parameters in this instance.
     *
     * @param parameter
     *            The additional parameter that the VDM generator should apply to this annotation.
     */
    public void addAnnotationParameter( @Nonnull final AnnotationParameter parameter )
    {
        annotationParameters.add(parameter);
    }
}
