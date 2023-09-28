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

    @Nonnull
    static ValueDateTime literal( @Nonnull final LocalDateTime v )
    {
        return ( protocol, prefixes ) -> protocol.getDateTimeSerializer().apply(v);
    }

    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueDateTime
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
