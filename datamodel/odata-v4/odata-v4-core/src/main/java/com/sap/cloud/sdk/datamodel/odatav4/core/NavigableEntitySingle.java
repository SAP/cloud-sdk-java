/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

/**
 * Related interface to provide access to type-safe request builders for the generic entity type.
 *
 * @param <EntityT>
 *            The generic entity type for which request builders can be instantiated.
 */
public interface NavigableEntitySingle<EntityT extends VdmEntity<EntityT>>
{
    /**
     * Fetch the current entity.
     *
     * @return A request builder to fetch multiple entities. This request builder allows methods which modify the
     *         underlying query to be called before executing the query itself. To perform execution, call the
     *         {@link GetByKeyRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<EntityT> get();

    /**
     * Update an existing entity and save it to the OData service.
     *
     * @param item
     *            The entity object that will be updated.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     * @return A request builder to update an existing entity. To perform execution, call the
     *         {@link UpdateRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<EntityT> update( @Nonnull final EntityT item );

    /**
     * Deletes the current entity in the OData service.
     *
     * @return A request builder to delete an existing entity. To perform execution, call the
     *         {@link DeleteRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    DeleteRequestBuilder<EntityT> delete();

    /**
     * Navigate to a specific navigation property of current entity type.
     *
     * @param property
     *            The navigation property (collection) to be used.
     * @param <NavigationT>
     *            The generic type of target entity.
     * @return A request builder to access further navigation properties or to instantiate type-safe request builders.
     */
    @Nonnull
    <NavigationT extends VdmEntity<NavigationT>> NavigableEntityCollection<NavigationT> navigateTo(
        @Nonnull final NavigationProperty.Collection<EntityT, NavigationT> property );

    /**
     * Navigate to a specific navigation property of current entity type.
     *
     * @param property
     *            The navigation property (single) to be used.
     * @param <NavigationT>
     *            The generic type of target entity.
     * @return A request builder to access further navigation properties or to instantiate type-safe request builders.
     */
    @Nonnull
    <NavigationT extends VdmEntity<NavigationT>> NavigableEntitySingle<NavigationT> navigateTo(
        @Nonnull final NavigationProperty.Single<EntityT, NavigationT> property );

    /**
     * Apply a bound function returning a single object to the current element.
     *
     * @param function
     *            The function to apply. Functions are available on generated entity classes.
     * @param <ResultT>
     *            The return type of the function.
     * @return A new request builder for the supplied function applied to the current element.
     */
    @Nonnull
    <ResultT> SingleValueFunctionRequestBuilder<ResultT> applyFunction(
        @Nonnull final BoundFunction.SingleToSingle<EntityT, ResultT> function );

    /**
     * Apply a bound function returning a collection of objects to the current element.
     *
     * @param function
     *            The function to apply. Functions are available on generated entity classes.
     * @param <ResultT>
     *            The type of items in the result of the function.
     * @return A new request builder for the supplied function applied to the current element.
     */
    @Nonnull
    <ResultT> CollectionValueFunctionRequestBuilder<ResultT> applyFunction(
        @Nonnull final BoundFunction.SingleToCollection<EntityT, ResultT> function );

    /**
     * Use a composable function as a path element in an OData request. Similar to
     * {@link #applyFunction(BoundFunction.SingleToSingle) applyFunction} but allows further path segments after the
     * function. Functions must be marked as {@code composable} by the service.
     *
     * @param function
     *            The composable functions.
     * @param <ResultT>
     *            The return type of the function.
     * @return A request builder that allows for adding further path segments.
     */
    @Nonnull
    <ResultT extends VdmEntity<ResultT>> NavigableEntitySingle<ResultT> withFunction(
        @Nonnull final BoundFunction.SingleToSingleEntity.Composable<EntityT, ResultT> function );

    /**
     * Use a composable function as a path element in an OData request. Similar to
     * {@link #applyFunction(BoundFunction.SingleToSingle) applyFunction} but allows further path segments after the
     * function. Functions must be marked as {@code composable} by the service.
     *
     * @param function
     *            The composable functions.
     * @param <ResultT>
     *            The return type of the function.
     * @return A request builder that allows for adding further path segments.
     */
    @Nonnull
    <ResultT extends VdmEntity<ResultT>> NavigableEntityCollection<ResultT> withFunction(
        @Nonnull final BoundFunction.SingleToCollectionEntity.Composable<EntityT, ResultT> function );

    /**
     * Apply a bound action returning a single or no object to the current element.
     *
     * @param action
     *            The action to apply. Actions are available on generated entity classes.
     * @param <ResultT>
     *            The return type of the action.
     * @return A new request builder for the supplied action applied to the current element.
     */
    @Nonnull
    <ResultT> SingleValueActionRequestBuilder<ResultT> applyAction(
        @Nonnull final BoundAction.SingleToSingle<EntityT, ResultT> action );

    /**
     * Apply a bound action returning a collection of elements to the current element.
     *
     * @param action
     *            The action to apply. Actions are available on generated entity classes.
     * @param <ResultT>
     *            The return type of the action.
     * @return A new request builder for the supplied action applied to the current element.
     */
    @Nonnull
    <ResultT> CollectionValueActionRequestBuilder<ResultT> applyAction(
        @Nonnull final BoundAction.SingleToCollection<EntityT, ResultT> action );
}
