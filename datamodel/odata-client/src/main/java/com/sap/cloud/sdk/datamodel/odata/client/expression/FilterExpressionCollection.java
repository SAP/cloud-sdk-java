package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for collection types.
 */
public interface FilterExpressionCollection
{
    @Nonnull
    static ValueBoolean.Expression hasSubset(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("hassubset", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression hasSubSequence(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("hassubsequence", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueCollection.Expression concat(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("concat", operand1, operand2);
        return new ValueCollection.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression contains(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("contains", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression endsWith(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("endswith", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression startsWith(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("startswith", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression indexOf(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("indexof", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression length( @Nonnull final Expressions.OperandMultiple operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("length", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static
        ValueCollection.Expression
        substring( @Nonnull final Expressions.OperandMultiple operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substring", operand1, operand2);
        return new ValueCollection.Expression(expression);
    }

    @Nonnull
    static ValueCollection.Expression substring(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final ValueNumeric operand2,
        @Nonnull final ValueNumeric operand3 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substring", operand1, operand2, operand3);
        return new ValueCollection.Expression(expression);
    }

    @Nonnull
    @Deprecated
    static ValueBoolean.Expression all(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final ValueBoolean operand2,
        @Nonnull final Predicate<FieldReference> lambdaFieldPredicate,
        @Nonnull final String lambdaFieldPrefix )
    {
        return all(operand1, operand2, lambdaFieldPredicate);
    }

    @Nonnull
    static ValueBoolean.Expression all(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final ValueBoolean operand2,
        @Nonnull final Predicate<FieldReference> lambdaFieldPredicate )
    {
        final FilterExpression expression =
            Expressions.createFunctionLambda("all", operand1, operand2, lambdaFieldPredicate);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    @Deprecated
    static ValueBoolean.Expression any(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final ValueBoolean operand2,
        @Nonnull final Predicate<FieldReference> lambdaFieldPredicate,
        @Nonnull final String lambdaFieldPrefix )
    {
        return any(operand1, operand2, lambdaFieldPredicate);
    }

    @Nonnull
    static ValueBoolean.Expression any(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final ValueBoolean operand2,
        @Nonnull final Predicate<FieldReference> lambdaFieldPredicate )
    {
        final FilterExpression expression =
            Expressions.createFunctionLambda("any", operand1, operand2, lambdaFieldPredicate);
        return new ValueBoolean.Expression(expression);
    }

    @Nonnull
    static ValueBoolean.Expression any( @Nonnull final Expressions.OperandMultiple operand1 )
    {
        final FilterExpression expression = Expressions.createFunctionLambda("any", operand1);
        return new ValueBoolean.Expression(expression);
    }
}
