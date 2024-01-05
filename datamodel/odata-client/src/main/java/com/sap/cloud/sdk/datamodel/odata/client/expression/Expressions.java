/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Wrapper class for expression types. The types listed here are used to differentiate input and output parameters of
 * functions used in expressions of OData filters.
 */
public class Expressions
{
    @RequiredArgsConstructor
    private static class DefaultFilterExpression implements FilterExpression
    {
        @Nonnull
        private final String format;

        @Nonnull
        @Getter
        private final String operator;

        @Nonnull
        @Getter
        private final List<Operand> operands;

        @Nonnull
        @Override
        public String getExpression(
            @Nonnull final ODataProtocol protocol,
            @Nonnull final Map<String, Predicate<FieldReference>> prefixes )
        {
            final List<String> parts =
                getOperands().stream().map(o -> o.getExpression(protocol, prefixes)).collect(Collectors.toList());
            parts.add(0, getOperator());
            return String.format(format, parts.toArray());
        }
    }

    /**
     * Filter function with a single parameter. Prefix notation without parentheses and a whitespace between operator
     * and operand.
     *
     * @param operator
     *            The function operator.
     * @param operand
     *            The operand of the function.
     * @return The FilterExpression.
     */
    static FilterExpression createOperatorPrefix( @Nonnull final String operator, @Nonnull final Operand operand )
    {
        return new DefaultFilterExpression("(%s %s)", operator, Collections.singletonList(operand));
    }

    /**
     * Filter function without parameter. Prefix notation.
     *
     * @param operator
     *            The function operator.
     * @return The FilterExpression.
     */
    static FilterExpression createFunctionPrefix( @Nonnull final String operator )
    {
        return new DefaultFilterExpression("%s()", operator, Collections.emptyList());
    }

    /**
     * Filter function with singular parameter. Prefix notation.
     *
     * @param operator
     *            The function operator.
     * @param operand
     *            The first operand of the function.
     * @return The FilterExpression.
     */
    static FilterExpression createFunctionPrefix( @Nonnull final String operator, @Nonnull final Operand operand )
    {
        final List<Operand> operands = Lists.newArrayList(operand);
        return new DefaultFilterExpression("%s(%s)", operator, operands);
    }

    /**
     * Filter function with two parameters. Prefix notation.
     *
     * @param operator
     *            The function operator.
     * @param operand1
     *            The first operand of the function.
     * @param operand2
     *            The second operand of the function.
     * @return The FilterExpression.
     */
    static FilterExpression createFunctionPrefix(
        @Nonnull final String operator,
        @Nonnull final Operand operand1,
        @Nonnull final Operand operand2 )
    {
        final List<Operand> operands = Lists.newArrayList(operand1, operand2);
        return new DefaultFilterExpression("%s(%s,%s)", operator, operands);
    }

    /**
     * Filter function with two parameters. Prefix notation.
     *
     * @param operator
     *            The function operator.
     * @param operand1
     *            The first operand of the function.
     * @param operand2
     *            The second operand of the function.
     * @param operand3
     *            The third operand of the function.
     * @return The FilterExpression.
     */
    static FilterExpression createFunctionPrefix(
        @Nonnull final String operator,
        @Nonnull final Operand operand1,
        @Nonnull final Operand operand2,
        @Nonnull final Operand operand3 )
    {
        final List<Operand> operands = Lists.newArrayList(operand1, operand2, operand3);
        return new DefaultFilterExpression("%s(%s,%s,%s)", operator, operands);
    }

    /**
     * Filter function with two parameters. Infix notation.
     *
     * @param operator
     *            The function operator.
     * @param operand1
     *            The first operand of the function.
     * @param operand2
     *            The second operand of the function.
     * @return The FilterExpression.
     */
    static FilterExpression createFunctionInfix(
        @Nonnull final String operator,
        @Nonnull final Operand operand1,
        @Nonnull final Operand operand2 )
    {
        final List<Operand> operands = Lists.newArrayList(operand1, operand2);
        return new DefaultFilterExpression("(%2$s %1$s %3$s)", operator, operands);
    }

    /**
     * Filter function with two parameters. Infix notation.
     *
     * @param operator
     *            The function operator.
     * @param operand1
     *            The first operand of the function.
     * @return The FilterExpression.
     */
    static
        FilterExpression
        createFunctionLambda( @Nonnull final String operator, @Nonnull final OperandMultiple operand1 )
    {
        final List<Operand> operands = Lists.newArrayList(operand1);
        return new DefaultFilterExpression("%2$s/%1$s()", operator, operands);
    }

