/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;

/**
 * Interface designed to be implemented by {@link EntityLink} class in order to provide a filter function.
 *
 * @param <EntityT>
 *            The entity where the link starts.
 * @param <LinkedEntityT>
 *            The entity where the link ends (i.e. the related entity).
 */
public interface OneToOneLink<EntityT, LinkedEntityT>
{
    /**
     * Add filter to a one-to-one navigation property relationship.
     *
     * @param filterExpression
     *            The filter expression to use for resolving the navigation property
     * @return The fluent helper to continue constructing a filter expression.
     */
    @Nonnull
    ExpressionFluentHelper<EntityT> filter( @Nonnull final ExpressionFluentHelper<LinkedEntityT> filterExpression );
}
