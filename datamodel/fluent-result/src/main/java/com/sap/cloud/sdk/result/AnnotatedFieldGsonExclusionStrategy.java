package com.sap.cloud.sdk.result;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of GSON {@link ExclusionStrategy} excluding all fields <strong>not</strong> annotated with the given
 * annotation.
 * <p>
 * Typical use-case is the usage of an annotation like {@link ElementName}. JSON should only (de-)serialize fields
 * annotated with this annotation and ignore every other field.
 *
 * @param <AnnotationT>
 *            The type of the annotation to check for.
 */
@RequiredArgsConstructor
public class AnnotatedFieldGsonExclusionStrategy<AnnotationT extends Annotation> implements ExclusionStrategy
{
    @Nonnull
    private final Class<AnnotationT> annotationClass;

    @Override
    public boolean shouldSkipField( @Nonnull final FieldAttributes fieldAttributes )
    {
        return fieldAttributes.getAnnotation(annotationClass) == null;
    }

    @Override
    public boolean shouldSkipClass( @Nonnull final Class<?> cls )
    {
        return false;
    }
}
