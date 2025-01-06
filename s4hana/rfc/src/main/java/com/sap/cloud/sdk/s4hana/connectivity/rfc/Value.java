/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter( AccessLevel.PACKAGE )
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( access = AccessLevel.PRIVATE, onConstructor = @__( @Deprecated ) )
final class Value<T>
{
    @Nonnull
    private final ValueType valueType;

    @Nullable
    private final String name;

    @Nullable
    private final T value;

    @Nullable
    @Deprecated
    private final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter;

    @SuppressWarnings( "unchecked" )
    List<Value<?>> getAsStructure()
    {
        return (List<Value<?>>) value;
    }

    @SuppressWarnings( "unchecked" )
    List<List<Value<?>>> getAsTable()
    {
        return (List<List<Value<?>>>) value;
    }

    @Nonnull
    @SuppressWarnings( "deprecation" )
    static <T> Value<T> ofField(
        @Nonnull final String name,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        if( value instanceof Iterable<?> ) {
            throw new IllegalArgumentException("Instance of Iterable<?> is not allowed as value: " + value);
        }
        if( value instanceof Map<?, ?> ) {
            throw new IllegalArgumentException("Instance of Map<?,?> is not allowed as value: " + value);
        }

        return new Value<>(ValueType.FIELD, name, value, typeConverter);
    }

    @Nonnull
    @SuppressWarnings( "deprecation" )
    static <T> Value<T> ofVectorElement(
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        if( value instanceof Iterable<?> ) {
            throw new IllegalArgumentException("Instance of Iterable<?> is not allowed as value: " + value);
        }
        if( value instanceof Map<?, ?> ) {
            throw new IllegalArgumentException("Instance of Map<?,?> is not allowed as value: " + value);
        }

        return new Value<>(ValueType.FIELD, null, value, typeConverter);
    }

    @Nonnull
    static Value<List<Value<?>>> ofStructure( @Nonnull final String name, @Nonnull final List<Value<?>> values )
    {
        return new Value<>(ValueType.STRUCTURE, name, values, null);
    }

    @Nonnull
    static Value<List<List<Value<?>>>> ofTable( @Nonnull final String name, @Nonnull final List<List<Value<?>>> cells )
    {
        return new Value<>(ValueType.TABLE, name, cells, null);
    }
}
