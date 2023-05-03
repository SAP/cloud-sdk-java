package com.sap.cloud.sdk.result;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;

/**
 * Extracts a {@code Collection} out of a given {@code ResultElement} by wrapping the result of an
 * {@code ObjectExtractor} into a {@code singletonList}.
 *
 * @param <T>
 *            The type of the content of the list to create.
 */
@RequiredArgsConstructor
public class GenericCollectionExtractor<T> implements CollectionExtractor<List<T>, T>
{
    private final ObjectExtractor<T> extractor;

    @Override
    @Nonnull
    public List<T> extract( @Nonnull final ResultElement resultElement )
    {
        return Collections.singletonList(extractor.extract(resultElement));
    }
}
