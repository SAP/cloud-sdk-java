/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableBoolean;

/**
 * Interface that allows for constructing filterable OData queries (reading) over a collection of entities.
 *
 * @param <EntityT>
 *            The entity type to be queried.
 */
interface ProtocolQueryFilter<EntityT extends VdmObject<?>>
{
    /**
     * Filter on properties of {@linkplain EntityT}.
     *
     * @param filters
     *            Filter expressions to be added to the request.
     * @return This request object with the added filters.
     */
    @Nonnull
    @SuppressWarnings( { "varargs", "unchecked" } )
    ProtocolQueryFilter<EntityT> filter( @Nonnull final FilterableBoolean<EntityT>... filters );

    /**
     * Request modifier to return the set of entities that contain the specified value. If this method is never called,
     * then all the accessible entities will be returned.
     *
     * @param search
     *            A string value as the search criteria.
     * @return The same request builder with this request modifier applied.
     */
    @Nonnull
    ProtocolQueryFilter<EntityT> search( @Nonnull final String search );

    /**
     * Request modifier to return the set of entities corresponding to the specified boolean expression. If this method
     * is never called, then all the accessible entities will be returned.
     *
     * @param expression
     *            SearchExpression as the search criteria
     * @return The same request builder with this request modifier applied.
     */
    @Nonnull
    ProtocolQueryFilter<EntityT> search( @Nonnull final SearchExpression expression );
}
