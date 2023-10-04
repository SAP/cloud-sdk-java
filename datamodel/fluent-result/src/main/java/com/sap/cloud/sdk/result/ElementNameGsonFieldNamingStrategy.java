/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;
import com.google.gson.FieldNamingStrategy;

/**
 * Implementation of GSON {@link FieldNamingStrategy} using the value from annotation {@link ElementName} when
 * serializing field names.
 */
public class ElementNameGsonFieldNamingStrategy implements FieldNamingStrategy
{
    @Override
    @Nonnull
    public String translateName( @Nonnull final Field field )
    {
        final ElementName annotation = field.getAnnotation(ElementName.class);
        final String name = field.getName();

        if( annotation == null ) {
            return name;
        }

        if( Strings.isNullOrEmpty(annotation.value()) ) {
            return name;
        }

        return annotation.value();
    }
}
