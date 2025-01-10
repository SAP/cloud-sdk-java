package com.sap.cloud.sdk.result;

import java.util.Collection;

import javax.annotation.Nonnull;

/**
 * Functional interface handling the type-safe transformation of a {@code ResultElement} to a collections of a specific
 * type.
 *
 * @param <CollectionT>
 *            The type of the collection to extract.
 * @param <T>
 *            The type of the values contained in the collection.
 */
public interface CollectionExtractor<CollectionT extends Collection<T>, T>
{
    /**
     * Transforms the given {@code ResultElement} into a {@code Collection}.
     *
     * @param resultElement
     *            The {@code ResultElement} to transform.
     * @return A {@code Collection} containing the content of the given {@code ResultElement}.
     */
    @Nonnull
    CollectionT extract( @Nonnull final ResultElement resultElement );
}
