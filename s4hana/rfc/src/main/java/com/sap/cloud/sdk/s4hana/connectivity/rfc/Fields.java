/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Stores a list of values.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Getter( AccessLevel.PACKAGE )
@EqualsAndHashCode
@ToString
@Deprecated
public class Fields
{
    final ArrayList<Value<?>> valueList = new ArrayList<>();

    /**
     * Creates a new instance of {@link Fields}.
     *
     * @return A new instance of {@link Fields}.
     */
    @Nonnull
    public static Fields fields()
    {
        return new Fields();
    }

    /**
     * Returns the size of this {@link Fields}.
     *
     * @return The size of this {@link Fields}.
     */
    public int size()
    {
        return valueList.size();
    }

    /**
     * Returns whether this {@link Fields} is empty.
     *
     * @return Whether this {@link Fields} is empty.
     */
    public boolean isEmpty()
    {
        return valueList.isEmpty();
    }

    /**
     * Adds a new field to this {@link Fields}.
     *
     * @param name
     *            The name of the field.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return This {@link Fields}.
     * @param <T>
     *            The type of the value.
     */
    @Nonnull
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        Fields
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final T value )
    {
        valueList.add(Value.ofField(name, value, null));
        return this;
    }

    /**
     * Adds a new field to this {@link Fields}.
     *
     * @param name
     *            The name of the field.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @param typeConverter
     *            The type converter to use.
     * @return This {@link Fields}.
     * @param <T>
     *            The type of the value.
     */
    @Nonnull
    public <T> Fields field(
        @Nonnull final String name,
        @Nullable final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        valueList.add(Value.ofField(name, value, typeConverter));
        return this;
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Boolean value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Byte value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Character value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final String value )
    {
        return field(name, dataType, value, null);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Short value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Integer value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Long value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Float value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Double value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final BigInteger value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final BigDecimal value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field(
        @Nonnull final String name,
        @Nullable final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final LocalDate value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE);
    }

    /**
     * Adds a new structure to this {@link Fields}.
     *
     * @param name
     *            The name of the structure.
     * @param dataType
     *            The data type of the structure.
     * @param value
     *            The value of the structure.
     * @return This {@link Fields}.
     */
    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Locale value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE);
    }
}
