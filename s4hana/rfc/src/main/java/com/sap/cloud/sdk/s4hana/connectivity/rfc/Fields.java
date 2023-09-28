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

    @Nonnull
    public static Fields fields()
    {
        return new Fields();
    }

    public int size()
    {
        return valueList.size();
    }

    public boolean isEmpty()
    {
        return valueList.isEmpty();
    }

    @Nonnull
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        Fields
        field( @Nonnull final String name, @Nullable final String dataType, @Nullable final T value )
    {
        valueList.add(Value.ofField(name, value, null));
        return this;
    }

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

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Boolean value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Byte value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Character value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final String value )
    {
        return field(name, dataType, value, null);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Short value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Integer value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Long value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Float value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Double value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final BigInteger value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final BigDecimal value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE);
    }

    @Nonnull
    public Fields field(
        @Nonnull final String name,
        @Nullable final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final LocalDate value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE);
    }

    @Nonnull
    public Fields field( @Nonnull final String name, @Nullable final String dataType, @Nullable final Locale value )
    {
        return field(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE);
    }
}
