/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Interface representing one collection of result elements (e.g. a list of complex business objects) resulting from a
 * call to an external service (e.g. after invoking a BAPI or a remote-enabled function module).
 * <p>
 * Use the method {@link #collect(String)} to collect elements inside this object as {@link CollectedResultCollection}
 * identified by a certain name.
 * <p>
 * Use the method {@link #asList(Class)} to cast this collection into a list of instances of a certain class type.
 * <p>
 * Use the method {@link #asSet(Class)} to cast this collection into a set of instances of a certain class type.
 * <p>
 * To cast this collection into a set or list of built-in data and object types, use methods such as
 * {@link #asStringList()} or {@link #asStringSet()}.
 */
public interface ResultCollection extends ResultElement, Iterable<ResultElement>
{
    /**
     * Collects elements inside this {@link ResultCollection} identified by {@code filterName} and returns a
     * {@link CollectedResultCollection} that contains only the collected elements.
     *
     * @param filterName
     *            The name used to identify the items to collect.
     *
     * @return Instance of {@link CollectedResultCollection} that contains only the collected elements.
     */
    @Nonnull
    CollectedResultCollection collect( @Nonnull final String filterName );

    /**
     * Casts all elements within this {@link ResultCollection} into type {@code T} and returns a {@link List} of them.
     *
     * @param objectType
     *            The {@link Class} type of the type {@code T}.
     * @param <T>
     *            The type the elements in this collection object shall be casted to.
     *
     * @return A {@link List} of all elements within this {@link ResultCollection} casted into class type {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code T}.
     */
    @Nonnull
    <T> List<T> asList( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException;

    /**
     * Casts all elements within this {@link ResultCollection} into type {@code T} and returns a {@link Set} of them.
     *
     * @param objectType
     *            The {@link Class} type of the type {@code T}.
     * @param <T>
     *            The type the elements in this collection object shall be casted to.
     *
     * @return A {@link Set} of all elements within this {@link ResultCollection} casted into class type {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code T}.
     */
    @Nonnull
    <T> Set<T> asSet( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link Boolean}.
     *
     * @return {@link List} of {@link Boolean} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code Boolean}.
     */
    @Nonnull
    List<Boolean> asBooleanList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link Boolean}.
     *
     * @return {@link Set} of {@link Boolean} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code Boolean}.
     */
    @Nonnull
    Set<Boolean> asBooleanSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link Byte}.
     *
     * @return {@link List} of {@link Byte} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code Byte}.
     */
    @Nonnull
    List<Byte> asByteList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link Byte}.
     *
     * @return {@link Set} of {@link Byte} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code Byte}.
     */
    @Nonnull
    Set<Byte> asByteSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link Character}.
     *
     * @return {@link List} of {@link Character} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code Character}.
     */
    @Nonnull
    List<Character> asCharacterList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link Character}.
     *
     * @return {@link Set} of {@link Character} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code Character}.
     */
    @Nonnull
    Set<Character> asCharacterSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link String}.
     *
     * @return {@link List} of {@link String} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code String}.
     */
    @Nonnull
    List<String> asStringList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link String}.
     *
     * @return {@link Set} of {@link String} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code String}.
     */
    @Nonnull
    Set<String> asStringSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link Integer}.
     *
     * @return {@link List} of {@link Integer} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code Integer}.
     */
    @Nonnull
    List<Integer> asIntegerList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link Integer}.
     *
     * @return {@link Set} of {@link Integer} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code Integer}.
     */
    @Nonnull
    Set<Integer> asIntegerSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link Short}.
     *
     * @return {@link List} of {@link Short} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code Short}.
     */
    @Nonnull
    List<Short> asShortList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link Short}.
     *
     * @return {@link Set} of {@link Short} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code Short}.
     */
    @Nonnull
    Set<Short> asShortSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link Long}.
     *
     * @return {@link List} of {@link Long} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code Long}.
     */
    @Nonnull
    List<Long> asLongList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link Long}.
     *
     * @return {@link Set} of {@link Long} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code Long}.
     */
    @Nonnull
    Set<Long> asLongSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link Float}.
     *
     * @return {@link List} of {@link Float} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code Float}.
     */
    @Nonnull
    List<Float> asFloatList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link Float}.
     *
     * @return {@link Set} of {@link Float} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code Float}.
     */
    @Nonnull
    Set<Float> asFloatSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link Double}.
     *
     * @return {@link List} of {@link Double} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code Double}.
     */
    @Nonnull
    List<Double> asDoubleList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link Double}.
     *
     * @return {@link Set} of {@link Double} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code Double}.
     */
    @Nonnull
    Set<Double> asDoubleSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link BigInteger}.
     *
     * @return {@link List} of {@link BigInteger} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code BigInteger}.
     */
    @Nonnull
    List<BigInteger> asBigIntegerList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link BigInteger}.
     *
     * @return {@link Set} of {@link BigInteger} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code BigInteger}.
     */
    @Nonnull
    Set<BigInteger> asBigIntegerSet()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link List} of all their values represented as {@link BigDecimal}.
     *
     * @return {@link List} of {@link BigDecimal} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code List} or at least one element could not be
     *             cast to {@code BigDecimal}.
     */
    @Nonnull
    List<BigDecimal> asBigDecimalList()
        throws UnsupportedOperationException;

    /**
     * Assuming all elements within this {@link ResultCollection} are instances of {@link ResultPrimitive}, this method
     * returns a {@link Set} of all their values represented as {@link BigDecimal}.
     *
     * @return {@link Set} of {@link BigDecimal} objects.
     *
     * @throws UnsupportedOperationException
     *             If this class does not support the creation of a {@code Set} or at least one element could not be
     *             cast to {@code BigDecimal}.
     */
    @Nonnull
    Set<BigDecimal> asBigDecimalSet()
        throws UnsupportedOperationException;
}
