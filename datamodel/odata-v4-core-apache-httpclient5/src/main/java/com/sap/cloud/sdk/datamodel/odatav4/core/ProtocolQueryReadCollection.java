package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odatav4.expression.FieldOrdering;

/**
 * Interface that allows for constructing OData queries (reading) over a collection of entities.
 *
 * @param <EntityT>
 *            The entity type to be queried.
 */
interface ProtocolQueryReadCollection<EntityT extends VdmObject<?>> extends ProtocolQueryFilter<EntityT>
{
    /**
     * Limit the number of results of this request.
     *
     * @param top
     *            The maximum amount of elements this request shall return.
     * @return This request with the top parameter set.
     */
    @Nonnull
    ProtocolQueryReadCollection<EntityT> top( int top );

    /**
     * Determine the how many first N entities of the result set should be skipped. If this method is never called, then
     * the full list will be returned from the first entity. If this method is called multiple times, then only the
     * value of the last call will be used.
     *
     * @param skip
     *            The amount of elements this request will skip.
     * @return This request with the skip parameter set.
     */
    @Nonnull
    ProtocolQueryReadCollection<EntityT> skip( int skip );

    /**
     * Sort the set of returned entities by non-complex fields.
     *
     * @param ordering
     *            Fields to sort by.
     * @return This request with the orderBy parameter set.
     */
    @Nonnull
    @SuppressWarnings( { "varargs", "unchecked" } )
    ProtocolQueryReadCollection<EntityT> orderBy( @Nonnull final FieldOrdering<EntityT>... ordering );
}
