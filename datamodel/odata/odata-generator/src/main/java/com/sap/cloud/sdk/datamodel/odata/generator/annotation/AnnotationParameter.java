/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator.annotation;

import javax.annotation.Nonnull;

import lombok.Getter;

/**
 * Data structure to represent a parameter of a Java annotation. When implementing the {@link AnnotationStrategy}
 * interface, you add instances of this class to {@link AnnotationDefinition} instances. The VDM generator uses this to
 * add parameters to annotations while generating Java code. <b>NOTE: Only primitive types, {@link String}, {@link Enum}
 * , and {@link Class} are supported.</b>
 */
public class AnnotationParameter
{
    @Getter
    private final String name;
    @Getter
    private final Object value;

    private AnnotationParameter( @Nonnull final String name, @Nonnull final Object value )
    {
        this.name = name;
        this.value = value;
    }

    // Done this way to restrict users to just the supported annotation parameters.
    // At least CodeModel (specifically JAnnotationUse.param()) does not support arbitrary types.

    /**
     * Creates an annotation parameter with a {@link Class} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, @Nonnull final Class<?> value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with an {@link Enum} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, @Nonnull final Enum<?> value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with a {@link String} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, @Nonnull final String value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with a {@code char} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, final char value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with a {@code byte} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, final byte value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with a {@code boolean} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, final boolean value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with a {@code double} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, final double value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with a {@code float} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, final float value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with a {@code long} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, final long value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with an {@code int} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, final int value )
    {
        this(name, (Object) value);
    }

    /**
     * Creates an annotation parameter with a {@code short} value.
     *
     * @param name
     *            Name of the annotation parameter. For cases where you want to set the single default parameter, use
     *            "value".
     * @param value
     *            The value to use in the parameter of the annotation itself.
     */
    public AnnotationParameter( @Nonnull final String name, final short value )
    {
        this(name, (Object) value);
    }
}
