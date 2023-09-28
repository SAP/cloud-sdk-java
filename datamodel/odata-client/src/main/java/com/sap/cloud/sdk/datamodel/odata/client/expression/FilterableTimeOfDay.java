package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Time-of-day operations for generic OData filter expression operands.
 */
public interface FilterableTimeOfDay extends Expressions.Operand
{
    /**
     *
     * Filter by expression "fractionalseconds".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression timeFractionalSeconds()
    {
        final ValueTimeOfDay thisTime = this::getExpression;
        return FilterExpressionTemporal.fractionalSeconds(thisTime);
    }

    /**
     *
     * Filter by expression "second".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression timeSecond()
    {
        final ValueTimeOfDay thisTime = this::getExpression;
        return FilterExpressionTemporal.second(thisTime);
    }

    /**
     *
     * Filter by expression "minute".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression timeMinute()
    {
        final ValueTimeOfDay thisTime = this::getExpression;
        return FilterExpressionTemporal.minute(thisTime);
    }

    /**
     *
     * Filter by expression "hour".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression timeHour()
    {
        final ValueTimeOfDay thisTime = this::getExpression;
        return FilterExpressionTemporal.hour(thisTime);
    }
}