    /**
     * Filter function with two parameters. Infix notation.
     *
     * @param operator
     *            The function operator.
     * @param operand1
     *            The first operand of the function.
     * @param operand2
     *            The second operand of the function.
     * @param lambdaFieldPredicate
     *            The predicate for which fields will be given a prefix.
     * @return The FilterExpression.
     */
    static FilterExpression createFunctionLambda(
        @Nonnull final String operator,
        @Nonnull final OperandMultiple operand1,
        @Nonnull final Operand operand2,
        @Nonnull final Predicate<FieldReference> lambdaFieldPredicate )
    {
        final String format = "%2$s/%1$s(%3$s)";

        final Operand operandLambda = new Operand()
        {
            /**
             * {@inheritDoc}
             */
            @Nonnull
            @Override
            public String getExpression(
                @Nonnull final ODataProtocol protocol,
                @Nonnull final Map<String, Predicate<FieldReference>> prefixes )
            {
                final String lambdaFieldPrefix = "" + (char) ('a' + prefixes.size());
                final Map<String, Predicate<FieldReference>> prefx = new LinkedHashMap<>();
                prefx.put(lambdaFieldPrefix, lambdaFieldPredicate); // prepend prefix
                prefx.putAll(prefixes);
                return lambdaFieldPrefix + ":" + operand2.getExpression(protocol, prefx);
            }
        };

        final List<Operand> operands = Lists.newArrayList(operand1, operandLambda);
        return new DefaultFilterExpression(format, operator, operands);
    }

    /**
     * Generic OData filter expression operand.
     */
    @FunctionalInterface
    public interface Operand
    {
        /**
         * The null operand, representing the absence of any value.
         */
        Operand NULL = ( protocol, prefixes ) -> "null";

        /**
         * Create the String representation of the expression based on a given {@link ODataProtocol}.
         *
         * @param protocol
         *            The {@link ODataProtocol} that the expression should conform to.
         *
         * @return The expression String.
         */
        @Nonnull
        default String getExpression( @Nonnull final ODataProtocol protocol )
        {
            return getExpression(protocol, Collections.emptyMap());
        }

        /**
         * Create the String representation of the expression.
         *
         * @param protocol
         *            The OData protocol to derive serialization rules from.
         * @param prefixes
         *            Additional field prefixes, e.g. when using lambda expressions.
         * @return The expression String.
         */
        @Nonnull
        String getExpression(
            @Nonnull final ODataProtocol protocol,
            @Nonnull final Map<String, Predicate<FieldReference>> prefixes );
    }

    /**
     * Singular OData filter expression operand.
     */
    public interface OperandSingle extends Operand
    {

    }

    /**
     * OData filter collection expression operand.
     */
    public interface OperandMultiple extends Operand
    {

    }

    /**
     * Helper function to generate an OData filter expression operand for a primitive Java type.
     *
     * @param value
     *            Java literal.
     * @param <PrimitiveT>
     *            Type of the Java literal.
     * @return The OData filter expression operand representation of the Java literal.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    public static <PrimitiveT> OperandSingle createOperand( @Nullable final PrimitiveT value )
    {
        if( value == null ) {
            return ( protocol, prefixes ) -> "null";
        }
        if( value instanceof OperandSingle ) {
            return (OperandSingle) value;
        }
        if( value instanceof String ) {
            return ValueString.literal((String) value);
        }
        if( value instanceof Boolean ) {
            return ValueBoolean.literal((Boolean) value);
        }
        if( value instanceof Number ) {
            return ValueNumeric.literal((Number) value);
        }
        if( value instanceof Duration ) {
            return ValueDuration.literal((Duration) value);
        }
        if( value instanceof LocalDateTime ) {
            return ValueDateTime.literal((LocalDateTime) value);
        }
        if( value instanceof OffsetDateTime ) {
            return ValueDateTimeOffset.literal((OffsetDateTime) value);
        }
        if( value instanceof ZonedDateTime ) {
            return ValueDateTimeOffset.literal(OffsetDateTime.from((TemporalAccessor) value));
        }
        if( value instanceof LocalDate ) {
            return ValueDate.literal((LocalDate) value);
        }
        if( value instanceof LocalTime ) {
            return ValueTimeOfDay.literal((LocalTime) value);
        }
        if( value instanceof UUID ) {
            return ValueGuid.literal((UUID) value);
        }
        if( value instanceof byte[] ) {
            return ValueBinary.literal((byte[]) value);
        }
        throw new IllegalArgumentException("Unable to create filter expression for value " + value);
    }
}
