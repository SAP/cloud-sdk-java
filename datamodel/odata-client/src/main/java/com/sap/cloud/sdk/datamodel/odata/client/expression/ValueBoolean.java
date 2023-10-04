/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.Boolean
 */
public interface ValueBoolean extends Expressions.OperandSingle, FilterableBoolean, FilterableComparisonAbsolute
{
    /**
     * Null value for boolean operations.
     */
    @Nonnull
    ValueBoolean NULL = Expressions.OperandSingle.NULL::getExpression;

    /**
     * Get the literal of this expression.
     *
     * @param v
     *            boolean.
     * @return The literal.
     */
    @Nonnull
    static ValueBoolean literal( @Nonnull final Boolean v )
    {
        return ( protocol, prefixes ) -> v.toString();
    }

    /**
     * Implementation with literal number value.
     */
    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueBoolean
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
