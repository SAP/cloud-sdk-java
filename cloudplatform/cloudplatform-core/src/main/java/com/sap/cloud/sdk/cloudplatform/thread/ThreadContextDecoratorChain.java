/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Chain of {@link ThreadContextDecorator}s.
 */
public interface ThreadContextDecoratorChain
{
    /**
     * Adds a {@link ThreadContextDecorator}.
     *
     * @param decorator
     *            The default decorator to be added.
     */
    void addDecorator( @Nonnull final ThreadContextDecorator decorator );

    /**
     * Removes a {@link ThreadContextDecorator} by its priority.
     *
     * @param decoratorPriority
     *            The priority of the decorator to be removed.
     */
    void removeDecorator( final int decoratorPriority );

    /**
     * Returns all decorators (including default decorators) by priority.
     *
     * @return The list of decorators.
     */
    @Nonnull
    List<ThreadContextDecorator> getDecoratorsOrderedByPriority();
}
