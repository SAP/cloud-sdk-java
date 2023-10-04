/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Helper class to enable a {@link Supplier} based {@link Spliterator} implementation.
 *
 * @param <T>
 *            Generic type of produced items.
 */
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class MultipartSpliterator<T> implements Spliterator<T>
{
    static final int CHARACTERISTICS = IMMUTABLE | ORDERED | NONNULL;

    // if Supplier returns a null, the end is signalled
    private final Supplier<T> producer;

    @Override
    public boolean tryAdvance( final Consumer<? super T> action )
    {
        final T result = producer.get();
        if( result != null ) {
            action.accept(result);
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit()
    {
        return null; // don't support split
    }

    @Override
    public long estimateSize()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics()
    {
        return CHARACTERISTICS;
    }
}
