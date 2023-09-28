package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.query.Order;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A class representing order expressions over fields, maintaining an order over them.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class OrderExpression
{
    private final Map<String, Order> orderBy = new LinkedHashMap<>();

    /**
     * To create OrderExpression with a field and ordering.
     *
     * @param fieldName
     *            The field name to create an order expression for.
     * @param order
     *            The order direction.
     * @return The order expression.
     */
    @Nonnull
    public static OrderExpression of( @Nonnull final String fieldName, @Nonnull final Order order )
    {
        return new OrderExpression().and(fieldName, order);
    }

    /**
     * To translate OrderExpression to query string.
     *
     * @return The part of the query string dedicated to ordering.
     */
    @Nonnull
    public String toOrderByString()
    {
        return orderBy
            .entrySet()
            .stream()
            .map(
                entry -> entry.getValue() == null
                    ? entry.getKey()
                    : entry.getKey() + " " + entry.getValue().toString().toLowerCase())
            .collect(Collectors.joining(","));
    }

    /**
     * To maintain a Map of OrderExpressions with field name and order.
     *
     * @param fieldName
     *            The field name to create an order expression for.
     * @param order
     *            The order direction.
     * @return The concatenated order expression (conjunction).
     */
    @Nonnull
    public OrderExpression and( @Nonnull final String fieldName, @Nonnull final Order order )
    {
        orderBy.put(fieldName, order);
        return this;
    }
}
