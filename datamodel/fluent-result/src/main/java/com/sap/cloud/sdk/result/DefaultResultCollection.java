/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
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
 * This class provides an abstraction of a collection of result elements
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class DefaultResultCollection implements ResultCollection
{
    @Getter
    private final Iterable<ResultElement> resultElements;

    @Nonnull
    @Override
    public CollectedResultCollection collect( @Nonnull final String elementName )
    {
        return new DefaultCollectedResultCollection(elementName, resultElements);
    }

    /**
     * Returns a list of values from this instance. The values are represented as an object of the given type.
     * <p>
     * <strong>Important:</strong> In order to deserialize a result element to an object of a given type, you have to
     * annotate all relevant field members of the respective class with {@link ElementName}. For example:
     *
     * <pre>
     * <code>class MyObject
     * {
     *     {@literal @}ElementName( "MANDT" )
     *     SapClient sapClient;
     * }
     * </code>
     * </pre>
     *
     * @param objectType
     *            The type into which the values should be converted.
     *
     * @return The list of values as objects of type {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public <T> List<T> asList( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException
    {
        return collectListFromObject(new GenericObjectExtractor<>(objectType));
    }

    private <T> List<T> collectListFromObject( final ObjectExtractor<T> extractor )
        throws UnsupportedOperationException
    {
        return collectListFromCollection(new GenericCollectionExtractor<>(extractor));
    }

    private <T> List<T> collectListFromCollection( final CollectionExtractor<List<T>, T> extractor )
        throws UnsupportedOperationException
    {
        return getCollection(extractor, Lists.newArrayList());
    }

    private <CollectionT extends Collection<T>, T> CollectionT getCollection(
        final CollectionExtractor<CollectionT, T> extractor,
        final CollectionT collection )
        throws UnsupportedOperationException
    {
        try {
            for( final ResultElement element : resultElements ) {
                collection.addAll(extractor.extract(element));
            }
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException("Failed to collect nested elements.", e);
        }

        return collection;
    }

    /**
     * Returns a set of values from this instance. The values are represented as an object of the given type.
     * <p>
     * <strong>Important:</strong> In order to deserialize a result element to an object of a given type, you have to
     * annotate all relevant field members of the respective class with {@link ElementName}. For example:
     *
     * <pre>
     * <code>class MyObject
     * {
     *     {@literal @}ElementName( "MANDT" )
     *     SapClient sapClient;
     * }
     * </code>
     * </pre>
     *
     * @param objectType
     *            The type into which the values should be converted.
     *
     * @return The set of values as objects of type {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public <T> Set<T> asSet( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new GenericObjectExtractor<>(objectType));
    }

    private <T> Set<T> collectSetFromObject( final ObjectExtractor<T> extractor )
        throws UnsupportedOperationException
    {
        return collectSetFromCollection(resultElement -> Collections.singleton(extractor.extract(resultElement)));
    }

    private <T> Set<T> collectSetFromCollection( final CollectionExtractor<Set<T>, T> extractor )
        throws UnsupportedOperationException
    {
        return getCollection(extractor, Sets.newHashSet());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code Boolean}.
     *
     * @return The list of values as {@code Boolean}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<Boolean> asBooleanList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new BooleanExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code Boolean}.
     *
     * @return The set of values as {@code Boolean}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<Boolean> asBooleanSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new BooleanExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code Byte}.
     *
     * @return The list of values as {@code Byte}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<Byte> asByteList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new ByteExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code Byte}.
     *
     * @return The set of values as {@code Byte}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<Byte> asByteSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new ByteExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code Character}.
     *
     * @return The list of values as {@code Character}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<Character> asCharacterList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new CharacterExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code Character}.
     *
     * @return The set of values as {@code Character}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<Character> asCharacterSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new CharacterExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code String}.
     *
     * @return The list of values as {@code String}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<String> asStringList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new StringExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code String}.
     *
     * @return The set of values as {@code String}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<String> asStringSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new StringExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code Integer}.
     *
     * @return The list of values as {@code Integer}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<Integer> asIntegerList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new IntegerExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code Integer}.
     *
     * @return The set of values as {@code Integer}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<Integer> asIntegerSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new IntegerExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code Short}.
     *
     * @return The list of values as {@code Short}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<Short> asShortList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new ShortExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code Short}.
     *
     * @return The set of values as {@code Short}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<Short> asShortSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new ShortExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code Long}.
     *
     * @return The list of values as {@code Long}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<Long> asLongList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new LongExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code Long}.
     *
     * @return The set of values as {@code Long}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<Long> asLongSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new LongExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code Float}.
     *
     * @return The list of values as {@code Float}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<Float> asFloatList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new FloatExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code Float}.
     *
     * @return The set of values as {@code Float}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<Float> asFloatSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new FloatExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code Double}.
     *
     * @return The list of values as {@code Double}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<Double> asDoubleList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new DoubleExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code Double}.
     *
     * @return The set of values as {@code Double}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<Double> asDoubleSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new DoubleExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code BigInteger}.
     *
     * @return The list of values as {@code BigInteger}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<BigInteger> asBigIntegerList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new BigIntegerExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code BigInteger}.
     *
     * @return The set of values as {@code BigInteger}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<BigInteger> asBigIntegerSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new BigIntegerExtractor());
    }

    /**
     * Returns a list of values from this instance. The values are represented as {@code BigDecimal}.
     *
     * @return The list of values as {@code BigDecimal}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public List<BigDecimal> asBigDecimalList()
        throws UnsupportedOperationException
    {
        return collectListFromObject(new BigDecimalExtractor());
    }

    /**
     * Returns a set of values from this instance. The values are represented as {@code BigDecimal}.
     *
     * @return The set of values as {@code BigDecimal}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    @Override
    public Set<BigDecimal> asBigDecimalSet()
        throws UnsupportedOperationException
    {
        return collectSetFromObject(new BigDecimalExtractor());
    }

    @Override
    public boolean isResultPrimitive()
    {
        return false;
    }

    @Override
    public boolean isResultCollection()
    {
        return true;
    }

    @Override
    public boolean isResultObject()
    {
        return false;
    }

    @Nonnull
    @Override
    public ResultPrimitive getAsPrimitive()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Nonnull
    @Override
    public ResultCollection getAsCollection()
    {
        return this;
    }

    @Nonnull
    @Override
    public ResultObject getAsObject()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as object.");
    }

    @Override
    public boolean asBoolean()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Override
    public byte asByte()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Override
    public char asCharacter()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Nonnull
    @Override
    public String asString()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Override
    public int asInteger()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Override
    public short asShort()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Override
    public long asLong()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Override
    public float asFloat()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Override
    public double asDouble()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Nonnull
    @Override
    public BigInteger asBigInteger()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Nonnull
    @Override
    public BigDecimal asBigDecimal()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get collection as primitive.");
    }

    @Nonnull
    @Override
    public Iterator<ResultElement> iterator()
    {
        return resultElements.iterator();
    }
}
