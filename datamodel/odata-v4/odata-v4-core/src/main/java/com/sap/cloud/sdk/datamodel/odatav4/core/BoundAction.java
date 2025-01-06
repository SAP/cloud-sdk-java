/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Interface representing an action bound to a specific type.
 *
 * @param <BindingT>
 *            The type the function is bound to.
 * @param <ResultT>
 *            The type this function returns.
 */
public interface BoundAction<BindingT, ResultT> extends BoundOperation<BindingT, ResultT>
{
    /**
     * The parameters to invoke this action with.
     *
     * @return The parameters to invoke this action with.
     */
    @Nonnull
    Map<String, Object> getParameters();

    /**
     * Specific {@link BoundAction action} operating on a single element and returning an object, if any.
     *
     * @param <BindingT>
     *            The type the action is bound to.
     * @param <ResultT>
     *            The type this action returns.
     */
    final class SingleToSingle<BindingT, ResultT> extends AbstractBoundOperation.AbstractBoundAction<BindingT, ResultT>
    {
        /**
         * Default constructor.
         *
         * @param src
         *            The type the action is bound to.
         * @param target
         *            The type this action returns.
         * @param name
         *            The fully qualified name of the action.
         * @param parameters
         *            The parameters to invoke this action with.
         */
        public SingleToSingle(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> parameters )
        {
            super(src, target, name, parameters);
        }
    }

    /**
     * Specific {@link BoundAction action} operating on a single element and returning a collection of objects.
     *
     * @param <BindingT>
     *            The type the action is bound to.
     * @param <ResultT>
     *            The type this action returns.
     */
    final class SingleToCollection<BindingT, ResultT>
        extends
        AbstractBoundOperation.AbstractBoundAction<BindingT, ResultT>
    {
        /**
         * Default constructor.
         *
         * @param src
         *            The type the action is bound to.
         * @param target
         *            The type this action returns.
         * @param name
         *            The fully qualified name of the action.
         * @param parameters
         *            The parameters to invoke this action with.
         */
        public SingleToCollection(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> parameters )
        {
            super(src, target, name, parameters);
        }
    }

    /**
     * Specific {@link BoundAction action} operating on a collection of elements and returning an object, if any.
     *
     * @param <BindingT>
     *            The type the action is bound to.
     * @param <ResultT>
     *            The type this action returns.
     */
    final class CollectionToSingle<BindingT, ResultT>
        extends
        AbstractBoundOperation.AbstractBoundAction<BindingT, ResultT>
    {
        /**
         * Default constructor.
         *
         * @param src
         *            The type the action is bound to.
         * @param target
         *            The type this action returns.
         * @param name
         *            The fully qualified name of the action.
         * @param parameters
         *            The parameters to invoke this action with.
         */
        public CollectionToSingle(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> parameters )
        {
            super(src, target, name, parameters);
        }
    }

    /**
     * Specific {@link BoundAction action} operating on a collection of element and returning a collection of objects.
     *
     * @param <BindingT>
     *            The type the action is bound to.
     * @param <ResultT>
     *            The type this action returns.
     */
    final class CollectionToCollection<BindingT, ResultT>
        extends
        AbstractBoundOperation.AbstractBoundAction<BindingT, ResultT>
    {
        /**
         * Default constructor.
         *
         * @param src
         *            The type the action is bound to.
         * @param target
         *            The type this action returns.
         * @param name
         *            The fully qualified name of the action.
         * @param parameters
         *            The parameters to invoke this action with.
         */
        public CollectionToCollection(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> parameters )
        {
            super(src, target, name, parameters);
        }
    }
}
