/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A collection consisting of elements collected by their {@code ElementName} via the
 * {@link ResultCollection#collect(String)} method. This class offers several methods to access the content in a
 * strongly typed manner.
 *
 * @see ResultCollection
 */
public interface CollectedResultCollection
{
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
     * @param <T>
     *            The type into which the values should be converted.
     * @param objectType
     *            Class object of the type into which the values should be converted.
     *
     * @return The list of values as objects of type {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    <T> List<T> asList( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException;

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
     * @param <T>
     *            The type into which the values should be converted.
     * @param objectType
     *            Class object of the type into which the values should be converted.
     *
     * @return The set of values as objects of type {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    <T> Set<T> asSet( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code Boolean}.
     *
     * @return The list of values as {@code Boolean}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<Boolean> asBooleanList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code Boolean}.
     *
     * @return The set of values as {@code Boolean}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<Boolean> asBooleanSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code Byte}.
     *
     * @return The list of values as {@code Byte}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<Byte> asByteList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code Byte}.
     *
     * @return The set of values as {@code Byte}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<Byte> asByteSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code Character}.
     *
     * @return The list of values as {@code Character}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<Character> asCharacterList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code Character}.
     *
     * @return The set of values as {@code Character}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<Character> asCharacterSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code String}.
     *
     * @return The list of values as {@code String}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<String> asStringList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code String}.
     *
     * @return The set of values as {@code String}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<String> asStringSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code Integer}.
     *
     * @return The list of values as {@code Integer}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<Integer> asIntegerList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code Integer}.
     *
     * @return The set of values as {@code Integer}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<Integer> asIntegerSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code Short}.
     *
     * @return The list of values as {@code Short}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<Short> asShortList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code Short}.
     *
     * @return The set of values as {@code Short}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<Short> asShortSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code Long}.
     *
     * @return The list of values as {@code Long}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<Long> asLongList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code Long}.
     *
     * @return The set of values as {@code Long}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<Long> asLongSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code Float}.
     *
     * @return The list of values as {@code Float}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<Float> asFloatList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code Float}.
     *
     * @return The set of values as {@code Float}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<Float> asFloatSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code Double}.
     *
     * @return The list of values as {@code Double}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<Double> asDoubleList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code Double}.
     *
     * @return The set of values as {@code Double}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<Double> asDoubleSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code BigInteger}.
     *
     * @return The list of values as {@code BigInteger}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<BigInteger> asBigIntegerList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code BigInteger}.
     *
     * @return The set of values as {@code BigInteger}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<BigInteger> asBigIntegerSet()
        throws UnsupportedOperationException;

    /**
     * Returns a list of values from this instance. The values are represented as {@code BigDecimal}.
     *
     * @return The list of values as {@code BigDecimal}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    List<BigDecimal> asBigDecimalList()
        throws UnsupportedOperationException;

    /**
     * Returns a set of values from this instance. The values are represented as {@code BigDecimal}.
     *
     * @return The set of values as {@code BigDecimal}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be found or could not be converted to the given type.
     */
    @Nonnull
    Set<BigDecimal> asBigDecimalSet()
        throws UnsupportedOperationException;
}
