/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionLogical;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueEnum;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Enum.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 * @param <EnumT>
 *            Type of the Enum value.
 */
public interface FilterableEnum<EntityT, EnumT extends VdmEnum> extends Expressions.Operand, EntityReference<EntityT>
{
    /**
     * OData Enum type identifier.
     *
     * @return The enum type identifier.
     */
    @Nonnull
    String getEnumType();

    /**
     *
     * Filter by expression "eq".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> equalTo( @Nonnull final FilterableEnum<?, EnumT> operand )
    {
        final FilterExpression expression = FilterExpressionLogical.equalTo(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "eq".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> equalTo( @Nullable final EnumT operand )
    {
        final Expressions.Operand value =
            operand == null ? Expressions.Operand.NULL : ValueEnum.literal(getEnumType(), operand.getName());
        final FilterExpression expression = FilterExpressionLogical.equalTo(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "ne".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> notEqualTo( @Nonnull final FilterableEnum<?, EnumT> operand )
    {
        final FilterExpression expression = FilterExpressionLogical.notEqualTo(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "ne".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> notEqualTo( @Nullable final EnumT operand )
    {
        final Expressions.Operand value =
            operand == null ? Expressions.Operand.NULL : ValueEnum.literal(getEnumType(), operand.getName());
        final FilterExpression expression = FilterExpressionLogical.notEqualTo(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "eq null".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> equalToNull()
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.equalTo(this, Expressions.Operand.NULL);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "ne null".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> notEqualToNull()
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.notEqualTo(this, Expressions.Operand.NULL);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "in".
     *
     * @param operand
     *            The generic operands to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> in( @Nonnull final FilterableCollection<?, EnumT> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.in(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }
}
