/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import java.time.LocalTime;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionTemporal;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueTimeOfDay;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Time.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableTime<EntityT> extends FilterableValue<EntityT, LocalTime>
{
    /**
     * Wrapper expression class, which delegates to another operation.
     *
     * @param <EntityT>
     *            Type of the entity which references the value.
     */
    @RequiredArgsConstructor
    @Getter
    class Expression<EntityT> implements FilterableTime<EntityT>, FilterExpression
    {
        @Delegate
        @Nonnull
        private final FilterExpression delegate;

        @Nonnull
        private final Class<EntityT> entityType;
    }

    /**
     *
     * Filter by expression "fractionalseconds".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericDecimal<EntityT> timeFractionalSeconds()
    {
        final ValueTimeOfDay thisTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.fractionalSeconds(thisTime);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "second".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> timeSecond()
    {
        final ValueTimeOfDay thisTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.second(thisTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "minute".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> timeMinute()
    {
        final ValueTimeOfDay thisTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.minute(thisTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "hour".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> timeHour()
    {
        final ValueTimeOfDay thisTime = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.hour(thisTime);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }
}
