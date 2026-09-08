package com.sap.cloud.sdk.datamodel.odatav4.expression;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionLogical;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueEnum;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by an OData complex property.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 * @param <ItemT>
 *            Type of the complex property.
 */
public interface FilterableComplex<EntityT, ItemT> extends Expressions.OperandSingle, EntityReference<EntityT>
{
    /**
     * Filter by expression "has".
     *
     * @param operand
     *            A generic String to be applied to the expression
     * @return The FluentHelper filter
     */
    @Nonnull
    default FilterableBoolean<EntityT> has( @Nonnull final String operand )
    {
        final ValueEnum value = ValueEnum.literal(operand);
        final FilterExpression expression = FilterExpressionLogical.has(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "has".
     *
     * @param operand
     *            A generic String to be applied to the expression
     * @param <EnumT>
     *            The enum value type.
     * @return The FluentHelper filter
     */
    @Nonnull
    default <
        EnumT extends VdmEnum> FilterableBoolean<EntityT> has( @Nonnull final FilterableEnum<EntityT, EnumT> operand )
    {
        final FilterExpression expression = FilterExpressionLogical.has(this, operand::getExpression);
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
    default FilterableBoolean<EntityT> in( @Nonnull final FilterableCollection<?, ItemT> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.in(this, operand);
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
}
