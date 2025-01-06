/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import javax.annotation.Nullable;

/**
 * Factory interface to encapsulating different ways to create a {@code ResultElement} from an arbitrary object.
 *
 * @param <ResultElementT>
 *            The type of the object to create the {@code ResultElement} from.
 */
public interface ResultElementFactory<ResultElementT>
{
    /**
     * Creates a {@code ResultElement} based on the given object.
     *
     * @param resultElement
     *            The object to build the {@code ResultElement} from.
     * @return A {@code ResultElement} created from the given object.
     * @throws IllegalArgumentException
     *             If the given object could not be used to build a {@code ResultElement}.
     */
    @Nullable
    ResultElement create( @Nullable final ResultElementT resultElement )
        throws IllegalArgumentException;
}
