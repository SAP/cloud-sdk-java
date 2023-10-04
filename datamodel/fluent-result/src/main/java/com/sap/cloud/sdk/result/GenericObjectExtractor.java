/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;

/**
 * Extractor trying to generically extract an object out of a given {@code ResultElement}.
 *
 * @param <T>
 *            The type of the object to extract.
 */
@RequiredArgsConstructor
public class GenericObjectExtractor<T> implements ObjectExtractor<T>
{
    @Nonnull
    private final Class<T> objectType;

    @Nonnull
    @Override
    public T extract( @Nonnull final ResultElement resultElement )
        throws UnsupportedOperationException
    {
        if( resultElement.isResultObject() ) {
            return resultElement.getAsObject().as(objectType);
        }

        if( resultElement.isResultPrimitive() ) {
            // TODO introduce concept for registering type converters
            // - try to use a matching converter
            // - if no converter found, try using PrimitiveBasedObjectExtractor

            return new PrimitiveBasedObjectExtractor<>(objectType).extract(resultElement);
        }

        throw new UnsupportedOperationException(
            "Failed to convert "
                + ResultElement.class.getSimpleName()
                + " to type "
                + objectType.getName()
                + ": "
                + resultElement
                + ".");
    }
}
