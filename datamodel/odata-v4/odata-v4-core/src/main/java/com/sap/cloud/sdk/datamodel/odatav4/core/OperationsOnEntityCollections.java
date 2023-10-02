/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

/**
 * Invoke actions and functions on OData collections
 */
public interface OperationsOnEntityCollections
{
    /**
     * Create a generic type-safe request using navigation properties on entity sets of the OData service.
     *
     * @param entity
     *            A template entity instance that contains all necessary key attributes.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @param <EntityT>
     *            The generic entity type for which navigation properties can be accessed.
     * @return A request builder to navigate the service request using the provided key fields of an entity. With
     *         another navigation property this object can be used to instantiate new request builders.
     */
    @Nonnull
    <EntityT extends VdmEntity<EntityT>> NavigableEntitySingle<EntityT> forEntity( @Nonnull final EntityT entity );

    /**
     * Use a composable function as a path element in an OData request. Similar to
     * {@link #applyFunction(BoundFunction.CollectionToSingle) applyFunction} but allows further path segments after the
     * function. Functions must be marked as {@code composable} by the service.
     *
     * @param function
     *            The composable functions.
     * @param <EntityT>
     *            The entity type
     * @param <ResultT>
     *            The return type of the function.
     * @return A request builder that allows for adding further path segments.
     */
    @Nonnull
    <
        EntityT extends VdmEntity<EntityT>, ResultT extends VdmEntity<ResultT>>
        NavigableEntitySingle<ResultT>

        withFunction( @Nonnull final BoundFunction.CollectionToSingleEntity.Composable<EntityT, ResultT> function );

    /**
     * Use a composable function as a path element in an OData request. Similar to
     * {@link #applyFunction(BoundFunction.CollectionToSingle) applyFunction} but allows further path segments after the
     * function. Functions must be marked as {@code composable} by the service.
     *
     * @param function
     *            The composable functions.
     * @param <EntityT>
     *            The entity type
     * @param <ResultT>
     *            The return type of the function.
     * @return A request builder that allows for adding further path segments.
     */
    @Nonnull

    <
        EntityT extends VdmEntity<EntityT>, ResultT extends VdmEntity<ResultT>>
        NavigableEntityCollection<ResultT>
        withFunction( @Nonnull final BoundFunction.CollectionToCollectionEntity.Composable<EntityT, ResultT> function );

    /**
     * Apply a bound action returning a collection of objects to the current entity collection.
     *
     * @param action
     *            The action to apply. Actions are available on generated entity classes.
     * @param <EntityT>
     *            The entity type
     * @param <ResultT>
     *            The return type of the action.
     * @return A new request builder for the supplied action applied to the current elements.
     */
    @Nonnull
    <EntityT extends VdmEntity<EntityT>, ResultT> CollectionValueActionRequestBuilder<ResultT> applyAction(
        @Nonnull final BoundAction.CollectionToCollection<EntityT, ResultT> action );

    /**
     * Apply a bound action returning a single or no object to an entity collection of the service.
     *
     * @param action
     *            The action to apply. Actions are available on generated entity classes.
     * @param <EntityT>
     *            The type this function is bound to
     * @param <ResultT>
     *            The return type of the action.
     * @return A new request builder for the supplied action applied to the current elements.
     */
    @Nonnull
    <EntityT extends VdmEntity<EntityT>, ResultT> SingleValueActionRequestBuilder<ResultT> applyAction(
        @Nonnull final BoundAction.CollectionToSingle<EntityT, ResultT> action );

    /**
     * Apply a bound function returning a single object to the current element.
     *
     * @param function
     *            The function to apply. Functions are available on generated entity classes.
     * @param <EntityT>
     *            The type this function is bound to
     * @param <ResultT>
     *            The return type of the function.
     * @return A new request builder for the supplied function applied to the current element.
     */
    @Nonnull
    <EntityT extends VdmEntity<EntityT>, ResultT> SingleValueFunctionRequestBuilder<ResultT> applyFunction(
        @Nonnull final BoundFunction.CollectionToSingle<EntityT, ResultT> function );

    /**
     * Apply a bound function returning a collection of objects to the current element.
     *
     * @param function
     *            The function to apply. Functions are available on generated entity classes.
     * @param <EntityT>
     *            The type this function is bound to
     * @param <ResultT>
     *            The type of items in the result of the function.
     * @return A new request builder for the supplied function applied to the current element.
     */
    @Nonnull
    <EntityT extends VdmEntity<EntityT>, ResultT> CollectionValueFunctionRequestBuilder<ResultT> applyFunction(
        @Nonnull final BoundFunction.CollectionToCollection<EntityT, ResultT> function );
}
