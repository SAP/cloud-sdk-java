/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import io.vavr.control.Option;

/**
 * OData Service interface to provide type-safe and recursive access to nested entities and their navigation properties.
 */
public interface ServiceWithNavigableEntities extends OperationsOnEntityCollections
{
    /**
     * Getter for the OData service root path.
     *
     * @return the service path.
     */
    @Nonnull
    String getServicePath();

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
    @Override
    @Nonnull
    default <
        EntityT extends VdmEntity<EntityT>> NavigableEntitySingle<EntityT> forEntity( @Nonnull final EntityT entity )
    {
        if( !VdmEntitySet.class.isAssignableFrom(entity.getType()) ) {
            throw new IllegalStateException(
                "Entity type "
                    + entity.getType().getSimpleName()
                    + " must be a sub class of "
                    + VdmEntitySet.class.getName());
        }

        final ODataResourcePath resourcePath = ResourcePathUtil.ofEntity(entity);

        return new ServiceWithNavigableEntitiesImpl.EntitySingle<>(
            getServicePath(),
            resourcePath,
            Option.of(entity),
            entity.getType());
    }

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
    @Override
    @Nonnull
    default <
        EntityT extends VdmEntity<EntityT>, ResultT extends VdmEntity<ResultT>>
        NavigableEntitySingle<ResultT>
        withFunction( @Nonnull final BoundFunction.CollectionToSingleEntity.Composable<EntityT, ResultT> function )
    {
        final ODataResourcePath resourcePath =
            ResourcePathUtil
                .ofBoundOperation(function)
                .addSegment(function.getQualifiedName(), function.getParameters());

        return new ServiceWithNavigableEntitiesImpl.EntitySingle<>(
            getServicePath(),
            resourcePath,
            Option.none(),
            function.getReturnType());
    }

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
    @Override
    @Nonnull
    default <
        EntityT extends VdmEntity<EntityT>, ResultT extends VdmEntity<ResultT>>
        NavigableEntityCollection<ResultT>
        withFunction( @Nonnull final BoundFunction.CollectionToCollectionEntity.Composable<EntityT, ResultT> function )
    {
        final ODataResourcePath resourcePath =
            ResourcePathUtil
                .ofBoundOperation(function)
                .addSegment(function.getQualifiedName(), function.getParameters());

        return new ServiceWithNavigableEntitiesImpl.EntityCollection<>(
            getServicePath(),
            resourcePath,
            function.getReturnType());
    }

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
    @Override
    @Nonnull
    default <EntityT extends VdmEntity<EntityT>, ResultT> CollectionValueActionRequestBuilder<ResultT> applyAction(
        @Nonnull final BoundAction.CollectionToCollection<EntityT, ResultT> action )
    {
        final ODataResourcePath resourcePath = ResourcePathUtil.ofBoundOperation(action);

        return new ServiceWithNavigableEntitiesImpl.EntityCollection<>(
            getServicePath(),
            resourcePath,
            action.getBindingType()).applyAction(action);
    }

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
    @Override
    @Nonnull
    default <EntityT extends VdmEntity<EntityT>, ResultT> SingleValueActionRequestBuilder<ResultT> applyAction(
        @Nonnull final BoundAction.CollectionToSingle<EntityT, ResultT> action )
    {
        final ODataResourcePath resourcePath = ResourcePathUtil.ofBoundOperation(action);

        return new ServiceWithNavigableEntitiesImpl.EntityCollection<>(
            getServicePath(),
            resourcePath,
            action.getBindingType()).applyAction(action);
    }

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
    @Override
    @Nonnull
    default <EntityT extends VdmEntity<EntityT>, ResultT> SingleValueFunctionRequestBuilder<ResultT> applyFunction(
        @Nonnull final BoundFunction.CollectionToSingle<EntityT, ResultT> function )
    {
        final ODataResourcePath resourcePath = ResourcePathUtil.ofBoundOperation(function);

        return new ServiceWithNavigableEntitiesImpl.EntityCollection<>(
            getServicePath(),
            resourcePath,
            function.getBindingType()).applyFunction(function);
    }

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
    @Override
    @Nonnull
    default <EntityT extends VdmEntity<EntityT>, ResultT> CollectionValueFunctionRequestBuilder<ResultT> applyFunction(
        @Nonnull final BoundFunction.CollectionToCollection<EntityT, ResultT> function )
    {
        final ODataResourcePath resourcePath = ResourcePathUtil.ofBoundOperation(function);

        return new ServiceWithNavigableEntitiesImpl.EntityCollection<>(
            getServicePath(),
            resourcePath,
            function.getBindingType()).applyFunction(function);
    }
}
