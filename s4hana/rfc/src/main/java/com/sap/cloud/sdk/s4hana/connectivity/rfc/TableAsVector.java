package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A representation of a generic table as single-column vector. Usually used with domain-types as table row-type.
 *
 * @param <RequestT>
 *            The invoking request to be returned.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
@Deprecated
public class TableAsVector<RequestT>
{
    @Nonnull
    private final RequestT request;

    @Nonnull
    private final List<List<Value<?>>> cells;

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @param <T>
     *            The type of the value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public <T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>> TableAsVector<RequestT> row(
        @Nonnull final T value )
    {
        cells.add(Collections.singletonList(Value.ofVectorElement(value, null)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @param typeConverter
     *            A custom type converter.
     * @param <T>
     *            The type of the value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public <T> TableAsVector<RequestT> row(
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        cells.add(Collections.singletonList(Value.ofVectorElement(value, typeConverter)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Boolean value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value
                            .ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Byte value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value.ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Character value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value
                            .ofVectorElement(
                                value,
                                com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final String value )
    {
        cells.add(Collections.singletonList(Value.ofVectorElement(value, null)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Short value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value.ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Integer value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value
                            .ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Long value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value.ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Float value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value.ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Double value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value.ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final BigInteger value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value
                            .ofVectorElement(
                                value,
                                com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final BigDecimal value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value
                            .ofVectorElement(
                                value,
                                com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value.ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final LocalDate value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value
                            .ofVectorElement(
                                value,
                                com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final LocalTime value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value
                            .ofVectorElement(
                                value,
                                com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter.INSTANCE)));
        return this;
    }

    /**
     * Creates a new {@code Value} based object as element for the given vector.
     *
     * @param value
     *            The vector element value.
     * @return This {@link TableAsVector} to facilitate a fluent interface.
     */
    @Nonnull
    public TableAsVector<RequestT> row( @Nullable final Locale value )
    {
        cells
            .add(
                Collections
                    .singletonList(
                        Value.ofVectorElement(value, com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE)));
        return this;
    }

    /**
     * Returns the initial request, finalizing the access to this {@code Table}.
     *
     * @return The original request.
     */
    @Nonnull
    public RequestT end()
    {
        return request;
    }
}
