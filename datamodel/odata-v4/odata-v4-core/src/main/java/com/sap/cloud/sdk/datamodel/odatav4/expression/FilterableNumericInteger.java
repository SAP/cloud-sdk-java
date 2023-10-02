/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionArithmetic;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueNumeric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Integer.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableNumericInteger<EntityT> extends FilterableNumeric<EntityT>
{
    /**
     * Wrapper expression class, which delegates to another operation.
     *
     * @param <EntityT>
     *            Type of the entity which references the value.
     */
    @RequiredArgsConstructor
    @Getter
    class Expression<EntityT> implements FilterableNumericInteger<EntityT>, FilterExpression
    {
        @Delegate
        @Nonnull
        private final FilterExpression delegate;

        @Nonnull
        private final Class<EntityT> entityType;
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> add( @Nonnull final FilterableNumericInteger<EntityT> operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.add(thisNumber, value);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    @Nonnull
    @Override
    default FilterableNumericDecimal<EntityT> add( @Nonnull final Number operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.add(thisNumber, literal);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> add( @Nonnull final Long operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.add(thisNumber, literal);
        return new Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> add( @Nonnull final Integer operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.add(thisNumber, literal);
        return new Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> multiply( @Nonnull final FilterableNumericInteger<EntityT> operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.multiply(thisNumber, value);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    @Nonnull
    @Override
    default FilterableNumericDecimal<EntityT> multiply( @Nonnull final Number operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.multiply(thisNumber, literal);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> multiply( @Nonnull final Long operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.multiply(thisNumber, literal);
        return new Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> multiply( @Nonnull final Integer operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.multiply(thisNumber, literal);
        return new Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> subtract( @Nonnull final FilterableNumericInteger<EntityT> operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisNumber, value);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    @Nonnull
    @Override
    default FilterableNumericDecimal<EntityT> subtract( @Nonnull final Number operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisNumber, literal);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> subtract( @Nonnull final Long operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisNumber, literal);
        return new Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> subtract( @Nonnull final Integer operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisNumber, literal);
        return new Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> modulo( @Nonnull final FilterableNumericInteger<EntityT> operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.modulo(thisNumber, value);
        return new Expression<>(expression, getEntityType());
    }

    @Nonnull
    @Override
    default FilterableNumericDecimal<EntityT> modulo( @Nonnull final Number operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.modulo(thisNumber, literal);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> modulo( @Nonnull final Long operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.modulo(thisNumber, literal);
        return new Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> modulo( @Nonnull final Integer operand )
    {
        final ValueNumeric thisNumber = this::getExpression;
        final ValueNumeric literal = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.modulo(thisNumber, literal);
        return new Expression<>(expression, getEntityType());
    }

    @Override
    @Nonnull
    default FilterableNumericInteger<EntityT> negate()
    {
        final ValueNumeric thisNumber = this::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.negate(thisNumber);
        return new Expression<>(expression, getEntityType());
    }
}
