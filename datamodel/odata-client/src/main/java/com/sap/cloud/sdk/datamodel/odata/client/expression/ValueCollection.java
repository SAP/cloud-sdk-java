/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.Streams;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression for a generic collection.
 */
public interface ValueCollection
    extends
    Expressions.OperandMultiple,
    FilterableCollection,
    FilterableComparisonAbsolute,
    FilterableComparisonRelative
{
    /**
     * Null value for collection operations.
     */
    @Nonnull
    ValueCollection NULL = Expressions.Operand.NULL::getExpression;

    /**
     * Returns a {@link ValueCollection} instance from the given {@code v}.
     *
     * @param v
     *            The values to be transformed into a {@link ValueCollection}.
     * @return A {@link ValueCollection} that contains the given {@code v}.
     */
    @Nonnull
    static ValueCollection literal( @Nonnull final Iterable<?> v )
    {
        return ( protocol, prefixes ) -> "["
            + Streams
                .stream(v)
                .map(Expressions::createOperand)
                .map(o -> o.getExpression(protocol))
                .collect(Collectors.joining(","))
            + "]";
    }

    /**
     * OData expression for generic value collections.
     */
    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueCollection
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
