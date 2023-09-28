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

    @Nonnull
    static ValueNumeric literal( @Nonnull final Number v )
    {
        return ( protocol, prefixes ) -> protocol.getNumberSerializer().apply(v);
    }

    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueNumeric
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
