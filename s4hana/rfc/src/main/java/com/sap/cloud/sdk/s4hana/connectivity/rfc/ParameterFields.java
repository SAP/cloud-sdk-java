/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Create requests from fields.
 *
 * @param <RequestT>
 *            The generic type of the request.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
@Deprecated
public class ParameterFields<RequestT>
{
    @Nonnull
    private final RequestT request;

    @Nonnull
    private final List<Value<?>> valueList;

    /**
     * Returns the number of fields.
     *
     * @return The number of fields.
     */
    public int size()
    {
        return valueList.size();
    }

    /**
     * Returns whether the fields are empty.
     *
     * @return Whether the fields are empty.
     */
    public boolean isEmpty()
    {
        return valueList.isEmpty();
    }

    @Nonnull
    private <T> ParameterFields<RequestT> field( @Nonnull final Value<T> value )
    {
        valueList.add(value);
        return this;
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     * @param <T>
     *            The generic value type.
     */
    @Nonnull
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final T value )
    {
        return field(Value.ofField(name, value, null));
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, {@code value}, and {@code typeConverter}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @param typeConverter
     *            The converter to convert the value to an ERP type.
     * @return The ParameterFields to allow for fluent formulation.
     * @param <T>
     *            The generic value type.
     */
    @Nonnull
    public <T> ParameterFields<RequestT> field(
        @Nonnull final String name,
        @Nullable final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        return field(Value.ofField(name, value, typeConverter));
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Boolean value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Byte value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Character value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final String value )
    {
        return field(name, dataType, value, null);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Short value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Integer value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Long value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Float value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Double value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final BigInteger value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final BigDecimal value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public ParameterFields<RequestT> field(
        @Nonnull final String name,
        @Nullable final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final LocalDate value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final LocalTime value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter.INSTANCE);
    }

    /**
     * Adds a field with the given {@code name}, {@code dataType}, and {@code value}.
     *
     * @param name
     *            The field name.
     * @param dataType
     *            The data type of the field.
     * @param value
     *            The value of the field.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Locale value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE);
    }

    /**
     * Adds the given fields.
     *
     * @param other
     *            The first field to add.
     * @param others
     *            The other fields to add.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public ParameterFields<RequestT> fields( @Nonnull final Fields other, @Nonnull final Fields... others )
    {
        for( final Value<?> value : other.valueList ) {
            field(value);
        }

        for( final Fields fields : others ) {
            for( final Value<?> value : fields.valueList ) {
                field(value);
            }
        }

        return this;
    }

    /**
     * Adds a parameter reflected by a table type.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return The Table to allow for fluent formulation.
     */
    @Nonnull
    public Table<ParameterFields<RequestT>> table( @Nonnull final String name, @Nullable final String dataType )
    {
        final List<List<Value<?>>> innerTable = new ArrayList<>();
        valueList.add(Value.ofTable(name, innerTable));
        return new Table<>(this, innerTable);
    }

    /**
     * Adds a parameter reflected by a structure.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return The Table to allow for fluent formulation.
     */
    @Nonnull
    public
        ParameterFields<ParameterFields<RequestT>>
        fields( @Nonnull final String name, @Nullable final String dataType )
    {
        final List<Value<?>> innerFields = new ArrayList<>();
        valueList.add(Value.ofStructure(name, innerFields));
        return new ParameterFields<>(this, innerFields);
    }

    /**
     * Ends the fluent parameter building by returning the request.
     *
     * @return The request.
     */
    @Nonnull
    public RequestT end()
    {
        return request;
    }
}
