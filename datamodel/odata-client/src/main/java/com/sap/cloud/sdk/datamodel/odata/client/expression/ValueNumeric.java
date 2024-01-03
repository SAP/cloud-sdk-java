/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.Int32, ...
 */
public interface ValueNumeric
    extends
    Expressions.OperandSingle,
    FilterableNumeric,
    FilterableComparisonAbsolute,
    FilterableComparisonRelative
{
    /**
     * Null value for numeric operations.
     */
    @Nonnull
    ValueNumeric NULL = Expressions.OperandSingle.NULL::getExpression;

    /**
     * Returns a {@link ValueNumeric} from the given {@code v}.
     *
     * @param v
     *            The value to be transformed into a {@link ValueNumeric}.
     * @return A {@link ValueNumeric} that contains the given {@code v}.
     */
    @Nonnull
    static ValueNumeric literal( @Nonnull final Number v )
    {
        return ( protocol, prefixes ) -> protocol.getNumberSerializer().apply(v);
    }

    /**
     * OData expression on numeric values.
     */
    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueNumeric
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
