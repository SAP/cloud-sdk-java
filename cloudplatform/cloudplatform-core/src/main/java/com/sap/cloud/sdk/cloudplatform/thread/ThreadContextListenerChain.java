/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ThreadContextListenerChain
{
    /**
     * Adds a {@link ThreadContextListener}.
     *
     * @param listener
     *            The default listener to be added.
     */
    void addListener( @Nonnull final ThreadContextListener listener );

    /**
     * Removes a {@link ThreadContextListener} by its priority.
     *
     * @param listenerPriority
     *            The priority of the listeners to be removed.
     */
    void removeListener( final int listenerPriority );

    /**
     * Returns all listeners (including default listeners) by priority.
     *
     * @return The list of listeners.
     */
    @Nonnull
    List<ThreadContextListener> getListenersOrderedByPriority();

    /**
     * Get a {@link ThreadContextListener} by its priority.
     *
     * @return The expected listener, or {@code null}.
     *
     * @param listenerPriority
     *            The priority of the listeners.
     */
    @Nullable
    default ThreadContextListener getListener( final int listenerPriority )
    {
        final List<ThreadContextListener> listeners = getListenersOrderedByPriority();
        return listeners.stream().filter(l -> listenerPriority == l.getPriority()).findFirst().orElse(null);
    }
}
