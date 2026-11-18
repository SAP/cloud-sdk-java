package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

/**
 * Related interface to provide access to type-safe request builders for the generic entity type.
 *
 * @param <EntityT>
 *            The generic entity type for which request builders can be instantiated.
 */
public interface NavigableEntityCollection<EntityT extends VdmEntity<EntityT>> extends OperationsOnEntityCollections
{
    /**
     * Fetch multiple entities.
     *
     * @return A request builder to fetch multiple entities. This request builder allows methods which modify the
     *         underlying query to be called before executing the query itself. To perform execution, call the
     *         {@link GetAllRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<EntityT> getAll();

    /**
     * Create a new entity and save it to the OData service.
     *
     * @param item
     *            The entity object that will be created and saved.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to create and save a new entity. To perform execution, call the
     *         {@link CreateRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<EntityT> create( @Nonnull final EntityT item );

    /**
     * Fetch the number of entries from the entity collection matching the filter and search expressions.
     *
     * @return A request builder to fetch the count of entities. This request builder allows methods which modify the
     *         underlying query to be called before executing the query itself. To perform execution, call the
     *         {@link CountRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<EntityT> count();
}
