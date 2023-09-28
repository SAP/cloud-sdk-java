package com.sap.cloud.sdk.result;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;

/**
 * Class representing one resulting element from a call to an external service (e.g. after invoking a BAPI or a
 * remote-enabled function module).
 * <p>
 * Note that accessing the result element varies depending on its structure:
 * <ul>
 * <li>If the result element represents an unstructured value (e.g. a String object), it is considered a
 * {@link ResultPrimitive}. <br>
 * When dealing with BAPI or RFM request results, this occurs if the respective result element is reflected as a data
 * element in the ABAP Data Dictionary.</li>
 * <li>If the result element represents a structured object (e.g. one complex business object), it is considered a
 * {@link ResultObject}. <br>
 * When dealing with BAPI or RFM request results, this occurs if the respective result element is reflected as a
 * structure in the ABAP Data Dictionary.</li>
 * <li>If the result element represents a collection of result elements (e.g. a list of complex business objects), it is
 * considered a {@link ResultCollection}. <br>
 * When dealing with BAPI or RFM request results, this occurs if the respective result element is reflected as a table
 * type in the ABAP Data Dictionary or is used as tables parameter in the interface of the BAPI or RFM.</li>
 * </ul>
 * Use the methods {@link #isResultPrimitive()}, {@link #isResultObject()}, or {@link #isResultCollection()} to check
 * for the respective case.
 * <p>
 * As accessing the content of the result element varies depending on its structure, use the methods
 * {@link #getAsPrimitive()}, {@link #getAsObject()}, or {@link #getAsCollection()} to access the result element
 * content.
 * <p>
 * If the result element is considered a {@link ResultPrimitive}, its content can be accessed with the respective method
 * directly, such as {@link #asString()} to obtain the result element content as String object.
 */
public interface ResultElement
{
    /**
     * Checks whether this {@link ResultElement} represents an unstructured value and, therefore, is a
     * {@link ResultPrimitive}.
     *
     * @return True of this object is a {@link ResultPrimitive}, otherwise false.
     */
    boolean isResultPrimitive();

    /**
     * Checks whether this {@link ResultElement} represents a collection of result elements and, therefore, is a
     * {@link ResultCollection}.
     *
     * @return True of this object is a {@link ResultCollection}, otherwise false.
     */
    boolean isResultCollection();

    /**
     * Checks whether this {@link ResultElement} represents a structured object and, therefore, is a
     * {@link ResultObject}.
     *
     * @return True of this object is a {@link ResultObject}, otherwise false.
     */
    boolean isResultObject();

    /**
     * Returns this {@link ResultElement} as an instance of {@link ResultPrimitive}.
     *
     * @return An instance of {@link ResultPrimitive}.
     *
     * @throws UnsupportedOperationException
     *             If the cast into {@link ResultPrimitive} failed, e.g. in case it is not a primitive.
     */
    @Nonnull
    ResultPrimitive getAsPrimitive()
        throws UnsupportedOperationException;

    /**
     * Returns this {@link ResultElement} as an instance of {@link ResultCollection}.
     *
     * @return An instance of {@link ResultCollection}.
     *
     * @throws UnsupportedOperationException
     *             If the cast into {@link ResultCollection} failed, e.g. in case it is not a collection.
     */
    @Nonnull
    ResultCollection getAsCollection()
        throws UnsupportedOperationException;

    /**
     * Returns this {@link ResultElement} as an instance of {@link ResultObject}.
     *
     * @return An instance of {@link ResultObject}.
     *
     * @throws UnsupportedOperationException
     *             If the cast into {@link ResultObject} failed, e.g. in case it is not an object.
     */
    @Nonnull
    ResultObject getAsObject()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code boolean}.
     *
     * @return {@code boolean} value.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code boolean}.
     */
    boolean asBoolean()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code byte}.
     *
     * @return {@code byte} value.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code byte}.
     */
    byte asByte()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code char}.
     *
     * @return {@code char} value.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code char}.
     */
    char asCharacter()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code String}.
     *
     * @return {@code String} object.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code String}.
     */
    @Nonnull
    String asString()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code int}.
     *
     * @return {@code int} value.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code int}.
     */
    int asInteger()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code short}.
     *
     * @return {@code short} value.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code short}.
     */
    short asShort() //NOPMD
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code long}.
     *
     * @return {@code long} value.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code long}.
     */
    long asLong()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code float}.
     *
     * @return {@code float} value.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code float}.
     */
    float asFloat()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@code double}.
     *
     * @return {@code double} value.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@code double}.
     */
    double asDouble()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@link BigInteger}.
     *
     * @return {@link BigInteger} object.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@link BigInteger}.
     */
    @Nonnull
    BigInteger asBigInteger()
        throws UnsupportedOperationException;

    /**
     * In case this {@link ResultElement} is considered a {@link ResultPrimitive}, use this method to access its value
     * as {@link BigDecimal}.
     *
     * @return {@link BigDecimal} object.
     *
     * @throws UnsupportedOperationException
     *             If this {@link ResultElement} is not considered a {@link ResultPrimitive} or its value cannot be
     *             represented as {@link BigDecimal}.
     */
    @Nonnull
    BigDecimal asBigDecimal()
        throws UnsupportedOperationException;
}
