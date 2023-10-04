/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Boolean operations for generic OData filter expression operands.
 */
public interface FilterableBoolean extends Expressions.Operand
{
    /**
     * Combine current filter expression with another expression in conjunction.
     *
     * @param operand
     *            The other expression.
     * @return This FluentHelper reference.
     */
    @Nonnull
    default ValueBoolean and( @Nonnull final ValueBoolean operand )
    {
        return FilterExpressionLogical.and(this::getExpression, operand);
    }

    /**
     * Combine the filter expression with another expression in conjunction
     *
     * @param operand
     *            A boolean value.
     * @return This FluentHelper reference.
     */
    @Nonnull
    default ValueBoolean and( @Nonnull final Boolean operand )
    {
        final ValueBoolean value = ValueBoolean.literal(operand);
        return and(value);
    }

    /**
     * Combine current filter expression with another expression in disjunction.
     *
     * @param operand
     *            The other expression.
     * @return This FluentHelper reference.
     */
    @Nonnull
    default ValueBoolean or( @Nonnull final ValueBoolean operand )
    {
        return FilterExpressionLogical.or(this::getExpression, operand);
    }

    /**
     * Combine the filter expression with another expression in disjunction
     *
     * @param operand
     *            A boolean value.
     * @return This FluentHelper reference.
     */
    @Nonnull
    default ValueBoolean or( @Nonnull final Boolean operand )
    {
        final ValueBoolean value = ValueBoolean.literal(operand);
        return or(value);
    }

    /**
     * Negate the current filter expression.
     *
     * @return This FluentHelper reference.
     */
    @Nonnull
    default ValueBoolean not()
    {
        return FilterExpressionLogical.not(this::getExpression);
    }
}
