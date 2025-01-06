/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
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
 * This class is tightly connected to an instance of {@code Table}, adding all values directly to this {@code Table}
 * instance. Because of this indirection, all {@code Value}s added through this {@code TableRow} will have the same row
 * Id and as such can be grouped together.
 *
 * @param <RequestT>
 *            The type of the original Request.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
@Deprecated
public class TableRow<RequestT>
{
    @Nonnull
    private final Table<RequestT> table;

    @Nonnull
    private final List<Value<?>> values;

    /**
     * Returns the number of {@code Value}s added in context of this {@code TableRow}.
     *
     * @return The number of {@code Value}s in this {@code TableRow}.
     */
    public int size()
    {
        return values.size();
    }

    /**
     * Checks whether any {@code Value}s where added in context of this {@code TableRow}.
     *
     * @return True, if there was no {@code Value} added; false, else.
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }

    @Nonnull
    private <T> TableRow<RequestT> field( @Nonnull final Value<T> value )
    {
        values.add(value);
        return this;
    }

    /**
     * Creates a new {@code Value} object based on the given parameter and the incremented field Id. This newly created
     * {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}. The {@code Value} is created
     * without a type converter, letting the default {@link com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer}
     * handling the serialization.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The parameter value.
     * @param <T>
     *            The type of the value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final T value )
    {
        return field(Value.ofField(name, value, null));
    }

    /**
     * Creates a new {@code Value} object based on the given parameter and the incremented field Id. This newly created
     * {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The parameter value.
     * @param typeConverter
     *            The converter to be used to parse the value to the ERP String value.
     * @param <T>
     *            The type of the value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public <T> TableRow<RequestT> field(
        @Nonnull final String name,
        @Nullable final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        return field(Value.ofField(name, value, typeConverter));
    }

    /**
     * Creates a new {@code Boolean} {@code Value} object based on the given parameter and the incremented field Id.
     * This newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Boolean} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Boolean value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE);
    }

    /**
     * Creates a new {@code Byte} {@code Value} object based on the given parameter and the incremented field Id. This
     * newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Byte} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Byte value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE);
    }

    /**
     * Creates a new {@code Character} {@code Value} object based on the given parameter and the incremented field Id.
     * This newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Character} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Character value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE);
    }

    /**
     * Creates a new {@code String} {@code Value} object based on the given parameter and the incremented field Id. This
     * newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code String} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final String value )
    {
        return field(name, dataType, value, null);
    }

    /**
     * Creates a new {@code Short} {@code Value} object based on the given parameter and the incremented field Id. This
     * newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Short} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Short value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE);
    }

    /**
     * Creates a new {@code Integer} {@code Value} object based on the given parameter and the incremented field Id.
     * This newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Integer} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Integer value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE);
    }

    /**
     * Creates a new {@code Long} {@code Value} object based on the given parameter and the incremented field Id. This
     * newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Long} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Long value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE);
    }

    /**
     * Creates a new {@code Float} {@code Value} object based on the given parameter and the incremented field Id. This
     * newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Float} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Float value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE);
    }

    /**
     * Creates a new {@code Double} {@code Value} object based on the given parameter and the incremented field Id. This
     * newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Double} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Double value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE);
    }

    /**
     * Creates a new {@code BigInteger} {@code Value} object based on the given parameter and the incremented field Id.
     * This newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code BigInteger} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final BigInteger value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE);
    }

    /**
     * Creates a new {@code BigDecimal} {@code Value} object based on the given parameter and the incremented field Id.
     * This newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code BigDecimal} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final BigDecimal value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE);
    }

    /**
     * Creates a new {@code Year} {@code Value} object based on the given parameter and the incremented field Id. This
     * newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Year} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public TableRow<RequestT> field(
        @Nonnull final String name,
        @Nullable final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE);
    }

    /**
     * Creates a new {@code LocalDate} {@code Value} object based on the given parameter and the incremented field Id.
     * This newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code LocalDate} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final LocalDate value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE);
    }

    /**
     * Creates a new {@code LocalTime} {@code Value} object based on the given parameter and the incremented field Id.
     * This newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code LocalTime} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final LocalTime value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter.INSTANCE);
    }

    /**
     * Creates a new {@code Locale} {@code Value} object based on the given parameter and the incremented field Id. This
     * newly created {@code Value} then gets added to the {@code Table} linked to this {@code TableRow}.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Locale} value.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public
        TableRow<RequestT>
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Locale value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE);
    }

    /**
     * Copies the given {@code Field}s to the {@code Table} linked to this {@code TableRow}, using a new/applicable
     * field Id.
     *
     * @param other
     *            The mandatory {@code Field} to copy all contained value from.
     * @param others
     *            The optional, additional {@code Field}s to copy the values from.
     * @return This {@code TableRow} to facilitate a fluent interface.
     */
    @Nonnull
    public TableRow<RequestT> fields( @Nonnull final Fields other, @Nonnull final Fields... others )
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
    public Table<TableRow<RequestT>> table( @Nonnull final String name, @Nullable final String dataType )
    {
        final List<List<Value<?>>> innerTable = new ArrayList<>();
        values.add(Value.ofTable(name, innerTable));
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
    public ParameterFields<TableRow<RequestT>> fields( @Nonnull final String name, @Nullable final String dataType )
    {
        final List<Value<?>> innerFields = new ArrayList<>();
        values.add(Value.ofStructure(name, innerFields));
        return new ParameterFields<>(this, innerFields);
    }

    /**
     * Creates a new {@code TableRow}, associated to the same table this {@code TableRow} is linked to.
     *
     * @return A new {@code TableRow}, adding values with a new row Id.
     */
    @Nonnull
    public TableRow<RequestT> row()
    {
        return table.row();
    }

    /**
     * Returns the initial request, basically finalizing this {@code TableRow} as well as the {@code Table} this row is
     * linked to.
     *
     * @return The original request.
     */
    @Nonnull
    public RequestT end()
    {
        return table.end();
    }
}
