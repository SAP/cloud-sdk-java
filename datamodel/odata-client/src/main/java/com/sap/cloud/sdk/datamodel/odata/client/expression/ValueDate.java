/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.Date
 */
public interface ValueDate
    extends
    Expressions.OperandSingle,
    FilterableDate,
    FilterableComparisonAbsolute,
    FilterableComparisonRelative
{
    /**
     * Null value for date operations.
     */
    @Nonnull
    ValueDate NULL = Expressions.OperandSingle.NULL::getExpression;

    @Nonnull
    static ValueDate literal( @Nonnull final LocalDate v )
    {
        return ( protocol, prefixes ) -> v.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueDate
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
