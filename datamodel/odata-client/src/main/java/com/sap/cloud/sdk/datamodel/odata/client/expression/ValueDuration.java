/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.Duration;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.Duration
 */
public interface ValueDuration
    extends
    Expressions.OperandSingle,
    FilterableDuration,
    FilterableComparisonAbsolute,
    FilterableComparisonRelative
{
    /**
     * Null value for enum operations.
     */
    @Nonnull
    ValueEnum NULL = Expressions.OperandSingle.NULL::getExpression;

    @Nonnull
    static ValueDuration literal( @Nonnull final Duration v )
    {
        return ( protocol, prefixes ) -> "duration'" + v + "'";
    }

    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueDuration
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
