package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.LocalTime;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.TimeOfDay in case of OData 4.0 or Edm.Time in case of OData 2.0.
 */
public interface ValueTimeOfDay
    extends
    Expressions.OperandSingle,
    FilterableTimeOfDay,
    FilterableComparisonAbsolute,
    FilterableComparisonRelative
{
    /**
     * Null value for time of day operations.
     */
    @Nonnull
    ValueTimeOfDay NULL = Expressions.OperandSingle.NULL::getExpression;

    /**
     * Returns a {@link ValueTimeOfDay} from the given {@code v}.
     *
     * @param v
     *            The value to be transformed into a {@link ValueTimeOfDay}.
     * @return A {@link ValueTimeOfDay} that contains the given {@code v}.
     */
    @Nonnull
    static ValueTimeOfDay literal( @Nonnull final LocalTime v )
    {
        return ( protocol, prefixes ) -> protocol.getTimeOfDaySerializer().apply(v);
    }

    /**
     * OData expression for time of day values.
     */
    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueTimeOfDay
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
