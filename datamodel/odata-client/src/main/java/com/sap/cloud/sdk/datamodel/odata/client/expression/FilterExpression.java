/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Generic interface to describe an OData filter expression.
 */
public interface FilterExpression extends Expressions.Operand
{
    /**
     * String representation of the OData filter expression operator.
     *
     * @return The operator.
     */
    @Nonnull
    String getOperator();

    /**
     * List of the operands used for the OData filter expression.
     *
     * @return The operands.
     */
    @Nonnull
    List<Expressions.Operand> getOperands();
}
