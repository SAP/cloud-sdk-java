/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * Functional interface handling the type-safe transformation of a {@code ResultElement} to a specific type.
 *
 * @param <T>
 *            The type of the value to extract.
 */
public interface ObjectExtractor<T>
{
    /**
     * Extracts the value of the given {@code ResultElement} as the type {@code T}.
     *
     * @param resultElement
     *            The {@code ResultElement} to transform.
     *
     * @return The {@code resultElement} as the given type.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be converted to the given type.
     */
    @Nonnull
    T extract( @Nonnull final ResultElement resultElement )
        throws UnsupportedOperationException;
}
