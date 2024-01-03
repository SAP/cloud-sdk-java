/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.expression.OrderExpression;
import com.sap.cloud.sdk.datamodel.odata.client.query.Order;
import com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents an asc/desc ordering of an entity via a property.
 *
 * @param <EntityT>
 *            The entity type that is ordered.
 */
@Getter
@RequiredArgsConstructor
public class FieldOrdering<EntityT>
{
    private final SimpleProperty<EntityT> property;
    private final Order order;

    /**
     * Creates a {@code FieldOrdering} representing an ascending order over the given property
     *
     * @param <EntityT>
     *            The entity the ordering is applied to.
     * @param property
     *            The property of {@code EntityT} to be sorted in descending order.
     * @return The FieldOrdering.
     */
    @Nonnull
    public static <EntityT> FieldOrdering<EntityT> asc( @Nonnull final SimpleProperty<EntityT> property )
    {
        return new FieldOrdering<>(property, Order.ASC);
    }

    /**
     * Creates a {@code FieldOrdering} representing an descending order over the given property.
     *
     * @param <EntityT>
     *            The entity the ordering is applied to.
     * @param property
     *            The property of {@code EntityT} to be sorted in descending order.
     * @return The FieldOrdering.
     */
    @Nonnull
    public static <EntityT> FieldOrdering<EntityT> desc( @Nonnull final SimpleProperty<EntityT> property )
    {
        return new FieldOrdering<>(property, Order.DESC);
    }

    /**
     * Builds an {@link OrderExpression} out of individual field orderings. The expression represents a sorting where
     * the orderings are applied in the order they are given.
     *
     * @param orderings
     *            The {@code FieldOrdering}s that should be applied to achieve a sorting.
     * @return The resulting {@link OrderExpression} or {@code null}, if no orderings where given.
     */
    @Nullable
    public static OrderExpression toOrderExpression( @Nonnull final FieldOrdering<?>... orderings )
    {
        final Queue<FieldOrdering<?>> fieldOrderings = new LinkedList<>(Arrays.asList(orderings));
        if( fieldOrderings.isEmpty() ) {
            return null;
        }
        final FieldOrdering<?> first = fieldOrderings.poll();
        final OrderExpression expr = OrderExpression.of(first.getProperty().getFieldName(), first.getOrder());
        fieldOrderings.forEach(o -> expr.and(o.getProperty().getFieldName(), o.getOrder()));
        return expr;
    }
}
