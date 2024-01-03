/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.Duration;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.Duration
 */
public interface ValueDuration
    extends
    Expressions.OperandSingle,
    FilterableDuration,
    FilterableComparisonAbsolute,
    FilterableComparisonRelative
{
    /**
     * Null value for enum operations.
     */
    @Nonnull
    ValueEnum NULL = Expressions.OperandSingle.NULL::getExpression;

    /**
     * Returns a {@link ValueDuration} from the given {@code v}.
     *
     * @param v
     *            The value to be transformed into a {@link ValueDuration}.
     * @return A {@link ValueDuration} that contains the given {@code v}.
     */
    @Nonnull
    static ValueDuration literal( @Nonnull final Duration v )
    {
        return ( protocol, prefixes ) -> "duration'" + v + "'";
    }

    /**
     * OData expression for duration values.
     */
    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueDuration
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
