/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.OffsetDateTime;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.DateTimeOffset
 */
public interface ValueDateTimeOffset
    extends
    Expressions.OperandSingle,
    FilterableDateTimeOffset,
    FilterableComparisonAbsolute,
    FilterableComparisonRelative
{
    /**
     * Null value for date time offset operations.
     */
    @Nonnull
    ValueDateTimeOffset NULL = Expressions.OperandSingle.NULL::getExpression;

    /**
     * Returns a {@link ValueDateTimeOffset} from the given {@code v}.
     *
     * @param v
     *            The value to be transformed into a {@link ValueDateTimeOffset}.
     * @return A {@link ValueDateTimeOffset} that contains the given {@code v}.
     */
    @Nonnull
    static ValueDateTimeOffset literal( @Nonnull final OffsetDateTime v )
    {
        return ( protocol, prefixes ) -> protocol.getDateTimeOffsetSerializer().apply(v);
    }

    /**
     * OData expression for date time offset values.
     */
    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueDateTimeOffset
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
