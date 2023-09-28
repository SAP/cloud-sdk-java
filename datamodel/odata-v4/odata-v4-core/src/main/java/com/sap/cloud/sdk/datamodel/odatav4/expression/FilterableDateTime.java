package com.sap.cloud.sdk.datamodel.odatav4.expression;

import java.time.Duration;
import java.time.OffsetDateTime;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionArithmetic;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionTemporal;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueDateTimeOffset;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueDuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by DateTime.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableDateTime<EntityT> extends FilterableValue<EntityT, OffsetDateTime>
{
    /**
     * Wrapper expression class, which delegates to another operation.
     *
     * @param <EntityT>
     *            Type of the entity which references the value.
     */
    @RequiredArgsConstructor
    @Getter
    class Expression<EntityT> implements FilterableDateTime<EntityT>, FilterExpression
    {
        @Delegate
        @Nonnull
        private final FilterExpression delegate;

        @Nonnull
        private final Class<EntityT> entityType;
    }

    /**
     *
     * Filter by expression "date".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableDate<EntityT> date()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.date(thisDateTime);
        return new FilterableDate.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "fractionalseconds".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericDecimal<EntityT> timeFractionalSeconds()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.fractionalSeconds(thisDateTime);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "second".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> timeSecond()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.second(thisDateTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "minute".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> timeMinute()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.minute(thisDateTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "hour".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> timeHour()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.hour(thisDateTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "day".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> dateDay()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.day(thisDateTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "month".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> dateMonth()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.month(thisDateTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "year".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> dateYear()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.year(thisDateTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "time".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableTime<EntityT> time()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.time(thisDateTime);
        return new FilterableTime.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "offsetminutes".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> offsetMinutes()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.totalOffsetMinutes(thisDateTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to add to the date time.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableDateTime<EntityT> add( @Nonnull final FilterableDuration<EntityT> operand )
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final ValueDuration value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.add(thisDateTime, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to add to the date time.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableDateTime<EntityT> add( @Nonnull final Duration operand )
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.add(thisDateTime, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The duration to subtract from the date time.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableDateTime<EntityT> subtract( @Nonnull final FilterableDuration<EntityT> operand )
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final ValueDuration value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisDateTime, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to subtract from the date time.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableDateTime<EntityT> subtract( @Nonnull final Duration operand )
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisDateTime, value);
        return new Expression<>(expression, getEntityType());
    }
}
