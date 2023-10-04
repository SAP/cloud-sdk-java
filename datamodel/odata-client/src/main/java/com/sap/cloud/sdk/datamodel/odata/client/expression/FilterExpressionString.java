/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for string types.
 */
public interface FilterExpressionString
{
    @Nonnull
    static
        ValueBoolean.Expression
        matchesPattern( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("matchesPattern", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueString.Expression.Expression toLower( @Nonnull final ValueString operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("tolower", operand);
        return new ValueString.Expression(expression);
    }

    @Nonnull
    static ValueString.Expression toUpper( @Nonnull final ValueString operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("toupper", operand);
        return new ValueString.Expression(expression);
    }

    @Nonnull
    static ValueString.Expression trim( @Nonnull final ValueString operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("trim", operand);
        return new ValueString.Expression(expression);
    }

    @Nonnull
    static ValueString.Expression concat( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("concat", operand1, operand2);
        return new ValueString.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression contains( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("contains", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        substringOf( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substringof", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression endsWith( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("endswith", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static
        ValueBoolean.Expression
        startsWith( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("startswith", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression indexOf( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("indexof", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression length( @Nonnull final ValueString operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("length", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueString.Expression substring( @Nonnull final ValueString operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substring", operand1, operand2);
        return new ValueString.Expression(expression);
    }

    @Nonnull
    static ValueString.Expression substring(
        @Nonnull final ValueString operand1,
        @Nonnull final ValueNumeric operand2,
        @Nonnull final ValueNumeric operand3 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substring", operand1, operand2, operand3);
        return new ValueString.Expression(expression);
    }
}
