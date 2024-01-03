/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Default implementation of the {@code CollectedResultCollection} interface, allowing strongly typed access to specific
 * fields annotated with {@code ElementName} in elements of a {@code ResultCollection}.
 *
 * @see ResultCollection
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class DefaultCollectedResultCollection implements CollectedResultCollection
{
    /**
     * The common name of all elements addressed by this collection.
     */
    @Getter
    private final String collectedElementName;

    @Getter
    private final Iterable<ResultElement> resultElements;

    @Nonnull
    @Override
    public <T> List<T> asList( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException
    {
        return collectListFromObject(new GenericObjectExtractor<>(objectType));
    }

    @Nonnull
    private <T> List<T> collectListFromObject( @Nonnull final ObjectExtractor<T> extractor )
        throws UnsupportedOperationException
    {
        return collectListFromCollection(new GenericCollectionExtractor<>(extractor));
    }

    @Nonnull
    private <T> List<T> collectListFromCollection( @Nonnull final CollectionExtractor<List<T>, T> extractor )
        throws UnsupportedOperationException
    {
        return getCollection(extractor, Lists.newArrayList());
    }

    @Nonnull
    private <CollectionT extends Collection<T>, T> CollectionT getCollection(
        @Nonnull final CollectionExtractor<CollectionT, T> extractor,
        @Nonnull final CollectionT collection )
        throws UnsupportedOperationException
    {
        try {
            for( final ResultElement element : resultElements ) {
                if( element.isResultObject() ) {
                    final ResultElement nestedElement = element.getAsObject().get(collectedElementName);
                    collection.addAll(extractor.extract(nestedElement));
                }
            }
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(
                "Failed to collect nested elements with name " + collectedElementName + ".",
                e);
        }

        return collection;
    }

    @Nonnull
    @Override
    public <T> Set<T> asSet( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new GenericObjectExtractor<>(objectType));
    }

    @Nonnull
    private <T> Set<T> collectSetFromObject( @Nonnull final ObjectExtractor<T> extractor )
        throws UnsupportedOperationException
    {
        return collectSetFromCollection(resultElement -> Collections.singleton(extractor.extract(resultElement)));
    }

    @Nonnull
    private <T> Set<T> collectSetFromCollection( @Nonnull final CollectionExtractor<Set<T>, T> extractor )
        throws UnsupportedOperationException
    {
        return getCollection(extractor, Sets.newHashSet());
    }

    @Nonnull
    @Override
    public List<Boolean> asBooleanList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new BooleanExtractor());
    }

    @Nonnull
    @Override
    public Set<Boolean> asBooleanSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new BooleanExtractor());
    }

    @Nonnull
    @Override
    public List<Byte> asByteList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new ByteExtractor());
    }

    @Nonnull
    @Override
    public Set<Byte> asByteSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new ByteExtractor());
    }

    @Nonnull
    @Override
    public List<Character> asCharacterList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new CharacterExtractor());
    }

    @Nonnull
    @Override
    public Set<Character> asCharacterSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new CharacterExtractor());
    }

    @Nonnull
    @Override
    public List<String> asStringList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new StringExtractor());
    }

    @Nonnull
    @Override
    public Set<String> asStringSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new StringExtractor());
    }

    @Nonnull
    @Override
    public List<Integer> asIntegerList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new IntegerExtractor());
    }

    @Nonnull
    @Override
    public Set<Integer> asIntegerSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new IntegerExtractor());
    }

    @Nonnull
    @Override
    public List<Short> asShortList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new ShortExtractor());
    }

    @Nonnull
    @Override
    public Set<Short> asShortSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new ShortExtractor());
    }

    @Nonnull
    @Override
    public List<Long> asLongList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new LongExtractor());
    }

    @Nonnull
    @Override
    public Set<Long> asLongSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new LongExtractor());
    }

    @Nonnull
    @Override
    public List<Float> asFloatList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new FloatExtractor());
    }

    @Nonnull
    @Override
    public Set<Float> asFloatSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new FloatExtractor());
    }

    @Nonnull
    @Override
    public List<Double> asDoubleList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new DoubleExtractor());
    }

    @Nonnull
    @Override
    public Set<Double> asDoubleSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new DoubleExtractor());
    }

    @Nonnull
    @Override
    public List<BigInteger> asBigIntegerList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new BigIntegerExtractor());
    }

    @Nonnull
    @Override
    public Set<BigInteger> asBigIntegerSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new BigIntegerExtractor());
    }

    @Nonnull
    @Override
    public List<BigDecimal> asBigDecimalList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new BigDecimalExtractor());
    }

    @Nonnull
    @Override
    public Set<BigDecimal> asBigDecimalSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new BigDecimalExtractor());
    }

    /**
     * Returns an iterator over the elements of this collection.
     *
     * @return An iterator over the elements of this collection.
     */
    @Nonnull
    public Iterator<ResultElement> iterator()
    {
        return resultElements.iterator();
    }
}
