/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
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

    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueCollection
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
