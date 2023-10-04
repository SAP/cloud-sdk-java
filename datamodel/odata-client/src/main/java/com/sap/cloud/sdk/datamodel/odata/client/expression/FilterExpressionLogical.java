/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for logical types.
 */
public interface FilterExpressionLogical
{
    @Nonnull
    static
        ValueBoolean.Expression
        equalTo( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("eq", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        notEqualTo( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("ne", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        greaterThan( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("gt", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        greaterThanEquals( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("ge", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        lessThan( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("lt", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        lessThanEquals( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("le", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression and( @Nonnull final ValueBoolean operand1, @Nonnull final ValueBoolean operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("and", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression or( @Nonnull final ValueBoolean operand1, @Nonnull final ValueBoolean operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("or", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression not( @Nonnull final ValueBoolean operand )
    {
        final FilterExpression expression = Expressions.createOperatorPrefix("not", operand);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        has( @Nonnull final Expressions.OperandSingle operand1, @Nonnull final ValueEnum operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("has", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        in( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand... operands2 )
    {
        final Expressions.OperandMultiple operand2 =
            ( protocol, prefixes ) -> "("
                + Arrays.stream(operands2).map(o -> o.getExpression(protocol)).collect(Collectors.joining(","))
                + ")";
        return in(operand1, operand2);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        in( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("in", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }
}
