/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.DateTime (OData 2.0 only)
 */
public interface ValueDateTime
    extends
    Expressions.OperandSingle,
    FilterableDateTimeOffset,
    FilterableComparisonAbsolute,
    FilterableComparisonRelative
{
    /**
     * Null value for date time operations.
     */
    @Nonnull
    ValueDateTime NULL = Expressions.OperandSingle.NULL::getExpression;

    /**
     * Returns a {@link ValueDateTime} from the given {@code v}.
     *
     * @param v
     *            The value to be transformed into a {@link ValueDateTime}.
     * @return A {@link ValueDateTime} that contains the given {@code v}.
     */
    @Nonnull
    static ValueDateTime literal( @Nonnull final LocalDateTime v )
    {
        return ( protocol, prefixes ) -> protocol.getDateTimeSerializer().apply(v);
    }

    /**
     * OData expression for date time values.
     */
    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueDateTime
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
