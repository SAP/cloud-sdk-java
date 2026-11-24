package com.sap.cloud.sdk.datamodel.odatav4.expression;

import java.time.Duration;
import java.time.LocalDate;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionArithmetic;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionTemporal;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueDate;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueDuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Date.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableDate<EntityT> extends FilterableValue<EntityT, LocalDate>
{
    /**
     * Wrapper expression class, which delegates to another operation.
     *
     * @param <EntityT>
     *            Type of the entity which references the value.
     */
    @RequiredArgsConstructor
    @Getter
    class Expression<EntityT> implements FilterableDate<EntityT>, FilterExpression
    {
        @Delegate
        @Nonnull
        private final FilterExpression delegate;

        @Nonnull
        private final Class<EntityT> entityType;
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
        final ValueDate thisDate = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.day(thisDate);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "month".
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> dateMonth()
    {
        final ValueDate thisDate = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.month(thisDate);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "year".
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> dateYear()
    {
        final ValueDate thisDate = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.year(thisDate);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to add to the date expression.
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableDate<EntityT> add( @Nonnull final FilterableDuration<EntityT> operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.add(thisDate, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to ad to the date expression.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableDate<EntityT> add( @Nonnull final Duration operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.add(thisDate, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The duration to subtract from the date.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableDate<EntityT> subtract( @Nonnull final FilterableDuration<EntityT> operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisDate, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The duration to subtract from the date.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableDate<EntityT> subtract( @Nonnull final Duration operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisDate, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The other date to calculate the difference from.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableDuration<EntityT> difference( @Nonnull final FilterableDate<EntityT> operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisDate, value);
        return new FilterableDuration.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The other date to calculate the difference from.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableDuration<EntityT> difference( @Nonnull final LocalDate operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDate value = ValueDate.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisDate, value);
        return new FilterableDuration.Expression<>(expression, getEntityType());
    }
}
