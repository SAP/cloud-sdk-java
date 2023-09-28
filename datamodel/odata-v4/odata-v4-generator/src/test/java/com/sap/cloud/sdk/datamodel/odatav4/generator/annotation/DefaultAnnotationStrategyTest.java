/**
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator.annotation;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class DefaultAnnotationStrategyTest
{
    private static final int SOME_SMALL_NUMBER = 4;
    private static final int SOME_LARGE_NUMBER = 345;

    @Test
    public void testGetAnnotationsForEntityAsUsual()
    {
        final EntityAnnotationModel annotationModel = getEntityAnnotationModel("someJavaClassName", SOME_SMALL_NUMBER);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForEntity(annotationModel);

        Assertions
            .assertThat(result)
            .extracting("annotationClass")
            .containsOnly(
                Builder.class,
                Data.class,
                NoArgsConstructor.class,
                AllArgsConstructor.class,
                ToString.class,
                EqualsAndHashCode.class,
                JsonAdapter.class,
                JsonSerialize.class,
                JsonDeserialize.class);
    }

    @Test
    public void testGetAnnotationsForEntityWithTooManyProperties()
    {
        final EntityAnnotationModel annotationModel = getEntityAnnotationModel("someJavaClassName", SOME_LARGE_NUMBER);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForEntity(annotationModel);

        Assertions
            .assertThat(result)
            .extracting("annotationClass")
            .containsOnly(
                Data.class,
                ToString.class,
                EqualsAndHashCode.class,
                JsonAdapter.class,
                JsonSerialize.class,
                JsonDeserialize.class);
    }

    @Test
    public void testGetAnnotationsForEntityPropertyWithSimpleBooleanType()
    {
        final EntityPropertyAnnotationModel annotationModel =
            getNormalEntityPropertyAnnotationModel("someEdmName", true, "Boolean", null, null);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForEntityProperty(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(Nullable.class, ElementName.class);
    }

    @Test
    public void testGetAnnotationsForEntityKeyPropertyWithSimpleStringType()
    {
        final EntityPropertyAnnotationModel annotationModel =
            getKeyEntityPropertyAnnotationModel("someEdmName", true, "String", null, null);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForEntityProperty(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(Nullable.class, ElementName.class);
    }

    @Test
    public void testGetAnnotationsForEntityPropertyWithComplexType()
    {
        final EntityPropertyAnnotationModel annotationModel =
            getNormalEntityPropertyAnnotationModel("someEdmName", false, "someType", null, null);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForEntityProperty(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(Nullable.class, ElementName.class);
    }

    @Test
    public void testGetAnnotationsForEntityPropertyWithDateType()
    {
        final EntityPropertyAnnotationModel annotationModel =
            getNormalEntityPropertyAnnotationModel("someEdmName", true, "DateTime", null, null);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForEntityProperty(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(Nullable.class, ElementName.class);
    }

    @Test
    public void testGetAnnotationsForAssociatedEntityWithSingleMultiplicity()
    {
        final NavigationPropertyAnnotationModel annotationModel =
            getNavigationPropertyAnnotationModel("someEdmName", false);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForAssociatedEntity(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(Nullable.class, ElementName.class);
    }

    @Test
    public void testGetAnnotationsForAssociatedEntityWithManyMultiplicity()
    {
        final NavigationPropertyAnnotationModel annotationModel =
            getNavigationPropertyAnnotationModel("someEdmName", true);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForAssociatedEntity(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(ElementName.class);
    }

    @Test
    public void testGetAnnotationsForComplexTypeAsUsual()
    {
        final EntityAnnotationModel annotationModel = getEntityAnnotationModel("someJavaClassName", SOME_SMALL_NUMBER);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForComplexType(annotationModel);

        Assertions
            .assertThat(result)
            .extracting("annotationClass")
            .containsOnly(
                Builder.class,
                Data.class,
                NoArgsConstructor.class,
                AllArgsConstructor.class,
                ToString.class,
                EqualsAndHashCode.class,
                JsonAdapter.class,
                JsonSerialize.class,
                JsonDeserialize.class);
    }

    @Test
    public void testGetAnnotationsForComplexTypeWithTooManyProperties()
    {
        final EntityAnnotationModel annotationModel = getEntityAnnotationModel("someJavaClassName", SOME_LARGE_NUMBER);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForComplexType(annotationModel);

        Assertions
            .assertThat(result)
            .extracting("annotationClass")
            .containsOnly(
                Data.class,
                ToString.class,
                EqualsAndHashCode.class,
                JsonAdapter.class,
                JsonSerialize.class,
                JsonDeserialize.class);
    }

    @Test
    public void testGetAnnotationsForComplexTypePropertyWithSimpleBooleanType()
    {
        final EntityPropertyAnnotationModel annotationModel =
            getNormalEntityPropertyAnnotationModel("someEdmName", true, "Boolean", null, null);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForComplexTypeProperty(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(Nullable.class, ElementName.class);
    }

    @Test
    public void testGetAnnotationsForComplexTypePropertyWithComplexType()
    {
        final EntityPropertyAnnotationModel annotationModel =
            getNormalEntityPropertyAnnotationModel("someEdmName", false, "someEdmType", null, null);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForComplexTypeProperty(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(Nullable.class, ElementName.class);
    }

    @Test
    public void testGetAnnotationsForComplexTypePropertyWithDateType()
    {
        final EntityPropertyAnnotationModel annotationModel =
            getNormalEntityPropertyAnnotationModel("someEdmName", true, "DateTime", null, null);

        final AnnotationStrategy sut = new DefaultAnnotationStrategy();
        final Set<AnnotationDefinition> result = sut.getAnnotationsForComplexTypeProperty(annotationModel);

        Assertions.assertThat(result).extracting("annotationClass").containsOnly(Nullable.class, ElementName.class);
    }

    private static
        EntityAnnotationModel
        getEntityAnnotationModel( final String javaClassName, final int numberOfProperties )
    {
        return new EntityAnnotationModel()
        {
            @Override
            public int getNumberOfProperties()
            {
                return numberOfProperties;
            }

            @Nonnull
            @Override
            public String getJavaClassName()
            {
                return javaClassName;
            }
        };
    }

    private static EntityPropertyAnnotationModel getNormalEntityPropertyAnnotationModel(
        final String edmName,
        final boolean isSimpleType,
        final String edmType,
        final Integer precision,
        final Integer scale )
    {
        return getEntityPropertyAnnotationModel(edmName, isSimpleType, edmType, false, precision, scale);
    }

    private static EntityPropertyAnnotationModel getKeyEntityPropertyAnnotationModel(
        final String edmName,
        final boolean isSimpleType,
        final String edmType,
        final Integer precision,
        final Integer scale )
    {
        return getEntityPropertyAnnotationModel(edmName, isSimpleType, edmType, true, precision, scale);
    }

    private static EntityPropertyAnnotationModel getEntityPropertyAnnotationModel(
        final String edmName,
        final boolean isSimpleType,
        final String edmType,
        boolean isKeyField,
        Integer precision,
        Integer scale )
    {
        return new EntityPropertyAnnotationModel()
        {
            @Nonnull
            @Override
            public String getEdmName()
            {
                return edmName;
            }

            @Override
            public boolean isSimpleType()
            {
                return isSimpleType;
            }

            @Nonnull
            @Override
            public String getEdmType()
            {
                return edmType;
            }

            @Override
            public boolean isKeyField()
            {
                return isKeyField;
            }

            @Override
            public Integer getPrecision()
            {
                return precision;
            }

            @Override
            public Integer getScale()
            {
                return scale;
            }
        };
    }

    private static
        NavigationPropertyAnnotationModel
        getNavigationPropertyAnnotationModel( final String edmName, final boolean isManyMultiplicity )
    {
        return new NavigationPropertyAnnotationModel()
        {
            @Nonnull
            @Override
            public String getEdmName()
            {
                return edmName;
            }

            @Override
            public boolean isManyMultiplicity()
            {
                return isManyMultiplicity;
            }
        };
    }
}
